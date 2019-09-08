package com.ldt.musicr.glide.palette;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.ldt.musicr.util.PhonographColorUtil;

public class BitmapPaletteTranscoder implements ResourceTranscoder<Bitmap, BitmapPaletteWrapper> {
    @Nullable
    @Override
    public Resource<BitmapPaletteWrapper> transcode(@NonNull Resource<Bitmap> bitmapResource, @NonNull Options options) {
        Bitmap bitmap = bitmapResource.get();
        BitmapPaletteWrapper bitmapPaletteWrapper = new BitmapPaletteWrapper(bitmap,PhonographColorUtil.generatePalette(bitmap));
        return new BitmapPaletteResource(bitmapPaletteWrapper);
    }
}