package onramp.m4

/**
 * Calcula la racha más larga de días seguidos con lectura efectiva (`minutos > 0`).
 *
 * Un día con `0` minutos (o un valor negativo) corta la racha y reinicia el conteo a cero.
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

/**
 * Versión con un error plantado a propósito para practicar pruebas automatizadas:
 * olvida reiniciar `currentStreak` cuando encuentra un día en `0`, por lo que suma
 * todos los días con lectura aunque no sean consecutivos.
 */
fun computeReadingStreakBuggy(dailyMinutes: List<Int>): Int {
    var bestStreak = 0
    var currentStreak = 0

    for (minutes in dailyMinutes) {
        if (minutes > 0) {
            currentStreak += 1
            if (currentStreak > bestStreak) {
                bestStreak = currentStreak
            }
        }
        // Bug plantado: falta `else { currentStreak = 0 }`
    }

    return bestStreak
}
