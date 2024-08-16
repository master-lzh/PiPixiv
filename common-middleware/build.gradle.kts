plugins {
    id("pixiv.android.library.compose")
}

android {
    namespace = "com.mrl.pixiv.common_viewmodel"
}

dependencies {
    implementation(project(":common"))
    implementation(project(":repository"))
    implementation(project(":domain"))
    ksp(libs.koin.ksp.compiler)


    
}