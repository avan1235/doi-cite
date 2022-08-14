package ml.dev.kotlin.util

inline fun <reified T : Any> readEnv(name: String): T? {
    val readValue: String? = System.getenv(name)
    return when (T::class) {
        String::class -> readValue as? T
        Int::class -> readValue?.toIntOrNull() as? T
        else -> null
    }
}
