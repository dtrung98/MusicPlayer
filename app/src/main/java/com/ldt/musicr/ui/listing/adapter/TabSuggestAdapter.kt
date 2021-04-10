package com.ldt.musicr.ui.listing.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ldt.musicr.model.Playlist
import com.ldt.musicr.model.Song
import java.util.concurrent.Executors

open class TabSuggestAdapter : ListAdapter<TabSuggestAdapter.DataItem, RecyclerView.ViewHolder>(AsyncDifferConfig.Builder(DiffCallback())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()) {
    sealed class DataItem : AdapterDataItem {
        companion object {
            const val TYPE_LABEL = 1
            const val TYPE_LISTING_PLAYLIST = 2
            const val TYPE_CARD_ITEM_PLAYLIST = 3
            const val TYPE_NON_CARD_ITEM_PLAYLIST = 4
            const val TYPE_ITEM_SONG = 5
            const val TYPE_SEE_ALL_SONGS = 6
        }

        sealed class Label(val data: String, val badge: Int, val functionType: Int) : DataItem()
        sealed class ListingPlaylist(val data: MutableList<Playlist>) : DataItem()
        sealed class CardItemPlaylist(val data: Playlist) : DataItem()
        sealed class NonCardItemPlaylist(val data: Playlist) : DataItem()
        sealed class ItemSong(val data: Song) : DataItem()
        object SeeAllSongs : DataItem()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Label -> DataItem.TYPE_LABEL
            is DataItem.ListingPlaylist -> DataItem.TYPE_LISTING_PLAYLIST
            is DataItem.CardItemPlaylist -> DataItem.TYPE_CARD_ITEM_PLAYLIST
            is DataItem.NonCardItemPlaylist -> DataItem.TYPE_NON_CARD_ITEM_PLAYLIST
            is DataItem.ItemSong -> DataItem.TYPE_ITEM_SONG
            is DataItem.SeeAllSongs -> DataItem.TYPE_SEE_ALL_SONGS
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    private class DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return false
        }

    }
}

/*
class DiffListCallback<T>: DiffUtil.ItemCallback<T>() {
    val callbacks = mutableListOf<DiffUtil.ItemCallback<T>>()
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return if(oldItem == null || newItem == null) false
        else
        callbacks.indexOfFirst { it.areItemsTheSame(oldItem, newItem) } != -1
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return if(oldItem == null || newItem == null) false
        else
            callbacks.indexOfFirst { it.areItemsTheSame(oldItem, newItem) } != -1
    }

    override fun getChangePayload(oldItem: T, newItem: T): Any? {
        return super.getChangePayload(oldItem, newItem)
    }

}*/
