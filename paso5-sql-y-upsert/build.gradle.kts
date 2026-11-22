plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.sqlite.jdbc)
    testImplementation(libs.junit)
}

tasks.test {
    testLogging {
        events("passed", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStackTraces = false
    }
}
