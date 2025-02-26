@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://www.jitpack.io")
    }
}
dependencyResolutionManagement {
    versionCatalogs {
        create("kotlinx") {
            from(files("gradle/kotlinx.versions.toml"))
        }
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
        create("compose") {
            from(files("gradle/compose.versions.toml"))
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://www.jitpack.io")
        maven (url ="https://androidx.dev/storage/compose-compiler/repository/")
    }
}
rootProject.name = "PiPixiv"
include(":app")
include(":common")
include(":util")
include(":repository")
include(":datasource")
include(":data")
include(":network")
include(":domain")
include(":common-ui")

// include modules in feature folder
file("./feature").listFiles()?.filter { it.isDirectory }?.forEach { moduleDir ->
    // 使用目录名称构建模块路径
    val moduleName = ":feature:${moduleDir.name}"
    println("module name: $moduleName")
    include(moduleName)
}
include(":baselineprofile")
