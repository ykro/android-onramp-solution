plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register("doctor") {
    group = "verification"
    description = "Inicio rápido: verifica JDK 21 o superior y ANDROID_HOME antes de empezar."
    dependsOn(":m0-doctor:doctor")
}

tasks.register("paso1") {
    group = "verification"
    description = "Paso 1: ejecuta las 4 pruebas de Kotlin, bloques DSL y StateFlow."
    dependsOn(":m1-kotlin-dsl-estado:test")
}

tasks.register("paso2") {
    group = "verification"
    description = "Paso 2: ejecuta las 3 pruebas de corrutinas, cancelación y estado en UI."
    dependsOn(":m2-corrutinas-y-ui:test")
}

tasks.register("paso3") {
    group = "verification"
    description = "Paso 3: ejecuta las 2 pruebas de HTTP y JSON tolerante con MockWebServer."
    dependsOn(":m3-http-y-sql:testPaso3")
}

tasks.register("paso4") {
    group = "verification"
    description = "Paso 4: ejecuta las 2 pruebas de SQLite y ON CONFLICT DO UPDATE."
    dependsOn(":m3-http-y-sql:testPaso4")
}

tasks.register("paso5") {
    group = "verification"
    description = "Paso 5: ejecuta las 3 pruebas automatizadas desde cero con JUnit y Turbine."
    dependsOn(":m4-pruebas-desde-cero:test")
}

tasks.register("ready") {
    group = "verification"
    description = "Ejecuta doctor y los 5 pasos completos (14 pruebas)."
    dependsOn(
        "doctor",
        "paso1",
        "paso2",
        "paso3",
        "paso4",
        "paso5",
    )
    doLast {
        println()
        println("[OK] Todo el on-ramp pasó al 100 % (14/14 pruebas). Ya puedes arrancar android-course.")
    }
}
