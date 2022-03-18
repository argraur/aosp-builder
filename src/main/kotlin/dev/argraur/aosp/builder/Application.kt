/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder

import dev.argraur.aosp.builder.config.ApplicationConfig
import dev.argraur.aosp.builder.config.BuildConfig
import dev.argraur.aosp.builder.services.Telegram
import dev.argraur.aosp.builder.utils.Logger

import kotlin.system.exitProcess

class Application {
    companion object {
        val TAG = Application::class.simpleName!!
        const val APPLICATION_NAME = "aosp-builder"
        private var INSTANCE: Application? = null

        fun getInstance(): Application {
            if (INSTANCE == null) {
                INSTANCE = Application()
            }
            return INSTANCE!!
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val app = getInstance()
            app.applicationConfig = ApplicationConfig(args)
            app.onConfigLoaded()
        }
    }

    lateinit var applicationConfig: ApplicationConfig
    private val logger: Logger = Logger.getInstance()
    val buildConfig: BuildConfig
    var isDebug = false

    init {
        logger.I(TAG, "aosp-builder awakening!!")
        logger.I(TAG, "Loading build config...")
        buildConfig = BuildConfig()
        isDebug = buildConfig.isDebug
    }

    fun onConfigLoaded() {
        logger.I(TAG, "Loaded application config.")
        logger.D(TAG, applicationConfig.toString())
        if (applicationConfig.telegram) {
            Telegram()
        } else {
            onDestroy(0)
        }
    }

    fun onDestroy(errorCode: Int) {
        logger.I(TAG, "Destroying application... Bye!")
        logger.onDestroy()
        exitProcess(errorCode)
    }
}