pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "android-onramp"

include(
    ":m0-doctor",
    ":m1-kotlin-dsl-estado",
    ":m2-corrutinas-y-ui",
    ":m3-http-y-sql",
    ":m4-pruebas-desde-cero",
)
