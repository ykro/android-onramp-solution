# Glosario de Android On-Ramp

Este glosario reúne los términos técnicos que aparecen a lo largo de los cinco pasos de Android On-Ramp. Cada definición explica cómo se usa en el día a día y enlaza a su documentación oficial.

## JDK 21

Kit de desarrollo de Java versión 21 (por ejemplo, [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21)). Gradle y las herramientas modernas de Android y Kotlin corren sobre esta versión de Java, por eso el Paso 1 comprueba que esté instalada antes de empezar.

## `ANDROID_HOME`

Variable de entorno que señala la carpeta donde vive tu Android SDK (plataformas y herramientas de línea de comandos), descrita en la [documentación oficial de variables de Android](https://developer.android.com/tools/variables#envar). Cuando Gradle construye un proyecto Android en `android-course`, lee esta ruta para encontrar la plataforma del SDK.

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

Función de [estado en Jetpack Compose](https://developer.android.com/develop/ui/compose/state#state-in-composables) que guarda un valor en memoria entre redibujos de una misma pantalla. Sin `remember`, cualquier variable local (`var count = 0`) vuelve a su valor inicial cada vez que Compose redibuja la función. En el Paso 3 practicas esta misma idea con `UiMemory.remember(...)`.

## `kotlinx.serialization`

Librería oficial de Kotlin para convertir entre objetos de Kotlin y JSON. Al configurarla con [`Json { ignoreUnknownKeys = true }`](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#ignoring-unknown-keys), tu cliente tolera campos adicionales que el servidor agregue en el futuro sin lanzar `SerializationException`.

## `MockWebServer`

Servidor HTTP local incluido en [OkHttp](https://github.com/square/okhttp/tree/master/mockwebserver) para pruebas unitarias. Permite encolar respuestas controladas (`200`, `500`, cuerpos JSON inválidos) en `localhost` y verificar tu cliente HTTP en milisegundos sin depender de internet ni de un servidor externo.

## SQLite `ON CONFLICT DO UPDATE`

Cláusula estándar de actualización condicional en SQLite, documentada en [SQLite UPSERT](https://www.sqlite.org/lang_upsert.html). Cuando insertas un registro cuya llave primaria ya existe, actualiza solo las columnas que elijas (`title = excluded.title, author = excluded.author`) y conserva intactas las columnas locales (`favorite`, `local_note`). A diferencia de `INSERT OR REPLACE`, no borra ni recrea la fila.

## Room

Librería oficial de persistencia sobre SQLite en Android, documentada en [Save data in a local database using Room](https://developer.android.com/training/data-storage/room). Traduce las mismas ideas del Paso 5 a anotaciones de Kotlin: `@Entity` describe el `CREATE TABLE`, `@PrimaryKey` define la llave primaria y `@Dao` con `@Query` ejecuta tus consultas `SELECT` y actualizaciones parciales.
