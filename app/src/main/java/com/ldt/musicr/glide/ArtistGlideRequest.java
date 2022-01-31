package com.ldt.musicr.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;


import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import com.ldt.musicr.App;
import com.ldt.musicr.glide.artistimage.ArtistImage;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.util.ArtistSignatureUtil;
import com.ldt.musicr.util.CustomArtistImageUtil;

import static com.ldt.musicr.glide.artistimage.ArtistImage.FIRST;


public class ArtistGlideRequest {

    private static final DiskCacheStrategy DEFAULT_DISK_CACHE_STRATEGY = DiskCacheStrategy.AUTOMATIC;
    public static final int DEFAULT_ANIMATION = android.R.anim.fade_in;

    public static class Builder {
        final RequestManager requestManager;
        final Artist artist;
        boolean noCustomImage = false;
        boolean forceDownload;
        boolean mLoadOriginalImage = false;
        int mImageNumber = FIRST;

        public static Builder from(@NonNull RequestManager requestManager, Artist artist) {
            return new Builder(requestManager, artist);
        }

        private Builder(@NonNull RequestManager requestManager, Artist artist) {
            this.requestManager = requestManager;
            this.artist = artist;
        }

        public PaletteBuilder generateBuilder(Context context) {
            return new PaletteBuilder(this, context);
        }

        public BitmapBuilder asBitmap() {
            return new BitmapBuilder(this);
        }

        public Builder noCustomImage(boolean noCustomImage) {
            this.noCustomImage = noCustomImage;
            return this;
        }

        public Builder forceDownload(boolean forceDownload) {
            this.forceDownload = forceDownload;
            return this;
        }
        public Builder requestHighResolutionArt(boolean b) {
            this.mLoadOriginalImage = b;
            return this;
        }

        public Builder whichImage(final  int whichImage) {
            this.mImageNumber = whichImage;
            return this;
        }

        public RequestBuilder<Bitmap> build() {
            return createBaseRequest(requestManager, artist, noCustomImage, forceDownload, mLoadOriginalImage, mImageNumber)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)

                    .transition(GenericTransitionOptions.with(DEFAULT_ANIMATION))
                    .priority(Priority.LOW)
                    //.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .signature(createSignature(artist,mLoadOriginalImage, mImageNumber));
        }
    }

    public static class BitmapBuilder {
        private final Builder builder;

        public BitmapBuilder(Builder builder) {
            this.builder = builder;
        }

        public RequestBuilder<Bitmap> build() {
            //noinspection unchecked
            return createBaseRequest(builder.requestManager, builder.artist, builder.noCustomImage, builder.forceDownload, builder.mLoadOriginalImage, builder.mImageNumber)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .priority(Priority.LOW)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .signature(createSignature(builder.artist,builder.mLoadOriginalImage, builder.mImageNumber));
        }
        public RequestBuilder<Drawable> buildRequestDrawable() {
            //noinspection unchecked
            return createBaseRequestForDrawable(builder.requestManager, builder.artist, builder.noCustomImage, builder.forceDownload, builder.mLoadOriginalImage, builder.mImageNumber)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .priority(Priority.LOW)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .signature(createSignature(builder.artist,builder.mLoadOriginalImage, builder.mImageNumber));
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
            //noinspection unchecked
            return createBaseRequest(builder.requestManager, builder.artist, builder.noCustomImage, builder.forceDownload, builder.mLoadOriginalImage, builder.mImageNumber)
                    //.transcode(new BitmapPaletteTranscoder(context), BitmapPaletteWrapper.class)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)

                    .transition(GenericTransitionOptions.with(DEFAULT_ANIMATION))
                    .priority(Priority.LOW)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .signature(createSignature(builder.artist,builder.mLoadOriginalImage, builder.mImageNumber));
        }

        public RequestBuilder<Drawable> buildRequestDrawable() {
            //noinspection unchecked
            return createBaseRequestForDrawable(builder.requestManager, builder.artist, builder.noCustomImage, builder.forceDownload, builder.mLoadOriginalImage, builder.mImageNumber)
                    .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
                    .priority(Priority.LOW)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .signature(createSignature(builder.artist,builder.mLoadOriginalImage, builder.mImageNumber));
        }

    }

    public static RequestBuilder<Drawable> createBaseRequestForDrawable( RequestManager requestManager, Artist artist, boolean noCustomImage, boolean forceDownload, boolean loadOriginal, int imageNumber) {
        RequestBuilder<Drawable> builder;
        boolean hasCustomImage = CustomArtistImageUtil.getInstance(App.getInstance()).hasCustomArtistImage(artist);
        if (noCustomImage || !hasCustomImage) {
            builder = requestManager.load(new ArtistImage(artist.getName(), forceDownload, loadOriginal, imageNumber));
        } else {
            builder =  requestManager.load(CustomArtistImageUtil.getFile(artist));
        }
        return builder;
    }

    public static RequestBuilder<Bitmap> createBaseRequest( RequestManager requestManager, Artist artist, boolean noCustomImage, boolean forceDownload, boolean loadOriginal, int imageNumber) {
         RequestBuilder<Bitmap> builder;
        boolean hasCustomImage = CustomArtistImageUtil.getInstance(App.getInstance()).hasCustomArtistImage(artist);
        if (noCustomImage || !hasCustomImage) {
            builder = requestManager.asBitmap().load(new ArtistImage(artist.getName(), forceDownload, loadOriginal, imageNumber));
        } else {
            builder =  requestManager.asBitmap().load(CustomArtistImageUtil.getFile(artist));
        }
        return builder;
    }

    public static Key createSignature(Artist artist, boolean isLoadOriginal, int whichImage) {
        return ArtistSignatureUtil.getInstance(App.getInstance()).getArtistSignature(artist.getName(), isLoadOriginal, whichImage);
    }
}
