package com.ldt.musicr.model.mp

import com.ldt.musicr.model.Playlist

class MPPlaylist(id: Int, name: String): Playlist(id, name) {
    val songs = mutableListOf<Int>()
}