/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.utils

import dev.argraur.aosp.builder.utils.observer.Observable
import dev.argraur.aosp.builder.utils.observer.Observer

class JobManager: Observer {
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
    private val tasks = mutableMapOf<Job, Observer>()
    private val pids = mutableMapOf<Int, Job>()
    private var pid = 0

    fun addTask(observer: Observer, job: Job): Int {
        tasks[job] = observer
        job.addObserver(this)
        job.addObserver(observer)
        pids[pid] = job
        return pid++
    }

    fun stopJob(pid: Int): Boolean {
        if (pid in pids && pids[pid] in tasks) {
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
        return if (pid in pids) {
            pids[pid]!!.status()
        } else {
            logger.E(TAG, "No such job with PID $pid")
            null
        }
    }

    fun jobStatus(): String {
        logger.D(TAG, "Requested status of all jobs.")
        val result = java.lang.StringBuilder()
        result.append("Current jobs: ${pids.size}\n\n")
        pids.forEach { (_, job) ->
            result.append("${job.status()}\n\n")
        }
        return result.dropLast(2).toString()
    }

    override fun onObserverEvent(o: Observable) {
        tasks.remove(o)
        pids.forEach { (pid, job) ->
            if (job == o) {
                pids.remove(pid)
            }
        }
    }
}