package onramp.m0

import org.junit.Assert.assertEquals
import org.junit.Test

class PrimerosPasosTest {

    @Test
    fun `CA-0-2 saludar usa el nombre y maneja null`() {
        assertEquals("Hola, Ana", saludar("Ana"))
        assertEquals("Hola, María José", saludar("María José"))
        assertEquals("Hola, lector", saludar(null))
        assertEquals("Hola, lector", saludar("   "))
    }

    @Test
    fun `CA-0-3 librosLargos filtra y ordena los titulos`() {
        assertEquals(
            listOf("Cien años de soledad", "El Señor Presidente", "Hombres de Maíz"),
            librosLargos(ESTANTE),
        )
        assertEquals(emptyList<String>(), librosLargos(emptyList()))
    }

    @Test
    fun `CA-0-4 nivelDeLectura cubre los tres rangos`() {
        assertEquals("sin lectura", nivelDeLectura(-5))
        assertEquals("sin lectura", nivelDeLectura(0))
        assertEquals("corta", nivelDeLectura(1))
        assertEquals("corta", nivelDeLectura(29))
        assertEquals("larga", nivelDeLectura(30))
    }
}
