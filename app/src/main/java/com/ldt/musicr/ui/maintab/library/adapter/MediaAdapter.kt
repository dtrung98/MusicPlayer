package com.ldt.musicr.ui.maintab.library.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.ldt.musicr.common.AppConfig
import com.ldt.musicr.helper.songpreview.PreviewSong
import com.ldt.musicr.helper.songpreview.SongPreviewListener
import com.ldt.musicr.interactors.AppExecutors
import com.ldt.musicr.interactors.postOnUiThread
import com.ldt.musicr.model.item.DataItem
import com.ldt.musicr.notification.Action
import com.ldt.musicr.notification.ActionResponder
import com.ldt.musicr.notification.PayLoadKey
import com.ldt.musicr.notification.ViewTypeKey
import com.ldt.musicr.service.MusicPlayerRemote
import com.ldt.musicr.ui.maintab.library.viewholder.*
import java.lang.IllegalArgumentException

/**
 * Ghost Adapter that binds not only media item but also media view control
 */
open class MediaAdapter: AbsListAdapter<DataItem, RecyclerView.ViewHolder>(), SongPreviewListener, ActionResponder {
    var actionResponder: ActionResponder? = null
    var previewingSong: PreviewSong? = null

    var playingId: Int = Int.MIN_VALUE
    var playingAdapterPosition: Int = -1
    var playingState = false

    var previewingAdapterPosition: Int = -1
    var layoutInflater: LayoutInflater? = null

    override fun getItemViewType(position: Int): Int {
        return when(val it = currentList[position]) {
            is DataItem.SongItem -> ViewTypeKey.TYPE_NORMAL_SONG
            is DataItem.PlaylistItem -> 0
            is DataItem.Control -> when(it) {
                DataItem.SortingTile -> ViewTypeKey.TYPE_TILE_SORTING_NORMAL_SONG
                is DataItem.FeatureSectionTile -> ViewTypeKey.TYPE_TILE_SECTION_FEATURE
                is DataItem.Empty -> ViewTypeKey.TYPE_EMPTY
                is DataItem.Dim.TopGradientDim -> ViewTypeKey.TYPE_TOP_GRADIENT_DIM
                is DataItem.Dim.BottomGradientDim -> ViewTypeKey.TYPE_BOTTOM_GRADIENT_DIM
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ViewTypeKey.TYPE_NORMAL_SONG -> NormalSongViewHolder(parent, this)
            ViewTypeKey.TYPE_TILE_SORTING_NORMAL_SONG -> SortingTileViewHolder(parent, this)
            ViewTypeKey.TYPE_TOP_GRADIENT_DIM -> TopGradientDimViewHolder(parent)
            ViewTypeKey.TYPE_BOTTOM_GRADIENT_DIM -> BottomGradientDimViewHolder(parent)
            else -> throw IllegalArgumentException("View type $viewType is not supported by this adapter")
        }
    }

    /**
     * Bind full
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is NormalSongViewHolder -> holder.bind(getItem(position) as DataItem.SongItem, previewingSong)

        }
    }

    /**
     * Bind partially
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            // flow bind full
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach {
                when (it) {
                    PayLoadKey.CHANGE_PLAYING_STATE -> {
                        if(holder is BindPlayingState) {
                            holder.bindPlayingState()
                        }
                    }
                    PayLoadKey.CHANGE_PREVIEW_STATE -> {
                        if(holder is BindPreviewState) {
                            holder.bindPreviewState(previewingSong)
                        }
                    }
                    PayLoadKey.CHANGE_THEME -> {
                        if(holder is BindTheme) {
                            holder.bindTheme()
                        }
                    }

                }
            }
        }
    }

    override fun invoke(action: Action<Any>): Action<Any>? {
        return actionResponder?.invoke(action)
    }

    override fun onSongPreviewStart(song: PreviewSong?) {
        song ?: return
        val newPosition = currentList.indexOfFirst { (it as? DataItem.SongItem)?.song?.id == song.song.id }
        if(newPosition > -1) {
            previewingSong = song
            previewingAdapterPosition = newPosition
            notifyItemChanged(newPosition, PayLoadKey.CHANGE_PREVIEW_STATE)
        }
    }

    override fun onSongPreviewFinish(song: PreviewSong?) {
        previewingSong = null
        val lastPosition = previewingAdapterPosition
        previewingAdapterPosition = -1
        if(lastPosition != -1) {
            notifyItemChanged(lastPosition, PayLoadKey.CHANGE_PREVIEW_STATE)
        }
    }

    fun onPlayingStateChanged() {
        if(!AppConfig.isApplyOptimizedFlowOnPlayingStateChanged) {
            if(itemCount != 0) {
                notifyItemRangeChanged(0, itemCount, PayLoadKey.CHANGE_PLAYING_STATE)
            }
            return
        }

        AppExecutors.io().execute {
            val playingIdNew = MusicPlayerRemote.getCurrentSong().id
            val playingStateNew = MusicPlayerRemote.isPlaying()

            if(playingState != playingStateNew || playingId != playingIdNew) {
                val playingAdapterPositionOld = playingAdapterPosition
                playingAdapterPosition = currentList.indexOfFirst { it is DataItem.SongItem && it.song.id == playingIdNew }

                postOnUiThread {
                    if (playingAdapterPosition != playingAdapterPositionOld && playingAdapterPositionOld > -1 && playingAdapterPositionOld < itemCount) {
                        notifyItemChanged(playingAdapterPositionOld, PayLoadKey.CHANGE_PLAYING_STATE)
                    }
                    if(playingAdapterPosition != -1 && playingAdapterPosition < itemCount) {
                        notifyItemChanged(playingAdapterPosition, PayLoadKey.CHANGE_PLAYING_STATE)
                    }
                }
            }

        }
    }

    fun onThemeChanged() {
        if(itemCount != 0) {
            notifyItemRangeChanged(0, itemCount, PayLoadKey.CHANGE_THEME)
        }
    }
}

abstract class AbsListAdapter<T, V> : RecyclerView.Adapter<V>() where V : RecyclerView.ViewHolder {
    val currentList = mutableListOf<T>()
    fun getItem(position: Int): T = currentList[position]
    override fun getItemCount(): Int = currentList.size

    @SuppressLint("NotifyDataSetChanged")
    @MainThread
    fun submitList(list: List<T>) {
        currentList.clear()
        currentList.addAll(list)

        notifyDataSetChanged()
    }
}