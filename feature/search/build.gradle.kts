plugins {
    id("pixiv.android.library.compose")
    id("pixiv.android.feature")
}

android {
    namespace = "com.mrl.pixiv.search"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":repository"))
    implementation(project(":domain"))
    implementation(project(":util"))
}