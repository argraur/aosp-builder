/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.buildertests

import dev.argraur.aosp.builder.Application
import dev.argraur.aosp.builder.cli.CLI
import dev.argraur.aosp.builder.cli.command.JobCommand
import dev.argraur.aosp.builder.cli.listeners.CommandListener
import dev.argraur.aosp.builder.config.ApplicationConfig
import dev.argraur.aosp.builder.utils.JobManager
import dev.argraur.aosp.builder.utils.Logger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class ApplicationTest {
    @Test
    fun applicationConfigTest() {
        // Ensures ApplicationConfig class reads arguments properly
        // Also ensures application config is hotswappable.
        val app = Application()
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
        val app = Application()
        app.applicationConfig = ApplicationConfig(arrayOf("--telegram"))
        assertTrue(app.applicationConfig.telegram)
        assertFalse(app.applicationConfig.debug)
        assertTrue(app.buildConfig.BOT_TOKEN != null)
        assertTrue(app.buildConfig.BOT_MASTER != null)
    }

    @Test
    fun cliListeningTest() {
        val app = Application.getInstance()
        app.applicationConfig = ApplicationConfig(arrayOf("-d"))
        app.onConfigLoaded()
        assertTrue(CommandListener.getInstance().listener.isAlive)
        CommandListener.getInstance().stopListening()
        Thread.sleep(1) // Needs 0 < x <= 1ms to stop listening :/
        assertFalse(CommandListener.getInstance().listener.isAlive)
    }

    @Test
    fun loggerTest() {
        val logger = Logger()
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
    fun jobTest() {
        val application = Application.getInstance()
        application.applicationConfig = ApplicationConfig(arrayOf("--debug"))
        val cli = CLI.getInstance()
        val jobManager = JobManager.getInstance()
        val testCommand = "sleep 120; ls"
        val exec = cli.longCommands["exec"]!!.java.constructors.first().newInstance() as JobCommand
        exec.start(testCommand)
        Thread.sleep(10)
        val status = jobManager.jobStatus(0)
        assertNotNull(status)
        val job = jobManager.getJob(0)
        assertNotNull(job)
        assertTrue(status!!.startsWith("Command:\n$testCommand\nPID: ${job!!.process.pid()}"))
    }

    @AfterEach
    fun resetApplication() {
        Application.getInstance().onDestroy()
    }
}