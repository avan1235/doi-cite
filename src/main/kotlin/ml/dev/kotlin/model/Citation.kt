package ml.dev.kotlin.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@ExperimentalSerializationApi
@Serializable
data class Citation(
    val title: String? = null,
    val type: String? = null,
    val author: List<Author>? = null,

    val publisher: String? = null,
    @JsonNames("container-title")
    val containerTitle: String? = null,
    @JsonNames("container-title-short")
    val containerTitleShort: String? = null,

    val volume: String? = null,
    val issue: String? = null,

    val DOI: String? = null,
    val page: String? = null,
)

@Serializable
data class Author(
    val given: String? = null,
    val family: String? = null,
    val sequence: String? = null,
)
