@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("pixiv.android.library.compose")
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.picture"

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
    implementation(project(":repository"))
    implementation(project(":domain"))
    implementation(androidx.constraintlayout.compose)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.com.google.android.material.material)
    implementation(project(":common-viewmodel"))
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}