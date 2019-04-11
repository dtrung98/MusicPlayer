/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
 * Copyright (C) 2015 Naman Dwivedi
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.ldt.musicr.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;


import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.PreferencesUtility;
import com.ldt.musicr.util.SortOrder;

import java.util.ArrayList;
import java.util.List;

public class LastAddedLoader {

    private static Cursor mCursor;
    public static List<Song> getLastAddedSongs(Context context) {
        return getLastAddedSongs(context, SortOrder.SongSortOrder.SONG_DATE);
    }

    public static List<Song> getLastAddedSongs(Context context, String sortOrder) {

        mCursor = makeLastAddedCursor(context, sortOrder);
        ArrayList<Song> mSongList = SongLoader.getSongsForCursor(mCursor);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

    public static final Cursor makeLastAddedCursor(final Context context, String sortOrder) {
        //four weeks ago
        long fourWeeksAgo = (System.currentTimeMillis() / 1000) - (4 * 3600 * 24 * 7);
        long cutoff = PreferencesUtility.getInstance(context).getLastAddedCutoff();
        // use the most recent of the two timestamps
        if (cutoff < fourWeeksAgo) {
            cutoff = fourWeeksAgo;
        }

        final StringBuilder selection = new StringBuilder();
        selection.append(AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + AudioColumns.TITLE + " != ''");
        selection.append(" AND " + MediaStore.Audio.Media.DATE_ADDED + ">");
        selection.append(cutoff);

        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                SongLoader.BASE_PROJECTION, selection.toString(), null, sortOrder);
    }
}
