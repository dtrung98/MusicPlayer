package com.ldt.musicr.glide.artistimage;

public class ArtistImage {
    public static final int FIRST = 0;
    public static final int RANDOM = -1;
    public static final int LOCAL = -2;

    public final String mArtistName;

    public String getArtistName() {
        return mArtistName;
    }

    public boolean isSkipOkHttpCache() {
        return mSkipOkHttpCache;
    }

    public final boolean mSkipOkHttpCache;
    public final boolean mLoadOriginal;
    public final int mImageNumber;

    public ArtistImage(String artistName, boolean skipOkHttpCache, boolean loadOriginal, int imageNumber) {
        this.mArtistName = artistName;
        this.mSkipOkHttpCache = skipOkHttpCache;
        this.mLoadOriginal = loadOriginal;
        this.mImageNumber = imageNumber;
    }
}
