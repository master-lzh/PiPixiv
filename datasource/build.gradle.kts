plugins {
    id("pixiv.android.library.compose")
    alias(kotlinx.plugins.serialization)
}

android {
    namespace = "com.mrl.pixiv.datasource"
}

dependencies {
    implementation(project(":data"))
    implementation(project(":api"))
    implementation(project(":common"))

    implementation(androidx.datastore)
    implementation(androidx.datastore.preferences)

    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)
    implementation(kotlinx.bundles.serialization)
    implementation(libs.com.google.android.material.material)

    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}