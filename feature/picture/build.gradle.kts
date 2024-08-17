plugins {
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.picture"
}

dependencies {
    implementation(project(":repository"))
    implementation(project(":domain"))


    implementation(project(":common-middleware"))
    implementation(project(":feature:home"))
    implementation(project(":network"))
    implementation(kotlinx.bundles.ktor)
    
}