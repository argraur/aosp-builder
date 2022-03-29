/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.commands.impl

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import dev.argraur.aosp.builder.android.enums.OutputType
import dev.argraur.aosp.builder.telegram.commands.JobCommand

import dev.argraur.aosp.builder.utils.Job
import dev.argraur.aosp.builder.utils.observer.Observable

class Exec: JobCommand {
    override var outputType = OutputType.TELEGRAM
    override var e: CommandHandlerEnvironment? = null
    private lateinit var command: String
    private lateinit var job: Job
    override fun start(e: CommandHandlerEnvironment) {
        this.e = e
        super.start(e)
        with (e) {
            if (isAllowed(message.from!!.id)) {
                command = message.text!!.replace("/$NAME ", "")
                job = Job(command)
                val n = jobManager.addTask(this@Exec, job)
                job.start()
                bot.sendMessage(ChatId.fromId(message.chat.id), text = "Launched job number $n.\n\nUse <code>/job</code> command to check on it.", parseMode = ParseMode.HTML)
            }
        }
    }

    override fun onObserverEvent(o: Observable) {
        val results = job.results()
        e!!.bot.sendMessage(
            chatId = ChatId.fromId(e!!.message.chat.id),
            text = "Command: <code>$command</code>\n\n<b>Output</b>\n<code>${results[0].dropLast(1)}</code>\n\n<b>Errors</b>\n<code>${results[1].dropLast(1)}</code>",
            parseMode = ParseMode.HTML
        )
    }
}