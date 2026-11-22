package onramp.m4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Paso 5 — Solución de referencia para pruebas automatizadas desde cero con JUnit.
 */
class ReadingStreakStudentTest {

    @Test
    fun `CA-5-1 caso normal devuelve la racha consecutiva mas larga`() {
        // Arrange
        val dailyMinutes = listOf(15, 20, 0, 30, 25, 10)

        // Act
        val streak = computeReadingStreak(dailyMinutes)

        // Assert
        assertEquals(3, streak)
    }

    @Test
    fun `CA-5-2 caso borde devuelve cero para lista vacia y dias en cero`() {
        // Arrange
        val emptyDays = emptyList<Int>()
        val zeroDays = listOf(0, 0, 0)
        val nonPositiveDays = listOf(0, -10, 0)

        // Act & Assert
        assertEquals(0, computeReadingStreak(emptyDays))
        assertEquals(0, computeReadingStreak(zeroDays))
        assertEquals(0, computeReadingStreak(nonPositiveDays))
    }

    @Test
    fun `CA-5-3 prueba pasa con la version sana y falla contra computeReadingStreakBuggy`() {
        // Arrange
        val dailyMinutes = listOf(15, 0, 20, 30)

        // Act & Assert: verde contra el código sano
        assertEquals(2, computeReadingStreak(dailyMinutes))

        // Rojo contra la versión con bug (devuelve 3 porque no reinicia en 0)
        assertThrows(AssertionError::class.java) {
            assertEquals(2, computeReadingStreakBuggy(dailyMinutes))
        }
    }
}
