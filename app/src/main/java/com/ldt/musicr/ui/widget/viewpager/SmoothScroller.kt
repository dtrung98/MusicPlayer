package com.ldt.musicr.ui.widget.viewpager

import android.content.Context
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller

/**
 * Created by Akshay.Jayakumar.
 *
 * This is a custom scroller which encapsulates scrolling. Provides data for scrolling
 * animation in response to the fling gesture of the viewpager. DurationScrollMillis
 * defines the duration for scroll animation.
 */
class SmoothScroller(context: Context, durationScroll: Int) : Scroller(context, DecelerateInterpolator()) {
    private var durationScrollMillis = 1
    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, durationScrollMillis)
    }

    init {
        durationScrollMillis = durationScroll
    }
}