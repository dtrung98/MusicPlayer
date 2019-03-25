package com.ldt.musicr.glide.artistimage;

public class ArtistImage {
    public final String mArtistName;

    public String getArtistName() {
        return mArtistName;
    }

    public boolean isSkipOkHttpCache() {
        return mSkipOkHttpCache;
    }

    public final boolean mSkipOkHttpCache;

    public ArtistImage(String artistName, boolean skipOkHttpCache) {
        this.mArtistName = artistName;
        this.mSkipOkHttpCache = skipOkHttpCache;
    }
}
