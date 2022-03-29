/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli

import dev.argraur.aosp.builder.cli.commands.Command
import dev.argraur.aosp.builder.cli.commands.impls.Exit
import dev.argraur.aosp.builder.cli.commands.impls.Help
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
    val commands = mutableMapOf<String, Command>()

    init {
        print("\u001b[H\u001b[2J")
        println("Welcome to AOSP builder CLI!\n")
        println("PROTIP: To see usage type \"help\"\n")
        commands["exit"] = Exit()
        commands["help"] = Help()
    }

    fun start() {
        commandListener.startListening()
    }

    fun onDestroy() {
        commandListener.stopListening()
        exitProcess(0)
    }
}