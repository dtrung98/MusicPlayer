package com.ldt.musicr.glide;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.ldt.musicr.glide.artistimage.ArtistImage;
import com.ldt.musicr.glide.artistimage.ArtistImageLoader;
import com.ldt.musicr.glide.audiocover.AudioFileCover;
import com.ldt.musicr.glide.audiocover.AudioFileCoverLoader;
import com.ldt.musicr.glide.palette.BitmapPaletteTranscoder;
import com.ldt.musicr.glide.palette.BitmapPaletteWrapper;


import java.io.InputStream;

@GlideModule
public class MPGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.prepend(ArtistImage.class, InputStream.class, new ArtistImageLoader.Factory(context));
        registry.prepend(AudioFileCover.class, InputStream.class, new AudioFileCoverLoader.Factory());

        registry.register(Bitmap.class, BitmapPaletteWrapper.class, new BitmapPaletteTranscoder());
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
