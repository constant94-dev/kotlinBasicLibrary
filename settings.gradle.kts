pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "KotlinBasicLibrary"
include(":app_stopwatch")
include(":app_obesitycalculator")
include(":app_mywebbrowser")
include(":app_electronicgallery")
include(":app_horizontalinstrument")
include(":app_xylophone")
include(":app_gpsmap")
include(":app_todolist")
