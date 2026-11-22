package onramp.m4

/**
 * Versión con un bug de [computeReadingStreak] que usa el verificador del Paso 5.
 * Cuenta como lectura cualquier día distinto de 0, incluidos los minutos negativos.
 * No la edites ni la llames desde tus tests.
 */
fun computeReadingStreak(dailyMinutes: List<Int>): Int {
    var bestStreak = 0
    var currentStreak = 0

    for (minutes in dailyMinutes) {
        if (minutes != 0) {
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
