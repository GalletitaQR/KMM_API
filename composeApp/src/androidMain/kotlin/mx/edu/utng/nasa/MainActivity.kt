package mx.edu.utng.nasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.utng.nasa.repository.ApodRepository
import mx.edu.utng.nasa.ui.AndroidApodMasterDetailWrapper

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val repository = remember { ApodRepository() }

            // Usar el wrapper específico de Android en lugar del componente común
            AndroidApodMasterDetailWrapper(repository)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}