/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.android.config

import dev.argraur.aosp.builder.android.enums.BuildType
import dev.argraur.aosp.builder.utils.Logger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.multiple

class DeviceBuildConfig(args: String) {
    companion object {
        val TAG = DeviceBuildConfig::class.simpleName!!
        val logger = Logger.getInstance()
        private var INSTANCE: DeviceBuildConfig? = null

        fun getInstance(): DeviceBuildConfig {
            if (INSTANCE == null) {
                logger.E(TAG, "getInstance() called before instance was created!")
                throw IllegalStateException("getInstance() called before instance was created!")
            }
            return INSTANCE!!
        }

        fun setInstance(config: DeviceBuildConfig) {
            logger.D(TAG, "Setting current build config.")
            INSTANCE = config
        }

        fun fromJSON(json: String): DeviceBuildConfig {
            TODO("Not yet implemented.")
        }
    }

    private val parser = ArgParser("/config")
    val sourceRoot by parser.option(ArgType.String).default("")
    val kernelRoot by parser.option(ArgType.String).default("")
    private val buildType by parser.option(ArgType.Choice<BuildType>(), shortName = "b").default(BuildType.ENG).multiple()
    private val threads by parser.option(ArgType.Int, shortName = "j").default(1)
    private val upload by parser.option(ArgType.Boolean, shortName = "u").default(false)
    private val uploadPath by parser.option(ArgType.String).default("")
    private val product by parser.option(ArgType.String, shortName = "p").default("aosp_arm64")
    private val ccache by parser.option(ArgType.Boolean).default(false)
    private val ccacheExec by parser.option(ArgType.String).default("/usr/bin/ccache")
    private val ccachePath by parser.option(ArgType.String).default("~/.ccache")
    private val buildTarget by parser.option(ArgType.String, shortName="t").default("")
    private val kernelConfig by parser.option(ArgType.String).default("")

    fun toJSON(): String {
        TODO("Not yet implemented")
    }

    init {
        logger.D(TAG, "Parsing device build config...")
        parser.parse(args.split(" ").toTypedArray())
        logger.D(TAG, "Parsed device build config:")
        logger.D(TAG, toString())
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Current build config\n\n")
        sb.append("Source root: $sourceRoot\n")
        sb.append("Kernel root: $kernelRoot\n")
        sb.append("Kernel defconfig: $kernelConfig\n")
        sb.append("Product: $product\n")
        sb.append("Target: $buildTarget\n")
        sb.append("Build type: $buildType\n")
        sb.append("Threads: $threads\n")
        sb.append("Should upload: $upload\n")
        if (upload) {
            sb.append("Filename regex for upload: $uploadPath\n")
        }
        sb.append("Should use ccache: $ccache\n")
        if (ccache) {
            sb.append("ccache exec path: $ccacheExec\n")
            sb.append("ccache path: $ccachePath\n")
        }
        return sb.toString()
    }
}