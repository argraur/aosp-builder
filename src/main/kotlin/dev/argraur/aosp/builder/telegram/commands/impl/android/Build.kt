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
import dev.argraur.aosp.builder.utils.observer.Observable

class Build: JobCommand {
    override var e: CommandHandlerEnvironment? = null
    private lateinit var kernelBuild: KernelBuild
    private lateinit var systemBuild: SystemBuild

    override fun start(e: CommandHandlerEnvironment) {
        this.e = e
        super.start(e)
        try {
            val config = DeviceBuildConfig.getInstance()
            if (config.kernelName.isNotEmpty()) {
                kernelBuild = KernelBuild(this@Build)
            }
            if (config.sourceRoot.isNotEmpty()) {
                systemBuild = SystemBuild(this@Build)
            }
            if (config.sourceRoot.isEmpty() && config.kernelName.isEmpty()) {
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

    override fun onObserverEvent(o: Observable) {

    }
}