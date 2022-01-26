package com.ldt.musicr.ui.maintab.library.viewholder

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.ldt.musicr.R
import android.widget.TextView
import com.ldt.musicr.ui.widget.CircularPlayPauseProgressBar
import com.ldt.musicr.model.Song
import android.os.Build
import android.graphics.drawable.RippleDrawable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ldt.musicr.helper.songpreview.PreviewSong
import com.ldt.musicr.service.MusicPlayerRemote
import com.ldt.musicr.model.item.DataItem
import com.ldt.musicr.notification.ActionResponder
import com.ldt.musicr.provider.ColorProvider
import com.ldt.musicr.utils.ArtworkUtils

open class NormalSongViewHolder(parent: ViewGroup, private val actionResponder: ActionResponder?, layoutInflater: LayoutInflater? = null) : RecyclerView.ViewHolder((layoutInflater ?: LayoutInflater.from(parent.context)).inflate(R.layout.item_song_normal, parent, false)), BindPlayingState, BindPreviewState, BindTheme {
    private val numberView: TextView = itemView.findViewById(R.id.number)
    private val titleTextView: TextView = itemView.findViewById(R.id.title)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.description)
    private val imageView: ImageView = itemView.findViewById(R.id.image)
    private val quickPlayPauseView: ImageView = itemView.findViewById(R.id.quick_play_pause)
    private val menuView: View = itemView.findViewById(R.id.menu_button)
    private val panelView: View = itemView.findViewById(R.id.panel)
    private val previewView: CircularPlayPauseProgressBar = itemView.findViewById(R.id.preview_button)
    private var data: DataItem.SongItem? = null

    private val playState = booleanArrayOf(false, false)

    init {
        itemView.setOnClickListener {
            data?.also { ItemViewUtils.playPlaylist(it.playlistId, it.song.id, true) }
        }

        itemView.findViewById<View>(R.id.menu_button).setOnClickListener {
            data?.also { ItemViewUtils.handleMenuClickOnNormalSongItem(this, it.song, bindingAdapterPosition) }
        }

        previewView.setOnClickListener {
            data?.also { ItemViewUtils.previewSongsOrStopCurrent(it.song) }
        }
    }

    fun bind(item: DataItem.SongItem, previewingSong: PreviewSong? = null) {
        val isTheSame = this.data?.song?.id == item.song.id
        this.data = item

        numberView.text = numberView.resources.getString(R.string.str_number_placeholder, item.positionInData + 1)
        titleTextView.text = item.name
        descriptionTextView.text = item.subName
        bindPlayingState()
        bindPreviewButton(item.song, previewingSong)
        bindTheme()
        if(!isTheSame) {
            ArtworkUtils.loadAlbumArtworkBySong(imageView, item.song)
        }
    }

    override fun bindTheme() {
        bindPlayingStateColor()

        // Touch effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val baseColorStateList = ColorProvider.baseColorStateList
            (panelView.background as RippleDrawable).setColor(baseColorStateList)
            (menuView.background as RippleDrawable).setColor(baseColorStateList)
            (previewView.background as RippleDrawable).setColor(baseColorStateList)
        }

        data?.also {
            if(DataItem.hasFlag(it.flags, DataItem.FLAG_DIM)) {
                //itemView.setBackgroundColor((0xFF212121).toInt())
                itemView.setBackgroundResource(R.drawable.search_song_normal_background)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun bindPreviewButton(song: Song, previewingSong: PreviewSong?) {
        if ((previewingSong == null || previewingSong.song.id != song.id) && previewView.mode == CircularPlayPauseProgressBar.PLAYING) {
            previewView.resetProgress()
        } else if (previewingSong != null && previewingSong.song == song) {
            var timePlayed = previewingSong.timePlayed
            if (timePlayed == -1L) previewView.resetProgress() else {
                if (timePlayed < 0) timePlayed = 0
                if (timePlayed <= previewingSong.totalPreviewDuration) previewView.syncProgress(previewingSong.totalPreviewDuration, timePlayed.toInt())
            }
        }
    }

    override fun bindPreviewState(previewSong: PreviewSong?) {
        data?.also {
            bindPreviewButton(it.song, previewSong)
        }
    }

    override fun bindPlayingState() {
        val isCurrentSongNew = data?.song?.id == MusicPlayerRemote.getCurrentSong().id
        val isPlayingNew = MusicPlayerRemote.isPlaying()

        // no changes, so return
        if(playState[0] == isCurrentSongNew && playState[1] == isPlayingNew) return
        playState[0] = isCurrentSongNew
        playState[1] = isPlayingNew
        bindPlayingStateNonColor()
        bindPlayingStateColor()

        if(playState[0]) {
            imageView.setOnClickListener { MusicPlayerRemote.playOrPause() }
        } else {
            imageView.setOnClickListener(null)
            imageView.isClickable = false
            imageView.isFocusable = false
        }
    }

    private fun bindPlayingStateColor() {
        // current song
        if(playState[0]) {
            titleTextView.setTextColor(ColorProvider.baseColorL60)
            descriptionTextView.setTextColor(ColorProvider.baseColorAaa)
            quickPlayPauseView.setColorFilter(ColorProvider.baseColorL60)
        } else {
            // not current song
            titleTextView.setTextColor(ColorProvider.flatWhite)
            descriptionTextView.setTextColor(ColorProvider.flatWhiteAaa)
        }
    }

    private fun bindPlayingStateNonColor() {
        when {
            playState[0] && playState[1] -> quickPlayPauseView.setImageResource(R.drawable.ic_volume_up_black_24dp)
            playState[0] -> quickPlayPauseView.setImageResource(R.drawable.ic_volume_mute_black_24dp)
            else -> quickPlayPauseView.setImageDrawable(null)
        }
    }
}