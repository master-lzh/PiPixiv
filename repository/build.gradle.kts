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
    implementation(project(":util"))

    implementation(androidx.paging)
    implementation(kotlinx.bundles.serialization)
}