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

rootProject.name = "MiTemperature2Alt"
include(":app")
include(":temperatureSensor")
include(":domain")
include(":core")
include(":coreTest")
include(":persistence")
include(":addThermometer")
include(":addThermometer:addThermometerPresentation")
include(":addThermometer:addThermometerDomain")
