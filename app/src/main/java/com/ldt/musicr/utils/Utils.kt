package com.ldt.musicr.utils

import android.app.Application
import com.ldt.musicr.App

object Utils {
    @JvmStatic
    fun getApp(): Application {
        return App.getInstance()
    }
}