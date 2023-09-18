plugins {
    id("pixiv.android.library.compose")
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.common_ui"

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
    implementation(libs.core.ktx)
    implementation(libs.appcompat)

    implementation(libs.com.google.android.material.material)
    implementation(libs.bundles.coil)

    implementation(project(mapOf("path" to ":util")))

    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}