package onramp.paso2

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Ejercicio 3 (Compose de verdad):
 * Compose vuelve a llamar esta función cada vez que cambia el estado que lee.
 *
 * `remember` guarda el estado entre recomposiciones. Sin él, cada recomposición creaba
 * un `mutableStateOf(0)` nuevo y el texto se quedaba en 0.
 *
 * Texto esperado:
 * - 0 -> "0 sesiones completadas"
 * - 1 -> "1 sesión completada"
 * - N -> "$N sesiones completadas"
 */
@Composable
fun StudyCounterScreen() {
    var count by remember { mutableStateOf(0) }
    val label = if (count == 1) "1 sesión completada" else "$count sesiones completadas"

    Column {
        Text(label)
        Button(onClick = { count += 1 }) {
            Text("Sumar sesión")
        }
        Button(onClick = { count = 0 }) {
            Text("Reiniciar")
        }
    }
}
