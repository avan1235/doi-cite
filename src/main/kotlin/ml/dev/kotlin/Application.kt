package ml.dev.kotlin

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.ExperimentalSerializationApi
import ml.dev.kotlin.plugins.configureRouting
import ml.dev.kotlin.util.readEnv

@ExperimentalSerializationApi
fun main() {
    embeddedServer(
        factory = Netty,
        port = readEnv("PORT") ?: DEFAULT_PORT,
        host = "0.0.0.0"
    ) {
        configureRouting()
    }.start(wait = true)
}

private const val DEFAULT_PORT: Int = 8080
