package com.ldt.musicr.model.dataitem

import com.ldt.musicr.model.Song

sealed class DataItem: AdapterDataItem {
    class SongItem(val song: Song, val positionInData: Int): DataItem()

    sealed class Control: DataItem()
    object SortingTile: Control()
    class FeatureSectionTile(): Control()
}