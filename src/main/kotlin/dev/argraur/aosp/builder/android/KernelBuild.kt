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
import dev.argraur.aosp.builder.telegram.command.JobCommand
import dev.argraur.aosp.builder.utils.Job
import dev.argraur.aosp.builder.utils.JobManager
import dev.argraur.aosp.builder.utils.Logger
import dev.argraur.aosp.builder.utils.observer.Observable
import dev.argraur.aosp.builder.utils.observer.Observer
import dev.argraur.aosp.builder.android.utils.Output.Companion.output

import java.io.File
import kotlin.properties.Delegates

class KernelBuild(private val caller: Observer): Observer {
    companion object {
        private val TAG = KernelBuild::class.java.simpleName
    }
    private val config = DeviceBuildConfig.getInstance()
    private val logger = Logger.getInstance()
    private val jobManager = JobManager.getInstance()
    private val job: Job
    var isTelegram by Delegates.notNull<Boolean>()
    var e: CommandHandlerEnvironment? = null

    init {
        if (caller is JobCommand) {
            this.e = caller.e
            isTelegram = true
        } else {
            isTelegram = false
        }
        // Getting required configs for kernel build
        val kernelDir = "kernel"
        val kernelBuildScript = "./build/build.sh"
        val kernelName = config.kernelName
        val kernelConfig = config.kernelConfig
        val kernelDistDir = config.kernelDistDir
        val outDir = "out"

        val command = StringBuilder()
        command.append("#!/bin/bash\n")
        command.append("set -e\n")
        command.append("cd $kernelDir\n")
        command.append("export OUT_DIR=$outDir\n")
        if (kernelDistDir.isNotEmpty()) {
            command.append("export DIST_DIR=$kernelDistDir\n")
        }
        command.append("test -e $kernelBuildScript\n")
        if (kernelConfig.isNotEmpty()) {
            command.append("export BUILD_CONFIG=$kernelName/$kernelConfig\n")
        } else {
            command.append("export KERNEL_DIR=$kernelName\n")
        }
        command.append("$kernelBuildScript -j${config.threads}")

        logger.I(TAG, "Starting kernel build with command:\n$command")
        output("Starting kernel build with command:\n<code>$command</code>", isTelegram, e)
        job = Job(command.toString())
        job.start()
        val pid = jobManager.addTask(this, job)
        output("Started kernel build job with internal PID <code>$pid</code>", isTelegram, e)

    }

    override fun onObserverEvent(o: Observable) {
        logger.I(TAG, "Job finished with exit code: ${job.process.exitValue()}")
        val fileOutput = "output-${System.currentTimeMillis()}.txt"
        File(fileOutput).writeText(job.output.toString())
        logger.I(TAG, "Wrote job output to file: $fileOutput")
        val fileError = "error-${System.currentTimeMillis()}.txt"
        File(fileError).writeText(job.error.toString())
        logger.I(TAG, "Wrote job errors to file: $fileError")
        output(
            "Kernel build finished with exit code: <code>${job.process.exitValue()}</code>",
            isTelegram,
            e
        )
        if (isTelegram) {
            with (e!!) {
                bot.sendDocument(
                    ChatId.fromId(message.chat.id),
                    TelegramFile.ByFile(File(fileOutput)),
                    caption = "<b>Kernel build output</b>",
                    ParseMode.HTML
                )
                bot.sendDocument(
                    ChatId.fromId(message.chat.id),
                    TelegramFile.ByFile(File(fileError)),
                    caption = "<b>Kernel build errors</b>",
                    ParseMode.HTML
                )
            }
        }
        if (job.process.exitValue() == 0) {
            output("Kernel build has been finished successfully!", isTelegram, e)
        } else {
            output("Kernel build has failed!", isTelegram, e)
        }
    }
}