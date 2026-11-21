package onramp.m3

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

@Serializable
data class BookDto(
    val id: Int,
    val title: String,
    val author: String,
)

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>

    data class HttpError(val code: Int) : ApiResult<Nothing>

    data object ParseError : ApiResult<Nothing>
}

/**
 * Ejercicio 1 (HTTP + JSON tolerante a campos extra):
 * - Haz GET a `"$baseUrl/books"`.
 * - Si `!response.isSuccessful`, devuelve `ApiResult.HttpError(response.code)`.
 * - Decodifica el JSON como `List<BookDto>` ignorando campos desconocidos (`ignoreUnknownKeys = true`).
 * - Si el JSON está roto (`SerializationException`), devuelve `ApiResult.ParseError`.
 */
class BookApiClient(
    private val baseUrl: String,
    private val client: OkHttpClient = OkHttpClient(),
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun fetchBooks(): ApiResult<List<BookDto>> {
        val url = "${baseUrl.trimEnd('/')}/books"
        val request = Request.Builder().url(url).get().build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return ApiResult.HttpError(response.code)
            }

            val body = response.body.string()
            return try {
                val books = json.decodeFromString<List<BookDto>>(body)
                ApiResult.Success(books)
            } catch (_: SerializationException) {
                ApiResult.ParseError
            }
        }
    }
}
