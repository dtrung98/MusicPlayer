package com.ldt.musicr.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ldt.musicr.glide.SongGlideRequest
import com.ldt.musicr.model.Song

object ArtworkUtils {
    @JvmStatic
    fun getBitmapRequestBuilder(context: Context, song: Song): RequestBuilder<Bitmap> {
        return SongGlideRequest.Builder.from(Glide.with(context), song)
            .ignoreMediaStore(true)
            .asBitmap().build()
    }

    @JvmStatic
    fun loadAlbumArtworkBySong(view: ImageView, song: Song, onLoadFailed: ()-> Boolean = { false }, onLoadSuccess: (Bitmap?)-> Boolean = { false }) {
        getBitmapRequestBuilder(view.context, song)
            .listener(object : RequestListener<Bitmap?> {
                override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    return onLoadSuccess(resource)
                }

                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
                    return onLoadFailed()
                }
            })
            .into(view)
    }
}