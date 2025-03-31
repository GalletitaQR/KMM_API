package mx.edu.utng.nasa

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform