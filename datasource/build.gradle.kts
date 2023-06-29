plugins {
    id("pixiv.android.library.compose")
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.datasource"

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
    implementation(project(":data"))
    implementation(project(":api"))
    implementation(project(":common"))

    implementation(androidx.datastore)
    implementation(androidx.datastore.preferences)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.com.google.android.material.material)

    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}