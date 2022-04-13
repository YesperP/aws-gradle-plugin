package com.jespage.plugin

import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.OutputStream

private class FanOutOutputStream(private vararg val streams: OutputStream) : OutputStream() {
    override fun close() = streams.forEach { it.close() }
    override fun flush() = streams.forEach { it.flush() }
    override fun write(b: Int) = streams.forEach { it.write(b) }
    override fun write(b: ByteArray) = streams.forEach { it.write(b) }
    override fun write(b: ByteArray, off: Int, len: Int) = streams.forEach { it.write(b, off, len) }
}

fun Project.cmd(
    vararg args: String,
    printStdout: Boolean = true,
    printStderr: Boolean = true,
    ignoreExitValue: Boolean = false
) = ByteArrayOutputStream().use { outputStream ->
    ByteArrayOutputStream().use { errorStream ->
        println("CMD: ${args.joinToString(" ")}")
        exec {
            it.commandLine(*args)
            it.isIgnoreExitValue = ignoreExitValue
            it.standardOutput =
                if (printStdout) FanOutOutputStream(outputStream, it.standardOutput) else FanOutOutputStream(outputStream)
            it.errorOutput =
                if (printStderr) FanOutOutputStream(outputStream, it.errorOutput) else FanOutOutputStream(errorStream)
        }
        outputStream.toString()
    }
}
