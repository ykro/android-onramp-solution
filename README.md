# Android On-Ramp (solución de referencia)

Este repositorio contiene la solución verificada de [Android On-Ramp](https://github.com/ykro/android-onramp), la nivelación previa al [curso de Android](https://github.com/ykro/android-course). Aquí `./gradlew ready --no-parallel` termina en verde con los siete pasos, de `paso0` a `paso6`. Cada guía en `steps/` termina con las respuestas de autoevaluación.

![Mapa de Android On-Ramp](docs/diagrams/overview.svg)

## Dónde ver la solución de cada paso

| Paso | Criterios | Qué se cambió y por qué | Archivo |
| --- | --- | --- | --- |
| Paso 0 | `CA-0.1` a `CA-0.4` | `saludar` revisa `isNullOrBlank()` antes de usar el nombre, y después de esa revisión Kotlin sabe que no es `null`. `librosLargos` encadena `filter`, `map` y `sorted`. `nivelDeLectura` usa un `when` con condiciones en orden. | [PrimerosPasos.kt](paso0-entorno-y-kotlin/src/main/kotlin/onramp/paso0/PrimerosPasos.kt) |
| Paso 1 | `CA-1.1` a `CA-1.3` | `miniColumn` crea el scope y ejecuta la lambda con receptor sobre él. `renderOrderBanner` usa un `when` exhaustivo sin `else`, así un estado nuevo no compila hasta manejarlo. `OrderBoardState` guarda una `List` de solo lectura y `OrderBoard` emite un estado nuevo con `update` y `copy`. | [DslBuilders.kt](paso1-kotlin-y-stateflow/src/main/kotlin/onramp/paso1/DslBuilders.kt), [OrderState.kt](paso1-kotlin-y-stateflow/src/main/kotlin/onramp/paso1/OrderState.kt) |
| Paso 2 | `CA-2.1` a `CA-2.3` | `countdownTicks` usa `flow` y `delay`, que el reloj virtual adelanta. `runSafely` relanza `CancellationException` antes del `catch` genérico. `StudyCounterScreen` crea su estado con `remember { mutableStateOf(0) }` y suma el botón «Reiniciar». | [CoroutinesAndUi.kt](paso2-corrutinas-y-ui/src/main/kotlin/onramp/paso2/CoroutinesAndUi.kt), [StudyCounterScreen.kt](paso2-corrutinas-y-ui/src/main/kotlin/onramp/paso2/StudyCounterScreen.kt) |
| Paso 3 | `CA-3.1` a `CA-3.5` | El ViewModel elige con el `BookPicker` que recibe, así el test controla qué libro sale, y lo hace después de un `delay` dentro de `viewModelScope.launch`. `uiState` es un `StateFlow` de solo lectura sobre un `_uiState` privado, y `markRead` avisa al repositorio y produce un estado nuevo con `copy`. `ShelfContent` lee `state` directo, sin copiarlo en `remember`, y solo avisa eventos. `shelfModule` declara el repositorio y el picker con `single` y el ViewModel con `viewModelOf`. | [ShelfViewModel.kt](paso3-mvvm-y-di/src/main/kotlin/onramp/paso3/ShelfViewModel.kt), [ShelfScreen.kt](paso3-mvvm-y-di/src/main/kotlin/onramp/paso3/ShelfScreen.kt), [ShelfModule.kt](paso3-mvvm-y-di/src/main/kotlin/onramp/paso3/ShelfModule.kt) |
| Paso 4 | `CA-4.1` a `CA-4.3` | `Json { ignoreUnknownKeys = true }` tolera campos nuevos. El cliente revisa `isSuccessful` antes de leer el cuerpo y convierte la `SerializationException` en `ParseError` y la `IOException` en `NetworkError`. | [BookApiClient.kt](paso4-http-json/src/main/kotlin/onramp/paso4/BookApiClient.kt) |
| Paso 5 | `CA-5.1`, `CA-5.2` | `CREATE TABLE IF NOT EXISTS` con `id` como llave primaria. `ON CONFLICT(id) DO UPDATE` actualiza solo `title` y `author`, así `favorite` y `local_note` sobreviven a la sincronización. La consulta de favoritos filtra y ordena en SQL. | [BookDatabase.kt](paso5-sql-y-upsert/src/main/kotlin/onramp/paso5/BookDatabase.kt) |
| Paso 6 | `CA-6.1` a `CA-6.3` | Tres tests con Arrange, Act, Assert, un caso normal con un día en `0`, los edge cases y un día negativo en medio de una racha. El primero atrapa `versionA`, que no reinicia la racha en `0`, y el tercero `versionB`, que cuenta los negativos como lectura. | [ReadingStreakStudentTest.kt](paso6-pruebas-desde-cero/src/test/kotlin/onramp/paso6/ReadingStreakStudentTest.kt) |

## Los 7 pasos

| Paso | Tiempo | Qué practicas | Archivo | Guía |
| --- | --- | --- | --- | --- |
| Paso 0 | 40 min | Revisar JDK y Android SDK con `doctor`, y Kotlin básico (tipos nulos, `data class`, `filter`/`map`, `when`) | [PrimerosPasos.kt](paso0-entorno-y-kotlin/src/main/kotlin/onramp/paso0/PrimerosPasos.kt) | [steps/00-entorno-y-kotlin](steps/00-entorno-y-kotlin/README.md) |
| Paso 1 | 35 min | Lambdas con receptor (la sintaxis de Compose y Koin), `sealed interface` y `StateFlow` inmutable | [DslBuilders.kt](paso1-kotlin-y-stateflow/src/main/kotlin/onramp/paso1/DslBuilders.kt), [OrderState.kt](paso1-kotlin-y-stateflow/src/main/kotlin/onramp/paso1/OrderState.kt) | [steps/01-kotlin-y-stateflow](steps/01-kotlin-y-stateflow/README.md) |
| Paso 2 | 45 min | `suspend` y `delay` con reloj virtual, cancelación y tu primer `@Composable` con estado y test de UI | [CoroutinesAndUi.kt](paso2-corrutinas-y-ui/src/main/kotlin/onramp/paso2/CoroutinesAndUi.kt), [StudyCounterScreen.kt](paso2-corrutinas-y-ui/src/main/kotlin/onramp/paso2/StudyCounterScreen.kt) | [steps/02-corrutinas-y-ui](steps/02-corrutinas-y-ui/README.md) |
| Paso 3 | 75 min | UI declarativa, MVVM con `ViewModel`, `StateFlow` y `viewModelScope`, repositorio e inyección de dependencias con Koin | [ShelfViewModel.kt](paso3-mvvm-y-di/src/main/kotlin/onramp/paso3/ShelfViewModel.kt), [ShelfScreen.kt](paso3-mvvm-y-di/src/main/kotlin/onramp/paso3/ShelfScreen.kt) | [steps/03-mvvm-y-di](steps/03-mvvm-y-di/README.md) |
| Paso 4 | 40 min | Requests con OkHttp, JSON tolerante a campos nuevos y errores HTTP o de parseo con `MockWebServer` | [BookApiClient.kt](paso4-http-json/src/main/kotlin/onramp/paso4/BookApiClient.kt) | [steps/04-http-json](steps/04-http-json/README.md) |
| Paso 5 | 45 min | Tu primera base de datos, tablas, llave primaria, `SELECT` y `ON CONFLICT DO UPDATE` en SQLite, y su equivalente en Room 3 | [BookDatabase.kt](paso5-sql-y-upsert/src/main/kotlin/onramp/paso5/BookDatabase.kt) | [steps/05-sql-y-upsert](steps/05-sql-y-upsert/README.md) |
| Paso 6 | 30 min | Tests desde cero con JUnit que se ponen en rojo ante dos versiones con un bug | [ReadingStreakStudentTest.kt](paso6-pruebas-desde-cero/src/test/kotlin/onramp/paso6/ReadingStreakStudentTest.kt) | [steps/06-pruebas-desde-cero](steps/06-pruebas-desde-cero/README.md) |

## Verificación

1. `./gradlew doctor --no-parallel` para revisar el JDK y el Android SDK.
2. `./gradlew paso0 --no-parallel` hasta `./gradlew paso6 --no-parallel`, o `./gradlew ready --no-parallel` para todos.
3. `./gradlew :paso0-entorno-y-kotlin:run --no-parallel` para ver el programa del Paso 0.

## Repositorios relacionados

- Índice de on-ramps: [ykro/courses-onramp](https://github.com/ykro/courses-onramp)
- Repositorio con los ejercicios: [ykro/android-onramp](https://github.com/ykro/android-onramp)
- Curso principal: [ykro/android-course](https://github.com/ykro/android-course)
