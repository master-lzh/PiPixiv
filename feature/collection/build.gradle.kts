plugins {
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.collection"

}

dependencies {
    implementation(androidx.bundles.paging)
}