plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

tasks.register("doctor") {
    group = "verification"
    description = "Verifica JDK 21 y ANDROID_HOME antes de empezar (alias de paso1)."
    dependsOn(":m0-doctor:doctor")
}

tasks.register("paso1") {
    group = "verification"
    description = "Paso 1: verifica JDK 21 o superior y ANDROID_HOME."
    dependsOn(":m0-doctor:doctor")
}

tasks.register("paso2") {
    group = "verification"
    description = "Paso 2: ejecuta las 4 pruebas de Kotlin, bloques DSL y StateFlow."
    dependsOn(":m1-kotlin-dsl-estado:test")
}

tasks.register("paso3") {
    group = "verification"
    description = "Paso 3: ejecuta las 3 pruebas de corrutinas, cancelación y estado en UI."
    dependsOn(":m2-corrutinas-y-ui:test")
}

tasks.register("paso4") {
    group = "verification"
    description = "Paso 4: ejecuta las 2 pruebas de HTTP y JSON tolerante con MockWebServer."
    dependsOn(":m3-http-y-sql:testPaso4")
}

tasks.register("paso5") {
    group = "verification"
    description = "Paso 5: ejecuta las 2 pruebas de SQLite y ON CONFLICT DO UPDATE."
    dependsOn(":m3-http-y-sql:testPaso5")
}

tasks.register("ready") {
    group = "verification"
    description = "Ejecuta los 5 pasos completos (doctor y las 11 pruebas)."
    dependsOn(
        "paso1",
        "paso2",
        "paso3",
        "paso4",
        "paso5",
    )
    doLast {
        println()
        println("[OK] Todo el onramp pasó al 100 %. Ya puedes arrancar android-course.")
    }
}
