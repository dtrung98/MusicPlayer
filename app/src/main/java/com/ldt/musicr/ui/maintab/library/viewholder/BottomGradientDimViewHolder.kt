package com.ldt.musicr.ui.maintab.library.viewholder

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ldt.musicr.R
import com.ldt.musicr.utils.ViewUtils

class BottomGradientDimViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(createItemView(parent)) {

    companion object {
        fun createItemView(parent: ViewGroup): View {
            val dimView = View(parent.context)
            dimView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewUtils.convertDipsToPixels(dimView.context, 100f))
            dimView.setBackgroundResource(R.drawable.search_top_gradient)
            dimView.rotationX = 180f
            dimView.rotationY = 180f
            return dimView
        }
    }
}

