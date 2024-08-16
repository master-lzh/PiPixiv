plugins {
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.api"
}

dependencies {
    implementation(project(":data"))
    implementation(project(":common"))

    implementation(libs.bundles.okhttp)
}