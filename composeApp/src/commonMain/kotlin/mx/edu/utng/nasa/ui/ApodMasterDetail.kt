// Archivo: commonMain/kotlin/mx.edu.utng.nasa/ui/ApodMasterDetail.kt
package mx.edu.utng.nasa.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mx.edu.utng.nasa.models.ApodItem
import mx.edu.utng.nasa.repository.ApodRepository
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ApodMasterDetailScreen(repository: ApodRepository) {
    val items by repository.apodItems.collectAsState()
    val selectedApod by repository.selectedApod.collectAsState()
    val isLoading = remember { mutableStateOf(true) }

    // Cargar datos cuando se inicia la pantalla
    LaunchedEffect(Unit) {
        isLoading.value = true

        // Primero intentamos depurar la API
        repository.debugApiResponse()

        // Luego intentamos obtener los datos
        try {
            repository.fetchLastNDays(20)
        } catch (e: Exception) {
            println("Error en UI al obtener datos: ${e.message}")
        }

        isLoading.value = false
    }

    // Detectar si estamos en pantalla pequeña (móvil) o grande (tablet)
    val isCompactScreen = remember { mutableStateOf(true) } // Simplificado, ideal sería detectar tamaño real

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (isCompactScreen.value) {
        // Vista para móviles - cambia entre lista y detalle
        if (selectedApod == null) {
            ApodListScreen(
                items = items,
                onItemSelected = { repository.selectApod(it.date!!) }
            )
        } else {
            ApodDetailScreen(
                apod = selectedApod!!,
                onBackPressed = { repository.clearSelection() }
            )
        }
    } else {
        // Vista para tablets - muestra lista y detalle lado a lado
        Row {
            Box(modifier = Modifier.weight(1f)) {
                ApodListScreen(
                    items = items,
                    onItemSelected = { repository.selectApod(it.date!!) }
                )
            }
            Box(modifier = Modifier.weight(2f)) {
                selectedApod?.let {
                    ApodDetailScreen(
                        apod = it,
                        onBackPressed = null // No necesita botón atrás en vista tablet
                    )
                } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Selecciona una imagen para ver detalles")
                }
            }
        }
    }
}

@Composable
fun ApodListScreen(
    items: List<ApodItem>,
    onItemSelected: (ApodItem) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NASA APOD") }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(items) { apod ->
                ApodListItem(apod = apod, onClick = { onItemSelected(apod) })
            }
        }
    }
}

@Composable
fun ApodListItem(apod: ApodItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.height(120.dp)) {
            // Miniatura de la imagen
            if (apod.mediaType == "image") {
                KamelImage(
                    resource = asyncPainterResource(data = apod.url!!),
                    contentDescription = apod.title,
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxHeight(),
                    contentScale = ContentScale.Crop,
                    onLoading = { CircularProgressIndicator() },
                    onFailure = { Text("Error") }
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxHeight()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Video")
                }
            }

            // Información
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = apod.title!!,
                    style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = apod.date!!,
                    style = MaterialTheme.typography.caption
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = apod.explanation!!,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ApodDetailScreen(
    apod: ApodItem,
    onBackPressed: (() -> Unit)?
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(apod.title!!) },
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
                .verticalScroll(scrollState)
        ) {
            // Imagen en alta resolución
            if (apod.mediaType == "image") {
                KamelImage(
                    resource = asyncPainterResource(data = apod.hdurl ?: apod.url!!),
                    contentDescription = apod.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop,
                    onLoading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    },
                    onFailure = { Text("Error al cargar la imagen") }
                )
            } else {
                // Aquí podrías implementar un reproductor para videos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Contenido de tipo: ${apod.mediaType}")
                }
            }

            // Información detallada
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = apod.title!!,
                    style = MaterialTheme.typography.h5
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Fecha: ${apod.date}",
                    style = MaterialTheme.typography.subtitle1
                )

                apod.copyright?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Copyright: $it",
                        style = MaterialTheme.typography.subtitle2
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = apod.explanation!!,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}