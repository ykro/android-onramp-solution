plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.sqlite.jdbc)
    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
}

tasks.test {
    testLogging {
        events("passed", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
    }
}

tasks.register<Test>("testPaso3") {
    group = "verification"
    description = "Ejecuta las pruebas del Paso 3 (HTTP y JSON tolerante con MockWebServer)."
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    filter {
        includeTestsMatching("onramp.m3.BookApiClientTest.*")
    }
    testLogging {
        events("passed", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
    }
}

tasks.register<Test>("testPaso4") {
    group = "verification"
    description = "Ejecuta las pruebas del Paso 4 (SQLite y ON CONFLICT DO UPDATE)."
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    filter {
        includeTestsMatching("onramp.m3.BookDatabaseTest.*")
    }
    testLogging {
        events("passed", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
    }
}
