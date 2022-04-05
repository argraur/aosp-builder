/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli.command.impl

import dev.argraur.aosp.builder.cli.command.Command
import dev.argraur.aosp.builder.utils.JobManager

class Job: Command {
    private val jobManager = JobManager.getInstance()

    override fun start(args: String) {
        when (args.split(" ")[0]) {
            "status" -> {
                try {
                    println(jobManager.jobStatus(args.split(" ")[1].toInt()) ?: "")
                } catch (e: IndexOutOfBoundsException) {
                    println(jobManager.jobStatus())
                }
            }
            "stop" -> {
                try {
                    val stopped = jobManager.stopJob(args.split(" ")[1].toInt())
                    if (stopped) {
                        println("Stopped job with PID ${args.split(" ")[1]}")
                    } else {
                        println("Failed to stop the job with PID ${args.split(" ")[1]}, does it even exist? Doubt.")
                    }
                } catch (e: IndexOutOfBoundsException) {
                    println("Please specify job PID!")
                }
            }
            else -> {

            }
        }
    }

    override fun help(): String = "[\n\tstatus [PID] -> Checks status of given job or all jobs.\n\tstop [PID] -> Stops a job with given PID.\n]"
}