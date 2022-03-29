/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli.listeners

import dev.argraur.aosp.builder.cli.CLI

class CommandListener {
    companion object {
        private val TAG = CommandListener::class.simpleName!!
        private var INSTANCE: CommandListener? = null

        fun getInstance(): CommandListener {
            if (INSTANCE == null) {
                INSTANCE = CommandListener()
            }
            return INSTANCE!!
        }
    }

    lateinit var listener: Thread
    private var alive = true

    fun startListening() {
        val commands = CLI.getInstance().commands
        listener =
            Thread {
                while (alive) {
                    print("> ")
                    val input = readLine()
                    val command = input!!.split(" ")[0]
                    val args = input.replace(command,"").removePrefix(" ")
                    if (command in commands.keys) {
                        commands[command]!!.start(args)
                    } else {
                        println("No such command \"$command\"!")
                    }
                }
            }
        listener.start()
    }

    fun stopListening() {
        alive = false
    }
}