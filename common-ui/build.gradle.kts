plugins {
    id("pixiv.android.library.compose")
}

android {
    namespace = "com.mrl.pixiv.common_ui"
}

dependencies {


    implementation(libs.bundles.coil)
    implementation(kotlinx.bundles.serialization)

    implementation(project(":util"))
    implementation(project(":common"))
    implementation(project(":common-middleware"))
    implementation(project(":domain"))


    
}