import org.gradle.api.GradleException

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    testImplementation(libs.junit)
}

application {
    mainClass.set("onramp.paso0.PrimerosPasosKt")
}

tasks.test {
    testLogging {
        events("passed", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStackTraces = false
    }
}

tasks.register("doctor") {
    group = "verification"
    description = "Verifica JDK 21 o superior y el Android SDK."

    doLast {
        val issues = mutableListOf<String>()
        val javaMajor = Runtime.version().feature()
        val javaVersion = System.getProperty("java.version") ?: javaMajor.toString()

        println("=== Android On-Ramp Doctor ===")
        println()

        if (javaMajor >= 21) {
            println("[OK] JDK detectado: $javaVersion (mínimo requerido: 21)")
        } else {
            println("[FALTA] JDK detectado: $javaVersion (se requiere JDK 21 o superior)")
            issues += """
                Instala JDK 21 y configúralo en JAVA_HOME:
                  - macOS (Homebrew): brew install openjdk@21
                  - Windows / Linux: descarga Eclipse Temurin 21 en https://adoptium.net/
                  - También puedes usar el JDK 21 incluido en Android Studio (Settings -> Build Tools -> Gradle).
            """.trimIndent()
        }

        val envAndroidHome = System.getenv("ANDROID_HOME")
            ?.takeIf { it.isNotBlank() }
            ?: System.getenv("ANDROID_SDK_ROOT")?.takeIf { it.isNotBlank() }

        val userHome = System.getProperty("user.home")
        val fallbackCandidates = listOf(
            file("$userHome/Library/Android/sdk"),
            file("$userHome/Android/Sdk"),
            file("$userHome/AppData/Local/Android/Sdk"),
        )

        val sdkDir = when {
            envAndroidHome != null -> file(envAndroidHome)
            else -> fallbackCandidates.firstOrNull { it.isDirectory }
        }

        val platformsDir = sdkDir?.resolve("platforms")
        val installedPlatforms = platformsDir
            ?.listFiles()
            ?.filter { it.isDirectory && it.name.startsWith("android-") }
            ?.map { it.name }
            ?.sorted()
            .orEmpty()

        if (sdkDir != null && sdkDir.isDirectory && installedPlatforms.isNotEmpty()) {
            val source = if (envAndroidHome != null) "ANDROID_HOME" else "ruta por defecto"
            println("[OK] Android SDK ($source): ${sdkDir.absolutePath}")
            println("[OK] Plataformas instaladas: ${installedPlatforms.joinToString(", ")}")
            if (envAndroidHome == null) {
                println("[AVISO] Conviene exportar ANDROID_HOME en tu terminal:")
                println("        export ANDROID_HOME=\"${sdkDir.absolutePath}\"")
            }
        } else {
            println("[FALTA] No se encontró un Android SDK válido con plataformas instaladas.")
            issues += """
                Configura el SDK de Android antes de pasar a android-course:
                  1. Abre Android Studio -> Settings -> Languages & Frameworks -> Android SDK.
                  2. Instala al menos una plataforma reciente (por ejemplo Android 35 o 36).
                  3. Exporta ANDROID_HOME en tu terminal:
                     - macOS: export ANDROID_HOME="${'$'}HOME/Library/Android/sdk"
                     - Linux: export ANDROID_HOME="${'$'}HOME/Android/Sdk"
                     - Windows (PowerShell): ${'$'}env:ANDROID_HOME = "${'$'}env:LOCALAPPDATA\\Android\\Sdk"
            """.trimIndent()
        }

        println()
        if (issues.isNotEmpty()) {
            issues.forEachIndexed { index, tip ->
                println("Paso ${index + 1}:")
                println(tip)
                println()
            }
            throw GradleException("El entorno necesita ajustes. Sigue los pasos anteriores y vuelve a correr ./gradlew doctor --no-parallel")
        }

        println("[OK] Entorno listo para trabajar.")
    }
}
