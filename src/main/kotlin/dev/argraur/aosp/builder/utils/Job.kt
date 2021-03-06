/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.utils

import dev.argraur.aosp.builder.utils.observer.Observable
import dev.argraur.aosp.builder.utils.observer.Observer
import java.io.BufferedReader
import java.io.IOException

open class Job(private val command: String): Observable {
    override val observers: MutableList<Observer> = mutableListOf()

    companion object {
        val TAG = Job::class.simpleName!!
    }

    private val logger = Logger.getInstance()
    val output = StringBuilder()
    val error = StringBuilder()
    lateinit var process: Process
    private lateinit var inputReader: BufferedReader
    private lateinit var errorReader: BufferedReader

    fun start() {
        Thread {
            logger.D(TAG, "Launching process $command")
            process = ProcessBuilder("bash", "-c", command).start()
            inputReader = process.inputReader()
            errorReader = process.errorReader()
            var line = ""
            try {
                while (inputReader.readLine()?.also { line = it } != null) {
                    output.append("\n" + line)
                    logger.D("$TAG-${process.pid()}", line)
                }
                while (errorReader.readLine()?.also { line = it } != null) {
                    error.append("\n" + line)
                    logger.E("$TAG-${process.pid()}", line)
                }
                inputReader.close()
                errorReader.close()
            } catch (e: IOException) {
                logger.E("$TAG-${process.pid()}", "Streams were closed. Proceed.")
            }
            process.waitFor()
            logger.D("$TAG-${process.pid()}","Process has ended with exit code: ${process.exitValue()}")
            onFinish()
        }.start()
    }

    fun results(): Array<String> = arrayOf(output.toString(), error.toString())

    private fun onFinish() {
        notifyObservers()
    }

    fun status(): String {
        val status = StringBuilder()
        status.append("Command:\n$command\n")
        status.append("PID: ${process.pid()}\n")
        status.append("Process alive: ${if (process.isAlive) "Yes" else "No"}\n")
        var lastLine = output.toString()
        if (lastLine.isNotEmpty()) {
            lastLine = lastLine.substring(output.lastIndexOf("\n"))
            if (lastLine.startsWith("\n")) {
                status.append("Last line: ${lastLine.removePrefix("\n")}")
            } else {
                status.append("Last line: $lastLine")
            }
        }
        return status.toString()
    }

    fun forceStop() {
        logger.D(TAG, "Destroying task with OS PID ${process.pid()}")
        process.descendants().forEach { it.destroyForcibly() }
        process.destroyForcibly()
        logger.D(TAG, "Stopped process ${process.pid()}")
    }
}