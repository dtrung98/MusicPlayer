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
public class MyGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.append(AudioFileCover.class,InputStream.class,new AudioFileCoverLoader.Factory());
        registry.append(ArtistImage.class,InputStream.class, new ArtistImageLoader.Factory(context));
        registry.register(Bitmap.class, BitmapPaletteWrapper.class, new BitmapPaletteTranscoder());
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
