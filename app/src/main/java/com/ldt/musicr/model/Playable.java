package com.ldt.musicr.model;

import android.content.Context;

import com.ldt.musicr.R;

public class Playable {
    public static final int UNDEFINED = 0;
    public static final int PLAYLIST = 1;
    public static final int ALBUM = 2;
    public static final int GENRE = 3;
    public static final int ALL_SONGS = 3;

    private int mPlayableType;
    private String mTitle = "";
    private int mId;

    public void setPlayableType(int type, int id) {
        mPlayableType = type;
        mId = id;
    }

    public void setPlayableType(boolean isAllSong) {
        if(isAllSong) {
            mPlayableType = ALL_SONGS;
        }
    }

    public Playable setTitle(String title) {
        if(title!=null)  mTitle = title;
        else mTitle = "";
        return this;
    }

    public String getTitle(Context context) {
        if(mPlayableType==ALL_SONGS) return context.getString(R.string.all_songs);
        else if(mTitle.isEmpty()) return context.getString(R.string.unknown_playlist);
        else return mTitle;
    }
}
