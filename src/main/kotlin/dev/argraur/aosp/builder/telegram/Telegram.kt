/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.logging.LogLevel

import dev.argraur.aosp.builder.Application
import dev.argraur.aosp.builder.telegram.commands.JobCommand
import dev.argraur.aosp.builder.telegram.commands.impl.*
import dev.argraur.aosp.builder.telegram.commands.impl.android.Build
import dev.argraur.aosp.builder.telegram.commands.impl.android.Config
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
            logLevel = if (application.applicationConfig.netDebug) LogLevel.All() else LogLevel.None
            token = buildConfig.BOT_TOKEN!!
            dispatch {
                // Multi-instance commands
                arrayOf(
                    Exec::class,
                    Build::class
                ).forEach {
                    logger.D(TAG, "Initialized command ${it.simpleName!!.lowercase()}")
                    command(it.simpleName!!.lowercase()) {
                        (it.java.constructors.first().newInstance() as JobCommand).start(this)
                    }
                }
                // Single-instance commands
                arrayOf(
                    Config(),
                    LExec(),
                    Ping(),
                    Exit(),
                    Job()
                ).forEach {
                    logger.D(TAG, "Initialized simple command ${it.NAME}")
                    command(it.NAME) {
                        it.start(this)
                    }
                }
            }
        }
        bot.startPolling()
    }
}