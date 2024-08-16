plugins {
    id("pixiv.android.library")
    alias(kotlinx.plugins.serialization)
    id("kotlin-parcelize")
    alias(libs.plugins.protubuf)
}

android {
    namespace = "com.mrl.pixiv.data"

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
    implementation(platform(compose.bom))
    implementation(compose.runtime.android)


    
}