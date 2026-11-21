package onramp.m2

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Ejercicio 1 (`suspend` + `delay` + `Flow`):
 * Emite una cuenta regresiva desde `from` hasta `0` (inclusive).
 * Entre cada número espera `stepMs` milisegundos usando `delay`.
 * Si `from < 0`, no emite nada.
 */
fun countdownTicks(from: Int, stepMs: Long): Flow<Int> =
    flow {
        if (from < 0) return@flow
        for (current in from downTo 0) {
            emit(current)
            if (current > 0) {
                delay(stepMs)
            }
        }
    }

/**
 * Ejercicio 2 (no atrapar `CancellationException`):
 * Ejecuta `action()`. Si lanza una excepción normal, devuelve `"Error: ${e.message ?: "desconocido"}"`.
 * Si la corrutina se cancela (`CancellationException`), debes relanzar esa excepción.
 */
suspend fun runSafely(action: suspend () -> String): String =
    try {
        action()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        "Error: ${error.message ?: "desconocido"}"
    }

// --- Parte 3: Estado en pantallas reactivas ---

class MutableSlot<T>(var value: T)

/**
 * Simulador mínimo de memoria de pantalla (equivalente conceptual a `remember` en Compose).
 * Sobrevive entre redibujos mientras la pantalla sigue abierta.
 */
class UiMemory {
    private val slots = mutableMapOf<String, MutableSlot<Any?>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> remember(key: String, initialValue: () -> T): MutableSlot<T> {
        val existing = slots.getOrPut(key) { MutableSlot(initialValue()) }
        return existing as MutableSlot<T>
    }
}

data class CounterFrame(
    val label: String,
    val onIncrement: () -> Unit,
    val onReset: () -> Unit,
)

/**
 * Ejercicio 3 (conservar estado entre redibujos):
 * Esta función se llama de nuevo cada vez que la pantalla se redibuja.
 * Guarda el contador en `memory.remember("sessions") { 0 }` para que `onIncrement`
 * y `onReset` conserven el valor al volver a llamar `renderStudyCounter(memory)`.
 *
 * Texto esperado en `label`:
 * - 0 -> "0 sesiones completadas"
 * - 1 -> "1 sesión completada"
 * - N -> "$N sesiones completadas"
 */
fun renderStudyCounter(memory: UiMemory): CounterFrame {
    val sessions = memory.remember("sessions") { 0 }
    val count = sessions.value
    val label =
        if (count == 1) "1 sesión completada" else "$count sesiones completadas"

    return CounterFrame(
        label = label,
        onIncrement = { sessions.value += 1 },
        onReset = { sessions.value = 0 },
    )
}
