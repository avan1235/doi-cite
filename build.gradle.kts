@file:Suppress("UNCHECKED_CAST")

import com.github.jengelman.gradle.plugins.shadow.relocation.RelocateClassContext
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicInteger

val ktor_version: String by project
val kotlin_version: String by project
val kotlinx_datetime_version: String by project
val logback_version: String by project
val citeproc_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ml.dev.kotlin"
version = "0.0.1"
application {
    mainClass.set("ml.dev.kotlin.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-pebble:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_datetime_version")
    implementation("de.undercouch:citeproc-java:$citeproc_version")
}

tasks {
    shadowJar {
        archiveBaseName.set("doi-cite")
        archiveClassifier.set("")
        archiveVersion.set("")
        transform(TruffleTransformer::class.java)
    }

    register("stage") {
        dependsOn(shadowJar)
    }
}

class TruffleTransformer : ServiceFileTransformer() {

    init {
        setPath("META-INF/truffle")
    }

    private val idx = AtomicInteger(1)

    override fun transform(context: TransformerContext) {
        val currIdx = idx.getAndIncrement()
        val lines = BufferedReader(InputStreamReader(context.`is`)).readLines().toMutableList()
        var targetPath = context.path
        context.relocators.forEach { relocator ->
            if (relocator.canRelocateClass(File(targetPath).name)) {
                val newContext = RelocateClassContext.builder().className(targetPath).stats(context.stats).build()
                targetPath = relocator.relocateClass(newContext)
            }
            lines.forEachIndexed { idx, line ->
                if (relocator.canRelocateClass(line)) {
                    val lineContext = RelocateClassContext.builder().className(line).stats(context.stats).build()
                    lines[idx] = relocator.relocateClass(lineContext)
                }
            }
        }
        val serviceEntries = getServiceEntries()
        lines.forEach { serviceEntries[targetPath]?.append(replaceIndex(it, currIdx).byteInputStream()) }
    }

    private fun getServiceEntries(): Map<String, ServiceStream> {
        val field = javaClass.superclass.getDeclaredField("serviceEntries")
        field.isAccessible = true
        return field.get(this) as Map<String, ServiceStream>
    }

    private fun replaceIndex(path: String, currIdx: Int): String {
        val splitOn = path.indexOf('.')
        val prefix = path.substring(0, splitOn).dropLastWhile { c -> c.isDigit() }
        val suffix = path.substring(splitOn + 1)
        return "$prefix$currIdx.$suffix"
    }
}
