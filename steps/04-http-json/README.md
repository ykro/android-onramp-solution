# Paso 4. HTTP y JSON tolerante con `MockWebServer`

![Paso 4. HTTP y JSON tolerante con `MockWebServer`](diagrams/http-json.svg)

Este paso toma unos 40 minutos.

## Antes de empezar

1. Si el backend agrega un campo nuevo, `isbn`, a cada libro, ¿debería dejar de funcionar una app que ya está publicada en Play Store?
2. ¿Qué diferencia hay, para quien usa la app, entre un servidor que responde `500` y uno que responde `200` con un JSON roto?
3. ¿Cómo probarías el caso `500` sin esperar a que el servidor real falle?

## Por qué importa

Una app consume APIs que cambian con el tiempo, agregan campos sin avisar, responden `500` cuando algo falla o devuelven un cuerpo dañado. Una app publicada no se puede actualizar al instante, así que tiene que tolerar esos cambios. [`kotlinx.serialization`](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md) convierte JSON en `data class` y se puede configurar para ignorar campos desconocidos, y [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) levanta un servidor falso dentro del test para simular `200`, `500` o JSON roto en milisegundos y sin internet.

## Lo mínimo que necesitas saber

- **Request y respuesta.** Tu app manda un request (`GET /books`) y el servidor devuelve un código de estado y un cuerpo. Los códigos `2xx` significan éxito, `4xx` que el request estaba mal y `5xx` que falló el servidor.
- **JSON.** El cuerpo suele venir en JSON, por ejemplo `[{"id": 1, "title": "Popol Vuh"}]`. Deserializar es convertir ese texto en objetos de Kotlin.
- **OkHttp y `kotlinx.serialization` (las tres firmas mínimas).** OkHttp es el cliente HTTP que usa Retrofit por debajo. Con estas tres llamadas construyes el request, lees el cuerpo cerrando la respuesta con `.use` y decodificas JSON ignorando campos nuevos:

  ```kotlin
  val request = Request.Builder().url("$baseUrl/books").build()
  val body = client.newCall(request).execute().use { it.body.string() }
  val books = Json { ignoreUnknownKeys = true }.decodeFromString<List<BookDto>>(body)
  ```
- **`execute()` bloquea.** Se queda esperando hasta que llega la respuesta. En el hilo principal de Android eso congela la app, y Android lanza `NetworkOnMainThreadException`. Por eso `fetchBooks` es `suspend` y hace el request dentro de `withContext(dispatcher)`, que lo mueve a un hilo pensado para esperar red y disco (`Dispatchers.IO`). Es el puente entre las corrutinas del Paso 2 y la red.
- **Sin red no hay código de estado.** Si no hay internet o el servidor no contesta, no llega ninguna respuesta. OkHttp lanza una `IOException`, y tu cliente debe convertirla en un resultado en lugar de dejar que la app se caiga.

## Qué vas a hacer

- **Leer.** Abre [BookApiClient.kt](../../paso4-http-json/src/main/kotlin/onramp/paso4/BookApiClient.kt) y [BookApiClientTest.kt](../../paso4-http-json/src/test/kotlin/onramp/paso4/BookApiClientTest.kt). En el test, identifica qué responde `MockWebServer` en cada caso y qué `ApiResult` espera.
- **Debuguear.** Corre `./gradlew paso4 --no-parallel`. Mira qué campos trae el JSON del primer test que `BookDto` no tiene.
- **Escribir.** Implementa `fetchBooks` cumpliendo los cinco puntos del comentario.

## Criterios de aceptación

- `CA-4.1` `fetchBooks()` convierte una respuesta `200` en `ApiResult.Success` aunque el JSON traiga campos extra (`pages`, `isbn`, `edition`).
- `CA-4.2` `fetchBooks()` devuelve `ApiResult.HttpError(500)` ante un `500` y `ApiResult.ParseError` ante JSON inválido.
- `CA-4.3` `fetchBooks()` devuelve `ApiResult.NetworkError` cuando el servidor no contesta, en lugar de lanzar la excepción.

## Al terminar

1. Si el backend cambia `title` por `name` en el JSON, ¿qué `ApiResult` devuelve tu cliente? ¿Te parece correcto?
2. ¿Por qué el cliente recibe `baseUrl` en el constructor en lugar de tener la URL del servidor real escrita adentro?
3. Explica en tres líneas, con tus palabras, qué cambiaste y por qué. Si no puedes, vuelve a la pista 1 antes de seguir.

## Cómo verificar

```bash
./gradlew paso4 --no-parallel
```

Si te trabas, abre la [pista 1](hints/1.md). Cada pista da un poco más que la anterior; la pista 3 trae un esqueleto, nunca la respuesta completa.

## Respuestas de autoevaluación («Al terminar»)

1. Devuelve `ParseError`, porque `title` es obligatorio en `BookDto` y ya no viene. Es correcto, la app no puede mostrar un libro sin título; ignorar campos extra no significa aceptar que falten los obligatorios.
2. Para poder apuntarlo a `MockWebServer` en los tests y a otro servidor en producción sin tocar el código. Es la misma idea que inyectar dependencias con Koin en el curso.
3. Respuesta personal. Debe mencionar correr el request dentro de `withContext(dispatcher)`, revisar el código de estado antes de leer, ignorar campos desconocidos y convertir tanto el JSON roto (`ParseError`) como la falla de red (`NetworkError`) en un resultado sin dejar que la app se caiga.
