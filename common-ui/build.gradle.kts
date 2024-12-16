plugins {
    id("pixiv.android.library.compose")
}

android {
    namespace = "com.mrl.pixiv.common_ui"
}

dependencies {
    implementation(androidx.bundles.paging)
    implementation(platform(libs.coil3.bom))
    implementation(libs.bundles.coil3)
    implementation(kotlinx.bundles.serialization)

    implementation(project(":util"))
    implementation(project(":common"))
    implementation(project(":repository"))
    implementation(project(":domain"))


    
}