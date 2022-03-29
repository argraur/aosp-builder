/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.android.utils

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode

class Output {
    companion object {
        fun output(text: String, isTelegram: Boolean, e: CommandHandlerEnvironment?) {
            if (isTelegram) {
                with(e) {
                    require(this != null)
                    bot.sendMessage(
                        ChatId.fromId(message.chat.id),
                        text,
                        ParseMode.HTML
                    )
                }
            } else {
                println(text)
            }
        }
    }
}