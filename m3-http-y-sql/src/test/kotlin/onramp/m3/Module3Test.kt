package onramp.m3

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test

class Module3Test {
    @Test
    fun `ejercicio 1a - BookApiClient lee JSON e ignora campos extra`() {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(
                MockResponse(
                    code = 200,
                    body = """
                        [
                          {"id": 1, "title": "Clean Code", "author": "Robert Martin", "pages": 464, "isbn": "9780132350884"},
                          {"id": 2, "title": "Kotlin in Action", "author": "Dmitry Jemerov", "edition": 2}
                        ]
                    """.trimIndent(),
                ),
            )

            val client = BookApiClient(server.url("/").toString())
            val result = client.fetchBooks()

            assertEquals(
                ApiResult.Success(
                    listOf(
                        BookDto(1, "Clean Code", "Robert Martin"),
                        BookDto(2, "Kotlin in Action", "Dmitry Jemerov"),
                    ),
                ),
                result,
            )
        } finally {
            server.close()
        }
    }

    @Test
    fun `ejercicio 1b - BookApiClient devuelve HttpError ante 500 y ParseError ante JSON invalido`() {
        val server = MockWebServer()
        server.start()
        try {
            server.enqueue(MockResponse(code = 500, body = """{"error":"fallo interno"}"""))
            server.enqueue(MockResponse(code = 200, body = """[{"id": "no-es-numero"}]"""))

            val client = BookApiClient(server.url("/").toString())

            assertEquals(ApiResult.HttpError(500), client.fetchBooks())
            assertEquals(ApiResult.ParseError, client.fetchBooks())
        } finally {
            server.close()
        }
    }

    @Test
    fun `ejercicio 2 y 4 - SQLite crea tabla, filtra favoritos y ordena por titulo`() {
        BookDatabase().use { db ->
            db.createSchema()
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
    fun `ejercicio 3 - sincronizar de nuevo actualiza el titulo sin borrar favorito ni nota local`() {
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
                "Al sincronizar de nuevo se borró favorite o local_note. Usa ON CONFLICT(id) DO UPDATE en lugar de INSERT OR REPLACE.",
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
