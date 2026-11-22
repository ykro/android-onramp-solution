package onramp.m4

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Paso 5: tests de computeReadingStreak con Arrange, Act y Assert.
 * `./gradlew paso5 --no-parallel` los corre contra la función correcta y contra dos
 * versiones con un bug.
 */
class ReadingStreakStudentTest {

    @Test
    fun `caso normal devuelve la racha consecutiva más larga`() {
        // Arrange: dos rachas, de 2 y de 3 días, separadas por un día en 0.
        val dailyMinutes = listOf(15, 20, 0, 30, 25, 10)

        // Act
        val streak = computeReadingStreak(dailyMinutes)

        // Assert
        assertEquals(3, streak)
    }

    @Test
    fun `casos borde devuelven cero`() {
        assertEquals(0, computeReadingStreak(emptyList()))
        assertEquals(0, computeReadingStreak(listOf(0, 0, 0)))
    }

    @Test
    fun `días negativos cortan la racha`() {
        // Un valor negativo no es lectura: corta la racha igual que un 0.
        assertEquals(2, computeReadingStreak(listOf(10, -5, 20, 30)))
    }
}
