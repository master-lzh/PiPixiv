plugins {
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.domain"
}

dependencies {
    implementation(project(":repository"))
    implementation(project(":util"))
    implementation(project(":common"))
    implementation(project(":data"))

    implementation(kotlinx.bundles.serialization)
    implementation(libs.koin)
}