plugins {
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.profile"
}

dependencies {
    implementation(project(":repository"))
    implementation(project(":domain"))

//    implementation(libs.collapsing.toolbar)


    
}