# Módulo 2: Corrutinas y Estado en Pantallas Reactivas (15 minutos)

En Android, la pantalla corre en el hilo principal. Si bloqueas ese hilo con `Thread.sleep`, la app se congela y no responde a toques. Con corrutinas usas funciones `suspend` y `delay`, que pausan la tarea sin congelar el hilo.

En este módulo practicas tres ideas clave que aparecen en todas las unidades del curso:

1. **`delay` y reloj virtual (`runTest` + Turbine):** `delay(10_000)` no obliga a tus pruebas a esperar 10 segundos. Dentro de `runTest`, el reloj virtual adelanta el tiempo al instante y Turbine verifica cada valor emitido por un `Flow`.
2. **Nunca atrapes `CancellationException`:** cuando el usuario sale de una pantalla, Android cancela las corrutinas de esa pantalla lanzando `CancellationException`. Si escribes `catch (e: Exception)` y devuelves un mensaje de error sin relanzar `CancellationException`, la cancelación se pierde.
3. **Estado entre redibujos:** en una interfaz reactiva como Compose, la función de la pantalla vuelve a ejecutarse cada vez que cambia un dato. Una variable local (`var count = 0`) vuelve a cero en cada redibujo. Para conservar el dato entre redibujos se guarda en una memoria asociada a la pantalla (el principio detrás de `remember`).

## Ejercicios

Abre `src/main/kotlin/onramp/m2/CoroutinesAndUi.kt` y completa:

1. `countdownTicks`: emite desde `from` hasta `0`, esperando `stepMs` entre cada número con `delay` (nunca `Thread.sleep`).
2. `runSafely`: ejecuta `action()`, devuelve `"Error: <mensaje>"` ante fallos normales, pero siempre relanza `CancellationException`.
3. `renderStudyCounter`: corrige la función para guardar el contador en `memory.remember("sessions") { 0 }` en lugar de una variable local que se borra en cada redibujo.

## Cómo probar este módulo

```bash
./gradlew :m2-corrutinas-y-ui:test --no-parallel
```
