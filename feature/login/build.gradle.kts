plugins {
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.login"
}

dependencies {
    implementation(project(":repository"))
    implementation(project(":domain"))


    
}