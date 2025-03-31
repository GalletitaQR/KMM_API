package mx.edu.utng.nasa.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import mx.edu.utng.nasa.repository.ApodRepository

@Composable
fun AndroidApodMasterDetailWrapper(repository: ApodRepository) {
    // Observar el selectedApod para saber cuándo interceptar el botón de atrás
    val selectedApod by repository.selectedApod.collectAsState()

    // Interceptar el botón de atrás solo cuando hay un elemento seleccionado
    if (selectedApod != null) {
        BackHandler {
            // Cuando se presiona atrás, solo limpiamos la selección
            repository.clearSelection()
        }
    }

    // Llamar al componente principal común
    ApodMasterDetailScreen(repository)
}