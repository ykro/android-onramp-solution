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
    ":paso0-entorno-y-kotlin",
    ":paso1-kotlin-y-stateflow",
    ":paso2-corrutinas-y-ui",
    ":paso3-mvvm-y-di",
    ":paso4-http-json",
    ":paso5-sql-y-upsert",
    ":paso6-pruebas-desde-cero",
)
