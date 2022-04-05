/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli.command.impl.android

import dev.argraur.aosp.builder.android.config.DeviceBuildConfig
import dev.argraur.aosp.builder.cli.command.Command

class Config: Command {
    override fun start(args: String) {
        if (args.isNotEmpty()) {
            DeviceBuildConfig.setInstance(DeviceBuildConfig(args))
        }
        try {
            println(DeviceBuildConfig.getInstance())
        } catch (e: IllegalStateException) {
            println("Arguments can't be empty!")
        }
    }

    override fun help(): String = "Creates configuration for Android/Kernel build"
}