// commonMain/kotlin/mx.edu.utng.nasa/ui/VideoPlayer.kt
package mx.edu.utng.nasa.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun VideoPlayer(url: String, modifier: Modifier = Modifier)

