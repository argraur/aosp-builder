/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli

import dev.argraur.aosp.builder.cli.command.Command
import dev.argraur.aosp.builder.cli.command.JobCommand
import dev.argraur.aosp.builder.cli.command.impl.*
import dev.argraur.aosp.builder.cli.command.impl.android.Build
import dev.argraur.aosp.builder.cli.command.impl.android.Config
import dev.argraur.aosp.builder.cli.listeners.CommandListener
import dev.argraur.aosp.builder.utils.Logger
import kotlin.reflect.KClass
import kotlin.system.exitProcess

class CLI {
    companion object {
        private val TAG = CLI::class.simpleName!!
        private var INSTANCE: CLI? = null

        fun getInstance(): CLI {
            if (INSTANCE == null) {
                INSTANCE = CLI()
            }
            return INSTANCE!!
        }
    }
    private val commandListener = CommandListener.getInstance()
    val longCommands = mutableMapOf<String, KClass<out JobCommand>>()
    val commands = mutableMapOf<String, Command>()

    init {
        osCheck()
        print("\u001b[H\u001b[2J")
        println("Welcome to AOSP builder CLI!\n")
        println("PROTIP: To see usage type \"help\"\n")
        longCommands["build"] = Build::class
        longCommands["exec"] = Exec::class
        commands["config"] = Config()
        commands["clear"] = Clear()
        commands["exit"] = Exit()
        commands["job"] = Job()
        commands["help"] = Help()
    }

    private fun osCheck() {
        if (System.getProperty("os.name").lowercase().startsWith("win")) {
            Logger.getInstance().F(TAG, "Windows is unsupported.")
        }
    }

    fun start() {
        commandListener.startListening()
    }

    fun onDestroy() {
        commandListener.stopListening()
        exitProcess(0)
    }
}