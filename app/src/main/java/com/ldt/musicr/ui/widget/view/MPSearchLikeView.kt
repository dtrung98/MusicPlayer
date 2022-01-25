package com.ldt.musicr.ui.widget.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.ldt.musicr.R

class MPSearchLikeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.compound_search_like_view, this)
    }
}