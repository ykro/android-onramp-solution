# Glosario de Android On-Ramp

Este glosario reúne los términos técnicos que aparecen en los seis pasos y las guías de lectura de Android On-Ramp. Cada definición explica cómo se usa en el día a día y enlaza a su documentación oficial.

## JDK 21

Kit de desarrollo de Java versión 21 (por ejemplo, [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21)). Gradle y las herramientas modernas de Android y Kotlin corren sobre esta versión de Java, por eso `./gradlew doctor --no-parallel` comprueba que esté instalada antes de empezar.

## Gradle y `./gradlew`

Herramienta que compila y prueba proyectos de Kotlin y Android ([Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)). `./gradlew` descarga la versión exacta que pide el proyecto, así todos usan la misma. Cada carpeta `m*` es un módulo y cada `pasoN` es una tarea que corre sus tests.

## `ANDROID_HOME`

Variable de entorno que señala la carpeta donde vive tu Android SDK (plataformas y herramientas de línea de comandos), descrita en la [documentación oficial de variables de Android](https://developer.android.com/tools/variables#envar). Cuando Gradle construye un proyecto Android en `android-course`, lee esta ruta para encontrar la plataforma del SDK.

## Tipos nulos (`String?`)

En Kotlin, `String` nunca es `null` y `String?` puede serlo ([Null safety](https://kotlinlang.org/docs/null-safety.html)). El compilador no te deja usar un `String?` sin revisarlo antes con `if`, `?.`, `?:` o `isNullOrBlank()`. Así los `NullPointerException` aparecen al compilar y no en el teléfono de alguien.

## Trailing lambdas y funciones con receptor

Dos reglas de sintaxis de Kotlin que permiten escribir código declarativo como `Column { Text("Hola") }` o `module { single { ... } }`:

- **[Trailing lambdas](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas):** cuando el último parámetro de una función es una lambda, puedes colocar las llaves `{ ... }` fuera de los paréntesis (y omitir los paréntesis si no hay otros argumentos).
- **[Funciones con receptor](https://kotlinlang.org/docs/lambdas.html#function-literals-with-receiver):** una función con firma `ColumnScope.() -> Unit` recibe un objeto `this` de tipo `ColumnScope`. Dentro del bloque puedes llamar directamente a los métodos de ese objeto, como `text(...)`.

## `sealed interface`

Jerarquía cerrada de tipos en Kotlin documentada en [Sealed classes and interfaces](https://kotlinlang.org/docs/sealed-classes.html). Todas sus implementaciones se declaran en el mismo paquete. Al evaluar una `sealed interface` con [`when`](https://kotlinlang.org/docs/control-flow.html#when-expressions-and-statements), Kotlin te obliga a cubrir todos los casos sin necesitar una rama `else`.

## `data class` y `copy()`

Clase de Kotlin pensada para guardar datos, documentada en [Data classes](https://kotlinlang.org/docs/data-classes.html). Genera automáticamente `equals()`, `hashCode()`, `toString()` y el método [`copy()`](https://kotlinlang.org/docs/data-classes.html#copying), que crea una copia nueva del objeto cambiando solo las propiedades que indiques sin alterar la instancia anterior.

## `StateFlow`

Flujo de estado observable de `kotlinx.coroutines`, explicado en [StateFlow and SharedFlow en Android](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow). Siempre guarda un valor actual y avisa a la pantalla solo cuando el valor nuevo es distinto al anterior según `==`. Por eso necesita objetos inmutables actualizados con `copy()` y `_state.update { ... }`.

## `suspend` y `delay`

Piezas básicas de las [corrutinas en Kotlin](https://kotlinlang.org/docs/coroutines-basics.html). Una función marcada con `suspend` puede pausarse y reanudarse sin bloquear el hilo donde corre. `delay(ms)` pausa la corrutina durante ese tiempo y deja libre el hilo principal para que la pantalla siga respondiendo, a diferencia de `Thread.sleep(ms)` que congela el hilo entero.

## `CancellationException`

Excepción especial que las corrutinas usan para detener tareas activas cuando el usuario sale de una pantalla o se cancela un `Job`, detallada en [Cancellation and timeouts](https://kotlinlang.org/docs/cancellation-and-timeouts.html). Si atrapas `Exception` de forma genérica, siempre debes relanzar `CancellationException` para que la cancelación llegue a su destino.

## `runTest` y Turbine

Herramientas para probar corrutinas y `Flow` de forma rápida y determinista:

- **[`runTest`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/run-test.html):** ejecuta código `suspend` con un reloj virtual que adelanta llamadas a `delay(...)` al instante, sin esperar segundos reales.
- **[Turbine](https://github.com/cashapp/turbine):** librería ligera que ofrece `.test { awaitItem(); awaitComplete() }` para comprobar paso a paso cada valor que emite un `Flow` o un `StateFlow`.

## `remember` en Compose

Función de [estado en Jetpack Compose](https://developer.android.com/develop/ui/compose/state#state-in-composables) que guarda un valor en memoria entre redibujos de una misma pantalla. Sin `remember`, cualquier variable local (`var count = 0`) vuelve a su valor inicial cada vez que Compose redibuja la función. En el Paso 2 practicas esta idea primero con `UiMemory.remember(...)` y después en un composable real.

## `@Composable`

Anotación de [Jetpack Compose](https://developer.android.com/develop/ui/compose/mental-model) que marca una función que describe una parte de la pantalla. Compose la vuelve a ejecutar cada vez que cambia un estado que lee; a eso se le llama recomposición. Los composables se prueban con [`createComposeRule`](https://developer.android.com/develop/ui/compose/testing), que los dibuja sin emulador.

## `kotlinx.serialization`

Librería oficial de Kotlin para convertir entre objetos de Kotlin y JSON. Por defecto falla con `SerializationException` cuando el JSON trae un campo que tu clase no declara. La [guía de JSON](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md) explica cómo configurarla para que tolere los campos que el servidor agregue en el futuro.

## `MockWebServer`

Servidor HTTP local incluido en [OkHttp](https://github.com/square/okhttp/tree/master/mockwebserver) para pruebas unitarias. Permite encolar respuestas controladas (`200`, `500`, cuerpos JSON inválidos) en `localhost` y verificar tu cliente HTTP en milisegundos sin depender de internet ni de un servidor externo.

## SQLite `ON CONFLICT DO UPDATE`

Cláusula estándar de actualización condicional en SQLite, documentada en [SQLite UPSERT](https://www.sqlite.org/lang_upsert.html). Cuando insertas un registro cuya llave primaria ya existe, en lugar de fallar actualiza solo las columnas que elijas, y las demás quedan como estaban. Dentro de esa actualización, `excluded` es el nombre de la fila que intentabas insertar. A diferencia de `INSERT OR REPLACE`, no borra ni recrea la fila.

## Room

Librería oficial de persistencia sobre SQLite en Android, documentada en [Save data in a local database using Room](https://developer.android.com/training/data-storage/room). Traduce las mismas ideas del Paso 4 a anotaciones de Kotlin: `@Entity` describe el `CREATE TABLE`, `@PrimaryKey` define la llave primaria y `@Dao` con `@Query` ejecuta tus consultas `SELECT` y actualizaciones parciales.

## JUnit 4 y patrón Arrange-Act-Assert

[JUnit 4](https://junit.org/junit4/) ejecuta funciones anotadas con `@Test` y reporta si cada aserción (`assertEquals`, `assertThrows`) pasa en verde o falla en rojo. El patrón **Arrange -> Act -> Assert** separa cada prueba en tres bloques claros: preparar datos de entrada, ejecutar la función que quieres probar y comprobar el resultado esperado. Lo practicas desde cero en el Paso 5.

## ADR (Architecture Decision Record)

Documento corto que registra una decisión de diseño importante (estado, contexto, decisión, alternativas descartadas y consecuencias), descrito en [adr.github.io](https://adr.github.io/). En `android-course` lo verás dentro de `ARCHITECTURE_DECISIONS.md`; revisa nuestro ejemplo de 15 líneas en [docs/adr-ejemplo.md](adr-ejemplo.md).

## Firebase (Auth, Firestore y `firestore.rules`)

Plataforma gestionada de Google que usarás en la Unidad 3 de `android-course`: [Firebase Authentication](https://firebase.google.com/docs/auth) verifica la identidad del usuario (`uid`), [Cloud Firestore](https://firebase.google.com/docs/firestore/query-data/listen) envía actualizaciones en tiempo real que envolvemos en [`callbackFlow`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/callback-flow.html) y [`firestore.rules`](https://firebase.google.com/docs/firestore/security/get-started) valida los permisos en el servidor. Consulta la guía visual en [docs/firebase.md](firebase.md).

## CI (Integración Continua)

Práctica de ejecutar automáticamente las pruebas y verificaciones de estilo en una máquina limpia cada vez que subes cambios con `git push` o abres un Pull Request, usando herramientas como [GitHub Actions](https://docs.github.com/es/actions/about-github-actions/understanding-github-actions). Revisa la guía visual en [docs/ci.md](ci.md).

## Versión con un bug

En el Paso 5, una copia de `computeReadingStreak` con un error a propósito, guardada en `m4-pruebas-desde-cero/src/conBug*/`. `./gradlew paso5` corre tus tests contra cada versión con un bug; si todos siguen en verde, tus tests no distinguen la función correcta de una rota.

## Corrutina

Una tarea que puede pausarse y continuar después sin ocupar un hilo mientras espera ([Kotlin: coroutines](https://kotlinlang.org/docs/coroutines-basics.html)). Se lanza con `launch` dentro de un scope y devuelve un `Job`. En Android sirve para esperar la red o la base de datos sin congelar la pantalla.

## `Flow`

Una secuencia de valores que llegan con el tiempo ([Kotlin: Flow](https://kotlinlang.org/docs/flow.html)). Se construye con `flow { emit(valor) }` y se recolecta con `collect`. `StateFlow` es un `Flow` que siempre tiene un valor actual.

## Hilo principal

El único hilo que dibuja la pantalla y atiende los toques en Android ([Android: threading](https://developer.android.com/guide/components/processes-and-threads#Threads)). Si lo bloqueas con una espera, la app se congela; por eso la red y el disco se mueven a otro hilo con `withContext(Dispatchers.IO)`.

## `Job`

El objeto que devuelve `launch` y representa una corrutina en marcha ([Kotlin: Job](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-job/)). Con `job.cancel()` la detienes; cuando se cierra una pantalla, su scope cancela todos sus `Job`.

## Koin

La librería de inyección de dependencias que usa el curso ([insert-koin.io](https://insert-koin.io/docs/quickstart/android/)). Declaras en un `module { single { ... } }` cómo se construye cada pieza y Koin la entrega a quien la pida. El Paso 1 imita esa sintaxis con `miniModule`.
