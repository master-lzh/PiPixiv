import com.mrl.pixiv.buildsrc.configureKotlinAndroid
import com.mrl.pixiv.buildsrc.disableUnnecessaryAndroidTests

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.google.devtools.ksp")
}

android {
    configureKotlinAndroid(this)

    flavorDimensions += "version"
    productFlavors {
        create("default") {
            dimension = flavorDimensionList[0]
        }
        create("foss") {
            dimension = flavorDimensionList[0]
        }
    }
    androidComponents {
        disableUnnecessaryAndroidTests(project)
    }
}