plugins {
    id("pixiv.android.library.compose")
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.common_ui"
}

dependencies {
    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)

    implementation(libs.com.google.android.material.material)
    implementation(libs.bundles.coil)
    implementation(androidx.constraintlayout.compose)
    implementation(kotlinx.bundles.serialization)

    implementation(project(":util"))
    implementation(project(":common"))
    implementation(project(":common-middleware"))
    implementation(project(":domain"))


    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}