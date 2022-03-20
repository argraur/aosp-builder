/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.utils

import dev.argraur.aosp.builder.telegram.commands.impl.Exec

class JobManager {
    companion object {
        private val TAG = JobManager::class.simpleName!!
        private var INSTANCE: JobManager? = null

        fun getInstance(): JobManager {
            if (INSTANCE == null) {
                INSTANCE = JobManager()
            }
            return INSTANCE!!
        }
    }

    private val logger = Logger.getInstance()
    private val tasks = mutableMapOf<Job, Exec>()
    private val pids = mutableMapOf<Int, Job>()
    private var pid = 0

    fun addTask(exec: Exec, job: Job): Int {
        tasks.put(job, exec)
        pids.put(pid, job)
        return pid++
    }

    fun removeTask(job: Job) {
        tasks[job]!!.onTaskFinish()
        tasks.remove(job)
    }

    fun stopJob(pid: Int): Boolean {
        if (pid in pids) {
            logger.D(TAG, "Stopping job with pid $pid...")
            pids[pid]!!.forceStop()
            tasks.remove(pids[pid]!!)
            pids.remove(pid)
            return true
        }
        return false
    }

    fun jobStatus(pid: Int): String? {
        logger.D(TAG, "Requested status for job PID $pid.")
        if (pid in pids) {
            return pids[pid]!!.status()
        } else {
            logger.E(TAG, "No such job with PID $pid")
            return null
        }
    }

    fun jobStatus(): String {
        logger.D(TAG, "Requested status of all jobs.")
        val result = java.lang.StringBuilder()
        result.append("Current jobs: <b>${pids.size}</b>\n\n")
        pids.forEach { (_, job) ->
            result.append("${job.status()}\n\n")
        }
        return result.dropLast(2).toString()
    }
}