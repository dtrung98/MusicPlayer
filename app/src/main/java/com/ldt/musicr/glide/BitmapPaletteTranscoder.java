package com.ldt.musicr.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.ldt.musicr.util.PhonographColorUtil;

public class BitmapPaletteTranscoder implements ResourceTranscoder<Bitmap, BitmapPaletteWrapper> {
    private final BitmapPool bitmapPool;

    public BitmapPaletteTranscoder(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public BitmapPaletteTranscoder(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Nullable
    @Override
    public Resource<BitmapPaletteWrapper> transcode(@NonNull Resource<Bitmap> toTranscode, @NonNull Options options) {
        Bitmap bitmap = toTranscode.get();
        BitmapPaletteWrapper bitmapPaletteWrapper = new BitmapPaletteWrapper(bitmap,PhonographColorUtil.generatePalette(bitmap));
        return new BitmapPaletteResource(bitmapPaletteWrapper,bitmapPool);
    }
}