pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    // Declare repositories for the buildscript dependencies
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

rootProject.name = "FitBite"
include(":app")
