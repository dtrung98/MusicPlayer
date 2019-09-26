package com.ldt.musicr.ui.widget.bubblepicker.rendering

import android.content.Context
import androidx.annotation.ColorInt
import android.util.AttributeSet
import android.view.MotionEvent
import com.ldt.musicr.R
import com.ldt.musicr.ui.widget.GLTextureView
import com.ldt.musicr.ui.widget.bubblepicker.BubblePickerListener
import com.ldt.musicr.ui.widget.bubblepicker.model.Color
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem

/**
 * Created by irinagalata on 1/19/17.
 */
class BubblePicker : GLTextureView {

    // Background variable
    @ColorInt var background: Int = 0
        set(value) {
            field = value
            renderer.backgroundColor = Color(value)
        }

    var adapter: Adapter? = null
        set(value) {
            field = value
            renderer.adapter = value
            value?.attach(this)
        }

    var maxSelectedCount: Int? = null
        set(value) {
            renderer.maxSelectedCount = value
            field = value
        }

    var listener: BubblePickerListener? = null
        set(value) {
            renderer.listener = value
            field = value
        }

    val selectedItems: List<PickerItem?>
        get() = renderer.selectedPickerItems

    var centerImmediately = false
        set(value) {
            field = value
            renderer.centerImmediately = value
        }

    val renderer = PickerRenderer(this)
    private var startX = 0f
    private var startY = 0f
    private var previousX = 0f
    private var previousY = 0f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        //setZOrderOnTop(true)
        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        //holder.setFormat(PixelFormat.RGBA_8888)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
        attrs?.let { retrieveAttributes(attrs) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                previousX = event.x
                previousY = event.y
            }
            MotionEvent.ACTION_UP -> {
                if (isClick(event)) renderer.onTap(event.x, event.y)
                renderer.onTouchEnd()
            }
            MotionEvent.ACTION_MOVE -> {
                if (isSwipe(event)) {
                    renderer.swipe(previousX - event.x, previousY - event.y)
                    previousX = event.x
                    previousY = event.y
                } else {
                    onTouchRelease()
                }
            }
            else -> onTouchRelease()
        }

        return true
    }

    private fun onTouchRelease() = postDelayed({ renderer.onTouchEnd() }, 0)

    private fun isClick(event: MotionEvent) = Math.abs(event.x - startX) < 20 && Math.abs(event.y - startY) < 20

    private fun isSwipe(event: MotionEvent) = Math.abs(event.x - previousX) > 20 && Math.abs(event.y - previousY) > 20

    private fun retrieveAttributes(attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.BubblePicker)

        if (array.hasValue(R.styleable.BubblePicker_maxSelectedCount)) {
            maxSelectedCount = array.getInt(R.styleable.BubblePicker_maxSelectedCount, -1)
        }

        if (array.hasValue(R.styleable.BubblePicker_backgroundColor)) {
            background = array.getColor(R.styleable.BubblePicker_backgroundColor, -1)
        }

        array.recycle()
    }

}
