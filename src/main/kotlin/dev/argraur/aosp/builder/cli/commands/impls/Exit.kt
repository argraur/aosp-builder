/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli.commands.impls

import dev.argraur.aosp.builder.cli.CLI
import dev.argraur.aosp.builder.cli.commands.Command

class Exit: Command {
    override fun start() {
        CLI.getInstance().onDestroy()
    }

    override fun help(): String = "Exits the program."
}