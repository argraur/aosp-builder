/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.buildertests

import dev.argraur.aosp.builder.Application
import dev.argraur.aosp.builder.cli.listeners.CommandListener
import dev.argraur.aosp.builder.config.ApplicationConfig
import dev.argraur.aosp.builder.utils.Logger
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class ApplicationTest {
    @Test
    fun applicationConfigTest() {
        // Ensures ApplicationConfig class reads arguments properly
        // Also ensures application config is hotswappable.
        val app = Application.getInstance()
        app.applicationConfig = ApplicationConfig(arrayOf("-d"))
        assertTrue(app.applicationConfig.debug)
        app.applicationConfig = ApplicationConfig(arrayOf("--netDebug"))
        assertTrue(app.applicationConfig.netDebug)
        assertFalse(app.applicationConfig.debug)
        app.applicationConfig = ApplicationConfig(arrayOf("--telegram", "-d"))
        assertTrue(app.applicationConfig.telegram)
        assertTrue(app.applicationConfig.debug)
        assertFalse(app.applicationConfig.netDebug)
    }

    @Test
    fun telegramTest() {
        val app = Application.getInstance()
        app.applicationConfig = ApplicationConfig(arrayOf("--telegram"))
        assertTrue(app.applicationConfig.telegram)
        assertFalse(app.applicationConfig.debug)
        assertTrue(app.buildConfig.BOT_TOKEN != null)
        assertTrue(app.buildConfig.BOT_MASTER != null)
        app.onDestroy()
    }

    @Test
    fun loggerTest() {
        val logger = Logger.getInstance()
        val logFileName = logger.logFileName
        logger.E("LoggerTest", "Test error")
        logger.I("LoggerTest", "Test info")
        assertThrows(UninitializedPropertyAccessException::class.java) {
            logger.D("LoggerTest", "Test debug")
        }
        // Logger requires ApplicationConfig to be present to show debug messages
        Application.getInstance().applicationConfig = ApplicationConfig(arrayOf("-d"))
        logger.D("LoggerTest", "Test debug")
        logger.onDestroy()
        val logDir = File("logs")
        assertTrue(logDir.exists())
        assertTrue(logDir.isDirectory)
        val logFiles = logDir.listFiles()
        assertNotNull(logFiles)
        val file = File("logs/$logFileName")
        val log = file.readText()
        assertTrue(log.contains("Test error"))
        assertTrue(log.contains("Test info"))
        assertTrue(log.contains("Test debug"))
        // We didn't do logger.F so yeah
        assertFalse(log.contains("Test fatal"))
    }

    @Test
    fun cliListeningTest() {
        val app = Application.getInstance()
        app.applicationConfig = ApplicationConfig(arrayOf("-d"))
        app.onConfigLoaded()
        assertTrue(CommandListener.getInstance().listener.isAlive)
        CommandListener.getInstance().stopListening()
        assertFalse(CommandListener.getInstance().listener.isAlive)
        app.onDestroy()
    }
}