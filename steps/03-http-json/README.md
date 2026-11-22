# Paso 3. HTTP y JSON tolerante con `MockWebServer`

![Paso 3. HTTP y JSON tolerante con `MockWebServer`](diagrams/http-json.svg)

Este paso toma unos 20 minutos.

## Antes de empezar

1. Si el backend agrega un campo nuevo, `isbn`, a cada libro, ¿debería dejar de funcionar una app que ya está publicada en Play Store?
2. ¿Qué diferencia hay, para quien usa la app, entre un servidor que responde `500` y uno que responde `200` con un JSON roto?
3. ¿Cómo probarías el caso `500` sin esperar a que el servidor real falle?

## Por qué importa

Una app consume APIs que cambian con el tiempo, agregan campos sin avisar, responden `500` cuando algo falla o devuelven un cuerpo dañado. Una app publicada no se puede actualizar al instante, así que tiene que tolerar esos cambios. [`kotlinx.serialization`](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md) convierte JSON en `data class` y se puede configurar para ignorar campos desconocidos, y [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) levanta un servidor falso dentro del test para simular `200`, `500` o JSON roto en milisegundos y sin internet.

## Lo mínimo que necesitas saber

- **Request y respuesta.** Tu app manda un request (`GET /books`) y el servidor devuelve un código de estado y un cuerpo. Los códigos `2xx` significan éxito, `4xx` que el request estaba mal y `5xx` que falló el servidor.
- **JSON.** El cuerpo suele venir en JSON, por ejemplo `[{"id": 1, "title": "Popol Vuh"}]`. Deserializar es convertir ese texto en objetos de Kotlin.
- **OkHttp.** Es el cliente HTTP que usa Retrofit por debajo. Armas un `Request`, lo ejecutas con `client.newCall(request).execute()` y lees la respuesta.

## Qué vas a hacer

- **Leer.** Abre [BookApiClient.kt](../../m3-http-y-sql/src/main/kotlin/onramp/m3/BookApiClient.kt) y [BookApiClientTest.kt](../../m3-http-y-sql/src/test/kotlin/onramp/m3/BookApiClientTest.kt). En el test, identifica qué responde `MockWebServer` en cada caso y qué `ApiResult` espera.
- **Debuguear.** Corre `./gradlew paso3 --no-parallel`. Mira qué campos trae el JSON del primer test que `BookDto` no tiene.
- **Escribir.** Implementa `fetchBooks` cumpliendo los cuatro puntos del comentario.

## Criterios de aceptación

- `CA-3.1` `fetchBooks()` convierte una respuesta `200` en `ApiResult.Success` aunque el JSON traiga campos extra (`pages`, `isbn`, `edition`).
- `CA-3.2` `fetchBooks()` devuelve `ApiResult.HttpError(500)` ante un `500` y `ApiResult.ParseError` ante JSON inválido.

## Al terminar

1. Si el backend cambia `title` por `name` en el JSON, ¿qué `ApiResult` devuelve tu cliente? ¿Te parece correcto?
2. Si el servidor tarda 30 segundos en responder, ¿qué pasa hoy con `fetchBooks()`? ¿Dónde configurarías un límite?
3. ¿Por qué el cliente recibe `baseUrl` en el constructor en lugar de tener la URL del servidor real escrita adentro?
4. Explica en tres líneas, con tus palabras, qué cambiaste y por qué. Si no puedes, vuelve a la pista 1 antes de seguir.

## Cómo verificar

```bash
./gradlew paso3 --no-parallel
```

Si te trabas, abre la [pista 1](hints/1.md). Cada pista da un poco más que la anterior; la pista 3 trae un esqueleto, nunca la respuesta completa.

## Respuestas de autoevaluación

1. Devuelve `ParseError`, porque `title` es obligatorio en `BookDto` y ya no viene. Es correcto, la app no puede mostrar un libro sin título; ignorar campos extra no significa aceptar que falten los obligatorios.
2. Hoy espera lo que tenga configurado OkHttp por defecto (10 segundos para conectar y leer). El límite se configura al crear el `OkHttpClient` con `readTimeout` y `connectTimeout`, y conviene convertir ese error en un `ApiResult` propio.
3. Para poder apuntarlo a `MockWebServer` en los tests y a otro servidor en producción sin tocar el código. Es la misma idea que inyectar dependencias con Koin en el curso.
4. Respuesta personal. Debe mencionar revisar el código de estado antes de leer, ignorar campos desconocidos y convertir el JSON roto en un resultado en lugar de dejar que la app falle.
