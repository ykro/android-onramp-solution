package onramp.paso5

import java.sql.Connection
import java.sql.DriverManager

/** Un libro como llega del API. */
data class BookDto(
    val id: Int,
    val title: String,
    val author: String,
)

data class StoredBook(
    val id: Int,
    val title: String,
    val author: String,
    val favorite: Boolean,
    val localNote: String,
)

/**
 * Base de datos SQLite en memoria para practicar SQL antes de usar Room 3.
 */
class BookDatabase(
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:"),
) : AutoCloseable {
    /**
     * Ejercicio 1 (`CREATE TABLE` + `PRIMARY KEY`):
     * Crea la tabla `books` con columnas:
     * - `id INTEGER PRIMARY KEY`
     * - `title TEXT NOT NULL`
     * - `author TEXT NOT NULL`
     * - `favorite INTEGER NOT NULL DEFAULT 0`
     * - `local_note TEXT NOT NULL DEFAULT ''`
     */
    fun createSchema() {
        connection.createStatement().use { statement ->
            statement.execute(
                """
                CREATE TABLE IF NOT EXISTS books (
                    id INTEGER PRIMARY KEY,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    favorite INTEGER NOT NULL DEFAULT 0,
                    local_note TEXT NOT NULL DEFAULT ''
                )
                """.trimIndent(),
            )
        }
    }

    /**
     * Ejercicio 2 (upsert conservando columnas locales):
     * Inserta los libros de la API. Si el `id` ya existe, actualiza solo `title` y `author`
     * sin borrar `favorite` ni `local_note`.
     */
    fun syncFromApi(books: List<BookDto>) {
        val sql =
            """
            INSERT INTO books (id, title, author)
            VALUES (?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                title = excluded.title,
                author = excluded.author
            """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            for (book in books) {
                statement.setInt(1, book.id)
                statement.setString(2, book.title)
                statement.setString(3, book.author)
                statement.executeUpdate()
            }
        }
    }

    /**
     * Función ya implementada: guarda la marca de favorito y una nota personal en el dispositivo.
     */
    fun setFavoriteAndNote(id: Int, favorite: Boolean, note: String) {
        val sql = "UPDATE books SET favorite = ?, local_note = ? WHERE id = ?"
        connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, if (favorite) 1 else 0)
            statement.setString(2, note)
            statement.setInt(3, id)
            statement.executeUpdate()
        }
    }

    /**
     * Ejercicio 3 (`SELECT ... WHERE ... ORDER BY`):
     * Devuelve los libros con `favorite = 1`, ordenados alfabéticamente por `title` ascendente.
     */
    fun findFavoritesOrderedByTitle(): List<StoredBook> {
        val sql =
            """
            SELECT id, title, author, favorite, local_note
            FROM books
            WHERE favorite = 1
            ORDER BY title ASC
            """.trimIndent()

        val result = mutableListOf<StoredBook>()
        connection.createStatement().use { statement ->
            statement.executeQuery(sql).use { rows ->
                while (rows.next()) {
                    result += StoredBook(
                        id = rows.getInt("id"),
                        title = rows.getString("title"),
                        author = rows.getString("author"),
                        favorite = rows.getInt("favorite") == 1,
                        localNote = rows.getString("local_note"),
                    )
                }
            }
        }
        return result
    }

    override fun close() {
        connection.close()
    }
}
