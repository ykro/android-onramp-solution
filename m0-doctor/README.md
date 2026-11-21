# Módulo 0: Doctor del entorno (10 minutos)

Antes de escribir código, confirma que tu computadora tiene las dos piezas que necesitarás en `curso3-android-compose`:

1. **JDK 21 o superior:** ejecuta Gradle y las pruebas de Kotlin.
2. **Android SDK (`ANDROID_HOME`):** contiene las plataformas y herramientas de Android.

## Qué debes hacer

Corre el comando de diagnóstico desde la raíz del repositorio:

```bash
./gradlew doctor --no-parallel
```

## Si algo aparece como `[FALTA]`

- **JDK 21:** instala Eclipse Temurin 21 desde https://adoptium.net/ o usa `brew install openjdk@21` en macOS. Luego verifica con `java -version`.
- **Android SDK:** abre Android Studio, entra a **Settings -> Languages & Frameworks -> Android SDK** e instala una plataforma reciente. Luego exporta la variable en tu terminal:
  - macOS: `export ANDROID_HOME="$HOME/Library/Android/sdk"`
  - Linux: `export ANDROID_HOME="$HOME/Android/Sdk"`
  - Windows (PowerShell): `$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"`

Cuando `doctor` termine con `[OK] Entorno listo para trabajar.`, pasa a `m1-kotlin-dsl-estado`.
