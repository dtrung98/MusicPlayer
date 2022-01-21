package com.ldt.musicr.contract

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class AbsBindAbleHolder<I>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(item: I) {}
}