plugins {
    id("pixiv.android.library")
    alias(kotlinx.plugins.serialization)
    id("kotlin-parcelize")
    alias(libs.plugins.protubuf)
}

android {
    namespace = "com.mrl.pixiv.data"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.25.3"
        }
        generateProtoTasks {
            all().forEach {
                it.builtins {
                    create("java") {
                        option("lite")
                    }
                    create("kotlin") {
                        option("lite")
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(libs.guava)
    implementation(kotlinx.reflect)
    implementation(kotlinx.bundles.serialization)
    implementation(androidx.datastore)
    api(libs.protobuf.kotlin.lite)

    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)
    implementation(libs.com.google.android.material.material)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}