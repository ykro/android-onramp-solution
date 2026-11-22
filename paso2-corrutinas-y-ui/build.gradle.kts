plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(compose.desktop.uiTestJUnit4)
    testImplementation(compose.desktop.currentOs)
}

tasks.test {
    testLogging {
        events("passed", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStackTraces = false
    }
}
