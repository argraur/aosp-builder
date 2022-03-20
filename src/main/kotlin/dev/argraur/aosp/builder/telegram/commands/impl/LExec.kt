/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.commands.impl

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import dev.argraur.aosp.builder.telegram.commands.Command

class LExec: Command {
    override fun start(e: CommandHandlerEnvironment) {
        super.start(e)
        with (e) {
            if (isAllowed(message.from!!.id)) {
                val command = message.text!!.replace("/$NAME ", "")
                logger.D(TAG, "Launching process $command")
                Thread {
                    val process = ProcessBuilder("bash", "-c", command).start()
                    val output = StringBuilder()
                    val error = StringBuilder()
                    val inputReader = process.inputReader()
                    val errorReader = process.errorReader()
                    var line = ""
                    while (inputReader.readLine()?.also { line = it } != null) {
                        output.append(line + "\n")
                    }
                    while (errorReader.readLine()?.also { line = it } != null) {
                        error.append(line + "\n")
                    }
                    inputReader.close()
                    errorReader.close()
                    process.waitFor()
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Command: <code>$command</code>\n\n<b>Output</b>\n<code>${output.dropLast(1)}</code>\n\n<b>Errors</b>\n<code>${error.dropLast(1)}</code>",
                        parseMode = ParseMode.HTML
                    )
                    logger.D(TAG, "Process has ended with exit code: ${process.exitValue()}")
                }.start()
            }
        }
    }
}