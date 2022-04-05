/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.command.impl

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.network.fold
import dev.argraur.aosp.builder.telegram.command.Command
import kotlin.system.exitProcess

class Exit: Command {
    override fun start(e: CommandHandlerEnvironment) {
        super.start(e)
        with (e) {
            if (isAllowed(message.from!!.id)) {
                bot.sendMessage(ChatId.fromId(message.chat.id), text = "Bye!").fold({
                    logger.I(TAG, "Stopping bot polling...")
                    bot.stopPolling()
                    exitProcess(0)
                })
            }
        }
    }
}