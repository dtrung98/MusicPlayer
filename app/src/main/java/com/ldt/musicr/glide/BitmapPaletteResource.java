package com.ldt.musicr.glide;

import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class BitmapPaletteResource implements Resource<BitmapPaletteWrapper> {

    private final BitmapPaletteWrapper bitmapPaletteWrapper;
    private final BitmapPool bitmapPool;

    public BitmapPaletteResource(BitmapPaletteWrapper bitmapPaletteWrapper, BitmapPool bitmapPool) {
        this.bitmapPaletteWrapper = bitmapPaletteWrapper;
        this.bitmapPool = bitmapPool;
    }

    @NonNull
    @Override
    public Class<BitmapPaletteWrapper> getResourceClass() {
        return null;
    }

    @Override
    public BitmapPaletteWrapper get() {
        return bitmapPaletteWrapper;
    }

    @Override
    public int getSize() {
        return Util.getBitmapByteSize(bitmapPaletteWrapper.getBitmap());
    }

    @Override
    public void recycle() {

    }
}
