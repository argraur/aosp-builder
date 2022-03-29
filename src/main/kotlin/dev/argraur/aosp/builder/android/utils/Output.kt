/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.android.utils

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import dev.argraur.aosp.builder.android.enums.OutputType

class Output {
    companion object {
        fun print(text: String, output: OutputType, e: CommandHandlerEnvironment?) {
            if (output == OutputType.TELEGRAM) {
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