package com.ldt.musicr.ui.maintab.library.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ldt.musicr.R
import com.ldt.musicr.model.item.DataItem
import com.ldt.musicr.notification.ActionResponder

open class PlaylistViewHolder(parent: ViewGroup, private val actionResponder: ActionResponder?, layoutInflater: LayoutInflater? = null) : RecyclerView.ViewHolder((layoutInflater ?: LayoutInflater.from(parent.context)).inflate(R.layout.item_feature_playlist, parent, false)), BindPlayingState, BindTheme {
    private val imageView = itemView.findViewById<ImageView>(R.id.playlist_image)
    private val textView = itemView.findViewById<TextView>(R.id.playlist_title)

    private var data: DataItem.PlaylistItem? = null
    init {

    }

    fun bind(item: DataItem.PlaylistItem) {
        this.data = item

        //TODO: Bind playlist image
        // 1. Request worker to generate the playlist image
        // 2. Worker creates and saves into somewhere (disk) and store path info to database
        // 3. Worker alarms view item to fetch image again
        // 4. Show the image
    }

    override fun bindTheme() {

    }

    override fun bindPlayingState() {

    }
}