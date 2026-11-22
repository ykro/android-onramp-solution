package onramp.paso3

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/**
 * La pantalla conectada: lee el estado del ViewModel y le pasa los eventos.
 * Este composable ya está bien escrito. En el curso verás `collectAsStateWithLifecycle()`,
 * que además deja de escuchar cuando la app pasa a segundo plano.
 */
@Composable
fun ShelfScreen(viewModel: ShelfViewModel) {
    val state by viewModel.uiState.collectAsState()
    ShelfContent(state = state, onEvent = viewModel::onEvent)
}

/**
 * Ejercicio 3 (UI declarativa): la pantalla es una función del estado. Lee `state` directo, sin
 * copiarlo en un `remember`, y los botones solo avisan eventos hacia arriba.
 */
@Composable
fun ShelfContent(state: ShelfUiState, onEvent: (ShelfEvent) -> Unit) {
    Column {
        Text("Pendientes: ${state.pending.size} · Leídos: ${state.readCount}")
        Text(state.suggestion?.let { "Te toca: $it" } ?: "Sin sugerencia")
        Button(onClick = { onEvent(ShelfEvent.Suggest) }, enabled = state.canSuggest) {
            Text("Sugerir")
        }
        state.suggestion?.let { title ->
            Button(onClick = { onEvent(ShelfEvent.MarkRead(title)) }) {
                Text("Marcar como leído")
            }
        }
    }
}
