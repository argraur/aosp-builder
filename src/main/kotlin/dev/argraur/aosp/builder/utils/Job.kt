/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.utils

import java.io.BufferedReader

class Job(private val command: String) {
    companion object {
        val TAG = Job::class.simpleName!!
    }

    private val jobManager = JobManager.getInstance()
    private val logger = Logger.getInstance()
    private val output = StringBuilder()
    private val error = StringBuilder()
    private lateinit var process: Process
    private lateinit var inputReader: BufferedReader
    private lateinit var errorReader: BufferedReader

    fun start() {
        Thread {
            logger.D(TAG, "Launching process $command")
            process = ProcessBuilder("bash", "-c", command).start()
            inputReader = process.inputReader()
            errorReader = process.errorReader()
            var line = ""
            while (inputReader.readLine()?.also { line = it } != null) {
                output.append(line + "\n")
            }
            while (errorReader.readLine()?.also { line = it } != null) {
                error.append(line + "\n")
            }
            inputReader.close()
            errorReader.close()
            process.waitFor()
            logger.D(TAG,"Process has ended with exit code: ${process.exitValue()}")
            onFinish()
        }.start()
    }

    fun results(): Array<String> = arrayOf(output.toString(), error.toString())

    private fun onFinish() {
        jobManager.removeTask(this)
    }

    fun status(): String {
        val status = StringBuilder()
        status.append("Command: $command\n")
        status.append("PID: ${process.pid()}\n")
        status.append("Process alive: ${process.isAlive}\n")
        status.append("Last line: ${output.substring(output.lastIndexOf("\n"))}")
        return status.toString()
    }

    fun forceStop() {
        logger.D(TAG, "Destroying task with OS PID ${process.pid()}")
        process.destroy()
    }
}