# Paso 5. Tests desde cero con JUnit

![Paso 5. Tests desde cero con JUnit](diagrams/ciclo-testing.svg)

Este paso toma unos 20 minutos.

## Antes de empezar

1. Si un test pasa, ¿qué te garantiza sobre el código? ¿Y si nunca ha fallado?
2. Para una función que calcula la racha de días seguidos con lectura, ¿qué listas de entrada probarías primero?
3. ¿Cómo comprobarías que tus tests detectan un error sin romper a propósito la función real?

## Por qué importa

En los pasos anteriores corriste tests que escribió el curso. En [android-course](https://github.com/ykro/android-course) también escribes los tuyos con [JUnit 4](https://junit.org/junit4/), y en CI corren en cada push. Un test útil sigue el patrón Arrange, Act, Assert (preparar, ejecutar, comprobar) y, sobre todo, se pone en rojo cuando el código tiene un bug. Un test que pasa siempre no protege nada. Por eso aquí tus tests se corren también contra dos versiones con un bug de la misma función.

## Qué vas a hacer

- **Leer.** Abre [ReadingStreak.kt](../../m4-pruebas-desde-cero/src/main/kotlin/onramp/m4/ReadingStreak.kt) y escribe en papel qué debería devolver con tres o cuatro listas distintas, incluyendo una vacía y una con números negativos.
- **Debuguear.** Corre `./gradlew paso5 --no-parallel` con el archivo como viene. Lee por qué falla aunque ningún test sale en rojo.
- **Escribir.** Completa los tres tests de [ReadingStreakStudentTest.kt](../../m4-pruebas-desde-cero/src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt) y quita cada `@Ignore`. Llama siempre a `computeReadingStreak`; el verificador se encarga de cambiarla por cada versión con un bug, que están en `src/conBug*/`.

## Criterios de aceptación

- `CA-5.1` `ReadingStreakStudentTest.kt` tiene al menos tres tests y todos pasan contra la función correcta.
- `CA-5.2` Al menos un test falla con una versión que no reinicia la racha cuando un día tiene 0 minutos.
- `CA-5.3` Al menos un test falla con una versión que cuenta como lectura los días con minutos negativos.

## Al terminar

1. Escribe un test que pase con la función correcta y también con las dos versiones con un bug. ¿Qué tiene de débil?
2. Imagina una tercera versión con un bug que devuelve la racha actual al final de la lista en lugar de la más larga. ¿Qué lista usarías para atraparla?
3. Para probar un `StateFlow` como el del Paso 1, ¿qué cambia en el Arrange, el Act y el Assert? Mira [Module1Test.kt](../../m1-kotlin-dsl-estado/src/test/kotlin/onramp/m1/Module1Test.kt).
4. Explica en tres líneas, con tus palabras, qué cambiaste y por qué. Si no puedes, vuelve a la pista 1 antes de seguir.

## Cómo verificar

```bash
./gradlew paso5 --no-parallel
./gradlew ready --no-parallel
```

Si te trabas, abre la [pista 1](hints/1.md). Cada pista da un poco más que la anterior; la pista 3 trae un esqueleto, nunca la respuesta completa.

## Respuestas de autoevaluación

1. Por ejemplo, `assertEquals(3, computeReadingStreak(listOf(10, 20, 30)))`. Sin ceros ni negativos, las tres versiones dan lo mismo; el test pasa siempre y no detecta nada.
2. Una lista donde la racha más larga quede al principio y la última sea más corta, como `listOf(10, 20, 30, 0, 5)`. La correcta devuelve `3` y esa versión devolvería `1`.
3. El Arrange crea el objeto y un `runTest`, el Act llama a sus métodos y el Assert lee las emisiones con `state.test { awaitItem() }` de Turbine. La idea es la misma, pero lo que compruebas es una secuencia de valores en el tiempo.
4. Respuesta personal. Debe mencionar Arrange, Act, Assert, los casos normal, borde y raro, y que un test útil se pone en rojo cuando la función tiene un bug.
