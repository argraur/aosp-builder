/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.android.enums

import dev.argraur.aosp.builder.utils.Logger

enum class BuildType {
    USER, USERDEBUG, ENG;

    companion object {
        private val TAG: String = BuildType::class.java.simpleName
        fun fromString(s: String): BuildType? =
            when (s) {
                "user" -> USER
                "userdebug" -> USERDEBUG
                "eng" -> ENG
                else -> {
                    Logger.getInstance().E(TAG, "Available build types: user, userdebug, eng")
                    null
                }
            }
    }

    override fun toString(): String =
        when (this) {
            USER -> "user"
            USERDEBUG -> "userdebug"
            ENG -> "eng"
        }
}