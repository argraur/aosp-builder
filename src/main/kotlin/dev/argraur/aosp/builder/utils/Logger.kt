/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.utils

import dev.argraur.aosp.builder.Application

import java.io.FileOutputStream
import java.text.DateFormat
import java.util.Date

class Logger {
    companion object {
        val TAG = Logger::class.simpleName
        private var INSTANCE: Logger? = null

        fun getInstance(): Logger {
            if (INSTANCE == null) {
                INSTANCE = Logger()
            }
            return INSTANCE!!
        }
    }

    private val file = FileOutputStream("logs/log-${System.currentTimeMillis()}.txt")
    private val writer = file.writer()

    private fun write(line: String) {
        println(line)
        writer.write(line + "\n")
    }

    fun D(tag: String, message: String) {
        if (Application.getInstance().isDebug)
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
        write("${getTimeDate()} F ${TAG!!}: FATAL: Can't continue. Exiting with error code 1")
        Application.getInstance().onDestroy(1)
    }

    fun I(tag: String, message: String) {
        message.split("\n").forEach {
            write("${getTimeDate()} I $tag: $it")
        }
    }

    private fun getTimeDate(): String = DateFormat.getDateTimeInstance().format(Date())

    fun onDestroy() {
        writer.close()
    }
}