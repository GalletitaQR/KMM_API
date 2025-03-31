package mx.edu.utng.nasa.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import android.annotation.SuppressLint
import android.webkit.WebSettings

@Composable
actual fun VideoPlayer(url: String, modifier: Modifier) {
    when {
        url.contains("youtube.com") || url.contains("youtu.be") -> {
            val embedUrl = convertToEmbedUrl(url)
            println("embedUrl"+embedUrl)
            YouTubeWebView(embedUrl, modifier)
        }
        url.contains("vimeo.com") -> {
            VimeoPlayer(extractVimeoId(url), modifier)
        }
        else -> {
            DirectVideoPlayer(url, modifier)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeWebView(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE

                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()

                loadUrl(url)
            }
        }
    )
}

fun convertToEmbedUrl(youtubeUrl: String): String {
    val videoId = extractYouTubeId(youtubeUrl)
    println("convertToEmbedUrl"+videoId)
    return "https://www.youtube.com/embed/$videoId"
}


fun extractYouTubeId(url: String): String? {
    println("extractYouTubeId"+url)
    val regex = Regex("(?:youtu\\.be/|youtube\\.com(?:/embed/|/v/|/watch\\?v=|/shorts/))([a-zA-Z0-9_-]{11})")
    return regex.find(url)?.groups?.get(1)?.value
}



// 🔹 Reproductor de Vimeo (puedes agregar más lógica si necesitas controles)
fun extractVimeoId(url: String): String {
    return url.split("/").lastOrNull() ?: ""
}

@Composable
fun VimeoPlayer(videoId: String, modifier: Modifier = Modifier) {
    val webViewUrl = "https://player.vimeo.com/video/$videoId"

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                webViewClient = WebViewClient()

                loadData(
                    """
                    <html>
                        <body style="margin:0;padding:0">
                            <iframe width="100%" height="100%" 
                                src="$webViewUrl" 
                                frameborder="0" allowfullscreen>
                            </iframe>
                        </body>
                    </html>
                    """,
                    "text/html",
                    "utf-8"
                )
            }
        },
        modifier = modifier
    )
}

// 🔹 Reproductor de videos directos (MP4, etc.)
@Composable
fun DirectVideoPlayer(url: String, modifier: Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = { ctx ->
            StyledPlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    )
}
