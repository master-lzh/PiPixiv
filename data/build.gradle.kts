plugins {
    id("pixiv.android.library")
    alias(kotlinx.plugins.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "com.mrl.pixiv.data"
}

dependencies {
    implementation(libs.guava)
    implementation(kotlinx.reflect)
    implementation(kotlinx.bundles.serialization)
    implementation(androidx.datastore)
    implementation(platform(compose.bom))
    implementation(compose.runtime.android)


    
}