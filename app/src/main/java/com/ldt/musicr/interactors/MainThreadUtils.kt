@file:JvmName("MainThreadUtils")

package com.ldt.musicr.interactors

import android.os.Handler
import android.os.Looper

private val handler = Handler(Looper.getMainLooper())

fun postOnUiThread(r: Runnable) {
    handler.post(r)
}

fun postDelayedOnUiThread(
    delayMillis: Long,
    r: Runnable
) {
    handler.postDelayed(r, delayMillis)
}

fun removeCallbacksOnUiThread(r: Runnable) {
    handler.removeCallbacks(r)
}

fun runOnUiThread(runnable: Runnable) {
    if (isMainThread()) {
        runnable.run()
    } else {
        postOnUiThread(runnable)
    }
}

fun isMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}