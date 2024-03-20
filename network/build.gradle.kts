plugins {
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.network"
}

dependencies {
    implementation(project(":data"))
    implementation(project(":repository"))
    implementation(project(":common"))

    implementation(libs.bundles.okhttp)
    implementation(kotlinx.bundles.coroutines)
    implementation(kotlinx.bundles.serialization)
}