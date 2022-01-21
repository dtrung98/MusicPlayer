package com.ldt.musicr.ui.maintab.library.viewholder

import com.ldt.musicr.helper.songpreview.PreviewSong

interface BindTheme {
    fun bindTheme()
}

interface BindPreviewState {
    fun bindPreviewState(previewSong: PreviewSong?)
}

interface BindPlayingState {
    fun bindPlayingState()
}