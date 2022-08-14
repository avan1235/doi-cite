package ml.dev.kotlin.model

import ml.dev.kotlin.util.FileUtil
import kotlin.io.path.name


const val STYLE_PATH: String = "csl/styles"

data class Style(val fileName: String) {
    val name = fileName.removeSuffix(".csl")
    val path: String = "$STYLE_PATH/$fileName"

    companion object {
        val AVAILABLE: List<Style> = listAll()

        private fun listAll(): List<Style> =
            FileUtil.getPathsFromResource(STYLE_PATH).map { Style(it.name) }.sortedBy { it.name }
    }
}
