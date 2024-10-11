plugins {
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.search"
}

dependencies {
    implementation(project(":repository"))
    implementation(project(":domain"))
    implementation(project(":util"))
}