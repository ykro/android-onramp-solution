pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "android-onramp"

include(
    ":m0-doctor",
    ":m1-kotlin-dsl-estado",
    ":m2-corrutinas-y-ui",
    ":m3-http-y-sql",
)
