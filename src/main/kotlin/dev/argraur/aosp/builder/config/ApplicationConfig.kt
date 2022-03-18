/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.config

import dev.argraur.aosp.builder.Application
import dev.argraur.aosp.builder.utils.Logger
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

class ApplicationConfig(args: Array<String>) {
    companion object {
        val TAG = ApplicationConfig::class.simpleName!!
    }
    private val logger = Logger.getInstance()
    private val parser = ArgParser(Application.APPLICATION_NAME)
    val telegram by parser.option(ArgType.Boolean, shortName = "t", description = "Run in Telegram bot mode").default(false)
    val debug by parser.option(ArgType.Boolean, shortName = "d", description = "Debug mode").default(false)

    init {
        logger.I(TAG, "Parsing input arguments...")
        parser.parse(args)
    }

    override fun toString(): String =
        StringBuilder().apply {
            append("Application config:\n")
            append("telegram -> $telegram\n")
            append("debug -> $debug")
        }.toString()
}