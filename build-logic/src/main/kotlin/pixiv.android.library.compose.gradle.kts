import com.mrl.pixiv.buildsrc.configureAndroidCompose

plugins {
    id("pixiv.android.library")
    kotlin("plugin.compose")
}

android {
    configureAndroidCompose(this)
}