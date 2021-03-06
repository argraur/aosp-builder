/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.command.impl

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import dev.argraur.aosp.builder.telegram.command.Command

class Ping: Command {
    override fun start(e: CommandHandlerEnvironment) {
        super.start(e)
        with (e) {
            if (isAllowed(message.from!!.id)) {
                bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Pong!")
            }
        }
    }
}