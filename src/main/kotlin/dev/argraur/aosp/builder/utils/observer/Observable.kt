/*
 * SPDX-FileCopyrightText: 2022 Arseniy Graur <me@argraur.dev>
 * SPDX-License-Identifier: MIT
 */

package dev.argraur.aosp.builder.utils.observer

interface Observable {
    val observers: MutableList<Observer>

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    fun notifyObservers() {
        observers.forEach {
            it.onObserverEvent(this)
        }
    }
}