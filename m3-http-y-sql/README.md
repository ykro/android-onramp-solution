# Módulo 3: HTTP, JSON y SQLite (20 minutos)

En la Unidad 2 de `curso3-android-compose` tu app descarga datos de una API y los guarda en una base de datos local SQLite usando Room 3. En este módulo practicas las dos piezas por separado, en pruebas rápidas sin internet ni emulador.

## Parte A: JSON y errores HTTP con `MockWebServer`

- Las APIs reales suelen agregar campos nuevos sin avisar. Con `Json { ignoreUnknownKeys = true }`, `kotlinx.serialization` ignora los campos extra en lugar de fallar.
- Una respuesta `404` o `500` no lanza una excepción automática en `OkHttp`: debes revisar `response.isSuccessful` y devolver un resultado controlado (`ApiResult.HttpError`).
- Si el cuerpo llega dañado y no es JSON válido, atrapa `SerializationException` y devuelve `ApiResult.ParseError`.

## Parte B: SQL en SQLite y cómo conservar columnas locales

En SQLite practicas tres operaciones esenciales:

1. **`CREATE TABLE` con `PRIMARY KEY`:** define las columnas y asegura que cada libro tenga un `id` único.
2. **`SELECT ... WHERE ... ORDER BY`:** filtra solo los favoritos (`favorite = 1`) y ordénalos alfabéticamente por `title`.
3. **Actualizar sin borrar datos locales:** `INSERT OR REPLACE` borra la fila vieja y crea una fila nueva desde cero, así que borra `favorite` y `local_note`. Para actualizar solo los campos que vienen de la API y conservar lo que el usuario guardó en su teléfono, usa:

```sql
INSERT INTO books (id, title, author)
VALUES (?, ?, ?)
ON CONFLICT(id) DO UPDATE SET
    title = excluded.title,
    author = excluded.author
```

### Cómo se traduce esto a Room 3 en la Unidad 2

En Room 3 escribirás las mismas ideas con anotaciones sobre clases de Kotlin:

- `CREATE TABLE books (id INTEGER PRIMARY KEY, ...)` -> `@Entity(tableName = "books")` con `@PrimaryKey val id: Int`.
- `SELECT ... WHERE favorite = 1 ORDER BY title ASC` -> `@Query("SELECT ... WHERE favorite = 1 ORDER BY title ASC")` dentro de una interfaz `@Dao`.
- `ON CONFLICT(id) DO UPDATE SET ...` -> un `@Query` propio o una combinación de lectura local + `@Upsert` para no perder los campos locales al sincronizar.

## Ejercicios

1. Abre `src/main/kotlin/onramp/m3/BookApiClient.kt` y completa `fetchBooks()`.
2. Abre `src/main/kotlin/onramp/m3/BookDatabase.kt` y completa `createSchema()`, corrige `syncFromApi()` (cambia `INSERT OR REPLACE` por `ON CONFLICT(id) DO UPDATE`) y completa `findFavoritesOrderedByTitle()`.

## Cómo probar este módulo

```bash
./gradlew :m3-http-y-sql:test --no-parallel
```
