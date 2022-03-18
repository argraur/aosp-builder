/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.config

import dev.argraur.aosp.builder.utils.Logger

import java.util.Properties

class BuildConfig {
    companion object {
        val TAG = BuildConfig::class.simpleName!!
    }

    private val logger = Logger.getInstance()
    private val prop = Properties()
    private var isConfigLoaded: Boolean = false

    var isDebug: Boolean = false
    var BOT_TOKEN: String? = null

    init {
        try {
            logger.I(TAG, "Loading configuration properties...")
            prop.load(javaClass.classLoader.getResourceAsStream("config.properties"))
            logger.I(TAG, "Configuration loaded.")
            isConfigLoaded = true
        } catch (e: NullPointerException) {
            logger.E(TAG, "No configuration found. Can't use Telegram bot")
            logger.E(TAG, e.stackTraceToString())
        }
        if (isConfigLoaded) {
            isDebug = prop.getProperty("debug").toBoolean()
            if (isDebug) {
                logger.I(TAG, "!! RUNNING IN DEBUG MODE !!")
            } else {
                logger.I(TAG, "Running in release mode.")
            }
            BOT_TOKEN = prop.getProperty("bot.token")
            if (BOT_TOKEN == null)
                logger.E(TAG, "Bot token wasn't found. Can't use Telegram bot")
            if (isDebug)
                logger.I(TAG, "Bot token loaded: $BOT_TOKEN")
        }
    }
}