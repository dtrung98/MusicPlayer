package com.ldt.musicr.helper.extension

import java.util.*

class ResettableLazyManager {
    private val mManagedDelegates = LinkedList<ResettableLazy<Any>>()

    fun register(managed: ResettableLazy<Any>) {
        synchronized (mManagedDelegates) {
            mManagedDelegates.add(managed)
        }
    }

    fun reset() {
        synchronized (mManagedDelegates) {
            mManagedDelegates.forEach { it.reset() }
            mManagedDelegates.clear()
        }
    }
}

fun resettableManager(): ResettableLazyManager = ResettableLazyManager()