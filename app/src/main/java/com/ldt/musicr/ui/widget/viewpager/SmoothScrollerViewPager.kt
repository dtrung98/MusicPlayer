package com.ldt.musicr.ui.widget.viewpager

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import java.lang.ref.WeakReference

/**
 * Created by Akshay.Jayakumar.
 *
 * This is a custom implementation of Viewpager with custom scroller.
 * Custom scroller will be used to override default.
 *
 * This custom implementation of the Viewpager also has the functionality to restrict
 * swiping to a  particular direction.
 *
 * This custom viewpager is used in Home to show all tabs nad fragments and also
 * is used in Media cards
 */
open class SmoothScrollerViewPager : GestureControlViewPager {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var initialXValue = 0f
    var direction: Int = SwipeDirection_ALL
    private var ownScroller: SmoothScroller? = null

    init {
        setScrollDuration(275)
        isSwipeGestureEnabled = true
        direction = SwipeDirection_ALL
    }

    fun setScrollDuration(millis: Int) {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            val wr: WeakReference<Context> = WeakReference(getContext())
            ownScroller = SmoothScroller(wr.get()!!, millis)
            scroller[this] = ownScroller
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (isSwipeAllowed(ev)) {
            super.onTouchEvent(ev)
        } else false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (isSwipeAllowed(ev)) {
            try {
                super.onInterceptTouchEvent(ev)
            } catch (e: Exception) {
                false
            }
        } else false
    }

    private fun isSwipeAllowed(event: MotionEvent): Boolean {
        if (direction == SwipeDirection_ALL) return true
        if (direction == SwipeDirection_NONE) //disable any swipe
            return false
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            initialXValue = event.getX()
            return true
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            try {
                val diffX: Float = event.getX() - initialXValue
                if (diffX > 0 && direction == SwipeDirection_RIGHT) {
                    // swipe from left to right detected
                    return false
                } else if (diffX < 0 && direction == SwipeDirection_LEFT) {
                    // swipe from right to left detected
                    return false
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return true
    }

    companion object {
        const val SwipeDirection_RIGHT = 1
        const val SwipeDirection_LEFT = 2
        const val SwipeDirection_ALL = 0
        const val SwipeDirection_NONE = -1
        const val SCROLL_MODE_DEFAULT = 250
        const val SCROLL_MODE_MEDIUM = 750
        const val SCROLL_MODE_SLOW = 1000
        const val SCROLL_MODE_ULTRA_SLOW = 2000
    }
}