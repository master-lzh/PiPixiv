plugins {
    id("pixiv.android.application")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(androidx.plugins.baselineprofile)
}

android {
    namespace = "com.mrl.pixiv"

    lint {
        disable.add("Instantiatable")
    }

    defaultConfig {
        applicationId = "com.mrl.pixiv"
        versionCode = 109
        versionName = "1.0.9"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("../pipixiv.jks")
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEYSTORE_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            versionNameSuffix = "-debug"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }


    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    setProperty(
        "archivesBaseName",
        "${rootProject.name}-v${defaultConfig.versionName}"
    )
}

dependencies {
    implementation(project(":common"))
    implementation(project(":common-ui"))
    implementation(project(":util"))
    implementation(project(":repository"))
    implementation(project(":datasource"))
    implementation(project(":network"))
    implementation(project(":data"))
    implementation(project(":domain"))

    file("../feature").listFiles()?.filter { it.isDirectory }?.forEach { moduleDir ->
        // 使用目录名称构建模块路径
        val moduleName = ":feature:${moduleDir.name}"
        println("module name: $moduleName")
        implementation(project(moduleName))
    }


    implementation(compose.bundles.accompanist)
    implementation(compose.ui.text)
    implementation(androidx.activity.compose)
    implementation(androidx.splashscreen)
    implementation(androidx.datastore)
    implementation(androidx.datastore.preferences)
    implementation(androidx.profileinstaller)
    implementation(kotlinx.bundles.serialization)
    implementation(kotlinx.bundles.coroutines)
    implementation(kotlinx.bundles.ktor)
    implementation(kotlinx.datetime)

    implementation(libs.bundles.coil3)
    implementation(libs.google.material)
    implementation(libs.koin)
    implementation(libs.bundles.coil3)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    baselineProfile(project(":baselineprofile"))
    ksp(libs.koin.ksp.compiler)
}