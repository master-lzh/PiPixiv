plugins {
    id("pixiv.android.library")
}

android {
    namespace = "com.mrl.pixiv.domain"

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
    implementation(project(":util"))
}