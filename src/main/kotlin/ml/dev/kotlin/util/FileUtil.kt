package ml.dev.kotlin.util

import java.io.File
import java.io.InputStream
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.ProviderNotFoundException
import java.util.stream.Collectors

object FileUtil {

    fun getFileFromResourceAsStream(fileName: String): InputStream {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(fileName)
        return inputStream ?: throw IllegalArgumentException("File not found! $fileName")
    }

    fun getPathsFromResource(folder: String): List<Path> = try {
        val uri = URI.create("jar:file:${javaClass.protectionDomain.codeSource.location.toURI().path}")
        FileSystems.newFileSystem(uri, emptyMap<String, Any>()).use { fs ->
            Files.walk(fs.getPath(folder))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList())
        }
    } catch (_: ProviderNotFoundException) {
        val url = FileUtil.javaClass.classLoader.getResource(folder)
        url?.path?.let { File(it) }?.listFiles()?.map { it.toPath() }.orEmpty()
    }
}