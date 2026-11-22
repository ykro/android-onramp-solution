# Android On-Ramp — Soluciones de referencia

Este repositorio contiene las soluciones completas y verificadas (`14/14` pruebas en verde) de [ykro/android-onramp](https://github.com/ykro/android-onramp). Úsalo para comparar tu trabajo y entender qué se cambió en cada uno de los cinco pasos independientes antes de iniciar [ykro/android-course](https://github.com/ykro/android-course).

![Mapa de Android On-Ramp](docs/diagrams/overview.svg)

## Inicio rápido

1. Exporta `ANDROID_HOME` si aún no está definido en tu terminal (por ejemplo en macOS: `export ANDROID_HOME="$HOME/Library/Android/sdk"`).
2. Verifica tu entorno con `doctor` y ejecuta la verificación completa (`paso1` a `paso5`, 14 pruebas):

```bash
./gradlew doctor --no-parallel
./gradlew ready --no-parallel
```

3. También puedes verificar cada paso por separado:

```bash
./gradlew paso1 --no-parallel
./gradlew paso2 --no-parallel
./gradlew paso3 --no-parallel
./gradlew paso4 --no-parallel
./gradlew paso5 --no-parallel
```

## Los 5 pasos (independientes)

| Paso | Tiempo | Enfoque | Verificación | Guía |
| --- | --- | --- | --- | --- |
| Paso 1 | 15 min | Trailing lambdas con receptor, `sealed interface` y `StateFlow` inmutable | `./gradlew paso1 --no-parallel` | [steps/01-kotlin-y-stateflow/README.md](steps/01-kotlin-y-stateflow/README.md) |
| Paso 2 | 15 min | `suspend`/`delay` con reloj virtual, `CancellationException` y `remember` | `./gradlew paso2 --no-parallel` | [steps/02-corrutinas-y-ui/README.md](steps/02-corrutinas-y-ui/README.md) |
| Paso 3 | 10 min | `kotlinx.serialization`, `MockWebServer` y errores HTTP o de parseo | `./gradlew paso3 --no-parallel` | [steps/03-http-json/README.md](steps/03-http-json/README.md) |
| Paso 4 | 10 min | SQLite (`CREATE TABLE`, `SELECT/ORDER BY`, `ON CONFLICT DO UPDATE`) y Room 3 | `./gradlew paso4 --no-parallel` | [steps/04-sql-y-upsert/README.md](steps/04-sql-y-upsert/README.md) |
| Paso 5 | 10 min | Pruebas automatizadas desde cero con JUnit y Turbine (Arrange -> Act -> Assert) | `./gradlew paso5 --no-parallel` | [steps/05-pruebas-desde-cero/README.md](steps/05-pruebas-desde-cero/README.md) |

## Dónde ver la solución de cada paso

| Paso | Criterio | Qué se cambió y por qué | Archivo de solución |
| --- | --- | --- | --- |
| Paso 1 | `CA-1.1` | `miniColumn` crea `ColumnScope()`, ejecuta `scope.content()` y devuelve `scope.buildLines()` para recolectar textos en orden. | [m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/DslBuilders.kt](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/DslBuilders.kt) |
| Paso 1 | `CA-1.2` | `miniModule` crea `ModuleScope()`, ejecuta `scope.definitions()` y devuelve el contenedor con fábricas `single` cacheadas. | [m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/DslBuilders.kt](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/DslBuilders.kt) |
| Paso 1 | `CA-1.3` | `renderOrderBanner` usa un `when` exhaustivo sin `else` sobre `OrderBannerState`, distinguiendo singular y plural en `Preparing`. | [m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/OrderState.kt](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/OrderState.kt) |
| Paso 1 | `CA-1.4` | `OrderBoardState` cambia `MutableList` por `List` de solo lectura y `OrderBoard` usa `_state.update { it.copy(...) }` para que `StateFlow` detecte cada cambio y emita a la pantalla. | [m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/OrderState.kt](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/OrderState.kt) |
| Paso 2 | `CA-2.1` | `countdownTicks` usa `flow { ... }` y `delay(stepMs)` en lugar de `Thread.sleep`, de modo que `runTest` adelanta el reloj virtual al instante. | [m2-corrutinas-y-ui/src/main/kotlin/onramp/m2/CoroutinesAndUi.kt](m2-corrutinas-y-ui/src/main/kotlin/onramp/m2/CoroutinesAndUi.kt) |
| Paso 2 | `CA-2.2` | `runSafely` captura y relanza `CancellationException` antes de capturar `Exception`, evitando que una pantalla cerrada deje corrutinas activas. | [m2-corrutinas-y-ui/src/main/kotlin/onramp/m2/CoroutinesAndUi.kt](m2-corrutinas-y-ui/src/main/kotlin/onramp/m2/CoroutinesAndUi.kt) |
| Paso 2 | `CA-2.3` | `renderStudyCounter` sustituye `var count = 0` por `memory.remember("sessions") { 0 }` para conservar el contador entre redibujos sucesivos. | [m2-corrutinas-y-ui/src/main/kotlin/onramp/m2/CoroutinesAndUi.kt](m2-corrutinas-y-ui/src/main/kotlin/onramp/m2/CoroutinesAndUi.kt) |
| Paso 3 | `CA-3.1` | `BookApiClient` configura `Json { ignoreUnknownKeys = true }` para decodificar `List<BookDto>` sin fallar cuando el servidor envía campos extra. | [m3-http-y-sql/src/main/kotlin/onramp/m3/BookApiClient.kt](m3-http-y-sql/src/main/kotlin/onramp/m3/BookApiClient.kt) |
| Paso 3 | `CA-3.2` | `fetchBooks()` revisa `!response.isSuccessful` para devolver `ApiResult.HttpError(response.code)` y atrapa `SerializationException` para devolver `ApiResult.ParseError`. | [m3-http-y-sql/src/main/kotlin/onramp/m3/BookApiClient.kt](m3-http-y-sql/src/main/kotlin/onramp/m3/BookApiClient.kt) |
| Paso 4 | `CA-4.1` | `BookDatabase.createSchema()` crea la tabla `books` con `PRIMARY KEY` y valores por defecto, y `findFavoritesOrderedByTitle()` filtra `favorite = 1` ordenado por `title ASC`. | [m3-http-y-sql/src/main/kotlin/onramp/m3/BookDatabase.kt](m3-http-y-sql/src/main/kotlin/onramp/m3/BookDatabase.kt) |
| Paso 4 | `CA-4.2` | `syncFromApi()` reemplaza `INSERT OR REPLACE` por `ON CONFLICT(id) DO UPDATE SET title = excluded.title, author = excluded.author` para actualizar sin borrar `favorite` ni `local_note`. | [m3-http-y-sql/src/main/kotlin/onramp/m3/BookDatabase.kt](m3-http-y-sql/src/main/kotlin/onramp/m3/BookDatabase.kt) |
| Paso 5 | `CA-5.1` | Prueba de caso normal en Arrange -> Act -> Assert que valida la racha consecutiva más larga sobre `computeReadingStreak`. | [m4-pruebas-desde-cero/src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt](m4-pruebas-desde-cero/src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt) |
| Paso 5 | `CA-5.2` | Prueba de casos borde que verifica el resultado `0` ante lista vacía, días en cero y valores negativos. | [m4-pruebas-desde-cero/src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt](m4-pruebas-desde-cero/src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt) |
| Paso 5 | `CA-5.3` | Prueba que pasa en verde contra `computeReadingStreak` y demuestra con `assertThrows(AssertionError::class.java)` que la misma aserción falla en rojo contra `computeReadingStreakBuggy`. | [m4-pruebas-desde-cero/src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt](m4-pruebas-desde-cero/src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt) |

## Guías de lectura y uso de IA

- **Cómo leer un ADR:** revisa el ejemplo de 15 líneas para Android en [docs/adr-ejemplo.md](docs/adr-ejemplo.md).
- **Qué es el CI (Integración Continua):** entiende qué corre GitHub Actions tras cada `git push` en [docs/ci.md](docs/ci.md).
- **Firebase en la Unidad 3:** repasa Firebase Auth, Firestore con `callbackFlow` y `firestore.rules` en [docs/firebase.md](docs/firebase.md).
- **Glosario técnico:** si quieres repasar algún término, consulta [docs/glosario.md](docs/glosario.md).
- **Uso de IA:** este repositorio conserva [AGENTS.md](AGENTS.md) en **modo tutor socrático**: puedes pedirle a cualquier asistente de IA que te explique una solución o te proponga un ejemplo alternativo.
- **Otros enlaces:** índice general en [ykro/courses-onramp](https://github.com/ykro/courses-onramp), repositorio inicial en [ykro/android-onramp](https://github.com/ykro/android-onramp) y curso principal en [ykro/android-course](https://github.com/ykro/android-course).
