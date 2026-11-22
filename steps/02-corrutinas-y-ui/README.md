# Paso 2. Corrutinas, cancelación y estado en UI

![Paso 2. Corrutinas, cancelación y estado en UI](diagrams/corrutinas-y-ui.svg)

Este paso toma unos 45 minutos.

## Antes de empezar

1. Si el hilo que dibuja la pantalla se queda esperando tres segundos a que responda un servidor, ¿qué ve la persona que usa la app?
2. Cuando alguien cierra una pantalla a medio cargar, ¿qué debería pasar con la descarga que esa pantalla había empezado?
3. Si una función que dibuja la pantalla declara `var count = 0` y se vuelve a llamar en cada redibujo, ¿cuánto vale `count` al inicio de cada llamada?

## Por qué importa

En Android la interfaz corre en el hilo principal; si lo bloqueas con `Thread.sleep`, la pantalla se congela. Las [corrutinas](https://kotlinlang.org/docs/coroutines-basics.html) usan `suspend` y `delay` para pausar solo la tarea. En los tests, [`runTest`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/run-test.html) usa un reloj virtual que adelanta un `delay(10_000L)` al instante. Dos errores frecuentes al empezar son atrapar la [cancelación](https://kotlinlang.org/docs/cancellation-and-timeouts.html) con un `catch (e: Exception)`, que deja tareas vivas cuando la persona cierra la pantalla, y guardar estado en una variable local que vuelve a cero en cada redibujo en lugar de usar [`remember` en Compose](https://developer.android.com/develop/ui/compose/state#state-in-composables).

## Lo mínimo que necesitas saber

- **Hilo principal.** En Android, un solo hilo dibuja la pantalla y atiende los toques. Si ese hilo espera, la app se congela, y a los cinco segundos Android ofrece cerrarla.
- **Corrutina.** Una tarea que puede pausarse y continuar después sin ocupar un hilo mientras espera. Miles de corrutinas pueden compartir pocos hilos. Se lanzan con `launch` dentro de un scope, como `viewModelScope` en el curso, y `launch` devuelve un `Job` con el que puedes cancelarla.
- **`suspend` y `Flow`.** `suspend` marca una función que puede pausarse sin bloquear el hilo; solo se llama desde otra función `suspend` o desde una corrutina. Un `Flow` es una secuencia de valores que llegan con el tiempo. Se construye con `flow { emit(valor) }` y alguien lo recolecta con `collect`.
- **Cancelación.** Cuando una corrutina se cancela, Kotlin lanza `CancellationException` dentro de ella para detenerla. Si la atrapas y no la vuelves a lanzar, la corrutina sigue viva.
- **Recomposición.** En Compose, cada función de pantalla se vuelve a ejecutar cuando cambia su estado. Lo que deba sobrevivir entre ejecuciones tiene que vivir fuera de la función; `remember` lo guarda por ti:

  ```kotlin
  @Composable
  fun Contador() {
      var veces by remember { mutableStateOf(0) }
      Button(onClick = { veces++ }) { Text("Clics: $veces") }
  }
  ```

  Sin `remember`, `veces` volvería a 0 en cada redibujo.
- **Composable.** Una función marcada con `@Composable` describe un pedazo de pantalla. No devuelve nada; llama a otras como `Text`, `Button` o `Column`, y Compose las dibuja. `Column` apila a sus hijos de arriba hacia abajo, y `Button` recibe en `onClick` lo que pasa al tocarlo.
- **Test de UI.** `createComposeRule()` dibuja un composable sin emulador. Con `onNodeWithText("…")` buscas un elemento por su texto, con `performClick()` lo tocas y con `assertExists()` compruebas que está en pantalla.

## Qué vas a hacer

- **Leer.** Abre [CoroutinesAndUi.kt](../../paso2-corrutinas-y-ui/src/main/kotlin/onramp/paso2/CoroutinesAndUi.kt) y [Paso2Test.kt](../../paso2-corrutinas-y-ui/src/test/kotlin/onramp/paso2/Paso2Test.kt). En el test de `countdownTicks`, ubica qué parte mide el tiempo real. Después abre [StudyCounterScreen.kt](../../paso2-corrutinas-y-ui/src/main/kotlin/onramp/paso2/StudyCounterScreen.kt) y [StudyCounterScreenTest.kt](../../paso2-corrutinas-y-ui/src/test/kotlin/onramp/paso2/StudyCounterScreenTest.kt), y sigue en el test qué toca y qué espera ver.
- **Debuguear.** Corre `./gradlew paso2 --no-parallel`. Para el ejercicio 3, el test dice que después del clic la pantalla sigue en 0. Compara `StudyCounterScreen` con el `Contador` de arriba.
- **Escribir.** Resuelve los tres ejercicios. En el 3 escribes tu primer `Button` en Compose, el de «Reiniciar». No uses `Thread.sleep` en ningún lado.

## Criterios de aceptación

- `CA-2.1` `countdownTicks(from = 3, stepMs = 10_000L)` emite `3, 2, 1, 0` y el test termina en menos de 2 segundos reales.
- `CA-2.2` `runSafely` devuelve `Error: sin internet` ante un `IllegalStateException("sin internet")` y deja pasar la cancelación del `Job`.
- `CA-2.3` En `StudyCounterScreen`, un clic en «Sumar sesión» muestra «1 sesión completada», otro muestra «2 sesiones completadas» y un clic en «Reiniciar» regresa a «0 sesiones completadas».

## Al terminar

1. Si cambias `delay(stepMs)` por `Thread.sleep(stepMs)`, ¿qué pasa con el tiempo del test de `CA-2.1`? ¿Por qué el reloj virtual no lo puede adelantar?
2. En el ejercicio 3, el clic sí cambiaba `count` a 1. ¿Por qué la pantalla seguía mostrando 0?
3. Explica en tres líneas, con tus palabras, qué cambiaste y por qué. Si no puedes, vuelve a la pista 1 antes de seguir.

## Cómo verificar

```bash
./gradlew paso2 --no-parallel
```

Si te trabas, abre la [pista 1](hints/1.md). Cada pista da un poco más que la anterior; la pista 3 trae un esqueleto, nunca la respuesta completa.

## Respuestas de autoevaluación

1. El test tarda unos 30 segundos reales y falla el límite de 2 segundos. `Thread.sleep` bloquea el hilo de verdad y no pasa por el planificador de corrutinas, así que el reloj virtual no se entera de que hay una espera.
2. El clic sí cambiaba el estado, y por eso Compose volvía a llamar `StudyCounterScreen`. Pero esa nueva llamada ejecutaba `mutableStateOf(0)` otra vez y creaba un estado nuevo en 0. Con `remember { mutableStateOf(0) }`, Compose crea el estado en la primera llamada y lo devuelve en las siguientes.
3. Respuesta personal. Debe mencionar `delay` contra `Thread.sleep`, relanzar `CancellationException` y guardar el estado fuera de la función que se vuelve a ejecutar.
