package onramp.paso2

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
