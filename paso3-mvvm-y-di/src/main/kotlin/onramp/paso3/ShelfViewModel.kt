package onramp.paso3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Todo lo que la pantalla necesita para dibujarse. La pantalla no guarda nada más. */
data class ShelfUiState(
    val pending: List<String> = emptyList(),
    val suggestion: String? = null,
    val readCount: Int = 0,
) {
    val canSuggest: Boolean get() = pending.isNotEmpty()
}

/** Lo que la persona puede hacer en la pantalla. La pantalla solo avisa; decide el ViewModel. */
sealed interface ShelfEvent {
    data object Suggest : ShelfEvent

    data class MarkRead(val title: String) : ShelfEvent
}

class ShelfViewModel(
    private val repository: ReadingListRepository,
    private val picker: BookPicker,
) : ViewModel() {
    // Ejercicio 2, parte A: el MutableStateFlow es privado y afuera solo se expone de lectura.
    private val _uiState = MutableStateFlow(ShelfUiState(pending = repository.pending()))
    val uiState: StateFlow<ShelfUiState> = _uiState.asStateFlow()

    fun onEvent(event: ShelfEvent) {
        when (event) {
            ShelfEvent.Suggest -> suggest()
            is ShelfEvent.MarkRead -> markRead(event.title)
        }
    }

    // Ejercicio 1: la elección pasa por el BookPicker inyectado, no por `random()` directo,
    // y llega después de un `delay` dentro de viewModelScope, que Android cancela al cerrar la pantalla.
    private fun suggest() {
        val options = _uiState.value.pending
        if (options.isEmpty()) return
        viewModelScope.launch {
            delay(SUGGEST_DELAY_MS)
            val choice = picker.pick(options)
            _uiState.update { it.copy(suggestion = choice) }
        }
    }

    // Ejercicio 2, parte B: el repositorio se entera y el estado nuevo sale de copy.
    private fun markRead(title: String) {
        repository.markRead(title)
        _uiState.update { state ->
            state.copy(
                pending = state.pending - title,
                readCount = state.readCount + 1,
                suggestion = state.suggestion.takeIf { it != title },
            )
        }
    }

    companion object {
        /** Lo que espera el ViewModel antes de mostrar la sugerencia. */
        const val SUGGEST_DELAY_MS = 500L
    }
}
