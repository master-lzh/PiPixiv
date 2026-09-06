package com.mrl.pixiv.buildsrc

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.Serializable
import java.nio.ByteBuffer
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.FileTime
import java.security.MessageDigest
import java.util.Properties
import java.util.UUID
import java.util.zip.ZipFile
import javax.inject.Inject

private const val DEBUG_META_RESOURCE = "sentry-debug-meta.properties"
private const val PROGUARD_UUID_PROPERTY = "io.sentry.ProguardUuids"
private val PROGUARD_NAMESPACE = uuidV5(
    UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8"),
    "guardsquare.com".toByteArray(Charsets.UTF_8),
)

/**
 * Injects the mapping ID before the producer's outputs are snapshotted and before packaging/signing.
 * The returned upload task is explicit: configuring this helper never uploads a mapping.
 * [outputJar] must be the complete, unsigned JAR produced by [proguardTask].
 */
fun Project.configureDesktopSentryMapping(
    proguardTask: TaskProvider<out Task>,
    mappingFile: Provider<RegularFile>,
    outputJar: Provider<RegularFile>,
    organization: String,
    sentryProject: String,
    uploadTaskName: String = "uploadDesktopSentryMapping",
): TaskProvider<UploadDesktopSentryMappingTask> {
    proguardTask.configure {
        inputs.property("desktopSentryMappingFormatVersion", 1)
        outputs.file(mappingFile).withPropertyName("desktopSentryMapping")
        outputs.file(outputJar).withPropertyName("desktopSentryMappedJar")
        outputs.file(mappingFile.map { it.asFile.resolveSibling(DEBUG_META_RESOURCE) })
            .withPropertyName("desktopSentryDebugMeta")
        doLast(InjectDesktopSentryMapping(mappingFile, outputJar))
    }

    val mappingProvider = mappingFile
    val jarProvider = outputJar
    val orgSlug = organization
    val projectSlug = sentryProject
    return tasks.register<UploadDesktopSentryMappingTask>(uploadTaskName) {
        group = "sentry"
        description = "Uploads the mapping matching the packaged desktop JVM application"
        dependsOn(proguardTask)
        this.mappingFile.set(mappingProvider)
        this.outputJar.set(jarProvider)
        this.organization.set(orgSlug)
        this.sentryProject.set(projectSlug)
        cliExecutable.convention(
            providers.environmentVariable("SENTRY_CLI_EXECUTABLE")
                .orElse(providers.gradleProperty("sentryCliExecutable"))
                .orElse("sentry-cli")
        )
    }
}

private class InjectDesktopSentryMapping(
    private val mappingFile: Provider<RegularFile>,
    private val outputJar: Provider<RegularFile>,
) : Action<Task>, Serializable {
    override fun execute(task: Task) {
        val uuid = injectDesktopSentryMapping(mappingFile.get().asFile, outputJar.get().asFile)
        task.logger.lifecycle("Embedded desktop Sentry mapping UUID: $uuid")
    }
}

@DisableCachingByDefault(because = "Uploads a mapping to an external Sentry project")
abstract class UploadDesktopSentryMappingTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val mappingFile: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val outputJar: RegularFileProperty

    @get:Input
    abstract val organization: Property<String>

    @get:Input
    abstract val sentryProject: Property<String>

    @get:Input
    abstract val cliExecutable: Property<String>

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @TaskAction
    fun upload() {
        val mapping = mappingFile.get().asFile
        val expectedUuid = desktopSentryMappingUuid(mapping)
        val packagedUuid = readDebugMeta(outputJar.get().asFile).getProperty(PROGUARD_UUID_PROPERTY)
        check(packagedUuid == expectedUuid) {
            "Desktop Sentry mapping does not match the UUID embedded in ${outputJar.get().asFile.name}"
        }

        // Authentication stays in SENTRY_AUTH_TOKEN, inherited by the child process.
        // Never put credentials in command arguments, Gradle inputs, or application resources.
        execOperations.exec {
            executable(cliExecutable.get())
            args(
                "upload-proguard", "--require-one",
                "--org", organization.get(), "--project", sentryProject.get(),
                mapping.absolutePath,
            )
        }.assertNormalExitValue()
    }
}

