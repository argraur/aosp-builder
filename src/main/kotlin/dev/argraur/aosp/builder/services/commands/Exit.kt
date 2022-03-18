/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.services.commands

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.network.fold
import dev.argraur.aosp.builder.Application
import dev.argraur.aosp.builder.services.Command
import kotlin.system.exitProcess

class Exit: Command {
    private val NAME = "exit"

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

    override fun getName(): String = NAME
}