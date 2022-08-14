package ml.dev.kotlin.service

import de.undercouch.citeproc.CSL
import de.undercouch.citeproc.ItemDataProvider
import de.undercouch.citeproc.ListItemDataProvider
import de.undercouch.citeproc.csl.CSLItemData
import de.undercouch.citeproc.csl.CSLItemDataBuilder
import de.undercouch.citeproc.csl.CSLType
import de.undercouch.citeproc.output.Bibliography
import kotlinx.serialization.ExperimentalSerializationApi
import ml.dev.kotlin.model.Citation
import ml.dev.kotlin.model.Locale
import ml.dev.kotlin.model.OutputFormat
import ml.dev.kotlin.model.Style

@ExperimentalSerializationApi
object CitationService {

    fun html(citations: List<Citation>, style: Style, locale: Locale, format: OutputFormat): String {
        val items = citations.map { citation ->
            CSLItemDataBuilder().apply {
                title(citation.title)
                type(citation.parsedType)

                publisher(citation.publisher)
                containerTitle(citation.containerTitle)
                containerTitleShort(citation.containerTitleShort)

                volume(citation.volume)
                issue(citation.issue)

                DOI(citation.DOI)
                page(citation.page)

                citation.author?.forEach { author(it.given, it.family) }
            }.build()
        }

        return makeAdhocBibliography(style, locale, format, items).makeString()
    }
}

private fun makeAdhocBibliography(
    style: Style,
    locale: Locale,
    format: OutputFormat,
    items: List<CSLItemData>
): Bibliography {
    val ids = items.map { it.id }.toTypedArray()
    val provider: ItemDataProvider = ListItemDataProvider(items)
    CSL(provider, LocaleProvider, style.path, locale.name, false).use { csl ->
        csl.setOutputFormat(format.name)
        csl.registerCitationItems(*ids)
        return csl.makeBibliography()
    }
}

@ExperimentalSerializationApi
private val Citation.parsedType: CSLType?
    get() = tryIllegalOrNull {
        type.let(CSLType::fromString)
    } ?: tryIllegalOrNull {
        type?.flipped()?.let(CSLType::fromString)
    }

private fun <T> tryIllegalOrNull(action: () -> T): T? = try {
    action()
} catch (_: IllegalArgumentException) {
    null
}

private fun String.flipped(): String? =
    split("-").takeIf { it.size == 2 }?.let { (fst, snd) -> "$snd-$fst" }
