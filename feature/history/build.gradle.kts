plugins {
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.history"
}

dependencies {
    implementation(project(":repository"))
}