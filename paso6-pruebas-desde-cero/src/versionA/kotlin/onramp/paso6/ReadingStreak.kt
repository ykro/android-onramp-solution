package onramp.paso6

/**
 * `versionA` de [computeReadingStreak], con un bug. La usa el verificador del Paso 6.
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
