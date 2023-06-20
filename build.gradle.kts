buildscript {

}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(androidx.plugins.android.application.get().pluginId) apply false
    id(androidx.plugins.android.library.get().pluginId) apply false
    id(kotlinx.plugins.android.get().pluginId) apply false
    id(kotlinx.plugins.jvm.get().pluginId) apply false
    kotlinx.plugins.serialization.get().let {
        id(it.pluginId) version it.version.requiredVersion apply false
    }
}