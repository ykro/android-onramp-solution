plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
}

tasks.register("doctor") {
    group = "verification"
    description = "Verifica JDK 21 o superior y el Android SDK antes de empezar."
    dependsOn(":m0-doctor:doctor")
}

tasks.register("paso0") {
    group = "verification"
    description = "Paso 0: revisa tu entorno y corre las 3 pruebas de Kotlin básico."
    dependsOn("doctor", ":m0-doctor:test")
}

tasks.register("paso1") {
    group = "verification"
    description = "Paso 1: corre las 4 pruebas de lambdas con receptor, sealed interface y StateFlow."
    dependsOn(":m1-kotlin-dsl-estado:test")
}

tasks.register("paso2") {
    group = "verification"
    description = "Paso 2: corre las 4 pruebas de corrutinas, cancelación, estado en UI y Compose."
    dependsOn(":m2-corrutinas-y-ui:test")
}

tasks.register("paso3") {
    group = "verification"
    description = "Paso 3: corre las 3 pruebas de HTTP, JSON tolerante y errores de red con MockWebServer."
    dependsOn(":m3-http-y-sql:testPaso3")
}

tasks.register("paso4") {
    group = "verification"
    description = "Paso 4: corre las 2 pruebas de SQLite y ON CONFLICT DO UPDATE."
    dependsOn(":m3-http-y-sql:testPaso4")
}

tasks.register("paso5") {
    group = "verification"
    description = "Paso 5: tus tests pasan con la función correcta y fallan con cada versión con un bug."
    dependsOn(":m4-pruebas-desde-cero:verificarPaso5")
}

tasks.register("ready") {
    group = "verification"
    description = "Corre los 6 pasos, del 0 al 5."
    dependsOn("paso0", "paso1", "paso2", "paso3", "paso4", "paso5")
    doLast {
        println()
        println("[OK] Los 6 pasos del on-ramp están en verde. Ya puedes arrancar android-course.")
    }
}
