import com.mrl.pixiv.buildsrc.configureAndroidCompose
import com.mrl.pixiv.buildsrc.configureKotlinAndroid

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    defaultConfig {
        targetSdk = 35
    }
    configureKotlinAndroid(this)
    configureAndroidCompose(this)
}