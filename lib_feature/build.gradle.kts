plugins {
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.feature"
}

dependencies {
    implementation(project(":lib_common"))
}