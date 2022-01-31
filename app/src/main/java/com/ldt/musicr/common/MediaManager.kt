package com.ldt.musicr.common

import com.ldt.musicr.App
import com.ldt.musicr.helper.extension.post
import com.ldt.musicr.loader.medialoader.ArtistLoader
import com.ldt.musicr.loader.medialoader.PlaylistLoader
import com.ldt.musicr.loader.medialoader.PlaylistSongLoader
import com.ldt.musicr.loader.medialoader.SongLoader
import com.ldt.musicr.model.Artist
import com.ldt.musicr.model.Song
import com.ldt.musicr.model.mp.MPPlaylist
import com.ldt.musicr.notification.EventKey
import com.ldt.musicr.notification.MediaKey
import com.ldt.musicr.utils.Utils
import com.zalo.gitlabmobile.notification.MessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object MediaManager {
    private val allSongs = Collections.synchronizedList(mutableListOf<Song>())

    /**
     * Song Id to Song
     */
    private val mapIdToSong = Collections.synchronizedMap(hashMapOf<Int, Song>())

    /**
     * Playlist Id to Playlist
     */
    private val mapIdToPlaylist = Collections.synchronizedMap(hashMapOf<Int, MPPlaylist>())

    /**
     * Song Id to Top Hit Score
     */
    private val mapSongIdToTopHit = Collections.synchronizedMap(hashMapOf<Int, Float>())

    /**
     * Artist Id to Artist
     */
    private val mapArtistIdToArtist = Collections.synchronizedMap(hashMapOf<Int, Artist>())
    private val allArtists = Collections.synchronizedList(mutableListOf<Artist>())

    private val isLoadedMediaInternal = AtomicBoolean(false)
    private val isLoadingMediaInternal = AtomicBoolean(false)
    private val isLoadedSongsInternal = AtomicBoolean(false)
    private val isLoadedPlaylistsInternal = AtomicBoolean(false)
    private val isLoadedArtistsInternal = AtomicBoolean(false)

    fun getSong(id: Int): Song? {
        return mapIdToSong[id]
    }

    fun getPlaylist(id: Int): MPPlaylist? {
        return mapIdToPlaylist[id]
    }

    fun getArtist(id: Int): Artist? {
        return mapArtistIdToArtist[id]
    }

    val isLoadedMedia: Boolean get() = isLoadedMediaInternal.get()
    val isLoadingMedia: Boolean get() = isLoadingMediaInternal.get()
    val isLoadedSongs: Boolean get() = isLoadedSongsInternal.get()
    val isLoadedPlaylists: Boolean get() = isLoadedMediaInternal.get()
    val isLoadedArtists: Boolean get() = isLoadedMediaInternal.get()

    @JvmStatic
    fun loadMediaIfNeeded() {
        if(isLoadedMediaInternal.get() || isLoadingMediaInternal.get()) return
        isLoadingMediaInternal.set(true)

        // Load Media
        loadAllSongs()
        loadAllPlaylists()
        loadAllArtists()

        isLoadingMediaInternal.set(false)
        isLoadedMediaInternal.set(true)
        EventKey.OnLoadedMedia.post()
    }

    private fun loadAllSongs() {
        mapIdToSong.clear()
        allSongs.clear()
        allSongs.addAll(SongLoader.getAllSongs(App.getInstance()))
        allSongs.forEach {
            mapIdToSong[it.id] = it
        }

        isLoadedSongsInternal.set(true)
        EventKey.OnLoadedSongs.post()
    }

    private fun loadAllPlaylists() {
        mapIdToPlaylist.clear()

        // Add All Songs Playlist
        val allSongPlaylist = MPPlaylist(MediaKey.PLAYLIST_ID_ALL_SONGS, "All Songs")
        allSongs.forEach {
            allSongPlaylist.songs.add(it.id)
        }
        mapIdToPlaylist[allSongPlaylist.id] = allSongPlaylist

        // Add Queue Playlist

        // Add All System Playlists
        val systemPlaylists = PlaylistLoader.getAllPlaylists(App.getInstance())
        systemPlaylists.forEach {
            val songIds = PlaylistSongLoader.getPlaylistSongIds(App.getInstance(), it.id)
            val mpPlaylist = MPPlaylist(it.id, it.name)
            mpPlaylist.songs.addAll(songIds)
            mapIdToPlaylist[mpPlaylist.id] = mpPlaylist
        }

        // Add Auto-generated Playlist

        // Add User Playlist

        isLoadedPlaylistsInternal.set(true)
        EventKey.OnLoadedPlaylists.post()
    }

    private fun loadAllArtists() {
        mapArtistIdToArtist.clear()
        allArtists.clear()
        val systemArtists = ArtistLoader.getAllArtists(Utils.getApp())
        allArtists.addAll(systemArtists)
        systemArtists.forEach {
            mapArtistIdToArtist[it.id] = it
        }

        isLoadedArtistsInternal.set(true)
        EventKey.OnLoadedArtists.post()
    }

    @JvmStatic
    fun clearMedia() {
        mapIdToSong.clear()
        mapIdToPlaylist.clear()

        isLoadingMediaInternal.set(false)
        isLoadedMediaInternal.set(false)

        isLoadedSongsInternal.set(false)
        isLoadedPlaylistsInternal.set(false)
        isLoadedArtistsInternal.set(false)
    }
}