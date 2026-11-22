package onramp.m4

/**
 * Versión con un bug de [computeReadingStreak] que usa el verificador del Paso 5.
 * No reinicia la racha cuando un día tiene 0 minutos, así que suma días que no son seguidos.
 * No la edites ni la llames desde tus tests.
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
        }
    }

    return bestStreak
}
