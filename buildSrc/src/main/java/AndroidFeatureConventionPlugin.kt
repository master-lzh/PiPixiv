/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("pixiv.android.library")
//                apply("pixiv.android.hilt")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            val androidx = extensions.getByType<VersionCatalogsExtension>().named("androidx")
            val kotlinx = extensions.getByType<VersionCatalogsExtension>().named("kotlinx")
            val compose = extensions.getByType<VersionCatalogsExtension>().named("compose")

            dependencies {
                add("implementation", project(":common"))
                add("implementation", project(":util"))
                add("implementation", project(":common-ui"))
                add("implementation", project(":common-middleware"))
                add("implementation", project(":repository"))

                add("testImplementation", kotlin("test"))
                add("androidTestImplementation", kotlin("test"))

                add("implementation", libs.findBundle("coil").get())
                add("implementation", libs.findLibrary("koin").get())

                add("implementation", androidx.findBundle("lifecycle").get())

                add("implementation", kotlinx.findBundle("coroutines").get())
                add("implementation", kotlinx.findBundle("serialization").get())
                add("implementation", kotlinx.findLibrary("reflect").get())

                add("implementation", compose.findBundle("accompanist").get())
            }
        }
    }
}
