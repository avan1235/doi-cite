package ml.dev.kotlin.service

import de.undercouch.citeproc.LocaleProvider
import ml.dev.kotlin.model.LANG_PATH
import ml.dev.kotlin.util.FileUtil
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object LocaleProvider : LocaleProvider {
    private val locales: MutableMap<String, String?> = HashMap()

    override fun retrieveLocale(lang: String): String {
        val cached = locales[lang]
        if (cached != null) return cached
        return try {
            val stream = FileUtil.getFileFromResourceAsStream("$LANG_PATH/locales-$lang.xml")
            BufferedReader(InputStreamReader(stream)).readLines().joinToString("\n")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }.also { locales[lang] = it }
    }
}