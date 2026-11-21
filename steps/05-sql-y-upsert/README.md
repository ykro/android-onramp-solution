# Paso 5. SQLite, `ON CONFLICT DO UPDATE` y puente hacia Room 3

![SQLite ON CONFLICT DO UPDATE frente a INSERT OR REPLACE](diagrams/sql-upsert.svg)

## Por qué importa

En Android, [Room 3](https://developer.android.com/training/data-storage/room) corre directamente sobre SQLite. Dominar tres operaciones básicas de SQLite —[`CREATE TABLE` con `PRIMARY KEY`](https://www.sqlite.org/lang_createtable.html), [`SELECT ... WHERE ... ORDER BY`](https://www.sqlite.org/lang_select.html) y [`ON CONFLICT(id) DO UPDATE`](https://www.sqlite.org/lang_upsert.html)— evita un error clásico de sincronización: usar `INSERT OR REPLACE`, que elimina la fila existente y reinicia `favorite` y `local_note` cada vez que la API actualiza el catálogo.

## Qué se construye

En [BookDatabase.kt](../../m3-http-y-sql/src/main/kotlin/onramp/m3/BookDatabase.kt):

- `createSchema()`: ejecuta `CREATE TABLE IF NOT EXISTS books` con `id INTEGER PRIMARY KEY`, `title TEXT NOT NULL`, `author TEXT NOT NULL`, `favorite INTEGER NOT NULL DEFAULT 0` y `local_note TEXT NOT NULL DEFAULT ''`.
- `syncFromApi()`: reemplaza `INSERT OR REPLACE` por:

```sql
INSERT INTO books (id, title, author)
VALUES (?, ?, ?)
ON CONFLICT(id) DO UPDATE SET
    title = excluded.title,
    author = excluded.author
```

- `findFavoritesOrderedByTitle()`: ejecuta `SELECT id, title, author, favorite, local_note FROM books WHERE favorite = 1 ORDER BY title ASC` y mapea cada fila a `StoredBook`.

### Equivalencia directa con Room 3 en `android-course`

- `CREATE TABLE books (id INTEGER PRIMARY KEY, ...)` -> `@Entity(tableName = "books")` con `@PrimaryKey val id: Int`.
- `SELECT ... WHERE favorite = 1 ORDER BY title ASC` -> `@Query("...")` dentro de una interfaz `@Dao`.
- `ON CONFLICT(id) DO UPDATE SET ...` -> un `@Query` propio o actualización parcial para conservar columnas locales al sincronizar.

## Criterios de aceptación

- `CA-5.1` [auto] `BookDatabase.createSchema()` y `findFavoritesOrderedByTitle()` crean la tabla, filtran `favorite = 1` y ordenan alfabéticamente por `title`.
- `CA-5.2` [auto] `BookDatabase.syncFromApi()` actualiza `title` y `author` sin borrar `favorite` ni `local_note`, verificado en [BookDatabaseTest.kt](../../m3-http-y-sql/src/test/kotlin/onramp/m3/BookDatabaseTest.kt).

## Cómo verificar

Este paso es completamente independiente (no necesita que hayas resuelto el Paso 4). Desde la raíz del repositorio ejecuta sus 2 pruebas y luego la verificación completa:

```bash
./gradlew paso5 --no-parallel
./gradlew ready --no-parallel
```

Cuando `./gradlew ready --no-parallel` termine con 11/11 pruebas en verde, estás listo para iniciar [ykro/android-course](https://github.com/ykro/android-course).
