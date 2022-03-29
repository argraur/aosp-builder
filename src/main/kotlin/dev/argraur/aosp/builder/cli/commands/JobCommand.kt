/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.cli.commands

import dev.argraur.aosp.builder.utils.Logger
import dev.argraur.aosp.builder.utils.observer.Observer

interface JobCommand: Observer {
    private val TAG: String
        get() = this.javaClass.simpleName
    private val logger: Logger
        get() = Logger.getInstance()
    fun start(args: String)
}