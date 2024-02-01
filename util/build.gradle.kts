plugins {
    id("pixiv.android.library.compose")
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.util"

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

    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)
    implementation(libs.com.google.android.material.material)
    implementation(libs.bundles.okhttp)
    implementation(libs.okio)

    implementation(platform(libs.firebase.bom))
    api(libs.bundles.firebase)

    implementation(compose.animation)
    implementation(compose.foundation)
    implementation(compose.ui)

    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}