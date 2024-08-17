plugins {
    id("pixiv.android.library.compose")
    alias(kotlinx.plugins.serialization)
}

android {
    namespace = "com.mrl.pixiv.datasource"
}

dependencies {
    implementation(project(":data"))
    implementation(project(":common"))

    implementation(androidx.datastore)
    implementation(androidx.datastore.preferences)

    implementation(kotlinx.bundles.serialization)
    implementation(kotlinx.bundles.ktor)
    implementation(project(":util"))
}