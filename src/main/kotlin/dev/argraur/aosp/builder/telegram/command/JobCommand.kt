/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.telegram.command

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import dev.argraur.aosp.builder.utils.JobManager
import dev.argraur.aosp.builder.utils.observer.Observer

interface JobCommand: Command, Observer {
    val jobManager: JobManager
        get() = JobManager.getInstance()
    var e: CommandHandlerEnvironment?
}