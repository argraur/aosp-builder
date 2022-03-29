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
                    logger.D(TAG, line)
                }
                while (errorReader.readLine()?.also { line = it } != null) {
                    error.append("\n" + line)
                    logger.E(TAG, line)
                }
                inputReader.close()
                errorReader.close()
            } catch (e: IOException) {
                logger.E(TAG, "Streams were closed. Proceed.")
            }
            process.waitFor()
            logger.D(TAG,"Process has ended with exit code: ${process.exitValue()}")
            onFinish()
        }.start()
    }

    fun results(): Array<String> = arrayOf(output.toString(), error.toString())

    private fun onFinish() {
        notifyObservers()
    }

    fun status(): String {
        val status = StringBuilder()
        status.append("Command:\n<code>$command</code>\n")
        status.append("PID: <code>${process.pid()}</code>\n")
        status.append("Process alive: <b>${if (process.isAlive) "Yes" else "No"}</b>\n")
        status.append("Last line: <code>${output.substring(output.lastIndexOf("\n"))}</code>")
        return status.toString()
    }

    fun forceStop() {
        logger.D(TAG, "Destroying task with OS PID ${process.pid()}")
        inputReader.close()
        errorReader.close()
        process.destroy()
    }
}