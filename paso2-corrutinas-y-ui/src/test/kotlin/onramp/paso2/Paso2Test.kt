package onramp.paso2

import app.cash.turbine.test
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class Paso2Test {
    @Test
    fun `CA-2_1 - countdownTicks emite toda la cuenta usando reloj virtual sin esperar tiempo real`() = runTest {
        val startRealMs = System.currentTimeMillis()

        countdownTicks(from = 3, stepMs = 10_000L).test {
            assertEquals(3, awaitItem())
            assertEquals(2, awaitItem())
            assertEquals(1, awaitItem())
            assertEquals(0, awaitItem())
            awaitComplete()
        }

        assertEquals(
            "El reloj virtual debe avanzar 30_000 ms (3 pausas de 10_000 ms). No omitas delay().",
            30_000L,
            currentTime,
        )

        val elapsedRealMs = System.currentTimeMillis() - startRealMs
        assertTrue(
            "La prueba tardó ${elapsedRealMs} ms. Usa delay() y no Thread.sleep().",
            elapsedRealMs < 2_000L,
        )
    }

    @Test
    fun `CA-2_2 - runSafely devuelve mensaje ante fallos normales y relanza CancellationException`() = runTest {
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
}
