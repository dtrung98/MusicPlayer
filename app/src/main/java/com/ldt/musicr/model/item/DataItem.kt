package com.ldt.musicr.model.item

import com.ldt.musicr.model.Song

sealed class DataItem: AdapterDataItem {
    class SongItem(val song: Song, val positionInData: Int, val playlistId: Int): DataItem()

    sealed class Control: DataItem()
    object SortingTile: Control()
    sealed class FeatureSectionTile(): Control()
    sealed class Empty: Control() {
        object Error: Empty()
        object NoResult: Empty()
    }
}