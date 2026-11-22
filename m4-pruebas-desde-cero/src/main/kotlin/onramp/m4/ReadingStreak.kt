package onramp.m4

/**
 * Calcula la racha más larga de días seguidos con lectura efectiva (`minutos > 0`).
 *
 * Un día con `0` minutos (o un valor negativo) corta la racha y reinicia el conteo a cero.
 *
 * Esta función ya está bien escrita. En el Paso 5 tu trabajo es probarla en
 * `src/test/kotlin/onramp/m4/ReadingStreakStudentTest.kt`; no la edites para hacer pasar tus tests.
 */
fun computeReadingStreak(dailyMinutes: List<Int>): Int {
    var bestStreak = 0
    var currentStreak = 0

    for (minutes in dailyMinutes) {
        if (minutes > 0) {
            currentStreak += 1
            if (currentStreak > bestStreak) {
                bestStreak = currentStreak
            }
        } else {
            currentStreak = 0
        }
    }

    return bestStreak
}
