package ml.dev.kotlin.model

import java.util.*

class DOI(
    private val prefix: String,
    private val suffix: String,
) {
    val url: String = "https://doi.org/$prefix/$suffix"

    override fun toString(): String = "doi:$prefix/$suffix"

    override fun hashCode(): Int = Objects.hash(prefix, suffix)

    override fun equals(other: Any?): Boolean {
        val doi = other as? DOI ?: return false
        return doi.prefix == prefix && doi.suffix == suffix
    }
}

fun String.toDOI(): DOI? {
    val suffix = when {
        startsWith("https://doi.org/") -> removePrefix("https://doi.org/")
        startsWith("http://doi.org/") -> removePrefix("http://doi.org/")
        startsWith("doi:") -> removePrefix("doi:")
        else -> this
    }
    val parts = suffix.split("/")
    if (parts.size != 2) return null
    return DOI(parts[0], parts[1])
}