package onramp.m3

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test

class BookApiClientTest {
    @Test
    fun `ejercicio 1a - BookApiClient lee JSON e ignora campos extra`() = runTest {
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
    fun `ejercicio 1b - BookApiClient devuelve HttpError ante 500 y ParseError ante JSON inválido`() = runTest {
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
    fun `ejercicio 1c - BookApiClient devuelve NetworkError si el servidor no contesta`() = runTest {
        val server = MockWebServer()
        server.start()
        val baseUrl = server.url("/").toString()
        server.close()

        val result = BookApiClient(baseUrl).fetchBooks()

        assertEquals(
            "Sin servidor no hay respuesta ni código de estado. Convierte la IOException en NetworkError.",
            ApiResult.NetworkError,
            result,
        )
    }
}
