package onramp.paso3

import kotlin.random.Random

// Capa de datos del estante. Este archivo ya está bien escrito: léelo, no lo cambies.

/** Elige un libro de una lista. Es una dependencia: quien la usa no sabe cómo elige. */
fun interface BookPicker {
    fun pick(options: List<String>): String
}

/** La versión de la app real: elige al azar. En los tests se usa otra que siempre elige lo mismo. */
class RandomBookPicker(private val random: Random = Random.Default) : BookPicker {
    override fun pick(options: List<String>): String = options.random(random)
}

/** De dónde salen los libros pendientes. En el curso, esta interfaz la implementa Room o Firestore. */
interface ReadingListRepository {
    fun pending(): List<String>

    fun markRead(title: String)
}

class InMemoryReadingListRepository(initial: List<String>) : ReadingListRepository {
    private val titles = initial.toMutableList()

    override fun pending(): List<String> = titles.toList()

    override fun markRead(title: String) {
        titles.remove(title)
    }
}

val SEED_BOOKS = listOf("Cien años de soledad", "El principito", "Rayuela")
