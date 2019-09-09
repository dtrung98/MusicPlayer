package com.ldt.musicr.glide.artistimage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.ldt.musicr.App;
import com.ldt.musicr.addon.lastfm.rest.LastFMRestClient;
import com.ldt.musicr.util.ArtistSignatureUtil;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */

public class ArtistImageLoader implements ModelLoader<ArtistImage, InputStream> {
    // we need these very low values to make sure our artist image loading calls doesn't block the image loading queue
    private static final int TIMEOUT = 750;

    private LastFMRestClient lastFMClient;
    private ModelLoader<GlideUrl, InputStream> urlLoader;

    public ArtistImageLoader(LastFMRestClient lastFMRestClient, ModelLoader<GlideUrl, InputStream> urlLoader) {
        this.lastFMClient = lastFMRestClient;
        this.urlLoader = urlLoader;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull ArtistImage artistImage, int width, int height, @NonNull Options options) {
        return new LoadData<>( ArtistSignatureUtil.getInstance(App.getInstance()).getArtistSignature(artistImage.mArtistName, artistImage.mLoadOriginal,artistImage.mImageNumber),new ArtistImageFetcher(lastFMClient,artistImage,urlLoader,width,height,options));
//        return new LoadData<>(new ObjectKey(String.valueOf(artistImage.getArtistName())),new ArtistImageFetcher(lastFMClient,artistImage,urlLoader,width,height,options));
     //   return new LoadData<>( ArtistSignatureUtil.getInstance(App.getInstance()).getArtistSignature(artistImage.getArtistName()), new ArtistImageFetcher(lastFMClient,artistImage,urlLoader,width,height, options));
    }

    @Override
    public boolean handles(@NonNull ArtistImage artistImage) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<ArtistImage, InputStream> {
        private LastFMRestClient lastFMClient;
        private OkHttpUrlLoader.Factory okHttpFactory;

        public Factory(Context context) {
            okHttpFactory = new OkHttpUrlLoader.Factory(new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .build());
            lastFMClient = new LastFMRestClient(LastFMRestClient.createDefaultOkHttpClientBuilder(context)
                    .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                    .build());
        }

        @NonNull
        @Override
        public ModelLoader<ArtistImage, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ArtistImageLoader(lastFMClient,okHttpFactory.build(multiFactory));
        }

        @Override
        public void teardown() {
          //  okHttpFactory.teardown();
        }
    }
}

