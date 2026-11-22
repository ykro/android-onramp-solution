package onramp.paso3

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.koinApplication

@OptIn(ExperimentalCoroutinesApi::class)
class ShelfViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `CA-3_1 - Suggest espera sin bloquear y usa el BookPicker que recibe el ViewModel`() = runTest {
        var calls = 0
        val alwaysLast = BookPicker { options ->
            calls++
            options.last()
        }
        val viewModel = ShelfViewModel(InMemoryReadingListRepository(listOf("A", "B", "C")), alwaysLast)

        repeat(5) { viewModel.onEvent(ShelfEvent.Suggest) }
        advanceTimeBy(ShelfViewModel.SUGGEST_DELAY_MS - 1)
        runCurrent()
        assertNull(
            "CA-3.1 La sugerencia debe llegar después de SUGGEST_DELAY_MS, no al instante. " +
                "Espera con delay dentro de viewModelScope.launch.",
            viewModel.uiState.value.suggestion,
        )

        advanceUntilIdle()
        assertEquals(
            "CA-3.1 El BookPicker que recibió el ViewModel debía elegir 5 veces y eligió $calls. " +
                "¿De dónde sale hoy el libro sugerido?",
            5,
            calls,
        )
        assertEquals("CA-3.1 Con un BookPicker que elige el último, la sugerencia es «C».", "C", viewModel.uiState.value.suggestion)
    }

    @Test
    fun `CA-3_2 - MarkRead produce un estado nuevo y solo el ViewModel escribe el estado`() = runTest {
        val repository = InMemoryReadingListRepository(listOf("A", "B"))
        val viewModel = ShelfViewModel(repository) { it.first() }
        viewModel.onEvent(ShelfEvent.Suggest)
        advanceUntilIdle()
        val before = viewModel.uiState.value

        viewModel.onEvent(ShelfEvent.MarkRead("A"))
        val after = viewModel.uiState.value

        assertNotSame("CA-3.2 Marcar como leído debe producir un estado nuevo.", before, after)
        assertEquals("CA-3.2 El estado anterior no debe cambiar.", listOf("A", "B"), before.pending)
        assertEquals("CA-3.2 «A» ya no está pendiente.", listOf("B"), after.pending)
        assertEquals("CA-3.2 readCount suma 1.", 1, after.readCount)
        assertNull("CA-3.2 «A» era la sugerencia, así que la sugerencia queda en null.", after.suggestion)
        assertEquals("CA-3.2 El repositorio también se entera.", listOf("B"), repository.pending())

        assertFalse(
            "CA-3.2 Desde afuera, uiState no debe dejarse escribir: la pantalla lee, el ViewModel decide.",
            (viewModel.uiState as Any) is MutableStateFlow<*>,
        )
    }

    @Test
    fun `CA-3_4 - Koin arma el ViewModel con un repositorio compartido`() {
        val koin = koinApplication { modules(shelfModule) }.koin

        val first = koin.get<ShelfViewModel>()
        val second = koin.get<ShelfViewModel>()

        assertNotSame("CA-3.4 Cada pantalla recibe su propio ViewModel.", first, second)
        assertSame(
            "CA-3.4 El repositorio es uno solo para toda la app.",
            koin.get<ReadingListRepository>(),
            koin.get<ReadingListRepository>(),
        )
        assertEquals("CA-3.4 El ViewModel arranca con los libros de SEED_BOOKS.", SEED_BOOKS, first.uiState.value.pending)
    }
}
