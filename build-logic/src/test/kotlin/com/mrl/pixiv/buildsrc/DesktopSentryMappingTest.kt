package com.mrl.pixiv.buildsrc

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.Properties
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DesktopSentryMappingTest {
    @get:Rule
    val temporary = TemporaryFolder()

    private val fixture: ByteArray
        get() = javaClass.getResourceAsStream("/desktop-sentry/mapping.txt")!!.use { it.readBytes() }

    @Test
    fun `mapping UUID matches sentry-cli and depends on exact content rather than path`() {
        val mapping = temporary.newFile("mapping.txt").apply { writeBytes(fixture) }
        val renamed = temporary.newFile("renamed.txt").apply { writeBytes(fixture) }

        // Generated using sentry-cli 3.1.0 upload-proguard --no-upload --write-properties.
        assertEquals("1b673da6-48d5-5d9e-91e6-a24d8748dbf7", desktopSentryMappingUuid(mapping))
        assertEquals(desktopSentryMappingUuid(mapping), desktopSentryMappingUuid(renamed))
        renamed.appendText("\n")
        assertNotEquals(desktopSentryMappingUuid(mapping), desktopSentryMappingUuid(renamed))
        assertFailsWith<IllegalArgumentException> { desktopSentryMappingUuid(temporary.newFile()) }
        assertFailsWith<IllegalArgumentException> {
            desktopSentryMappingUuid(File(temporary.root, "missing.txt"))
        }
    }

    @Test
    fun `injection retains payload and existing metadata without embedding the mapping`() {
        val mapping = temporary.newFile("mapping.txt").apply { writeBytes(fixture) }
        val payload = mapOf(
            "example/Main.class" to ByteArray(512) { (it % 251).toByte() },
            "native/libexample.dylib" to ByteArray(32768) { (it % 239).toByte() },
            "META-INF/services/example.Service" to "example.Implementation\n".toByteArray(),
        )
        val jar = createJar(payload + (RESOURCE to "io.sentry.ProguardUuids=stale\nio.sentry.bundle-ids=existing-bundle\n".toByteArray()))
        val compressedSizes = ZipFile(jar).use { zip -> payload.keys.associateWith { zip.getEntry(it).compressedSize } }

        val uuid = injectDesktopSentryMapping(mapping, jar)

        ZipFile(jar).use { zip ->
            assertEquals(payload.keys + RESOURCE, zip.entries().asSequence().map { it.name }.toSet())
            payload.forEach { (name, bytes) ->
                assertContentEquals(bytes, zip.getInputStream(zip.getEntry(name)).use { it.readBytes() })
                assertEquals(compressedSizes[name], zip.getEntry(name).compressedSize)
            }
            val properties = Properties().apply { zip.getInputStream(zip.getEntry(RESOURCE)).use(::load) }
            assertEquals(uuid, properties.getProperty(UUID_PROPERTY))
            assertEquals("existing-bundle", properties.getProperty("io.sentry.bundle-ids"))
            assertContentEquals(
                mapping.resolveSibling(RESOURCE).readBytes(),
                zip.getInputStream(zip.getEntry(RESOURCE)).use { it.readBytes() },
            )
        }
    }

    @Test
    fun `same mapping leaves jar unchanged but restores missing sidecar`() {
        val mapping = temporary.newFile("mapping.txt").apply { writeBytes(fixture) }
        val jar = createJar(mapOf("Main.class" to byteArrayOf(1, 2, 3)))
        val originalUuid = injectDesktopSentryMapping(mapping, jar)
        val originalJar = jar.readBytes()
        val originalModified = jar.lastModified()
        val sidecar = mapping.resolveSibling(RESOURCE)
        val originalSidecar = sidecar.readBytes()
        assertTrue(sidecar.delete())

        assertEquals(originalUuid, injectDesktopSentryMapping(mapping, jar))
        assertContentEquals(originalJar, jar.readBytes())
        assertEquals(originalModified, jar.lastModified())
        assertContentEquals(originalSidecar, sidecar.readBytes())

        sidecar.writeText("io.sentry.ProguardUuids=stale\n")
        injectDesktopSentryMapping(mapping, jar)
        assertContentEquals(originalSidecar, sidecar.readBytes())

        mapping.appendText("com.example.Another -> c:\n")
        val changedUuid = injectDesktopSentryMapping(mapping, jar)
        assertNotEquals(originalUuid, changedUuid)
        assertEquals(changedUuid, readUuid(jar))
        assertEquals(changedUuid, Properties().apply { sidecar.inputStream().use(::load) }.getProperty(UUID_PROPERTY))
    }

    @Test
    fun `signed jar is rejected before its contents are changed`() {
        val mapping = temporary.newFile("mapping.txt").apply { writeBytes(fixture) }
        val jar = createJar(mapOf("Main.class" to byteArrayOf(1), "META-INF/APP.SF" to byteArrayOf(2)))
        val before = jar.readBytes()

        assertFailsWith<IllegalArgumentException> { injectDesktopSentryMapping(mapping, jar) }
        assertContentEquals(before, jar.readBytes())
    }

    @Test
    fun `producer remains incremental with configuration cache and restores deleted metadata`() {
        val project = temporary.newFolder("gradle-project")
        File(project, "settings.gradle.kts").writeText("rootProject.name = \"sentry-mapping-test\"\n")
        File(project, "mapping-input.txt").writeBytes(fixture)
        val helperClasses = File(UploadDesktopSentryMappingTask::class.java.protectionDomain.codeSource.location.toURI())
        val escapedClasspath = helperClasses.absolutePath.replace("\\", "\\\\").replace("\"", "\\\"")
        File(project, "build.gradle.kts").writeText(
            """
            import com.mrl.pixiv.buildsrc.configureDesktopSentryMapping
            import java.util.zip.ZipEntry
            import java.util.zip.ZipOutputStream

            buildscript { dependencies { classpath(files("$escapedClasspath")) } }

            abstract class FakeProguard : DefaultTask() {
                @get:InputFile abstract val source: RegularFileProperty
                @get:OutputFile abstract val mapping: RegularFileProperty
                @get:OutputFile abstract val jar: RegularFileProperty
                @TaskAction fun generate() {
                    mapping.get().asFile.apply { parentFile.mkdirs(); writeBytes(source.get().asFile.readBytes()) }
                    ZipOutputStream(jar.get().asFile.outputStream()).use {
                        it.putNextEntry(ZipEntry("Main.class"))
                        it.write(byteArrayOf(1, 2, 3))
                        it.closeEntry()
                    }
                }
            }
            val producer = tasks.register<FakeProguard>("fakeProguard") {
                source.set(layout.projectDirectory.file("mapping-input.txt"))
                mapping.set(layout.buildDirectory.file("proguard/mapping.txt"))
                jar.set(layout.buildDirectory.file("proguard/application.jar"))
            }
            configureDesktopSentryMapping(
                producer,
                layout.buildDirectory.file("proguard/mapping.txt"),
                layout.buildDirectory.file("proguard/application.jar"),
                "test-org", "test-project",
            )
            """.trimIndent()
        )
        fun build() = GradleRunner.create().withProjectDir(project)
            .withArguments("fakeProguard", "--configuration-cache", "--stacktrace", "--console=plain")
            .build()

        val jar = File(project, "build/proguard/application.jar")
        val sidecar = File(project, "build/proguard/$RESOURCE")
        assertEquals(TaskOutcome.SUCCESS, build().task(":fakeProguard")!!.outcome)
        assertEquals("1b673da6-48d5-5d9e-91e6-a24d8748dbf7", readUuid(jar))
        val second = build()
        assertEquals(TaskOutcome.UP_TO_DATE, second.task(":fakeProguard")!!.outcome)
        assertTrue(second.output.contains("Reusing configuration cache."), second.output)

        assertTrue(sidecar.delete())
        assertEquals(TaskOutcome.SUCCESS, build().task(":fakeProguard")!!.outcome)
        assertTrue(sidecar.isFile)
        assertEquals(TaskOutcome.UP_TO_DATE, build().task(":fakeProguard")!!.outcome)

        File(project, "mapping-input.txt").appendText("com.example.Another -> c:\n")
        assertEquals(TaskOutcome.SUCCESS, build().task(":fakeProguard")!!.outcome)
        assertNotEquals("1b673da6-48d5-5d9e-91e6-a24d8748dbf7", readUuid(jar))
        assertEquals(TaskOutcome.UP_TO_DATE, build().task(":fakeProguard")!!.outcome)
    }

    private fun createJar(entries: Map<String, ByteArray>): File = temporary.newFile("application.jar").apply {
        ZipOutputStream(outputStream()).use { zip ->
            entries.forEach { (name, bytes) ->
                zip.putNextEntry(ZipEntry(name).apply { time = 315532800000L })
                zip.write(bytes)
                zip.closeEntry()
            }
        }
    }

    private fun readUuid(jar: File): String? = ZipFile(jar).use { zip ->
        Properties().apply { zip.getInputStream(zip.getEntry(RESOURCE)).use(::load) }.getProperty(UUID_PROPERTY)
    }

    private companion object {
        const val RESOURCE = "sentry-debug-meta.properties"
        const val UUID_PROPERTY = "io.sentry.ProguardUuids"
    }
}
