package com.ldt.musicr.ui.widget.bubblepicker.adapter

import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem

interface BubblePickerAdapter {

    val totalCount: Int

    fun getItem(position: Int): PickerItem

}