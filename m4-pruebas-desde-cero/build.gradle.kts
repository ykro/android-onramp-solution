import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}

// El estudiante escribe sus tests en src/test/kotlin. El verificador del Paso 5 los corre
// primero contra la función correcta y después contra cada versión con un bug de
// src/conBug<Nombre>/kotlin, que define la misma función con el mismo nombre.
val versionesConBug = linkedMapOf(
    "NoReiniciaEnCero" to "no reinicia la racha cuando un día tiene 0 minutos",
    "CuentaNegativos" to "cuenta como lectura los días con minutos negativos",
)

var testsQuePasaron = 0L
val resultadosConBug = linkedMapOf<String, Long>()
tasks.test {
    testLogging {
        events("passed", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) testsQuePasaron = result.successfulTestCount
        }
    })
    doLast {
        // Cada @Test debe llamar a computeReadingStreak y comprobar el resultado con un assert.
        val archivo = file("src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt")
        val debiles = archivo.readText().split("@Test").drop(1).mapNotNull { bloque ->
            val nombre = Regex("fun\\s+(`[^`]+`|\\w+)").find(bloque)?.groupValues?.get(1) ?: return@mapNotNull null
            val cuerpo = bloque.substringAfter(nombre)
            if ("computeReadingStreak(" in cuerpo && Regex("\\bassert\\w*\\(").containsMatchIn(cuerpo)) null else nombre
        }
        if (debiles.isNotEmpty()) {
            throw GradleException(
                "CA-5.1 Cada test debe llamar a computeReadingStreak y comprobar el resultado con un assert. " +
                    "Estos no lo hacen: ${debiles.joinToString(", ")}.",
            )
        }
        if (testsQuePasaron < 3) {
            throw GradleException(
                "CA-5.1 Hay $testsQuePasaron tests que pasan; se esperan al menos 3 en ReadingStreakStudentTest.kt.",
            )
        }
        println("[OK] CA-5.1 Tus $testsQuePasaron tests pasan contra la función correcta.")
    }
}

val verificaciones = versionesConBug.entries.mapIndexed { indice, (nombre, descripcion) ->
    val principal = sourceSets.create("conBug$nombre")
    val pruebas = sourceSets.create("testConBug$nombre") {
        kotlin.srcDir("src/test/kotlin")
        compileClasspath += principal.output
        runtimeClasspath += principal.output
    }
    configurations[principal.implementationConfigurationName].extendsFrom(configurations.implementation.get())
    configurations[pruebas.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
    configurations[pruebas.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())
    dependencies.add(pruebas.implementationConfigurationName, libs.kotlinx.coroutines.core)

    val criterio = "CA-5.${indice + 2}"
    tasks.register<Test>("testContraVersionConBug$nombre") {
        group = "verification"
        description = "Paso 5: corre tus tests contra una versión que $descripcion."
        testClassesDirs = pruebas.output.classesDirs
        classpath = pruebas.runtimeClasspath
        ignoreFailures = true
        mustRunAfter(tasks.test)
        if (indice > 0) mustRunAfter("testContraVersionConBug${versionesConBug.keys.elementAt(indice - 1)}")
        testLogging {
            events("failed")
            exceptionFormat = TestExceptionFormat.SHORT
        }
        doFirst {
            println()
            println("== $criterio Ahora corren tus tests contra una versión que $descripcion.")
            println("   Aquí sí quieres ver tests en rojo (FAILED).")
        }
        outputs.upToDateWhen { false }
        outputs.cacheIf { false }
        var fallidos = 0L
        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent == null) fallidos = result.failedTestCount
            }
        })
        doLast {
            resultadosConBug[criterio] = fallidos
            if (fallidos == 0L) {
                println("[X] $criterio Todos tus tests pasan con una versión que $descripcion. Agrega un test que la atrape.")
            } else {
                println("[OK] $criterio $fallidos test(s) se ponen en rojo con la versión que $descripcion.")
            }
        }
    }
}

tasks.register("verificarPaso5") {
    group = "verification"
    description = "Paso 5: tus tests pasan con la función correcta y fallan con cada versión con un bug."
    dependsOn(tasks.test)
    dependsOn(verificaciones)
    doLast {
        val faltan = resultadosConBug.filterValues { it == 0L }.keys
        if (faltan.isNotEmpty()) {
            throw GradleException("Paso 5 incompleto. Ningún test atrapa ${faltan.joinToString(" ni ")}.")
        }
        println()
        println("[OK] Paso 5 completo. Tus tests detectan las dos versiones con un bug.")
    }
}
