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

    var BOT_TOKEN: String? = null
    var BOT_MASTER: Long? = null

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
            BOT_TOKEN = prop.getProperty("bot.token")
            if (BOT_TOKEN == null)
                logger.E(TAG, "ERROR: Bot token wasn't found. Can't use Telegram bot")
            logger.I(TAG, "Bot token loaded: $BOT_TOKEN")
            BOT_MASTER = prop.getProperty("bot.master").toLongOrNull()
            if (BOT_TOKEN == null)
                logger.E(TAG, "ERROR: Master ID wasn't found. Can't use Telegram bot")
            logger.I(TAG, "Master ID loaded: $BOT_MASTER")
        }
    }
}