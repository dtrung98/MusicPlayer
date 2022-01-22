package com.ldt.musicr.notification

import java.util.concurrent.atomic.AtomicInteger

object MediaKey {
    const val PLAYLIST_ID_ALL_SONGS = 0
    const val PLAYLIST_ID_QUEUE = -1

    private var customId = AtomicInteger(-1000)
    fun generateId(): Int {
        return customId.getAndDecrement()
    }
}