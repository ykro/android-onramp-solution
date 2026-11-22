plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
}

tasks.register("doctor") {
    group = "verification"
    description = "Verifica JDK 21 o superior y el Android SDK antes de empezar."
    dependsOn(":paso0-entorno-y-kotlin:doctor")
}

tasks.register("paso0") {
    group = "verification"
    description = "Paso 0: revisa tu entorno y corre las 3 pruebas de Kotlin básico."
    dependsOn("doctor", ":paso0-entorno-y-kotlin:test")
}

tasks.register("paso1") {
    group = "verification"
    description = "Paso 1: corre las 3 pruebas de lambdas con receptor, sealed interface y StateFlow."
    dependsOn(":paso1-kotlin-y-stateflow:test")
}

tasks.register("paso2") {
    group = "verification"
    description = "Paso 2: corre las 3 pruebas de corrutinas, cancelación y Compose."
    dependsOn(":paso2-corrutinas-y-ui:test")
}

tasks.register("paso3") {
    group = "verification"
    description = "Paso 3: corre las 6 pruebas de UI declarativa, ViewModel (MVVM) e inyección de dependencias con Koin."
    dependsOn(":paso3-mvvm-y-di:test")
}

tasks.register("paso4") {
    group = "verification"
    description = "Paso 4: corre las 3 pruebas de HTTP, JSON tolerante y errores de red con MockWebServer."
    dependsOn(":paso4-http-json:test")
}

tasks.register("paso5") {
    group = "verification"
    description = "Paso 5: corre las 2 pruebas de SQLite y ON CONFLICT DO UPDATE."
    dependsOn(":paso5-sql-y-upsert:test")
}

tasks.register("paso6") {
    group = "verification"
    description = "Paso 6: tus tests pasan con la función correcta y fallan con versionA y versionB."
    dependsOn(":paso6-pruebas-desde-cero:verificarPaso6")
}

tasks.register("ready") {
    group = "verification"
    description = "Corre los 7 pasos, del 0 al 6."
    dependsOn("paso0", "paso1", "paso2", "paso3", "paso4", "paso5", "paso6")
    doLast {
        println()
        println("[OK] Los 7 pasos del on-ramp están en verde. Ya puedes arrancar android-course.")
    }
}
