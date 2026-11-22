# Paso 0. Entorno y tu primer programa en Kotlin

Este paso toma unos 25 minutos.

## Antes de empezar

1. En Java, ¿qué pasa si llamas un método sobre una variable que vale `null`? ¿En qué momento te enteras, al compilar o al correr?
2. Si tienes una lista de libros y quieres solo los títulos de los que tienen más de 300 páginas, ¿cuántas líneas te toma en Java con un `for`?
3. ¿Qué crees que hace Gradle cuando corres `./gradlew` por primera vez en un proyecto?

## Por qué importa

Todo el curso de Android está escrito en [Kotlin](https://kotlinlang.org/docs/getting-started.html) y se compila con [Gradle](https://docs.gradle.org/current/userguide/gradle_wrapper.html). Antes de hablar de `StateFlow`, corrutinas o Room, conviene confirmar que tu JDK y tu Android SDK funcionan, y ver en un programa pequeño las piezas de Kotlin que aparecen en cada archivo del curso. Si vienes de Java, la mayoría te va a sonar; lo nuevo es que el compilador te protege de los `null` y que las listas se transforman con funciones en lugar de ciclos.

## Lo mínimo que necesitas saber

- **`val` y `var`.** `val` es una referencia que no se puede reasignar, como `final` en Java; `var` sí cambia. En Kotlin se prefiere `val`.
- **Tipos nulos.** `String` nunca es `null`; `String?` puede serlo. El compilador no te deja llamar métodos sobre un `String?` sin revisar antes. `isNullOrBlank()`, el operador `?.` y el operador elvis `?:` son las herramientas de todos los días.
- **`data class`.** Una clase que solo guarda datos, como `data class Libro(val titulo: String, val paginas: Int)`. Kotlin genera `equals`, `toString` y `copy` por ti.
- **Plantillas de texto.** `"Hola, $nombre"` o `"Total: ${lista.size}"` arman texto sin concatenar.
- **Lambdas y colecciones.** `filter { it.paginas > 300 }` se queda con los elementos que cumplen la condición, `map { it.titulo }` transforma cada elemento y `sorted()` ordena. `it` es el nombre por defecto del único parámetro de una lambda.
- **`when`.** Un `switch` que devuelve un valor y acepta condiciones, por ejemplo `when { x < 0 -> "negativo"; else -> "positivo" }`.
- **Gradle.** `./gradlew` descarga la versión de Gradle que pide el proyecto y compila cada módulo (`m0-doctor`, `m1-...`). La primera vez tarda unos minutos porque baja dependencias.

## Qué vas a hacer

- **Preparar.** Instala [Android Studio](https://developer.android.com/studio), que trae un JDK 21 y el Android SDK. Si ya tienes un JDK 21 aparte, también sirve. Corre `./gradlew doctor --no-parallel` (en Windows, `gradlew.bat doctor --no-parallel`) y sigue lo que te pida hasta que diga `[OK] Entorno listo para trabajar.`
- **Leer.** Corre `./gradlew :m0-doctor:run --no-parallel` y compara lo que imprime con [PrimerosPasos.kt](../../m0-doctor/src/main/kotlin/onramp/m0/PrimerosPasos.kt). Ubica qué función produce cada línea.
- **Debuguear.** Tres de las líneas impresas están mal. Lee los tres `TODO` y anota qué esperabas ver en cada una.
- **Escribir.** Corrige las tres funciones. Intenta escribir `librosLargos` sin `for`, con funciones de colecciones. Vuelve a correr el programa después de cada cambio.

## Criterios de aceptación

- `CA-0.1` `./gradlew doctor --no-parallel` encuentra un JDK 21 o superior y un Android SDK con al menos una plataforma.
- `CA-0.2` `saludar` devuelve `Hola, <nombre>` con un nombre y `Hola, lector` con `null` o solo espacios.
- `CA-0.3` `librosLargos` devuelve, en orden alfabético, los títulos de los libros con más de 300 páginas.
- `CA-0.4` `nivelDeLectura` devuelve `sin lectura` con 0 minutos o menos, `corta` de 1 a 29 y `larga` desde 30.

## Al terminar

1. Escribe `val x: String = null` dentro de `main` y compila. ¿Qué te dice Kotlin y en qué momento? Compáralo con lo que pasaría en Java.
2. ¿Qué devuelve `librosLargos` si dos libros tienen el mismo título? ¿Cambiarías algo si el resultado debiera ser una lista sin repetidos?
3. Si agregas `"Hombres de Maíz"` con `-10` páginas al estante, ¿qué parte de tu programa debería rechazarlo y por qué no es `librosLargos`?
4. Explica en tres líneas, con tus palabras, qué cambiaste y por qué. Si no puedes, vuelve a la pista 1 antes de seguir.

## Cómo verificar

```bash
./gradlew paso0 --no-parallel
```

Si te trabas, abre la [pista 1](hints/1.md). Cada pista da un poco más que la anterior; la pista 3 trae un esqueleto, nunca la respuesta completa.

## Respuestas de autoevaluación

1. Kotlin no compila y avisa que `null` no puede ser un valor de un tipo no nulo `String`. Te enteras al compilar. En Java la asignación compila y el error aparece al correr, como `NullPointerException`, cuando alguien usa la variable.
2. Devuelve el título dos veces, porque `filter` y `map` conservan todos los elementos. Para quitar repetidos agregarías `distinct()` antes de ordenar.
3. La validación pertenece a quien crea el `Libro` (por ejemplo, un `init` con `require(paginas > 0)` en la `data class`). `librosLargos` responde una pregunta sobre libros válidos; si cada función revisara todo, el código se llenaría de validaciones repetidas.
4. Respuesta personal. Debe mencionar los tipos nulos y cómo revisarlos, `filter`, `map` y `sorted` sobre listas, y `when` con condiciones.
