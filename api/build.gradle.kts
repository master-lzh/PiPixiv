plugins {
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.api"
}

dependencies {
    implementation(project(":data"))
    implementation(project(":common"))

    implementation(libs.bundles.okhttp)

    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)
    implementation(libs.com.google.android.material.material)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}