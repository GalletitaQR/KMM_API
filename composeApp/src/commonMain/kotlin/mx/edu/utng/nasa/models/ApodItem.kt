package mx.edu.utng.nasa.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ApodItem(
    val date: String? = null,
    val title: String? = null,
    val explanation: String? = null,
    val url: String? = null,

    @SerialName("media_type")
    val mediaType: String? = null,

    val hdurl: String? = null,
    val copyright: String? = null,

    @SerialName("thumbnail_url")
    val thumbnailUrl: String? = null,

    @SerialName("service_version")
    val serviceVersion: String? = null
)