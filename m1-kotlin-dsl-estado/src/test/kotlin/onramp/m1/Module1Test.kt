package onramp.m1

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class Module1Test {
    @Test
    fun `ejercicio 1 - miniColumn recolecta textos con sintaxis de bloque`() {
        val rendered = miniColumn {
            text("Café americano")
            text("Pan dulce")
            text("Total: Q25")
        }

        assertEquals(
            listOf("Café americano", "Pan dulce", "Total: Q25"),
            rendered,
        )
    }

    @Test
    fun `ejercicio 2 - miniModule registra dependencias singleton`() {
        var creations = 0
        val container = miniModule {
            single("clock") {
                creations += 1
                "Reloj#$creations"
            }
        }

        val first: String = container.get("clock")
        val second: String = container.get("clock")

        assertEquals("Reloj#1", first)
        assertSame(first, second)
        assertEquals(1, creations)
    }

    @Test
    fun `ejercicio 3 - renderOrderBanner cubre todos los estados de la sealed interface`() {
        assertEquals("Sin pedidos activos", renderOrderBanner(OrderBannerState.Empty))
        assertEquals("Preparando 1 pedido", renderOrderBanner(OrderBannerState.Preparing(1)))
        assertEquals("Preparando 3 pedidos", renderOrderBanner(OrderBannerState.Preparing(3)))
        assertEquals("Pedido A-12 listo", renderOrderBanner(OrderBannerState.Ready("A-12")))
        assertEquals("Error: Sin conexión", renderOrderBanner(OrderBannerState.Failed("Sin conexión")))
    }

    @Test
    fun `ejercicio 4 - StateFlow emite cada vez que se agrega o quita un pedido`() = runTest {
        val board = OrderBoard()

        board.state.test {
            assertEquals(emptyList<String>(), awaitItem().orders)

            board.addOrder("Capuchino")
            assertEquals(listOf("Capuchino"), awaitItem().orders)

            board.addOrder("Té chai")
            assertEquals(listOf("Capuchino", "Té chai"), awaitItem().orders)

            board.removeFirstOrder()
            assertEquals(listOf("Té chai"), awaitItem().orders)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
