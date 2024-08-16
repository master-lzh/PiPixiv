plugins {
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.setting"
}

dependencies {
    implementation(project(":repository"))
}