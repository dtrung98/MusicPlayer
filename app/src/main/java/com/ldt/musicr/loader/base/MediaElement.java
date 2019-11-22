package com.ldt.musicr.loader.base;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class MediaElement {
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_AUTO_PLAYLIST = 1;
    public static final int TYPE_SAVED_PLAYLIST = 2;
    public static final int TYPE_ALL_SONGS = 3;
    public static final int TYPE_SONGS_IN_GENRE = 4;
    public static final int TYPE_SONGS_IN_ARTIST = 5;
    public static final int TYPE_SONGS_IN_FOLDER = 6;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            TYPE_UNKNOWN,
            TYPE_AUTO_PLAYLIST,
            TYPE_SAVED_PLAYLIST,
            TYPE_ALL_SONGS,
            TYPE_SONGS_IN_GENRE,
            TYPE_SONGS_IN_ARTIST,
            TYPE_SONGS_IN_FOLDER
    })
    public @interface MediaType{}

    public MediaElement(@MediaType int type, String name) {
        mType = type;
        mName = name;
    }

    public int getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    private final @MediaType int mType;
    private final String mName;
    public static MediaElement create(final @MediaType int type,final String name) {
        return new MediaElement(type, name);
    }
}
