package dev.argraur.aosp.builder

class Application {
    companion object {
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
}