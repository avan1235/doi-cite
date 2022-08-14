package ml.dev.kotlin.model

sealed class OutputFormat(val name: String)
object HTMLOutputFormat : OutputFormat("html")