package mx.edu.utng.nasa.repository

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import mx.edu.utng.nasa.models.ApodItem
import kotlinx.datetime.*

class ApodRepository {
    private val apiKey = "e0ZFnxcaspLvm3qpiTLMdWjB85q0fNYg5f8dwuw2"
    private val baseUrl = "https://api.nasa.gov/planetary/apod"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        prettyPrint = false
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
    }

    private val _apodItems = MutableStateFlow<List<ApodItem>>(emptyList())
    val apodItems: StateFlow<List<ApodItem>> = _apodItems

    private val _selectedApod = MutableStateFlow<ApodItem?>(null)
    val selectedApod: StateFlow<ApodItem?> = _selectedApod

    suspend fun fetchLastNDays(count: Int = 20) {
        try {
            println("Iniciando solicitud a la API de APOD para los últimos $count días...")

            // Calcular fechas usando kotlinx-datetime
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val startDate = today.minus(count - 1, DateTimeUnit.DAY)

            // Formatear fechas en formato YYYY-MM-DD
            val endDateStr = today.toString()
            val startDateStr = startDate.toString()

            val url = "$baseUrl?api_key=$apiKey&start_date=$startDateStr&end_date=$endDateStr"
            println("URL de solicitud: $url")

            val response = client.get(url)
            val responseText = response.bodyAsText()

            println("Respuesta API sin procesar: $responseText")

            // Corrección en la sintaxis de decodeFromString
            val itemsList = json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(ApodItem.serializer()), responseText)

            // Ordenar los elementos por fecha (más reciente primero)
            val sortedItems = itemsList.sortedByDescending { it.date }
            println("Respuesta recibida exitosamente con ${sortedItems.size} elementos")

            // Verificar los tipos de media
            sortedItems.forEach { item ->
                println("Elemento: ${item.date}, Tipo: ${item.mediaType}")
            }

            _apodItems.value = sortedItems
        } catch (e: Exception) {
            println("Error fetching APOD data: ${e.message}")
            e.printStackTrace()

            // Intentar con un solo elemento como fallback
            println("Intentando obtener un solo elemento como respaldo...")
            fetchSingleApod()
        }
    }

    suspend fun fetchSingleApod() {
        try {
            val response = client.get("$baseUrl?api_key=$apiKey")
            val responseText = response.bodyAsText()

            println("Respuesta API sin procesar (single): $responseText")

            // Corrección en la sintaxis de decodeFromString
            val singleItem = json.decodeFromString(ApodItem.serializer(), responseText)
            println("Dato recibido: $singleItem")
            _apodItems.value = listOf(singleItem)
        } catch (e: Exception) {
            println("Error fetching single APOD data: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun debugApiResponse() {
        try {
            val response = client.get("$baseUrl?api_key=$apiKey&count=1")
            val responseText = response.bodyAsText()
            println("Respuesta API sin procesar: $responseText")
        } catch (e: Exception) {
            println("Error obteniendo respuesta: ${e.message}")
            e.printStackTrace()
        }
    }

    fun selectApod(date: String) {
        _selectedApod.value = _apodItems.value.find { it.date == date }
    }

    fun clearSelection() {
        _selectedApod.value = null
    }

    fun dispose() {
        client.close()
    }
}