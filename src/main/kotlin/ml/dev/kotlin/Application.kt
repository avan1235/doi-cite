package ml.dev.kotlin

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ml.dev.kotlin.plugins.configureRouting
import ml.dev.kotlin.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
