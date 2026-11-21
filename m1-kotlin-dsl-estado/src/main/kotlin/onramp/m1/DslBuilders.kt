package onramp.m1

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

// --- Parte 2: Cómo funciona `module { single { ... } }` ---

class MiniContainer(
    private val factories: Map<String, () -> Any>,
) {
    private val instances = mutableMapOf<String, Any>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(name: String): T {
        val instance = instances.getOrPut(name) {
            val factory = factories[name]
                ?: throw IllegalArgumentException("No existe una dependencia registrada con el nombre '$name'")
            factory()
        }
        return instance as T
    }
}

class ModuleScope {
    private val factories = mutableMapOf<String, () -> Any>()

    fun single(name: String, factory: () -> Any) {
        factories[name] = factory
    }

    internal fun buildContainer(): MiniContainer = MiniContainer(factories.toMap())
}

/**
 * Ejercicio 2 (DSL de dependencias):
 * Crea un `ModuleScope`, ejecuta `definitions` sobre ese receptor y devuelve el `MiniContainer`.
 */
fun miniModule(definitions: ModuleScope.() -> Unit): MiniContainer {
    val scope = ModuleScope()
    scope.definitions()
    return scope.buildContainer()
}
