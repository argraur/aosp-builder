package dev.argraur.aosp.builder

import dev.argraur.aosp.builder.config.BuildConfig
import dev.argraur.aosp.builder.utils.Logger

import kotlin.system.exitProcess

class Application {
    companion object {
        val TAG = Application::class.simpleName!!
        var INSTANCE: Application? = null

        fun getInstance(): Application {
            if (INSTANCE == null) {
                INSTANCE = Application()
            }
            return INSTANCE!!
        }

        @JvmStatic
        fun main(args: Array<String>) {
            getInstance()
        }
    }

    private val logger: Logger = Logger()
    val buildConfig: BuildConfig
    var isDebug = false

    init {
        logger.I(TAG, "aosp-builder awakening!!")
        logger.I(TAG, "Loading build config...")
        buildConfig = BuildConfig()
        isDebug = true
    }

    fun onDestroy(errorCode: Int) {
        logger.onDestroy()
        exitProcess(errorCode)
    }
}