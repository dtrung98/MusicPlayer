package com.ldt.musicr.ui.widget.bubblepicker.model

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.ldt.musicr.ui.widget.bubblepicker.rendering.Item

/**
 * Created by irinagalata on 1/19/17.
 */
data class PickerItem @JvmOverloads constructor(var title: String? = null,
                                                var icon: Drawable? = null,
                                                var iconSizeUnit: Float = 1f,
                                                var iconOnTop: Boolean = true,
                                                @ColorInt var color: Int? = null,
                                                var gradient: BubbleGradient? = null,
                                                var overlayAlpha: Float = 0.5f,
                                                var typeface: Typeface = Typeface.DEFAULT,
                                                @ColorInt var textColor: Int? = null,
                                                var textSize: Float = Item.textSize,
                                                var backgroundImage: Drawable? = null,
                                                var isSelected: Boolean = false,
                                                var customData: Any? = null) {
    companion object {
        const val SIZE_RANDOM = -1f
    }
}