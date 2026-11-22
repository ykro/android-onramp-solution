package onramp.paso1

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// --- Parte 2: Estados de pantalla con sealed interface ---

sealed interface OrderBannerState {
    data object Empty : OrderBannerState

    data class Preparing(val count: Int) : OrderBannerState

    data class Ready(val ticketCode: String) : OrderBannerState

    data class Failed(val reason: String) : OrderBannerState
}

/**
 * Ejercicio 2 (`sealed interface` + `when` sin `else`):
 * Devuelve el texto exacto para cada estado:
 * - Empty -> "Sin pedidos activos"
 * - Preparing -> "Preparando <count> pedidos" (o "Preparando 1 pedido" si count == 1)
 * - Ready -> "Pedido <ticketCode> listo"
 * - Failed -> "Error: <reason>"
 */
fun renderOrderBanner(state: OrderBannerState): String =
    when (state) {
        OrderBannerState.Empty -> "Sin pedidos activos"
        is OrderBannerState.Preparing ->
            if (state.count == 1) "Preparando 1 pedido" else "Preparando ${state.count} pedidos"
        is OrderBannerState.Ready -> "Pedido ${state.ticketCode} listo"
        is OrderBannerState.Failed -> "Error: ${state.reason}"
    }

// --- Parte 3: Por qué una MutableList rompe StateFlow ---

data class OrderBoardState(
    val orders: List<String> = emptyList(),
)

/**
 * Ejercicio 3 (inmutabilidad en StateFlow):
 * Agrega pedidos y marca entregas emitiendo siempre un estado nuevo con una lista inmutable.
 */
class OrderBoard {
    private val _state = MutableStateFlow(OrderBoardState())
    val state: StateFlow<OrderBoardState> = _state.asStateFlow()

    fun addOrder(item: String) {
        val clean = item.trim()
        if (clean.isEmpty()) return
        _state.update { current ->
            current.copy(orders = current.orders + clean)
        }
    }

    fun removeFirstOrder() {
        _state.update { current ->
            if (current.orders.isEmpty()) current else current.copy(orders = current.orders.drop(1))
        }
    }
}
