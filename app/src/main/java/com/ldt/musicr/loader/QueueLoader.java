/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.ldt.musicr.loader;

import android.content.Context;


import com.ldt.musicr.model.Song;

import java.util.ArrayList;
import java.util.List;


public class QueueLoader {


    private static NowPlayingCursor mCursor;

    public static List<Song> getQueueSongs(Context context) {

        mCursor = new NowPlayingCursor(context);

        final ArrayList<Song> mSongList = new ArrayList<>(SongLoader.getSongsForCursor(mCursor));
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

}
