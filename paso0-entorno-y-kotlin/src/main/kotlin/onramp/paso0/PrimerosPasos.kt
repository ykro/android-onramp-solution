package onramp.paso0

/**
 * Paso 0: tu primer programa en Kotlin.
 *
 * Córrelo con `./gradlew :paso0-entorno-y-kotlin:run --no-parallel` y verifica con
 * `./gradlew paso0 --no-parallel`.
 */

data class Libro(val titulo: String, val paginas: Int)

val ESTANTE = listOf(
    Libro("Hombres de Maíz", 352),
    Libro("El Señor Presidente", 480),
    Libro("Leyendas de Guatemala", 112),
    Libro("Cien años de soledad", 496),
)

fun main() {
    println("Hola desde Kotlin")
    println(saludar("Ana"))
    println(saludar(null))
    println("Libros largos: ${librosLargos(ESTANTE)}")
    for (minutos in listOf(0, 15, 45)) {
        println("$minutos minutos -> ${nivelDeLectura(minutos)}")
    }
}

fun saludar(nombre: String?): String {
    // `isNullOrBlank` cubre null, texto vacío y solo espacios; el compilador sabe que después
    // de esa revisión `nombre` ya no es null.
    if (nombre.isNullOrBlank()) return "Hola, lector"
    return "Hola, ${nombre.trim()}"
}

fun librosLargos(libros: List<Libro>): List<String> =
    libros.filter { it.paginas > 300 }.map { it.titulo }.sorted()

fun nivelDeLectura(minutos: Int): String = when {
    minutos <= 0 -> "sin lectura"
    minutos < 30 -> "corta"
    else -> "larga"
}
