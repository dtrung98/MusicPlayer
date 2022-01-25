package com.ldt.musicr.helper.menu

import androidx.annotation.StringRes
import com.ldt.musicr.R
import androidx.appcompat.app.AppCompatActivity
import com.ldt.musicr.model.Song
import com.ldt.musicr.ui.AppActivity
import com.ldt.musicr.helper.songpreview.SongPreviewController
import com.ldt.musicr.loader.medialoader.SongLoader
import android.content.Intent
import com.ldt.musicr.util.MusicUtil
import com.ldt.musicr.ui.dialog.DeleteSongsDialog
import com.ldt.musicr.ui.dialog.AddToPlaylistDialog
import com.ldt.musicr.service.MusicPlayerRemote
import com.ldt.musicr.ui.floating.LyricFragment
import com.ldt.musicr.util.NavigationUtil
import com.ldt.musicr.util.RingtoneManager

object SongMenuHelper {
    @JvmField
    @StringRes
    val SONG_OPTION = intArrayOf( /*   R.string.play,*/
        R.string.play_next,
        R.string.play_preview,
        R.string.play_preview_all,
        R.string.add_to_queue,
        R.string.add_to_playlist,  /*    R.string.go_to_source_playlist,*/ /*        R.string.go_to_album,*/
        R.string.go_to_artist,
        R.string.show_lyric,
        R.string.edit_tag,
        R.string.detail,
        R.string.divider,
        R.string.share,
        R.string.set_as_ringtone,  /*  R.string.delete_from_playlist,*/
        R.string.delete_from_device
    )

    @StringRes
    val SONG_OPTION_STOP_PREVIEW = intArrayOf( /*   R.string.play,*/
        R.string.play_next,
        R.string.play_preview,
        R.string.str_stop_play_preview,
        R.string.add_to_queue,
        R.string.add_to_playlist,  /*    R.string.go_to_source_playlist,*/ /*        R.string.go_to_album,*/
        R.string.go_to_artist,
        R.string.show_lyric,
        R.string.edit_tag,
        R.string.detail,
        R.string.divider,
        R.string.share,
        R.string.set_as_ringtone,  /*  R.string.delete_from_playlist,*/
        R.string.delete_from_device
    )

    @JvmField
    @StringRes
    val SONG_QUEUE_OPTION = intArrayOf(
        R.string.play_next,
        R.string.play_preview,
        R.string.remove_from_queue,
        R.string.add_to_playlist,  /* R.string.go_to_source_playlist,*/ /*        R.string.go_to_album,*/
        R.string.go_to_artist,
        R.string.show_lyric,
        R.string.edit_tag,
        R.string.detail,
        R.string.divider,
        R.string.share,
        R.string.set_as_ringtone,  /*  R.string.delete_from_playlist,*/
        R.string.delete_from_device
    )

    @JvmField
    @StringRes
    val NOW_PLAYING_OPTION = intArrayOf(
        R.string.repeat_it_again,
        R.string.play_preview,  /* R.string.remove_from_queue,*/ /*  R.string.go_to_source_playlist,*/
        R.string.add_to_playlist,  /*        R.string.go_to_album,*/
        R.string.go_to_artist,
        R.string.show_lyric,
        R.string.edit_tag,
        R.string.detail,
        R.string.divider,
        R.string.share,
        R.string.set_as_ringtone,  /*  R.string.delete_from_playlist,*/
        R.string.delete_from_device
    )

    @JvmField
    @StringRes
    val SONG_ARTIST_OPTION = intArrayOf( /*   R.string.play,*/
        R.string.play_next,
        R.string.play_preview,
        R.string.add_to_queue,
        R.string.add_to_playlist,  /*    R.string.go_to_source_playlist,*/ /*        R.string.go_to_album,*/ /*R.string.go_to_artist,*/
        R.string.show_lyric,
        R.string.edit_tag,
        R.string.detail,
        R.string.divider,
        R.string.share,
        R.string.set_as_ringtone,  /*  R.string.delete_from_playlist,*/
        R.string.delete_from_device
    )

    @JvmStatic
    fun handleMenuClick(activity: AppCompatActivity, song: Song, string_res_option: Int): Boolean {
        when (string_res_option) {
            R.string.play_preview -> if (activity is AppActivity) {
                activity.songPreviewController.previewSongs(song)
            }
            R.string.play_preview_all -> {
                val preview = SongPreviewController.getInstance()
                if (preview != null) {
                    if (preview.isPlayingPreview) preview.cancelPreview()
                    val list = SongLoader.getAllSongs(activity)
                    list.shuffle()
                    var index = 0
                    var i = 0
                    while (i < list.size) {
                        if (song.id == list[i]?.id) index = i
                        i++
                    }
                    if (index != 0) list.add(0, list.removeAt(index))
                    preview.previewSongsAndStopCurrent(list)
                }
            }
            R.string.str_stop_play_preview -> {
                val controller = SongPreviewController.getInstance()
                if (controller != null && controller.isPlayingPreview) {
                    SongPreviewController.getInstance().cancelPreview()
                }
            }
            R.string.set_as_ringtone -> {
                if (RingtoneManager.requiresDialog(activity)) {
                    RingtoneManager.showDialog(activity)
                } else {
                    val ringtoneManager = RingtoneManager()
                    ringtoneManager.setRingtone(activity, song.id)
                }
                return true
            }
            R.string.share -> {
                activity.startActivity(Intent.createChooser(MusicUtil.createShareSongFileIntent(song, activity), null))
                return true
            }
            R.string.delete_from_device -> {
                DeleteSongsDialog.create(song).show(activity.supportFragmentManager, "DELETE_SONGS")
                return true
            }
            R.string.add_to_playlist -> {
                AddToPlaylistDialog.create(song).show(activity.supportFragmentManager, "ADD_PLAYLIST")
                return true
            }
            R.string.repeat_it_again, R.string.play_next -> {
                MusicPlayerRemote.playNext(song)
                return true
            }
            R.string.add_to_queue -> {
                MusicPlayerRemote.enqueue(song)
                return true
            }
            R.string.show_lyric -> LyricFragment.newInstance(song).show(activity.supportFragmentManager, LyricFragment.TAG)
            R.string.edit_tag -> return true
            R.string.detail ->                 //  SongDetailDialog.create(song).show(activity.getSupportFragmentManager(), "SONG_DETAILS");
                return true
            R.string.go_to_album ->                 // NavigationUtil.goToAlbum(activity, song.albumId);
                return true
            R.string.go_to_artist -> {
                NavigationUtil.navigateToArtist(activity, song.artistId)
                return true
            }
        }
        return false
    }
}