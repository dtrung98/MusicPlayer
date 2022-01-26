package com.ldt.musicr.loader.artwork

import android.graphics.Bitmap
import com.ldt.musicr.model.Media

interface ArtworkGenerator<T> where T : Media{
    fun get(media: T): Bitmap
}