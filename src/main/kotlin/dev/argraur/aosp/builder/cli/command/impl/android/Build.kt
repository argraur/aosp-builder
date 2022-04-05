/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli.command.impl.android

import dev.argraur.aosp.builder.android.KernelBuild
import dev.argraur.aosp.builder.android.SystemBuild
import dev.argraur.aosp.builder.android.config.DeviceBuildConfig
import dev.argraur.aosp.builder.cli.command.JobCommand
import dev.argraur.aosp.builder.utils.observer.Observable

class Build: JobCommand {
    private lateinit var kernelBuild: KernelBuild
    private lateinit var systemBuild: SystemBuild

    override fun start(args: String) {
        try {
            val config = DeviceBuildConfig.getInstance()
            if (config.kernelName.isNotEmpty()) {
                kernelBuild = KernelBuild(this@Build)
            }
            if (config.sourceRoot.isNotEmpty()) {
                systemBuild = SystemBuild(this@Build)
            }
            if (config.sourceRoot.isEmpty() && config.kernelName.isEmpty()) {
                println("Nothing to do! Check build config.")
            }
        } catch (ex: Exception) {
            when (ex) {
                is IllegalStateException -> println("No build config found.\n${ex.message!!}")
                else -> println(ex.message!!)
            }
        }
    }

    override fun onObserverEvent(o: Observable) {}
}