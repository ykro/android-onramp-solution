package onramp.paso5

import org.junit.Assert.assertEquals
import org.junit.Test

class BookDatabaseTest {
    @Test
    fun `CA-5_1 - SQLite crea tabla, filtra favoritos y ordena por título`() {
        BookDatabase().use { db ->
            db.createSchema()
            db.createSchema() // la segunda llamada no debe fallar
            db.syncFromApi(
                listOf(
                    BookDto(10, "Refactoring", "Martin Fowler"),
                    BookDto(11, "Designing Data-Intensive Applications", "Martin Kleppmann"),
                    BookDto(12, "Algorithms", "Robert Sedgewick"),
                ),
            )

            db.setFavoriteAndNote(10, favorite = true, note = "Leer capítulo 3")
            db.setFavoriteAndNote(12, favorite = true, note = "Repasar grafos")

            val favorites = db.findFavoritesOrderedByTitle()
            assertEquals(listOf("Algorithms", "Refactoring"), favorites.map { it.title })
            assertEquals(listOf("Repasar grafos", "Leer capítulo 3"), favorites.map { it.localNote })
        }
    }

    @Test
    fun `CA-5_2 - sincronizar de nuevo actualiza el título sin borrar favorito ni nota local`() {
        BookDatabase().use { db ->
            db.createSchema()
            db.syncFromApi(
                listOf(
                    BookDto(1, "Kotlin Basics", "Ana López"),
                ),
            )
            db.setFavoriteAndNote(1, favorite = true, note = "Mi libro favorito")

            // La API corrige el título y vuelve a sincronizar el mismo id.
            db.syncFromApi(
                listOf(
                    BookDto(1, "Kotlin Moderno", "Ana López"),
                ),
            )

            val favorites = db.findFavoritesOrderedByTitle()
            assertEquals(
                "Al sincronizar de nuevo se borró favorite o local_note. Revisa qué hace tu SQL cuando el id ya existe.",
                listOf(
                    StoredBook(
                        id = 1,
                        title = "Kotlin Moderno",
                        author = "Ana López",
                        favorite = true,
                        localNote = "Mi libro favorito",
                    ),
                ),
                favorites,
            )
        }
    }
}
