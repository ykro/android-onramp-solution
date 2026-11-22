# Android On-Ramp (solución de referencia)

Este repositorio contiene la solución verificada de [Android On-Ramp](https://github.com/ykro/android-onramp), la nivelación previa al [curso de Android](https://github.com/ykro/android-course). Aquí `./gradlew ready --no-parallel` termina en verde con los seis pasos, de `paso0` a `paso5`. Cada guía en `steps/` termina con las respuestas de autoevaluación.

![Mapa de Android On-Ramp](docs/diagrams/overview.svg)

## Dónde ver la solución de cada paso

| Paso | Criterios | Qué se cambió y por qué | Archivo |
| --- | --- | --- | --- |
| Paso 0 | `CA-0.1` a `CA-0.4` | `saludar` revisa `isNullOrBlank()` antes de usar el nombre, y después de esa revisión Kotlin sabe que no es `null`. `librosLargos` encadena `filter`, `map` y `sorted`. `nivelDeLectura` usa un `when` con condiciones en orden. | [PrimerosPasos.kt](m0-doctor/src/main/kotlin/onramp/m0/PrimerosPasos.kt) |
| Paso 1 | `CA-1.1` a `CA-1.4` | `miniColumn` y `miniModule` crean el scope y ejecutan la lambda con receptor sobre él. `renderOrderBanner` usa un `when` exhaustivo sin `else`, así un estado nuevo no compila hasta manejarlo. `OrderBoardState` guarda una `List` de solo lectura y `OrderBoard` emite un estado nuevo con `update` y `copy`. | [DslBuilders.kt](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/DslBuilders.kt), [OrderState.kt](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/OrderState.kt) |
| Paso 2 | `CA-2.1` a `CA-2.3` | `countdownTicks` usa `flow` y `delay`, que el reloj virtual adelanta. `runSafely` relanza `CancellationException` antes del `catch` genérico. El contador vive en `memory.remember("sessions")` y sobrevive al redibujo. | [CoroutinesAndUi.kt](m2-corrutinas-y-ui/src/main/kotlin/onramp/m2/CoroutinesAndUi.kt) |
| Paso 3 | `CA-3.1`, `CA-3.2` | `Json { ignoreUnknownKeys = true }` tolera campos nuevos. El cliente revisa `isSuccessful` antes de leer el cuerpo y convierte la `SerializationException` en `ParseError`. | [BookApiClient.kt](m3-http-y-sql/src/main/kotlin/onramp/m3/BookApiClient.kt) |
| Paso 4 | `CA-4.1`, `CA-4.2` | `CREATE TABLE IF NOT EXISTS` con `id` como llave primaria. `ON CONFLICT(id) DO UPDATE` actualiza solo `title` y `author`, así `favorite` y `local_note` sobreviven a la sincronización. La consulta de favoritos filtra y ordena en SQL. | [BookDatabase.kt](m3-http-y-sql/src/main/kotlin/onramp/m3/BookDatabase.kt) |
| Paso 5 | `CA-5.1` a `CA-5.3` | Tres tests con Arrange, Act, Assert, un caso normal con un día en `0`, los casos borde y un día negativo en medio de una racha. El primero atrapa la versión que no reinicia en `0` y el tercero la que cuenta negativos. | [ReadingStreakStudentTest.kt](m4-pruebas-desde-cero/src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt) |

## Los 6 pasos

| Paso | Tiempo | Qué practicas | Archivo | Guía |
| --- | --- | --- | --- | --- |
| Paso 0 | 25 min | Revisar JDK y Android SDK con `doctor`, y Kotlin básico (tipos nulos, `data class`, `filter`/`map`, `when`) | [PrimerosPasos.kt](m0-doctor/src/main/kotlin/onramp/m0/PrimerosPasos.kt) | [steps/00-entorno-y-kotlin](steps/00-entorno-y-kotlin/README.md) |
| Paso 1 | 30 min | Lambdas con receptor (la sintaxis de Compose y Koin), `sealed interface` y `StateFlow` inmutable | [DslBuilders.kt](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/DslBuilders.kt), [OrderState.kt](m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/OrderState.kt) | [steps/01-kotlin-y-stateflow](steps/01-kotlin-y-stateflow/README.md) |
| Paso 2 | 25 min | `suspend` y `delay` con reloj virtual, cancelación y estado que sobrevive al redibujo | [CoroutinesAndUi.kt](m2-corrutinas-y-ui/src/main/kotlin/onramp/m2/CoroutinesAndUi.kt) | [steps/02-corrutinas-y-ui](steps/02-corrutinas-y-ui/README.md) |
| Paso 3 | 20 min | Requests con OkHttp, JSON tolerante a campos nuevos y errores HTTP o de parseo con `MockWebServer` | [BookApiClient.kt](m3-http-y-sql/src/main/kotlin/onramp/m3/BookApiClient.kt) | [steps/03-http-json](steps/03-http-json/README.md) |
| Paso 4 | 30 min | Tu primera base de datos, tablas, llave primaria, `SELECT` y `ON CONFLICT DO UPDATE` en SQLite, y su equivalente en Room 3 | [BookDatabase.kt](m3-http-y-sql/src/main/kotlin/onramp/m3/BookDatabase.kt) | [steps/04-sql-y-upsert](steps/04-sql-y-upsert/README.md) |
| Paso 5 | 20 min | Tests desde cero con JUnit que se ponen en rojo ante una versión con un bug | [ReadingStreakStudentTest.kt](m4-pruebas-desde-cero/src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt) | [steps/05-pruebas-desde-cero](steps/05-pruebas-desde-cero/README.md) |

## Verificación

1. `./gradlew doctor --no-parallel` para revisar el JDK y el Android SDK.
2. `./gradlew paso0 --no-parallel` hasta `./gradlew paso5 --no-parallel`, o `./gradlew ready --no-parallel` para todos.
3. `./gradlew :m0-doctor:run --no-parallel` para ver el programa del Paso 0.

## Repositorios relacionados

- Índice de on-ramps: [ykro/courses-onramp](https://github.com/ykro/courses-onramp)
- Repositorio con los ejercicios: [ykro/android-onramp](https://github.com/ykro/android-onramp)
- Curso principal: [ykro/android-course](https://github.com/ykro/android-course)
