/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.commands.impl

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import dev.argraur.aosp.builder.telegram.commands.Command

class Build: Command {
    override fun start(e: CommandHandlerEnvironment) {
        super.start(e)
        logger.E(TAG, "Not yet implemented.")
        TODO("Not yet implemented.")
    }
}