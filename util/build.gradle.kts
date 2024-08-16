plugins {
    id("pixiv.android.library.compose")
}

android {
    namespace = "com.mrl.pixiv.util"
}

dependencies {


    implementation(libs.bundles.okhttp)
    implementation(libs.okio)

    implementation(platform(libs.firebase.bom))
    api(libs.bundles.firebase)

    implementation(compose.animation)
    implementation(compose.foundation)
    implementation(compose.ui)

    
}