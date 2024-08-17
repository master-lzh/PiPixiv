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

import com.mrl.pixiv.buildsrc.androidTestImplementation
import com.mrl.pixiv.buildsrc.implementation
import com.mrl.pixiv.buildsrc.testImplementation
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
                apply("pixiv.android.library.compose")
//                apply("pixiv.android.hilt")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            val androidx = extensions.getByType<VersionCatalogsExtension>().named("androidx")
            val kotlinx = extensions.getByType<VersionCatalogsExtension>().named("kotlinx")
            val compose = extensions.getByType<VersionCatalogsExtension>().named("compose")

            dependencies {
                implementation(project(":common"))
                implementation(project(":util"))
                implementation(project(":common-ui"))
                implementation(project(":common-middleware"))
                implementation(project(":repository"))

                testImplementation(kotlin("test"))
                androidTestImplementation(kotlin("test"))

                implementation(libs.findBundle("coil3").get())
                implementation(libs.findLibrary("koin").get())

                implementation(androidx.findBundle("lifecycle").get())

                implementation(kotlinx.findBundle("coroutines").get())
                implementation(kotlinx.findBundle("serialization").get())
                implementation(kotlinx.findLibrary("reflect").get())

                implementation(compose.findBundle("accompanist").get())
            }
        }
    }
}
