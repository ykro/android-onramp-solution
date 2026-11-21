# Módulo 1: Kotlin DSL y Estado Inmutable (15 minutos)

En Jetpack Compose y Koin verás bloques como estos todo el tiempo:

```kotlin
Column {
    Text("Hola")
}

module {
    single { OrderRepository() }
}
```

No hay magia oculta. Ambos usan dos reglas normales de Kotlin:

1. **Lambda como último parámetro:** si el último parámetro de una función es una lambda, puedes escribir las llaves fuera de los paréntesis (y omitir los paréntesis si no hay más argumentos).
2. **Función con receptor (`Scope.() -> Unit`):** el bloque recibe un objeto implícito (`this`) del tipo `Scope`. Por eso dentro de `Column { ... }` puedes llamar `Text(...)` directamente y dentro de `module { ... }` puedes llamar `single { ... }`.

También verás el error más común al conectar un `StateFlow` con una pantalla:

- `StateFlow` evita redibujar la pantalla si el estado nuevo es igual al anterior (`anterior == nuevo`).
- Si guardas una `MutableList` dentro de una `data class` y solo llamas `lista.add(...)`, el estado anterior y el estado nuevo apuntan **a la misma lista en memoria**. Al compararla consigo misma, `StateFlow` concluye que nada cambió y nunca avisa a la pantalla.
- La solución es usar una `List` de solo lectura y crear un estado nuevo con `copy(items = current.items + newItem)`.

## Ejercicios

1. Abre `src/main/kotlin/onramp/m1/DslBuilders.kt` y completa `miniColumn` y `miniModule`.
2. Abre `src/main/kotlin/onramp/m1/OrderState.kt`:
   - Completa `renderOrderBanner` usando `when` sin `else` sobre `OrderBannerState`.
   - Corrige `OrderBoard` para que use una lista de solo lectura (`List<String>`) y `copy(...)` dentro de `_state.update { ... }`, de modo que `StateFlow` emita cada pedido nuevo.

## Cómo probar este módulo

```bash
./gradlew :m1-kotlin-dsl-estado:test --no-parallel
```
