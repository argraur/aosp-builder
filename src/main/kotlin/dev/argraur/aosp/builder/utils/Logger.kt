/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.utils

import dev.argraur.aosp.builder.Application

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.DateFormat
import java.util.Date
import kotlin.system.exitProcess

class Logger {
    companion object {
        val TAG = Logger::class.simpleName!!
        private var INSTANCE: Logger? = null

        fun getInstance(): Logger {
            if (INSTANCE == null) {
                INSTANCE = Logger()
            }
            return INSTANCE!!
        }
    }

    val logFileName: String
    private val file: FileOutputStream
    private val writer: OutputStreamWriter

    init {
        val logFolder = File("logs")
        if (!logFolder.exists()) {
            logFolder.mkdir()
        }
        logFileName = "log-${java.lang.System.currentTimeMillis()}.txt"
        file = FileOutputStream("logs/$logFileName")
        writer = file.writer()
    }

    private fun write(line: String) {
        println(line)
        writer.write(line + "\n")
    }

    fun D(tag: String, message: String) {
        if (Application.getInstance().applicationConfig.debug)
            message.split("\n").forEach {
                write("${getTimeDate()} D $tag: $it")
            }
    }

    fun E(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} E $tag: $it")
        }
    }

    fun F(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} F $tag: $it")
        }
        write("${getTimeDate()} F $TAG: FATAL: Can't continue. Exiting with error code 1")
        exitProcess(1)
    }

    fun I(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} I $tag: $it")
        }
    }

    private fun getTimeDate(): String = DateFormat.getDateTimeInstance().format(Date())

    fun onDestroy() {
        I(TAG, "Closing writer...")
        I(TAG, "Have a great day!")
        writer.close()
    }
}