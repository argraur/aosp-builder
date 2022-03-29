/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.commands

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import dev.argraur.aosp.builder.utils.Job
import dev.argraur.aosp.builder.utils.JobManager

interface JobCommand: Command {
    val jobManager: JobManager
        get() = JobManager.getInstance()
    var job: Job
    var e: CommandHandlerEnvironment?
    fun onTaskFinish()
}