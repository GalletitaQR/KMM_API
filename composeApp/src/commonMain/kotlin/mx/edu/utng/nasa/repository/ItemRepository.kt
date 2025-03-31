package mx.edu.utng.nasa.repository

import mx.edu.utng.nasa.models.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ItemRepository {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _selectedItem = MutableStateFlow<Item?>(null)
    val selectedItem: StateFlow<Item?> = _selectedItem

    init {
        // Datos de ejemplo
        _items.value = listOf(
            Item("1", "Lanzamiento Apollo", "Descripción del lanzamiento de Apollo", null),
            Item("2", "Hubble Telescope", "Imágenes del telescopio espacial Hubble", null),
            Item("3", "Mars Rover", "Exploración de Marte", null)
        )
    }

    fun selectItem(id: String) {
        _selectedItem.value = _items.value.find { it.id == id }
    }

    fun clearSelection() {
        _selectedItem.value = null
    }
}