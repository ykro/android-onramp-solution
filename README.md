# Android On-Ramp — Solución explicada paso a paso

Este repositorio contiene la solución completa y verificada (11/11 pruebas en verde) de [ykro/android-onramp](https://github.com/ykro/android-onramp). Usa este documento para comparar tu código y entender exactamente qué estaba roto en el repositorio inicial, qué cambio se hizo en cada archivo y por qué pasa cada prueba.

- Índice general de todos los on-ramps: [ykro/courses-onramp](https://github.com/ykro/courses-onramp)
- Repositorio inicial (para practicar): [ykro/android-onramp](https://github.com/ykro/android-onramp)
- Curso principal: [ykro/android-course](https://github.com/ykro/android-course)

## Cómo verificar la solución en tu computadora

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk" # Ajusta la ruta según tu sistema operativo
./gradlew ready --no-parallel
```

El comando `ready` corre primero `doctor` (verifica JDK 21 y `ANDROID_HOME`) y después ejecuta las 11 pruebas de los módulos `m1`, `m2` y `m3`.

---

## Módulo 1: `m1-kotlin-dsl-estado` (4 pruebas)

Pruebas del módulo: [`Module1Test.kt`](m1-kotlin-dsl-estado/src/test/kotlin/onramp/m1/Module1Test.kt)

### 1. `DslBuilders.kt` — `miniColumn` y `miniModule`

Archivo resuelto: [`DslBuilders.kt`](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/DslBuilders.kt)

- **Qué estaba roto en el inicial:** `miniColumn` y `miniModule` lanzaban `TODO(...)` sin crear el receptor ni ejecutar la lambda recibida.
- **Cambio exacto realizado:**
  - En `miniColumn`, creamos `val scope = ColumnScope()`, ejecutamos `scope.content()` y devolvemos `scope.buildLines()`.
  - En `miniModule`, creamos `val scope = ModuleScope()`, ejecutamos `scope.definitions()` y devolvemos `scope.buildContainer()`.
- **Por qué pasan las pruebas:**
  - `ejercicio 1 - miniColumn recolecta textos con sintaxis de bloque`: al llamar `scope.content()`, cada llamada a `text(...)` dentro del bloque se ejecuta sobre `scope` y agrega su línea en orden. `buildLines()` devuelve `listOf("Café americano", "Pan dulce", "Total: Q25")`.
  - `ejercicio 2 - miniModule registra dependencias singleton`: al llamar `scope.definitions()`, `single("clock") { ... }` guarda la fábrica en el contenedor. La primera llamada a `container.get("clock")` crea `"Reloj#1"` y la guarda en caché; la segunda devuelve la misma instancia (`assertSame`) sin incrementar `creations`.

### 2. `OrderState.kt` — `renderOrderBanner` y `OrderBoard`

Archivo resuelto: [`OrderState.kt`](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/OrderState.kt)

- **Qué estaba roto en el inicial:**
  - `renderOrderBanner` lanzaba `TODO(...)`.
  - `OrderBoardState` guardaba una `MutableList<String>`. En `OrderBoard.addOrder` y `OrderBoard.removeFirstOrder`, el código mutaba `current.orders` en el mismo lugar y volvía a asignar `_state.value = current`. Como la referencia anterior y la nueva eran el mismo objeto, `StateFlow` determinaba que el estado no había cambiado y no emitía actualizaciones.
- **Cambio exacto realizado:**
  - En `renderOrderBanner`, implementamos un `when (state)` exhaustivo con las cuatro subclases de `OrderBannerState`: `Empty`, `Preparing` (distinguiendo singular `"Preparando 1 pedido"` de plural `"Preparando ${state.count} pedidos"`), `Ready` y `Failed`, sin rama `else`.
  - En `OrderBoardState`, cambiamos `val orders: MutableList<String> = mutableListOf()` por `val orders: List<String> = emptyList()`.
  - Importamos `kotlinx.coroutines.flow.update` y reescribimos `addOrder` con `_state.update { current -> current.copy(orders = current.orders + clean) }` y `removeFirstOrder` con `_state.update { current -> if (current.orders.isEmpty()) current else current.copy(orders = current.orders.drop(1)) }`.
- **Por qué pasan las pruebas:**
  - `ejercicio 3 - renderOrderBanner cubre todos los estados de la sealed interface`: cada rama del `when` produce el texto exacto que verifica la prueba.
  - `ejercicio 4 - StateFlow emite cada vez que se agrega o quita un pedido`: `copy(...)` crea un `OrderBoardState` nuevo con una nueva `List<String>` en cada cambio. Al comparar el estado anterior con el nuevo, `StateFlow` detecta la diferencia y Turbine recibe cada emisión con `awaitItem()`.

---

## Módulo 2: `m2-corrutinas-y-ui` (3 pruebas)

Pruebas del módulo: [`Module2Test.kt`](m2-corrutinas-y-ui/src/test/kotlin/onramp/m2/Module2Test.kt)

### `CoroutinesAndUi.kt` — `countdownTicks`, `runSafely` y `renderStudyCounter`

Archivo resuelto: [`CoroutinesAndUi.kt`](m2-corrutinas-y-ui/src/main/kotlin/onramp/m2/CoroutinesAndUi.kt)

- **Qué estaba roto en el inicial:**
  - `countdownTicks` y `runSafely` lanzaban `TODO(...)`.
  - `renderStudyCounter` declaraba `var count = 0` como variable local dentro de la función. Cada vez que la pantalla se volvía a dibujar (una nueva llamada a `renderStudyCounter(memory)`), `count` regresaba a `0` y los incrementos anteriores se perdían.
- **Cambio exacto realizado:**
  - En `countdownTicks`, devolvemos un bloque `flow { ... }`. Si `from < 0`, salimos con `return@flow`. En caso contrario, iteramos `for (current in from downTo 0)`, llamamos `emit(current)` y ejecutamos `delay(stepMs)` solo si `current > 0`.
  - En `runSafely`, envolvemos `action()` en `try`, capturamos primero `CancellationException` para relanzarla (`throw cancelled`) y luego capturamos `Exception` para devolver `"Error: ${error.message ?: "desconocido"}"`.
  - En `renderStudyCounter`, guardamos el contador con `val sessions = memory.remember("sessions") { 0 }`, leemos `val count = sessions.value` para construir `label` y actualizamos `sessions.value` dentro de `onIncrement` (`sessions.value += 1`) y `onReset` (`sessions.value = 0`).
- **Por qué pasan las pruebas:**
  - `ejercicio 1 - countdownTicks emite toda la cuenta usando reloj virtual sin esperar tiempo real`: `flow` emite `3, 2, 1, 0` y termina. Como usamos `delay(stepMs)` en lugar de `Thread.sleep`, el reloj virtual de `runTest` adelanta los 30 segundos en milisegundos y se cumple `elapsedRealMs < 2_000L`.
  - `ejercicio 2 - runSafely devuelve mensaje ante fallos normales y relanza CancellationException`: ante `IllegalStateException("sin internet")`, el segundo `catch` devuelve `"Error: sin internet"`. Cuando `job.cancel()` interrumpe `delay(60_000L)`, el primer `catch` relanza `CancellationException` y la prueba confirma `caughtCancellation == true`.
  - `ejercicio 3 - renderStudyCounter conserva el contador cuando la pantalla se vuelve a dibujar`: `memory.remember("sessions") { 0 }` devuelve siempre el mismo `MutableSlot<Int>` entre llamadas sucesivas con la misma instancia de `UiMemory`, por lo que las etiquetas progresan de `"0 sesiones completadas"` a `"1 sesión completada"`, `"2 sesiones completadas"` y vuelven a `"0 sesiones completadas"` tras `onReset()`.

---

## Módulo 3: `m3-http-y-sql` (4 pruebas)

Pruebas del módulo: [`Module3Test.kt`](m3-http-y-sql/src/test/kotlin/onramp/m3/Module3Test.kt)

### 1. `BookApiClient.kt` — `fetchBooks`

Archivo resuelto: [`BookApiClient.kt`](m3-http-y-sql/src/main/kotlin/onramp/m3/BookApiClient.kt)

- **Qué estaba roto en el inicial:** `fetchBooks()` lanzaba `TODO(...)` sin enviar la petición HTTP ni decodificar el cuerpo JSON.
- **Cambio exacto realizado:**
  - Definimos `private val json = Json { ignoreUnknownKeys = true }`.
  - Construimos la URL `"${baseUrl.trimEnd('/')}/books"` y ejecutamos la llamada GET con `client.newCall(request).execute().use { response -> ... }`.
  - Verificamos `if (!response.isSuccessful) return ApiResult.HttpError(response.code)`.
  - Leemos `val body = response.body.string()` y lo decodificamos dentro de `try` con `json.decodeFromString<List<BookDto>>(body)`, devolviendo `ApiResult.Success(books)` o `ApiResult.ParseError` si ocurre `SerializationException`.
- **Por qué pasan las pruebas:**
  - `ejercicio 1a - BookApiClient lee JSON e ignora campos extra`: `MockWebServer` responde `200` con los campos extra `pages`, `isbn` y `edition`. Gracias a `ignoreUnknownKeys = true`, la decodificación omite esos campos y devuelve la lista de `BookDto` esperada.
  - `ejercicio 1b - BookApiClient devuelve HttpError ante 500 y ParseError ante JSON invalido`: ante la respuesta `500`, `!response.isSuccessful` devuelve `ApiResult.HttpError(500)`; ante la respuesta `200` con `"id": "no-es-numero"`, `kotlinx.serialization` lanza `SerializationException` y el `catch` devuelve `ApiResult.ParseError`.

### 2. `BookDatabase.kt` — `createSchema`, `syncFromApi` y `findFavoritesOrderedByTitle`

Archivo resuelto: [`BookDatabase.kt`](m3-http-y-sql/src/main/kotlin/onramp/m3/BookDatabase.kt)

- **Qué estaba roto en el inicial:**
  - `createSchema()` y `findFavoritesOrderedByTitle()` lanzaban `TODO(...)`.
  - `syncFromApi()` usaba `INSERT OR REPLACE INTO books (id, title, author) VALUES (?, ?, ?)`. En SQLite, `INSERT OR REPLACE` borra la fila vieja e inserta una nueva, así que reiniciaba `favorite` a `0` y `local_note` a `''` cada vez que llegaba una actualización de la API.
- **Cambio exacto realizado:**
  - En `createSchema()`, ejecutamos `CREATE TABLE IF NOT EXISTS books` con las columnas `id INTEGER PRIMARY KEY`, `title TEXT NOT NULL`, `author TEXT NOT NULL`, `favorite INTEGER NOT NULL DEFAULT 0` y `local_note TEXT NOT NULL DEFAULT ''`.
  - En `syncFromApi()`, cambiamos la sentencia SQL a:

```sql
INSERT INTO books (id, title, author)
VALUES (?, ?, ?)
ON CONFLICT(id) DO UPDATE SET
    title = excluded.title,
    author = excluded.author
```

  - En `findFavoritesOrderedByTitle()`, ejecutamos `SELECT id, title, author, favorite, local_note FROM books WHERE favorite = 1 ORDER BY title ASC` y convertimos cada fila del `ResultSet` en un `StoredBook`.
- **Por qué pasan las pruebas:**
  - `ejercicio 2 y 4 - SQLite crea tabla, filtra favoritos y ordena por titulo`: `createSchema()` crea la tabla con sus restricciones y valores por defecto; `findFavoritesOrderedByTitle()` devuelve solo los registros con `favorite = 1` ordenados alfabéticamente (`"Algorithms"` antes que `"Refactoring"`) junto con sus notas locales.
  - `ejercicio 3 - sincronizar de nuevo actualiza el titulo sin borrar favorito ni nota local`: al sincronizar el libro `id = 1` con el título nuevo `"Kotlin Moderno"`, `ON CONFLICT(id) DO UPDATE` modifica únicamente `title` y `author`, dejando intactos `favorite = 1` y `local_note = "Mi libro favorito"`.

---

## Uso de asistentes de IA

Este repositorio mantiene [`AGENTS.md`](AGENTS.md) en **modo tutor socrático**. Si quieres profundizar en por qué funciona una solución o pedir un ejemplo alternativo, puedes consultarlo con cualquier asistente de IA.
