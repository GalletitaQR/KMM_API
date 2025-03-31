package mx.edu.utng.nasa.models

data class Item(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null
)