package onramp.paso3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShelfScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `CA-3_3 - ShelfContent dibuja siempre el estado que recibe`() {
        var state by mutableStateOf(ShelfUiState(pending = listOf("A", "B")))
        compose.setContent { ShelfContent(state = state, onEvent = {}) }
        compose.onNodeWithText("Sin sugerencia").assertExists()

        state = state.copy(suggestion = "B")

        compose.onNodeWithText("Te toca: B").assertExists(
            "CA-3.3 Llegó un estado con sugerencia «B» y la pantalla no lo muestra.",
        )
    }

    @Test
    fun `CA-3_3 - ShelfContent avisa los eventos y deshabilita Sugerir sin pendientes`() {
        val events = mutableListOf<ShelfEvent>()
        var state by mutableStateOf(ShelfUiState(pending = listOf("A"), suggestion = "A"))
        compose.setContent { ShelfContent(state = state, onEvent = { events += it }) }

        compose.onNodeWithText("Marcar como leído").assertExists("CA-3.3 Falta el botón «Marcar como leído».")
        compose.onNodeWithText("Marcar como leído").performClick()
        assertEquals("CA-3.3 El botón debe avisar MarkRead del libro sugerido.", listOf(ShelfEvent.MarkRead("A")), events)

        state = ShelfUiState(pending = emptyList(), readCount = 1)
        compose.onNodeWithText("Sugerir").assertIsNotEnabled()
        compose.onNodeWithText("Marcar como leído").assertDoesNotExist()
    }

    @Test
    fun `CA-3_5 - La pantalla completa con su ViewModel`() = runTest {
        val viewModel = ShelfViewModel(InMemoryReadingListRepository(listOf("A", "B"))) { it.first() }
        compose.setContent { ShelfScreen(viewModel) }

        compose.onNodeWithText("Sugerir").performClick()
        advanceUntilIdle()
        compose.onNodeWithText("Te toca: A").assertExists("CA-3.5 Después de «Sugerir» la pantalla debe decir «Te toca: A».")

        compose.onNodeWithText("Marcar como leído").performClick()
        compose.onNodeWithText("Pendientes: 1 · Leídos: 1").assertExists()
        compose.onNodeWithText("Sin sugerencia").assertExists()
    }
}
