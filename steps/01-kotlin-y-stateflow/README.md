# Paso 1. Lambdas con receptor, `sealed interface` y `StateFlow`

![Paso 1. Lambdas con receptor, `sealed interface` y `StateFlow`](diagrams/stateflow.svg)

Este paso toma unos 30 minutos.

## Antes de empezar

1. En Compose escribes `Column { Text("Hola") }`. ¿Dónde está la llamada a la función y dónde están sus argumentos?
2. Si una pantalla puede estar vacía, cargando, lista o con error, ¿cómo lo representarías en Java? ¿Qué pasa si mañana agregas un quinto estado?
3. Si dos variables apuntan a la misma lista y una le agrega un elemento, ¿cómo sabría alguien que la lista cambió?

## Por qué importa

En Jetpack Compose y Koin verás bloques como `Column { Text("Hola") }` y `module { single { ... } }` desde el primer día. Esa sintaxis combina dos reglas normales de Kotlin, las [trailing lambdas](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas) y las [lambdas con receptor](https://kotlinlang.org/docs/lambdas.html#function-literals-with-receiver). Además, casi todas las pantallas del curso leen su estado desde un [`StateFlow`](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow), que solo avisa a la pantalla cuando el estado nuevo es distinto del anterior. Si modificas una lista dentro del estado, el anterior y el nuevo son el mismo objeto y la pantalla no se entera. Una [`sealed interface`](https://kotlinlang.org/docs/sealed-classes.html) con `when` hace que el compilador te avise cuando falta manejar un estado.

## Qué vas a hacer

- **Leer.** Abre [DslBuilders.kt](../../m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/DslBuilders.kt) y [Module1Test.kt](../../m1-kotlin-dsl-estado/src/test/kotlin/onramp/m1/Module1Test.kt). Fíjate cómo el test llama a `miniColumn { text("...") }` y busca de dónde sale `text` si no hay ningún objeto antes del punto.
- **Debuguear.** Corre `./gradlew paso1 --no-parallel`. El ejercicio 4 falla aunque `addOrder` sí agrega el pedido. Antes de cambiar algo, explica con el comentario de [OrderState.kt](../../m1-kotlin-dsl-estado/src/main/kotlin/onramp/m1/OrderState.kt) por qué `StateFlow` no emite.
- **Escribir.** Resuelve los cuatro ejercicios, guiándote por los `TODO` y los comentarios de cada función.

## Criterios de aceptación

- `CA-1.1` `miniColumn` devuelve los textos en el orden en que el bloque los agregó.
- `CA-1.2` `miniModule` registra fábricas con `single` y `container.get(...)` devuelve siempre la misma instancia.
- `CA-1.3` `renderOrderBanner` devuelve el texto de los cuatro estados, con singular y plural en `Preparing`, sin rama `else`.
- `CA-1.4` `OrderBoard` emite un estado nuevo en su `StateFlow` cada vez que agregas o quitas un pedido.

## Al terminar

1. Agrega temporalmente un quinto estado `Cancelled` a `OrderBannerState` y compila. ¿Qué te dice el compilador? ¿Qué habría pasado si `renderOrderBanner` tuviera una rama `else`?
2. Si `OrderBoard` emitiera `current.copy()` sin cambiar nada, ¿la pantalla recibiría un estado nuevo? ¿Por qué?
3. En `miniModule`, ¿qué cambiaría en `CA-1.2` si `MiniContainer.get` llamara a la fábrica cada vez en lugar de guardar la instancia?
4. Explica en tres líneas, con tus palabras, qué cambiaste y por qué. Si no puedes, vuelve a la pista 1 antes de seguir.

## Cómo verificar

```bash
./gradlew paso1 --no-parallel
```

Si te trabas, abre la [pista 1](hints/1.md). Cada pista da un poco más que la anterior; la pista 3 trae un esqueleto, nunca la respuesta completa.

## Respuestas de autoevaluación

1. El compilador marca el `when` como no exhaustivo y no compila hasta que agregues la rama. Con un `else`, compilaría y el estado nuevo caería en esa rama sin que nadie lo note.
2. No. `copy()` sin cambios crea un objeto nuevo, pero `StateFlow` compara con `==` y una `data class` con los mismos valores es igual a la anterior, así que no emite. Eso evita redibujos inútiles.
3. `CA-1.2` fallaría, porque cada `get` devolvería una instancia nueva. El test comprueba que dos `get` seguidos devuelven el mismo objeto, igual que un `single` de Koin.
4. Respuesta personal. Debe mencionar que una lambda con receptor se ejecuta sobre un objeto, que `when` sobre una `sealed interface` obliga a cubrir todos los casos y que `StateFlow` necesita un estado nuevo para avisar.
