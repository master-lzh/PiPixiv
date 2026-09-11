package com.mrl.pixiv.buildsrc

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.UnresolvedDependencyResult
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

fun Project.registerFossDependencyCheck(
    runtimeConfiguration: Provider<Configuration>,
): TaskProvider<VerifyFossDependenciesTask> {
    val coordinates = runtimeConfiguration.map { configuration ->
        val resolution = configuration.incoming.resolutionResult
        val unresolved = resolution.allDependencies.filterIsInstance<UnresolvedDependencyResult>()
        if (unresolved.isNotEmpty()) {
            throw GradleException("Cannot verify FOSS dependencies: ${unresolved.joinToString { it.attempted.displayName }}")
        }
        resolution.allComponents.map { component ->
            when (val id = component.id) {
                is ModuleComponentIdentifier -> "${id.group}:${id.module}:${id.version}"
                is ProjectComponentIdentifier -> id.projectPath
                else -> id.displayName
            }
        }.sorted()
    }
    return tasks.register<VerifyFossDependenciesTask>("verifyFossDependencies") {
        group = "verification"
        description = "Checks the resolved FOSS runtime graph for excluded analytics dependencies"
        dependencyCoordinates.set(coordinates)
        reportFile.set(layout.buildDirectory.file("reports/foss/dependencies.txt"))
    }
}

@CacheableTask
abstract class VerifyFossDependenciesTask : DefaultTask() {
    @get:Input
    abstract val dependencyCoordinates: ListProperty<String>

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @TaskAction
    fun verify() {
        val dependencies = dependencyCoordinates.get()
        val analyticsGroups = listOf("io.kotzilla", "com.google.firebase", "io.sentry")
        val excluded = dependencies.filter { coordinate ->
            val group = coordinate.substringBefore(':')
            coordinate == ":common:analytics-default" || analyticsGroups.any {
                group == it || group.startsWith("$it.")
            }
        }
        if (excluded.isNotEmpty()) {
            throw GradleException("FOSS runtime contains excluded analytics dependencies:\n${excluded.joinToString("\n")}")
        }
        reportFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(dependencies.joinToString("\n", postfix = "\n"))
        }
        logger.lifecycle("Verified FOSS runtime: ${dependencies.size} components, no excluded analytics dependencies.")
    }
}
