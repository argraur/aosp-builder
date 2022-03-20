/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.commands.impl

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import dev.argraur.aosp.builder.telegram.commands.Command
import dev.argraur.aosp.builder.utils.JobManager

class Job: Command {
    private val jobManager = JobManager.getInstance()

    override fun start(e: CommandHandlerEnvironment) {
        super.start(e)
        with (e) {
            val arg = message.text!!.replace("/$NAME ","")
            when (arg.split(" ")[0]) {
                "check" -> {
                    try {
                        bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = jobManager.jobStatus(arg.split(" ")[1].toInt())!!, parseMode = ParseMode.HTML)
                    } catch (e: IndexOutOfBoundsException) {
                        bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = jobManager.jobStatus(), parseMode = ParseMode.HTML)
                    }
                }
                "stop" -> {
                    try {
                        val stopped = jobManager.stopJob(arg.split(" ")[1].toInt())
                        if (stopped) {
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = "Stopped job with PID ${arg.split(" ")[1]}"
                            )
                        } else {
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = "Failed to stop the job with PID ${arg.split(" ")[1]}, does it even exist? Doubt."
                            )
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Please specify job PID!")
                    }
                }
                else -> {

                }
            }
        }
    }
}