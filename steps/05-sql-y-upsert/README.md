# Paso 5. SQLite, `ON CONFLICT DO UPDATE` y puente hacia Room 3

![Paso 5. SQLite, `ON CONFLICT DO UPDATE` y puente hacia Room 3](diagrams/sql-upsert.svg)

Este paso toma unos 45 minutos.

## Antes de empezar

1. Si guardas los libros de la app en una lista en memoria, ¿qué pasa con ellos cuando la persona cierra la app?
2. La app marca un libro como favorito y le pone una nota. Después sincroniza el catálogo con el API, que no sabe nada de favoritos. ¿Qué debería pasar con la marca y la nota?
3. ¿Qué diferencia hay entre borrar una fila y volver a crearla, y modificar solo algunas de sus columnas?

## Por qué importa

Las apps del curso guardan datos en el teléfono con [Room 3](https://developer.android.com/training/data-storage/room), que corre sobre SQLite. Room genera parte del SQL, pero tú sigues escribiendo consultas y decidiendo cómo se sincronizan los datos del API con los del teléfono. Un error clásico es sincronizar con `INSERT OR REPLACE`, que borra la fila vieja y crea una nueva, así que se pierden las columnas que solo existen en el teléfono, como un favorito o una nota. El [upsert de SQLite](https://www.sqlite.org/lang_upsert.html) actualiza solo las columnas que vienen del API.

## Lo mínimo que necesitas saber

Si nunca has usado una base de datos, este recuadro alcanza para el paso.

- **Tabla.** Guarda datos en filas y columnas, como una hoja de cálculo donde cada columna tiene un tipo. Un `CREATE TABLE IF NOT EXISTS` concreto se ve así:

  ```sql
  CREATE TABLE IF NOT EXISTS books (
      id INTEGER PRIMARY KEY,
      title TEXT NOT NULL,
      author TEXT NOT NULL,
      favorite INTEGER NOT NULL DEFAULT 0,
      local_note TEXT NOT NULL DEFAULT ''
  )
  ```

- **Llave primaria.** Una columna marcada `PRIMARY KEY` identifica cada fila; no puede haber dos filas con el mismo valor. Aquí es el `id` del libro.
- **Restricciones.** `NOT NULL` obliga a que la columna tenga valor y `DEFAULT 0` define el valor cuando no lo mandas.
- **Leer.** `SELECT columnas FROM tabla WHERE condición ORDER BY columna ASC` trae las filas que cumplen la condición, ordenadas.
- **Escribir y upsert.** `INSERT INTO tabla (columnas) VALUES (?, ?)` agrega una fila y `UPDATE tabla SET columna = ? WHERE id = ?` cambia filas existentes. Si insertas un `id` que ya existe, SQLite reporta un conflicto; con `ON CONFLICT(id) DO UPDATE` decides qué columnas actualizar y conservas el resto:

  ```sql
  INSERT INTO books (id, title, author) VALUES (?, ?, ?)
  ON CONFLICT(id) DO UPDATE SET
      title = excluded.title,
      author = excluded.author
  ```

  En SQLite, `excluded` representa la fila nueva que intentaste insertar y chocó con una fila que ya tenía ese `id`.
- **Parámetros `?`.** Los valores se pasan aparte con `statement.setInt(1, ...)` y nunca se pegan dentro del texto SQL. Así un título con comillas no rompe la consulta ni permite inyección SQL.
- **JDBC.** Es la forma estándar de hablar con bases de datos desde la JVM. Para ejecutar un `SELECT` sin parámetros y recorrer el `ResultSet` fila por fila:

  ```kotlin
  connection.createStatement().use { statement ->
      statement.executeQuery("SELECT id, title FROM books").use { rows ->
          while (rows.next()) {
              val id = rows.getInt("id")
              val title = rows.getString("title")
          }
      }
  }
  ```

### Equivalencias con Room 3

| En este paso | En Room 3, en el curso |
| --- | --- |
| `CREATE TABLE books (id INTEGER PRIMARY KEY, ...)` | Una `data class` con `@Entity(tableName = "books")` y `@PrimaryKey val id: Int` |
| Un `SELECT ... WHERE ... ORDER BY ...` | Un método con `@Query("...")` dentro de una interfaz `@Dao` |
| `INSERT ... ON CONFLICT(id) DO UPDATE SET ...` | Un `@Query` propio o una actualización parcial que conserva las columnas locales |

## Qué vas a hacer

- **Leer.** Abre [BookDatabase.kt](../../paso5-sql-y-upsert/src/main/kotlin/onramp/paso5/BookDatabase.kt) y [BookDatabaseTest.kt](../../paso5-sql-y-upsert/src/test/kotlin/onramp/paso5/BookDatabaseTest.kt). En el segundo test, anota en orden qué se sincroniza, qué se marca como favorito y qué se espera al final.
- **Debuguear.** Corre `./gradlew paso5 --no-parallel`. Explica con tus palabras por qué `INSERT OR REPLACE` borra el favorito aunque la consulta no menciona esa columna. La [documentación de REPLACE](https://www.sqlite.org/lang_conflict.html) lo dice en una línea.
- **Escribir.** Implementa `createSchema` y `findFavoritesOrderedByTitle`, y cambia el SQL de `syncFromApi`.

## Criterios de aceptación

- `CA-5.1` `createSchema()` crea la tabla y `findFavoritesOrderedByTitle()` devuelve solo los favoritos, ordenados alfabéticamente por título.
- `CA-5.2` Sincronizar de nuevo un libro existente actualiza `title` y `author` sin borrar `favorite` ni `local_note`.

## Al terminar

1. Si el API deja de mandar un libro que ya estaba en el teléfono, ¿qué hace hoy `syncFromApi`? ¿Debería borrarlo?
2. En Room, ¿dónde escribirías la consulta de favoritos? Usa la tabla de equivalencias de este paso.
3. Explica en tres líneas, con tus palabras, qué cambiaste y por qué. Si no puedes, vuelve a la pista 1 antes de seguir.

## Cómo verificar

```bash
./gradlew paso5 --no-parallel
```

Si te trabas, abre la [pista 1](hints/1.md). Cada pista da un poco más que la anterior; la pista 3 trae un esqueleto, nunca la respuesta completa.

## Respuestas de autoevaluación («Al terminar»)

1. No hace nada con él, el libro se queda en el teléfono. Borrarlo o no es una decisión de producto; si la persona lo marcó como favorito, quizás conviene conservarlo y marcarlo como no disponible.
2. En una interfaz anotada con `@Dao`, como un método con `@Query("SELECT ... WHERE favorite = 1 ORDER BY title ASC")`. Room revisa esa consulta al compilar.
3. Respuesta personal. Debe mencionar la llave primaria, que `INSERT OR REPLACE` borra la fila y que `ON CONFLICT DO UPDATE` actualiza solo las columnas que vienen del API.
