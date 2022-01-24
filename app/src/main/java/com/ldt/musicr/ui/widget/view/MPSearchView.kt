package com.ldt.musicr.ui.widget.view

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.ldt.musicr.R

class MPSearchView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
    val searchEditText: EditText
    init {
        inflate(context, R.layout.compound_search_view, this)
        searchEditText = findViewById(R.id.searchEditText)
    }
}