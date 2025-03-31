package mx.edu.utng.nasa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import mx.edu.utng.nasa.repository.ItemRepository
import mx.edu.utng.nasa.ui.MasterDetailScreen
import nasaproject.composeapp.generated.resources.Res
import nasaproject.composeapp.generated.resources.compose_multiplatform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import mx.edu.utng.nasa.repository.ApodRepository
import mx.edu.utng.nasa.ui.ApodMasterDetailScreen

@Composable
fun App() {
    val repository = remember { ApodRepository() }

    MaterialTheme {
        ApodMasterDetailScreen(repository = repository)
    }
}