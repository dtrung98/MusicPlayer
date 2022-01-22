package com.ldt.musicr.common

import androidx.annotation.WorkerThread
import com.ldt.musicr.interactors.AppExecutors

object AppStartup {
    @JvmStatic
    fun onAppStartup() {
        AppExecutors.single().execute {
            initDataAsyncOnStartUp()
        }
    }

    @WorkerThread
    private fun initDataAsyncOnStartUp() {
        MediaManager.loadMediaIfNeeded()
    }
}