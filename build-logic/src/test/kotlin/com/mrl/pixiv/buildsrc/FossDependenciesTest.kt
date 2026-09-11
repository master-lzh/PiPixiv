package com.mrl.pixiv.buildsrc

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FossDependenciesTest {
    @get:Rule
    val temporary = TemporaryFolder()

    @Test
    fun `runtime graph accepts clean dependencies and rejects transitive Kotzilla`() {
        val project = fixture()
        val clean = runner(project).build()
        assertEquals(TaskOutcome.SUCCESS, clean.task(":verifyFossDependencies")?.outcome)
        val report = File(project, "build/reports/foss/dependencies.txt").readText()
        assertTrue("example:clean:1.0" in report)
        assertFalse("kotzilla" in report)

        val rejected = runner(project, "-PwithAnalytics=true").buildAndFail()
        assertTrue("FOSS runtime contains excluded analytics dependencies" in rejected.output, rejected.output)
        assertTrue("io.kotzilla:kotzilla-sdk-compose:1.0" in rejected.output, rejected.output)
    }

    @Test
    fun `unresolved dependencies cannot produce a successful verification`() {
        val project = fixture()
        val rejected = runner(project, "-PwithMissingDependency=true").buildAndFail()
        assertTrue("Cannot verify FOSS dependencies" in rejected.output, rejected.output)
        assertFalse(File(project, "build/reports/foss/dependencies.txt").exists())
    }

    @Test
    fun `default analytics project is rejected even without SDK artifacts`() {
        val project = fixture()
        val rejected = runner(project, "-PwithDefaultAnalytics=true").buildAndFail()
        assertTrue("FOSS runtime contains excluded analytics dependencies" in rejected.output, rejected.output)
        assertTrue(":common:analytics-default" in rejected.output, rejected.output)
    }

    private fun fixture(): File {
        val project = temporary.newFolder()
        File(project, "settings.gradle.kts").writeText(
            "rootProject.name = \"foss-check\"\ninclude(\":common:analytics-default\")\n",
        )
        File(project, "common/analytics-default").apply { mkdirs() }
            .resolve("build.gradle.kts").writeText("plugins { `java-library` }\n")
        val helperClasses = File(VerifyFossDependenciesTask::class.java.protectionDomain.codeSource.location.toURI())
        val classpath = helperClasses.absolutePath.replace("\\", "\\\\").replace("\"", "\\\"")
        File(project, "build.gradle.kts").writeText(
            """
            import com.mrl.pixiv.buildsrc.registerFossDependencyCheck
            buildscript { dependencies { classpath(files("$classpath")) } }
            plugins { `java-library` }
            repositories { maven { url = uri("repo") } }
            dependencies {
                implementation("example:clean:1.0")
                if (providers.gradleProperty("withAnalytics").isPresent) {
                    implementation("example:bridge:1.0")
                }
                if (providers.gradleProperty("withMissingDependency").isPresent) {
                    implementation("example:missing:1.0")
                }
                if (providers.gradleProperty("withDefaultAnalytics").isPresent) {
                    implementation(project(":common:analytics-default"))
                }
            }
            val verify = registerFossDependencyCheck(configurations.named("runtimeClasspath"))
            tasks.named("check") { dependsOn(verify) }
            """.trimIndent(),
        )
        publishPom(project, "example", "clean")
        publishPom(project, "io.kotzilla", "kotzilla-sdk-compose")
        publishPom(
            project, "example", "bridge",
            """<dependencies><dependency><groupId>io.kotzilla</groupId><artifactId>kotzilla-sdk-compose</artifactId><version>1.0</version></dependency></dependencies>""",
        )
        return project
    }

    private fun publishPom(project: File, group: String, artifact: String, dependencies: String = "") {
        File(project, "repo/${group.replace('.', '/')}/$artifact/1.0/$artifact-1.0.pom").apply {
            parentFile.mkdirs()
            writeText(
                """<project><modelVersion>4.0.0</modelVersion><groupId>$group</groupId><artifactId>$artifact</artifactId><version>1.0</version><packaging>pom</packaging>$dependencies</project>""",
            )
        }
    }

    private fun runner(project: File, vararg options: String) = GradleRunner.create()
        .withProjectDir(project)
        .withArguments("verifyFossDependencies", "--offline", "--console=plain", *options)
}
