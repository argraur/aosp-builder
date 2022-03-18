/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.services

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command

import dev.argraur.aosp.builder.Application
import dev.argraur.aosp.builder.services.commands.Exec
import dev.argraur.aosp.builder.services.commands.Exit
import dev.argraur.aosp.builder.services.commands.Job
import dev.argraur.aosp.builder.services.commands.Ping
import dev.argraur.aosp.builder.utils.Logger

class Telegram {
    companion object {
        val TAG = Telegram::class.simpleName!!
    }
    private val application = Application.getInstance()
    private val buildConfig = application.buildConfig
    private val logger = Logger.getInstance()
    private val bot: Bot

    init {
        if (buildConfig.BOT_TOKEN.isNullOrBlank()) {
            logger.F(TAG, "Tried to launch bot without providing token. Bailing out.")
        }
        logger.I(TAG, "Starting Telegram bot polling...")
        bot = bot {
            token = buildConfig.BOT_TOKEN!!
            dispatch {
                arrayOf(Exec(), Ping(), Exit(), Job()).forEach {
                    command(it.getName()) {
                        it.start(this)
                    }
                }
            }
        }
        bot.startPolling()
    }

    fun isAllowed(id: Long): Boolean = id == buildConfig.BOT_MASTER
}