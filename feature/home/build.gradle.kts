plugins {
    id("pixiv.android.feature")
}


android {
    namespace = "com.mrl.pixiv.home"
}

dependencies {
    implementation(project(":repository"))
    implementation(project(":domain"))



    
}