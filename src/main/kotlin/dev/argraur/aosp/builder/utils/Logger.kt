package dev.argraur.aosp.builder.utils

import dev.argraur.aosp.builder.Application

import java.io.FileOutputStream

class Logger {
    companion object {
        val TAG = Logger::class.simpleName
        var INSTANCE: Logger? = null

        fun getInstance(): Logger {
            if (INSTANCE == null) {
                INSTANCE = Logger()
            }
            return INSTANCE!!
        }
    }

    private val writer = FileOutputStream("logs/log-${System.currentTimeMillis()}.txt").writer(Charsets.UTF_8)

    private fun write(line: String) {
        println(line)
        writer.write(line)
    }

    fun D(tag: String, message: String) {
        message.split("\n").forEach {
            write("D $tag: $it")
        }
    }

    fun E(tag: String, message: String) {
        message.split("\n").forEach {
            write("E $tag: $it")
        }
    }

    fun F(tag: String, message: String) {
        message.split("\n").forEach {
            write("F $tag: $it")
        }
        write("F ${TAG!!}: FATAL: Can't continue. Exiting with error code 1")
        Application.getInstance().onDestroy(1)
    }

    fun I(tag: String, message: String) {
        message.split("\n").forEach {
            write("I $tag: $it")
        }
    }

    fun onDestroy() {
        writer.close()
    }
}