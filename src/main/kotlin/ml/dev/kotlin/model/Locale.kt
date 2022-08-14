package ml.dev.kotlin.model

import ml.dev.kotlin.util.FileUtil
import kotlin.io.path.name

const val LANG_PATH: String = "csl/locales"

data class Locale(val fileName: String) {
    val name: String = fileName.removePrefix("locales-").removeSuffix(".xml")

    companion object {
        val AVAILABLE: List<Locale> = listAll()

        private fun listAll(): List<Locale> =
            FileUtil.getPathsFromResource(LANG_PATH).map { Locale(it.name) }.sortedBy { it.name }
    }
}