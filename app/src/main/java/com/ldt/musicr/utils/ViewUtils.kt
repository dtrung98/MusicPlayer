package com.ldt.musicr.utils

import android.content.Context

object ViewUtils {
    @JvmStatic
    fun convertDipsToPixels(context: Context, dipValue: Float): Int {
        return (0.5f + dipValue * context.resources.displayMetrics.density).toInt()
    }

    @JvmStatic
    fun convertDipsToPixels(dipValue: Float): Int {
        return convertDipsToPixels(Utils.getApp(), dipValue)
    }
}