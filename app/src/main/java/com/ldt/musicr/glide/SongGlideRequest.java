package com.ldt.musicr.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.bumptech.glide.signature.MediaStoreSignature;
import com.ldt.musicr.R;
import com.ldt.musicr.glide.audiocover.AudioFileCover;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.MusicUtil;
import com.ldt.musicr.util.PreferenceUtil;

public class SongGlideRequest {

    public static final DiskCacheStrategy DEFAULT_DISK_CACHE_STRATEGY = DiskCacheStrategy.NONE;
    public static final int DEFAULT_ERROR_IMAGE = R.drawable.ic_music_style;
    public static final int DEFAULT_ANIMATION = android.R.anim.fade_in;

    public static class Builder {
        final RequestManager requestManager;
        final Song song;
        boolean ignoreMediaStore;

        public static Builder from(@NonNull RequestManager requestManager, Song song) {
            return new Builder(requestManager, song);
        }

        private Builder(@NonNull RequestManager requestManager, Song song) {
            this.requestManager = requestManager;
            this.song = song;
        }

        public PaletteBuilder generatePalette(Context context) {
            return new PaletteBuilder(this, context);
        }

        public BitmapBuilder asBitmap() {
            return new BitmapBuilder(this);
        }

        public Builder checkIgnoreMediaStore(Context context) {
            return ignoreMediaStore(PreferenceUtil.getInstance(context).ignoreMediaStoreArtwork());
        }

        public Builder ignoreMediaStore(boolean ignoreMediaStore) {
            this.ignoreMediaStore = ignoreMediaStore;
            return this;
        }

        public RequestBuilder<Bitmap> build() {
            //noinspection unchecked
            return createBaseRequest(requestManager, song, ignoreMediaStore)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(DEFAULT_ERROR_IMAGE)
                    //.transition(GenericTransitionOptions.with(DEFAULT_ANIMATION))
                    .signature(createSignature(song));
        }
    }

    public static class BitmapBuilder {
        private final Builder builder;

        public BitmapBuilder(Builder builder) {
            this.builder = builder;
        }

        public RequestBuilder<Bitmap> build() {
            return createBaseRequest(builder.requestManager, builder.song, builder.ignoreMediaStore)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(DEFAULT_ERROR_IMAGE)
                    .signature(createSignature(builder.song));
        }
    }

    public static class PaletteBuilder {
        final Context context;
        private final Builder builder;

        public PaletteBuilder(Builder builder, Context context) {
            this.builder = builder;
            this.context = context;
        }

        public RequestBuilder<Bitmap> build() {
            return createBaseRequest(builder.requestManager, builder.song, builder.ignoreMediaStore)
              //     .transcode(new BitmapPaletteTranscoder(context), BitmapPaletteWrapper.class)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .error(DEFAULT_ERROR_IMAGE)
                    .transition(GenericTransitionOptions.with(DEFAULT_ANIMATION))
                    .signature(createSignature(builder.song));
        }
    }

    public static RequestBuilder<Bitmap> createBaseRequest(RequestManager requestManager, Song song, boolean ignoreMediaStore) {
        if (ignoreMediaStore || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return requestManager.asBitmap().load(new AudioFileCover(song.data));
        } else {
            return requestManager.asBitmap().load(MusicUtil.getMediaStoreAlbumCoverUri(song.albumId));
        }
    }

    public static Key createSignature(Song song) {
        return new MediaStoreSignature("", song.dateModified, 0);
    }
}
