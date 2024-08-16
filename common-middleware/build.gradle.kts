plugins {
    id("pixiv.android.library.compose")
}

android {
    namespace = "com.mrl.pixiv.common_viewmodel"
}

dependencies {
    implementation(project(":common"))
    implementation(project(":repository"))
    implementation(project(":domain"))

    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)
    implementation(libs.com.google.android.material.material)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}