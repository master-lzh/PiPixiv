plugins {
    id("pixiv.android.library.compose")
}

android {
    namespace = "com.mrl.pixiv.repository"
}

dependencies {
    implementation(project(":datasource"))
    implementation(project(":data"))
    implementation(project(":common"))

    implementation(kotlinx.bundles.serialization)

    
}