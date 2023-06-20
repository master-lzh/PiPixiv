plugins {
    id("pixiv.android.library")
    alias(kotlinx.plugins.serialization)
}

android {
    namespace = "com.mrl.pixiv.data"

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
    implementation(libs.guava)
    implementation(kotlinx.reflect)
    implementation(kotlinx.bundles.serialization)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.com.google.android.material.material)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}