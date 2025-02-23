plugins {
    id("pixiv.android.library.compose")
    alias(kotlinx.plugins.serialization)
}

android {
    namespace = "com.mrl.pixiv.common"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(project(":lib_strings"))
    // core-ktx
    api(androidx.core.ktx)
    api(androidx.appcompat)
    // Lifecycle
    api(androidx.bundles.lifecycle)
    // splash screen
    api(androidx.splashscreen)
    // DataStore
    api(androidx.datastore)
    api(androidx.datastore.preferences)
    // ProfileInstaller
    api(androidx.profileinstaller)
    // Paging
    api(androidx.bundles.paging)
    // Compose
    api(platform(compose.bom))
    api(compose.bundles.baselibs)
    api(androidx.activity.compose)
    // Accompanist
    api(compose.bundles.accompanist)
    // Koin
    api(libs.bundles.koin)
    // Ktor
    api(kotlinx.bundles.ktor)
    // Coroutines
    api(kotlinx.bundles.coroutines)
    // Serialization
    api(kotlinx.bundles.serialization)
    // DateTime
    api(kotlinx.datetime)
    // Reflect
    api(kotlinx.reflect)
    // Coil3
    api(platform(libs.coil3.bom))
    api(libs.bundles.coil3)
    // Okio
    api(libs.okio)
    // Firebase
    "defaultApi"(platform(libs.firebase.bom))
    "defaultApi"(libs.bundles.firebase)
    // Guava
    api(libs.guava)
    // MMKV
    api(libs.mmkv)
    api(libs.mmkv.kotlin)
}