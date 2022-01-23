package com.ldt.musicr.ui.widget.viewpager

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by TrungLD2
 */
open class GestureControlViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    var isSwipeGestureEnabled = true
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // returning false will not propagate the swipe event
        return if (isSwipeGestureEnabled) {
            super.onTouchEvent(ev)
        } else false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (isSwipeGestureEnabled) {
            super.onInterceptTouchEvent(ev)
        } else false
    }
}