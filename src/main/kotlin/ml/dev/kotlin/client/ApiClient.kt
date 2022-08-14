package ml.dev.kotlin.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import ml.dev.kotlin.model.Citation
import ml.dev.kotlin.model.DOI

@ExperimentalSerializationApi
object ApiClient : Closeable {

    private val client: HttpClient = HttpClient(CIO) {
        expectSuccess = true
        install(Logging)
        install(ContentNegotiation) {
            json(
                json = Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                },
                contentType = ContentType("application", "vnd.citationstyles.csl+json")
            )
        }
    }

    suspend fun get(doi: DOI): Citation =
        client.get(doi.url).body()

    override fun close() {
        client.close()
    }
}
