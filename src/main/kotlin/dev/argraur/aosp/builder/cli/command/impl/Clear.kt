/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli.command.impl

import dev.argraur.aosp.builder.cli.command.Command

class Clear: Command {
    override fun help(): String = "Clears current output."

    override fun start(args: String) {
        print("\u001b[H\u001b[2J")
    }
}