/** Matches getsentry/rust-proguard: UUIDv5(UUIDv5(DNS, "guardsquare.com"), mapping bytes). */
internal fun desktopSentryMappingUuid(mappingFile: File): String {
    require(mappingFile.isFile && mappingFile.length() > 0) {
        "Missing or empty desktop ProGuard mapping: $mappingFile"
    }
    val digest = MessageDigest.getInstance("SHA-1")
    digest.update(uuidBytes(PROGUARD_NAMESPACE))
    mappingFile.inputStream().buffered().use { input ->
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            digest.update(buffer, 0, count)
        }
    }
    return uuidFromDigest(digest.digest()).toString()
}

internal fun injectDesktopSentryMapping(mappingFile: File, outputJar: File): String {
    val uuid = desktopSentryMappingUuid(mappingFile)
    val properties = readDebugMeta(outputJar)
    val alreadyEmbedded = properties.getProperty(PROGUARD_UUID_PROPERTY) == uuid
    properties.setProperty(PROGUARD_UUID_PROPERTY, uuid)
    val metadata = deterministicProperties(properties)
    val sidecar = mappingFile.resolveSibling(DEBUG_META_RESOURCE)
    if (!sidecar.isFile || !sidecar.readBytes().contentEquals(metadata)) {
        sidecar.writeBytes(metadata)
    }
    if (alreadyEmbedded) return uuid

    ZipFile(outputJar).use { zip ->
        require(zip.entries().asSequence().none { entry ->
            val name = entry.name.uppercase()
            name.startsWith("META-INF/") && name.count { it == '/' } == 1 &&
                listOf(".SF", ".RSA", ".DSA", ".EC").any(name::endsWith)
        }) { "Inject desktop Sentry metadata before signing the JAR: $outputJar" }
    }

    val jarPath = outputJar.toPath()
    val temporaryJar = Files.createTempFile(jarPath.toAbsolutePath().parent, ".sentry-mapping-", ".jar")
    try {
        Files.copy(jarPath, temporaryJar, REPLACE_EXISTING, COPY_ATTRIBUTES)
        // ZipFS copies untouched compressed entries as-is instead of recompressing the entire JAR.
        FileSystems.newFileSystem(temporaryJar, emptyMap<String, Any>()).use { zip ->
            val resource = zip.getPath("/$DEBUG_META_RESOURCE")
            Files.write(resource, metadata)
            val timestamp = FileTime.fromMillis(315532800000L)
            Files.getFileAttributeView(resource, BasicFileAttributeView::class.java)
                .setTimes(timestamp, timestamp, timestamp)
        }
        try {
            Files.move(temporaryJar, jarPath, ATOMIC_MOVE, REPLACE_EXISTING)
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(temporaryJar, jarPath, REPLACE_EXISTING)
        }
    } finally {
        Files.deleteIfExists(temporaryJar)
    }
    return uuid
}

private fun readDebugMeta(jar: File): Properties = Properties().apply {
    require(jar.isFile) { "Missing desktop output JAR: $jar" }
    ZipFile(jar).use { zip ->
        zip.getEntry(DEBUG_META_RESOURCE)?.let { entry ->
            zip.getInputStream(entry).use(::load)
        }
    }
}

private fun deterministicProperties(properties: Properties): ByteArray {
    val output = ByteArrayOutputStream()
    properties.store(output, null)
    return output.toString(Charsets.ISO_8859_1)
        .lineSequence()
        .filter { it.isNotEmpty() && !it.startsWith('#') }
        .sorted()
        .joinToString("\n", postfix = "\n")
        .toByteArray(Charsets.ISO_8859_1)
}

private fun uuidV5(namespace: UUID, bytes: ByteArray): UUID {
    val digest = MessageDigest.getInstance("SHA-1")
    digest.update(uuidBytes(namespace))
    return uuidFromDigest(digest.digest(bytes))
}

private fun uuidBytes(uuid: UUID): ByteArray = ByteBuffer.allocate(16)
    .putLong(uuid.mostSignificantBits)
    .putLong(uuid.leastSignificantBits)
    .array()

private fun uuidFromDigest(hash: ByteArray): UUID {
    hash[6] = ((hash[6].toInt() and 0x0f) or 0x50).toByte()
    hash[8] = ((hash[8].toInt() and 0x3f) or 0x80).toByte()
    return ByteBuffer.wrap(hash).let { UUID(it.long, it.long) }
}
