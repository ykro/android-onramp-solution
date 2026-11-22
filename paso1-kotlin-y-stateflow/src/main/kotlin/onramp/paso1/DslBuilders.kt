package onramp.paso1

// --- Parte 1: Cómo funciona `Column { Text("Hola") }` ---

class ColumnScope {
    private val lines = mutableListOf<String>()

    fun text(value: String) {
        lines += value
    }

    internal fun buildLines(): List<String> = lines.toList()
}

/**
 * Ejercicio 1 (lambda con receptor):
 * Crea un `ColumnScope`, ejecuta `content` sobre ese receptor y devuelve las líneas acumuladas.
 */
fun miniColumn(content: ColumnScope.() -> Unit): List<String> {
    val scope = ColumnScope()
    scope.content()
    return scope.buildLines()
}
