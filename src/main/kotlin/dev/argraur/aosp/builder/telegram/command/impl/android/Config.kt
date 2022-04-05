/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.command.impl.android

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode

import dev.argraur.aosp.builder.android.config.DeviceBuildConfig
import dev.argraur.aosp.builder.telegram.command.Command

class Config: Command {
    override fun start(e: CommandHandlerEnvironment) {
        super.start(e)
        with (e) {
            val args = message.text!!.replace("/$NAME", "").removePrefix(" ")
            if (args.isNotEmpty()) {
                DeviceBuildConfig.setInstance(DeviceBuildConfig(args))
            }
            try {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = "<code>${DeviceBuildConfig.getInstance()}</code>",
                    parseMode = ParseMode.HTML
                )
            } catch (e: IllegalStateException) {
                bot.sendMessage(
                    ChatId.fromId(message.chat.id),
                    text = "<b>Arguments can't be empty!</b>",
                    parseMode = ParseMode.HTML
                )
            }
        }
    }
}