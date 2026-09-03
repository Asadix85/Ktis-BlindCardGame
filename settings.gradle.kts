pluginManagement {
    repositories {
        maven("https://en-mirror.ir")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        maven("https://en-mirror.ir")
        google()
        mavenCentral()
    }
}

rootProject.name = "Ktis"

include(":app")