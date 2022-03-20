/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.commands.impl

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import dev.argraur.aosp.builder.telegram.commands.Command

import dev.argraur.aosp.builder.utils.Job
import dev.argraur.aosp.builder.utils.JobManager

class Exec: Command {
    private val NAME = "exec"
    private val jobManager = JobManager.getInstance()
    private lateinit var commandHandler: CommandHandlerEnvironment
    private lateinit var command: String
    private lateinit var job: Job

    override fun start(e: CommandHandlerEnvironment) {
        commandHandler = e
        super.start(commandHandler)
        with (commandHandler) {
            if (isAllowed(message.from!!.id)) {
                command = message.text!!.replace("/$NAME ", "")
                job = Job(command)
                val n = jobManager.addTask(this@Exec, job)
                job.start()
                bot.sendMessage(ChatId.fromId(message.chat.id), text = "Launched job number $n.\n\nUse <code>/job</code> command to check on it.", parseMode = ParseMode.HTML)
            }
        }
    }

    fun onTaskFinish() {
        val results = job.results()
        commandHandler.bot.sendMessage(
            chatId = ChatId.fromId(commandHandler.message.chat.id),
            text = "Command: <code>$command</code>\n\n<b>Output</b>\n<code>${results[0].dropLast(1)}</code>\n\n<b>Errors</b>\n<code>${results[1].dropLast(1)}</code>",
            parseMode = ParseMode.HTML
        )
    }

    override fun getName(): String = NAME
}