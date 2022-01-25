package com.ldt.musicr.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.ldt.musicr.provider.ColorProvider
import com.ldt.musicr.ui.widget.CustomTypefaceSpan

object ViewUtils {
    const val NOT_SET = -1
    const val THIN = 1
    const val NORMAL = 0
    const val MEDIUM = 2
    const val BOLD = 3

    val typefaceRobotoNormal by lazy { Typeface.defaultFromStyle(Typeface.NORMAL) }
    val typefaceRobotoBold by lazy { Typeface.defaultFromStyle(Typeface.BOLD) }
    val typefaceRobotoMedium by lazy { Typeface.create("sans-serif-medium", Typeface.NORMAL) }

    @JvmStatic
    fun convertDipsToPixels(context: Context, dipValue: Float): Int {
        return (0.5f + dipValue * context.resources.displayMetrics.density).toInt()
    }

    @JvmStatic
    fun convertDipsToPixels(dipValue: Float): Int {
        return convertDipsToPixels(Utils.getApp(), dipValue)
    }

    fun setSpanToHighlightPositions(stringBuilder: Spannable, start: Int, end: Int, typefaceValue: Int) {
        setSpanToHighlightPositions(stringBuilder, start, end, ColorProvider.baseColor, typefaceValue)
    }

    /**
     * @param typefaceValue a typeface value that is defined in [RobotoTypefaceManager] or -1 to not apply custom typeface
     */
    fun setSpanToHighlightPositions(stringBuilder: Spannable, start: Int, end: Int, foregroundColor: Int, typefaceValue: Int, flag: Int = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) {
        if (foregroundColor != 0) {
            val colorSpan = ForegroundColorSpan(foregroundColor)
            stringBuilder.setSpan(
                colorSpan,
                start,
                end,
                flag
            )
        }

        val typeface: Typeface? = when (typefaceValue) {
            NOT_SET -> null
            NORMAL -> typefaceRobotoNormal
            MEDIUM -> typefaceRobotoMedium
            BOLD -> typefaceRobotoBold
            else -> null
        }
        if (typeface != null) {
            stringBuilder.setSpan(CustomTypefaceSpan("", typeface), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        }
    }

    fun getHighlightedText(orgText: CharSequence, spanPosArray: MutableList<Int>, foregroundColor: Int = ColorProvider.baseColor, typefaceValue: Int = NOT_SET): CharSequence {
        val spannable: Spannable = SpannableString(orgText)
        try {
            var i = 0
            while (i < spanPosArray.size - 1) {
                if (spanPosArray[i] >= 0 && spanPosArray[i + 1] > spanPosArray[i]) {
                    setSpanToHighlightPositions(spannable,
                        spanPosArray[i],
                        spanPosArray[i + 1],
                        foregroundColor,
                        typefaceValue,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                i += 2
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return spannable
    }
}