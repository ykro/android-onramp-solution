package onramp.m2

import app.cash.turbine.test
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Module2Test {
    @Test
    fun `ejercicio 1 - countdownTicks emite toda la cuenta usando reloj virtual sin esperar tiempo real`() = runTest {
        val startRealMs = System.currentTimeMillis()

        countdownTicks(from = 3, stepMs = 10_000L).test {
            assertEquals(3, awaitItem())
            assertEquals(2, awaitItem())
            assertEquals(1, awaitItem())
            assertEquals(0, awaitItem())
            awaitComplete()
        }

        val elapsedRealMs = System.currentTimeMillis() - startRealMs
        assertTrue(
            "La prueba tardó ${elapsedRealMs} ms. Usa delay() y no Thread.sleep().",
            elapsedRealMs < 2_000L,
        )
    }

    @Test
    fun `ejercicio 2 - runSafely devuelve mensaje ante fallos normales y relanza CancellationException`() = runTest {
        val handled = runSafely {
            throw IllegalStateException("sin internet")
        }
        assertEquals("Error: sin internet", handled)

        var caughtCancellation = false
        val started = CompletableDeferred<Unit>()
        val job = launch {
            try {
                runSafely {
                    started.complete(Unit)
                    delay(60_000L)
                    "nunca llega"
                }
            } catch (_: CancellationException) {
                caughtCancellation = true
            }
        }

        started.await()
        job.cancel()
        job.join()

        assertTrue(
            "runSafely no debe atrapar CancellationException; relánzala para que la corrutina se cancele.",
            caughtCancellation,
        )
    }

    @Test
    fun `ejercicio 3 - renderStudyCounter conserva el contador cuando la pantalla se vuelve a dibujar`() {
        val memory = UiMemory()

        val firstDraw = renderStudyCounter(memory)
        assertEquals("0 sesiones completadas", firstDraw.label)

        firstDraw.onIncrement()
        val secondDraw = renderStudyCounter(memory)
        assertEquals("1 sesión completada", secondDraw.label)

        secondDraw.onIncrement()
        val thirdDraw = renderStudyCounter(memory)
        assertEquals("2 sesiones completadas", thirdDraw.label)

        thirdDraw.onReset()
        val fourthDraw = renderStudyCounter(memory)
        assertEquals("0 sesiones completadas", fourthDraw.label)
    }
}
