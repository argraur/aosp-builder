/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli.command.impl

import dev.argraur.aosp.builder.cli.CLI
import dev.argraur.aosp.builder.cli.command.Command

class Help: Command {
    override fun start(args: String) {
        println("Available commands:")
        CLI.getInstance().commands.forEach { (key, command) ->
            println("$key -> ${command.help()}")
        }
    }

    override fun help(): String = "Prints usage of this program."
}