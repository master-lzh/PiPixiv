plugins {
    id("pixiv.android.library.compose")
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.common"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":util"))

    implementation(androidx.appcompat)
    implementation(androidx.activity.compose)
    implementation(libs.bundles.lottie)
    implementation(compose.accompanist.systemuicontroller)
    implementation(libs.bundles.coil)
}