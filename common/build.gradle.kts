plugins {
    id("pixiv.android.library.compose")
    alias(kotlinx.plugins.serialization)
}

android {
    namespace = "com.mrl.pixiv.common"
}

dependencies {
    implementation(project(":util"))
    api(libs.koin.annotations)

    implementation(androidx.activity.compose)
    implementation(libs.bundles.lottie)
    implementation(libs.bundles.coil3)
    implementation(kotlinx.bundles.serialization)
}