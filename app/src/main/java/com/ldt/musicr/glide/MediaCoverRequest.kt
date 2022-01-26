package com.ldt.musicr.glide

import android.content.Context
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ldt.musicr.R
import com.bumptech.glide.RequestManager
import com.ldt.musicr.model.Song
import android.graphics.Bitmap
import android.os.Build
import com.ldt.musicr.glide.audiocover.AudioFileCover
import com.ldt.musicr.util.MusicUtil
import com.bumptech.glide.signature.MediaStoreSignature
import com.ldt.musicr.util.PreferenceUtil
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.Key
import com.ldt.musicr.model.Media

object MediaCoverRequest {
    val defaultDiskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.NONE
    const val defaultErrorImage = R.drawable.ic_music_style
    const val defaultAnimation = android.R.anim.fade_in
}