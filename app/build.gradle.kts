import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
    id("pixiv.android.application")
    alias(androidx.plugins.baselineprofile)
}
if (project.findProperty("applyFirebasePlugins") == "true") {
    pluginManager.apply(libs.plugins.google.services.get().pluginId)
    pluginManager.apply(libs.plugins.firebase.crashlytics.get().pluginId)
}

android {
    namespace = "com.mrl.pixiv"

    lint {
        disable.add("Instantiatable")
    }

    defaultConfig {
        applicationId = "com.mrl.pixiv"
        versionCode = 10010
        versionName = "1.0.10"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }

    signingConfigs {
        create("release") {
            storeFile = file("../pipixiv.jks")
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEYSTORE_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("default") {
            isDefault = true
            dimension = flavorDimensionList[0]
        }
        create("foss") {
            dimension = flavorDimensionList[0]
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
            signingConfig = signingConfigs.getByName("release")
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
    applicationVariants.configureEach {
        outputs.configureEach {
            (this as? ApkVariantOutputImpl)?.outputFileName =
                "${rootProject.name}-v${defaultConfig.versionName}-$name.apk"
        }
    }
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

    implementation(platform(libs.coil3.bom))
    implementation(libs.bundles.coil3)
    implementation(libs.google.material)
    implementation(libs.koin)

    "defaultImplementation"(platform(libs.firebase.bom))
    "defaultImplementation"(libs.bundles.firebase)

    baselineProfile(project(":baselineprofile"))
    ksp(libs.koin.ksp.compiler)
}
