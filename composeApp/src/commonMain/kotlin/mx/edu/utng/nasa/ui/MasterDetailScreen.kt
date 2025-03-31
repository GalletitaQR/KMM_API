// Archivo: commonMain/kotlin/mx.edu.utng.nasa/ui/MasterDetailScreen.kt
package mx.edu.utng.nasa.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.utng.nasa.models.Item
import mx.edu.utng.nasa.repository.ItemRepository
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MasterDetailScreen(repository: ItemRepository) {
    val items by repository.items.collectAsState()
    val selectedItem by repository.selectedItem.collectAsState()

    // Detectar si estamos en pantalla pequeña (móvil) o grande (tablet)
    val isCompactScreen = remember { mutableStateOf(true) } // Simplificado, ideal sería detectar tamaño real

    if (isCompactScreen.value) {
        // Vista para móviles - cambia entre lista y detalle
        if (selectedItem == null) {
            ItemListScreen(
                items = items,
                onItemSelected = { repository.selectItem(it.id) }
            )
        } else {
            ItemDetailScreen(
                item = selectedItem!!,
                onBackPressed = { repository.clearSelection() }
            )
        }
    } else {
        // Vista para tablets - muestra lista y detalle lado a lado
        Row {
            Box(modifier = Modifier.weight(1f)) {
                ItemListScreen(
                    items = items,
                    onItemSelected = { repository.selectItem(it.id) }
                )
            }
            Box(modifier = Modifier.weight(2f)) {
                selectedItem?.let {
                    ItemDetailScreen(
                        item = it,
                        onBackPressed = null // No necesita botón atrás en vista tablet
                    )
                } ?: Box(Modifier.fillMaxSize()) {
                    Text("Selecciona un elemento de la lista", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun ItemListScreen(
    items: List<Item>,
    onItemSelected: (Item) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NASA Data") }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(items) { item ->
                ItemRow(item = item, onClick = { onItemSelected(item) })
            }
        }
    }
}

@Composable
fun ItemRow(item: Item, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun ItemDetailScreen(
    item: Item,
    onBackPressed: (() -> Unit)?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.title) },
                navigationIcon = onBackPressed?.let {
                    {
                        IconButton(onClick = onBackPressed) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.h4
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.body1
            )
            // Aquí podrías agregar una imagen u otros detalles
        }
    }
}