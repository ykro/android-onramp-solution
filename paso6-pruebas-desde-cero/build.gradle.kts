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

// El estudiante escribe sus tests en src/test/kotlin. El verificador del Paso 6 los corre
// primero contra la función correcta y después contra src/versionA y src/versionB, que
// definen la misma función con el mismo nombre y un bug distinto cada una.
val versiones = listOf("versionA", "versionB")

var testsQuePasaron = 0L
val resultados = linkedMapOf<String, Long>()
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
        val archivo = file("src/test/kotlin/onramp/paso6/ReadingStreakStudentTest.kt")
        val debiles = archivo.readText().split("@Test").drop(1).mapNotNull { bloque ->
            val nombre = Regex("fun\\s+(`[^`]+`|\\w+)").find(bloque)?.groupValues?.get(1) ?: return@mapNotNull null
            val cuerpo = bloque.substringAfter(nombre)
            if ("computeReadingStreak(" in cuerpo && Regex("\\bassert\\w*\\(").containsMatchIn(cuerpo)) null else nombre
        }
        if (debiles.isNotEmpty()) {
            throw GradleException(
                "CA-6.1 Cada test debe llamar a computeReadingStreak y comprobar el resultado con un assert. " +
                    "Estos no lo hacen: ${debiles.joinToString(", ")}.",
            )
        }
        if (testsQuePasaron < 3) {
            throw GradleException(
                "CA-6.1 Hay $testsQuePasaron tests que pasan; se esperan al menos 3 en ReadingStreakStudentTest.kt.",
            )
        }
        println("[OK] CA-6.1 Tus $testsQuePasaron tests pasan contra la función correcta.")
    }
}

val verificaciones = versiones.mapIndexed { indice, nombre ->
    val principal = sourceSets.create(nombre)
    val pruebas = sourceSets.create("test${nombre.replaceFirstChar { it.uppercase() }}") {
        kotlin.srcDir("src/test/kotlin")
        compileClasspath += principal.output
        runtimeClasspath += principal.output
    }
    configurations[principal.implementationConfigurationName].extendsFrom(configurations.implementation.get())
    configurations[pruebas.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
    configurations[pruebas.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())
    dependencies.add(pruebas.implementationConfigurationName, libs.kotlinx.coroutines.core)

    val criterio = "CA-6.${indice + 2}"
    tasks.register<Test>("testContra${nombre.replaceFirstChar { it.uppercase() }}") {
        group = "verification"
        description = "Paso 6: corre tus tests contra $nombre."
        testClassesDirs = pruebas.output.classesDirs
        classpath = pruebas.runtimeClasspath
        ignoreFailures = true
        mustRunAfter(tasks.test)
        if (indice > 0) mustRunAfter("testContra${versiones[indice - 1].replaceFirstChar { it.uppercase() }}")
        testLogging {
            events("failed")
            exceptionFormat = TestExceptionFormat.SHORT
        }
        doFirst {
            println()
            println("== $criterio Ahora corren tus tests contra $nombre, que tiene un bug.")
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
            resultados[criterio] = fallidos
            if (fallidos == 0L) {
                println("[X] $criterio Todos tus tests pasan con $nombre. Agrega un test que la atrape.")
            } else {
                println("[OK] $criterio $fallidos test(s) se ponen en rojo con $nombre.")
            }
        }
    }
}

tasks.register("verificarPaso6") {
    group = "verification"
    description = "Paso 6: tus tests pasan con la función correcta y fallan con versionA y versionB."
    dependsOn(tasks.test)
    dependsOn(verificaciones)
    doLast {
        val faltan = resultados.filterValues { it == 0L }.keys
        if (faltan.isNotEmpty()) {
            throw GradleException("Paso 6 incompleto. Ningún test atrapa ${faltan.joinToString(" ni ")}.")
        }
        println()
        println("[OK] Paso 6 completo. Tus tests detectan versionA y versionB.")
    }
}
