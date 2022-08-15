package ml.dev.kotlin.plugins

import com.mitchellbosecke.pebble.loader.ClasspathLoader
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.pebble.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import ml.dev.kotlin.client.ApiClient
import ml.dev.kotlin.model.*
import ml.dev.kotlin.service.CitationService

@ExperimentalSerializationApi
fun Application.configureRouting() {
    install(Pebble) {
        loader(ClasspathLoader().apply {
            prefix = "templates"
        })
    }

    routing {
        static {
            resource(remotePath = "styles.css", resource = "templates/styles.css")
        }

        get("/") {
            val params = mapOf(
                "styles" to Style.AVAILABLE,
                "locales" to Locale.AVAILABLE,
            )
            call.respond(PebbleContent("index.html", params))
        }

        post("/doi") {
            val formParameters = call.receiveParameters()
            val doi = formParameters["doi"]?.parseDOIList()
            val style = formParameters["style"]?.let(::Style)
            val locale = formParameters["locale"]?.let(::Locale)
            val citations = doi?.map { ApiClient.get(it) }
            if (style != null && locale != null && citations != null) {
                val html = CitationService.html(citations, style, locale, HTMLOutputFormat)
                call.respondText(html, ContentType.Text.Html)
            } else {
                call.respondText("Error input", ContentType.Text.Plain)
            }
        }
    }
}

private fun String.parseDOIList(): List<DOI> =
    split("[\\s+,]".toRegex()).mapNotNull { it.toDOI() }
