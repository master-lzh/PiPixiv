buildscript {

}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(androidx.plugins.android.application.get().pluginId) apply false
    id(androidx.plugins.android.library.get().pluginId) apply false
    alias(kotlinx.plugins.serialization) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(kotlinx.plugins.compose.compiler) apply false
}