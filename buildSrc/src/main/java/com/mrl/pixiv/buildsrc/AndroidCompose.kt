/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mrl.pixiv.buildsrc

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Configure Compose-specific options
 */
fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    val compose = extensions.getByType<VersionCatalogsExtension>().named("compose")
    val androidx = extensions.getByType<VersionCatalogsExtension>().named("androidx")
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {
        buildFeatures {
            this.compose = true
        }

//        composeOptions {
//            kotlinCompilerExtensionVersion = compose.findVersion("compiler").get().toString()
//        }

        dependencies {
            val bom = compose.findLibrary("bom").get()
            add("implementation", platform(bom))

            add("implementation", androidx.findLibrary("core-ktx").get())
            add("implementation", androidx.findBundle("lifecycle").get())
            add("implementation", androidx.findLibrary("navigation.compose").get())
            add("implementation", androidx.findLibrary("constraintlayout.compose").get())
            add("implementation", androidx.findLibrary("tracing").get())

            add("implementation", compose.findBundle("material").get())
            add("implementation", compose.findBundle("baselibs").get())

            add("implementation", libs.findLibrary("koin.compose").get())

            add("implementation", project(":data"))
            if (commonExtension !is ApplicationExtension) {
                add("androidTestImplementation", compose.findLibrary("ui-test-junit4").get())
            }
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(buildComposeMetricsParameters())
            languageVersion.set(KotlinVersion.KOTLIN_2_0)
        }
    }
}

private fun Project.buildComposeMetricsParameters(): List<String> {
    val metricParameters = mutableListOf<String>()
    val enableMetricsProvider = project.providers.gradleProperty("enableComposeCompilerMetrics")
    val enableMetrics = (enableMetricsProvider.orNull == "true") || true
    if (enableMetrics) {
        val metricsFolder = project.layout.buildDirectory.file("compose-metrics").get().asFile
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + metricsFolder.absolutePath
        )
    }

    val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
    val enableReports = (enableReportsProvider.orNull == "true") || true
    if (enableReports) {
        val reportsFolder = project.layout.buildDirectory.file("compose-reports").get().asFile
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + reportsFolder.absolutePath
        )
    }
    return metricParameters.toList()
}
