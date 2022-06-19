package com.ldt.musicr.glide

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.elmurzaev.music.R

object MediaCoverRequest {
  val defaultDiskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.NONE
  const val defaultErrorImage = R.drawable.ic_music_style
  const val defaultAnimation = android.R.anim.fade_in
}