/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli.command.impl

import dev.argraur.aosp.builder.cli.command.JobCommand
import dev.argraur.aosp.builder.utils.Job
import dev.argraur.aosp.builder.utils.JobManager
import dev.argraur.aosp.builder.utils.observer.Observable

class Exec() : JobCommand {
    private lateinit var command: String
    private lateinit var job: Job
    private val jobManager = JobManager.getInstance()

    override fun start(args: String) {
        command = args
        job = Job(args)
        val n = jobManager.addTask(this@Exec, job)
        job.start()
        println("Launched job number $n.\nYou can use \"job\" command to check on it.")
    }

    override fun onObserverEvent(o: Observable) {
        val results = job.results()
        print(
            "\nFinished command: $command\n\n" +
                    (if (results[0].isNotEmpty())
                        "Output:\n${results[0]}\n\n"
                    else
                        "") +
                    (if (results[1].isNotEmpty())
                        "Errors:\n${results[1]}\n"
                    else
                        "") + "> "
        )
    }
}