plugins {
    id("pixiv.android.library.compose")
}

android {
    namespace = "com.mrl.pixiv.common"
}

dependencies {
    implementation(project(":util"))

    implementation(androidx.appcompat)
    implementation(androidx.activity.compose)
    implementation(libs.bundles.lottie)
    implementation(compose.accompanist.systemuicontroller)
    implementation(libs.bundles.coil)
}