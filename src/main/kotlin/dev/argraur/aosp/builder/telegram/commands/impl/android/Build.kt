/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.commands.impl.android

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import dev.argraur.aosp.builder.android.KernelBuild
import dev.argraur.aosp.builder.android.SystemBuild
import dev.argraur.aosp.builder.android.config.DeviceBuildConfig
import dev.argraur.aosp.builder.telegram.commands.JobCommand
import dev.argraur.aosp.builder.utils.Job

class Build: JobCommand {
    override var e: CommandHandlerEnvironment? = null
    override lateinit var job: Job

    override fun start(e: CommandHandlerEnvironment) {
        super.start(e)
        try {
            val config = DeviceBuildConfig.getInstance()
            if (config.kernelRoot.isNotEmpty()) {
                val kernelBuild = KernelBuild(this@Build)
            }
            if (config.sourceRoot.isNotEmpty()) {
                val systemBuild = SystemBuild(this@Build)
            }
            if (config.sourceRoot.isEmpty() && config.kernelRoot.isEmpty()) {
                e.bot.sendMessage(
                    ChatId.fromId(e.message.chat.id),
                    "<b>Nothing to do! Check build config.</b>",
                    ParseMode.HTML
                )
            }
        } catch (ex: Exception) {
            e.bot.sendMessage(
                ChatId.fromId(e.message.chat.id),
                when (ex) {
                    is IllegalStateException -> "<b>No build config found.</b>\n\n<code>${ex.message!!}</code>"
                    else -> ex.message!!
                },
                ParseMode.HTML
            )
        }
    }

    override fun onTaskFinish() {
        TODO("Not yet implemented")
    }
}