plugins {
    id("pixiv.android.library.compose")
}

android {
    namespace = "com.mrl.pixiv.repository"
}

dependencies {
    implementation(project(":datasource"))
    implementation(project(":data"))
    implementation(project(":common"))

    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)
    implementation(kotlinx.bundles.serialization)
    implementation(libs.com.google.android.material.material)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}