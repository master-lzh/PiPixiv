plugins {
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.login"
}

dependencies {
    implementation(project(":repository"))
    implementation(project(":domain"))

    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)
    implementation(libs.com.google.android.material.material)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}