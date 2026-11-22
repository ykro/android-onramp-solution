package onramp.paso3

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Ejercicio 4 (Koin): las dependencias se declaran por interfaz. `single` crea una sola
 * instancia para toda la app; `viewModelOf` crea un ViewModel nuevo por pantalla y le
 * pasa lo que pide su constructor.
 */
val shelfModule = module {
    single<ReadingListRepository> { InMemoryReadingListRepository(SEED_BOOKS) }
    single<BookPicker> { RandomBookPicker() }
    viewModelOf(::ShelfViewModel)
}
