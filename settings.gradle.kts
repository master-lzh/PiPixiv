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
        maven(url = "https://repo1.maven.org/")
        maven(url = "https://maven.aliyun.com/repository/central")
    }
}
rootProject.name = "PiPixiv"
include(":app")
include(":common")
include(":util")
include(":feature:login")
include(":repository")
include(":datasource")
include(":data")
include(":api")
include(":network")
include(":domain")
include(":feature:home")
include(":feature:profile")
include(":common-ui")
include(":feature:picture")
include(":common-viewmodel")
