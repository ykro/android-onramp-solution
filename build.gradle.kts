plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register("doctor") {
    group = "verification"
    description = "Verifica JDK 21 y ANDROID_HOME antes de empezar."
    dependsOn(":m0-doctor:doctor")
}

tasks.register("ready") {
    group = "verification"
    description = "Ejecuta doctor y todas las pruebas de los módulos m1, m2 y m3."
    dependsOn(
        ":m0-doctor:doctor",
        ":m1-kotlin-dsl-estado:test",
        ":m2-corrutinas-y-ui:test",
        ":m3-http-y-sql:test",
    )
    doLast {
        println()
        println("[OK] Todo el onramp pasó al 100 %. Ya puedes arrancar curso3-android-compose.")
    }
}
