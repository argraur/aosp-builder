/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.android

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.TelegramFile
import dev.argraur.aosp.builder.android.config.DeviceBuildConfig
import dev.argraur.aosp.builder.android.utils.Output.Companion.output
import dev.argraur.aosp.builder.telegram.commands.JobCommand
import dev.argraur.aosp.builder.utils.Job
import dev.argraur.aosp.builder.utils.JobManager
import dev.argraur.aosp.builder.utils.Logger
import dev.argraur.aosp.builder.utils.observer.Observable
import dev.argraur.aosp.builder.utils.observer.Observer
import java.io.File
import kotlin.properties.Delegates

class SystemBuild(private val caller: Observer): Observer {
    companion object {
        private val TAG = SystemBuild::class.simpleName!!
    }
    private val config = DeviceBuildConfig.getInstance()
    private val logger = Logger.getInstance()
    private val jobManager = JobManager.getInstance()
    private val job: Job
    var isTelegram by Delegates.notNull<Boolean>()
    private var e: CommandHandlerEnvironment? = null

    init {
        if (caller is JobCommand) {
            e = caller.e
            isTelegram = true
        } else {
            isTelegram = false
        }
        val sourceRoot = config.sourceRoot
        val buildTarget = config.buildTarget
        val buildType = config.buildType
        val product = config.product
        val ccache = config.ccache
        val ccacheExec = config.ccacheExec
        val ccachePath = config.ccachePath
        val upload = config.upload
        val uploadPath = config.uploadPath
        val threads = config.threads

        val command = StringBuilder()
        command.append("#!/bin/bash\n")
        command.append("set -e\n")
        command.append("cd $sourceRoot\n")
        command.append("source build/envsetup.sh\n")
        command.append("lunch $product-$buildType\n")
        if (ccache) {
            command.append("export USE_CCACHE=1\n")
            command.append("export CCACHE_EXEC=$ccacheExec\n")
            command.append("export CCACHE_DIR=$ccachePath\n")
        }
        command.append("m -j$threads $buildTarget")
        if (upload) {
            TODO("Can't upload anything yet :(")
        }
        logger.I(TAG, "Starting Android system build with command line:\n$command")
        output("Starting Android system build with command line:\n<code>$command</code>", isTelegram, e)
        job = Job(command.toString())
        val pid = jobManager.addTask(this, job)
        job.start()
        output("Started Android system build with internal PID <code>$pid</code>", isTelegram, e)
    }

    override fun onObserverEvent(o: Observable) {
        logger.I(TAG, "Job finished with exit code: ${job.process.exitValue()}")
        val fileOutput = "output-${System.currentTimeMillis()}.txt"
        File(fileOutput).writeText(job.output.toString())
        logger.I(TAG, "Wrote job output to file: $fileOutput")
        val fileError = "error-${System.currentTimeMillis()}.txt"
        File(fileError).writeText(job.error.toString())
        logger.I(TAG, "Wrote job errors to file: $fileError")
        output("Android build finished with exit code: <code>${job.process.exitValue()}</code>", isTelegram, e)
        if (isTelegram) {
            with (e!!) {
                bot.sendDocument(
                    ChatId.fromId(message.chat.id),
                    TelegramFile.ByFile(File(fileOutput)),
                    caption = "<b>Android build output</b>",
                    ParseMode.HTML
                )
                bot.sendDocument(
                    ChatId.fromId(message.chat.id),
                    TelegramFile.ByFile(File(fileError)),
                    caption = "<b>Android build errors</b>",
                    ParseMode.HTML
                )
            }
        }
        if (job.process.exitValue() == 0) {
            output("<b><i>Android build has finished successfully!</i></b>", isTelegram, e)
        } else {
            output("<b><i>Android build has failed!</i></b>", isTelegram, e)
        }
        /*
        if (upload) {
            TODO("Can't upload anything yet :(")
        }
        */
    }
}