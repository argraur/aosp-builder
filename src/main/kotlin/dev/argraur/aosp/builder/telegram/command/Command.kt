/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.command

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import dev.argraur.aosp.builder.Application
import dev.argraur.aosp.builder.utils.Logger

interface Command {
    val TAG: String
        get() = this.javaClass.simpleName + "Command"
    val NAME: String
        get() = this.javaClass.simpleName.lowercase()
    val logger: Logger
        get() = Logger.getInstance()
    fun start(e: CommandHandlerEnvironment) {
        logger.D(TAG, "Received query from UID ${e.message.from!!.id}")
    }
    fun isAllowed(id: Long) = Application.getInstance().buildConfig.BOT_MASTER == id
}