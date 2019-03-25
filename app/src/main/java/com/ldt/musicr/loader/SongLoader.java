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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.PreferencesUtility;

import java.util.ArrayList;
import java.util.List;

public class SongLoader {
    private static final long[] sEmptyList = new long[0];

    protected static final String BASE_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
    public static final String[] BASE_PROJECTION = new String[]{
            BaseColumns._ID,// 0
            MediaStore.Audio.AudioColumns.TITLE,// 1
            MediaStore.Audio.AudioColumns.TRACK,// 2
            MediaStore.Audio.AudioColumns.YEAR,// 3
            MediaStore.Audio.AudioColumns.DURATION,// 4
            MediaStore.Audio.AudioColumns.DATA,// 5
            MediaStore.Audio.AudioColumns.DATE_MODIFIED,// 6
            MediaStore.Audio.AudioColumns.ALBUM_ID,// 7
            MediaStore.Audio.AudioColumns.ALBUM,// 8
            MediaStore.Audio.AudioColumns.ARTIST_ID,// 9
            MediaStore.Audio.AudioColumns.ARTIST,// 10
    };

    public static ArrayList<Song> getAllSongs(Context context) {
        Cursor  cursor = makeSongCursor(context, null, null);
        return getSongsForCursor(cursor);
    }

    public static ArrayList<Song> getAllSongs(Context context, String sortOrder) {
        Cursor  cursor = makeSongCursor(context, null, null,sortOrder);
        return getSongsForCursor(cursor);
    }

    @NonNull
    private static Song getSongAtThisColumnIndex(@NonNull Cursor cursor) {
        final int id = cursor.getInt(0);
        final String title = cursor.getString(1);
        final int trackNumber = cursor.getInt(2);
        final int year = cursor.getInt(3);
        final int duration = cursor.getInt(4);
        final String data = cursor.getString(5);
        final long dateModified = cursor.getLong(6);
        final int albumId = cursor.getInt(7);
        final String albumName = cursor.getString(8);
        final int artistId = cursor.getInt(9);
        final String artistName = cursor.getString(10);

        return new Song(id, title, trackNumber, year, duration, data, dateModified, albumId, albumName, artistId, artistName);
    }

    public static ArrayList<Song> getSongsForCursor(Cursor cursor) {
        ArrayList<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongAtThisColumnIndex(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    /**
     *  Get song with a query
     * @param context
     * @param query
     * @return
     */
    @NonNull
    public static ArrayList<Song> getSongsForCursor(@NonNull final Context context, final String query) {
        Cursor cursor = makeSongCursor(context, MediaStore.Audio.AudioColumns.TITLE + " LIKE ?", new String[]{"%" + query + "%"});
        return getSongsForCursor(cursor);
    }

    public static Song getSongForCursor(Cursor cursor) {
        Song song;
        if (cursor != null && cursor.moveToFirst()) {
            song = getSongAtThisColumnIndex(cursor);
        } else {
            song = new Song();
        }
        if (cursor != null) {
            cursor.close();
        }
        return song;
    }

    public static final long[] getSongListForCursor(Cursor cursor) {
        if (cursor == null) {
            return sEmptyList;
        }
        final int len = cursor.getCount();
        final long[] list = new long[len];
        cursor.moveToFirst();
        int columnIndex = -1;
        try {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
        } catch (final IllegalArgumentException notaplaylist) {
            columnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
        for (int i = 0; i < len; i++) {
            list[i] = cursor.getLong(columnIndex);
            cursor.moveToNext();
        }
        cursor.close();
        cursor = null;
        return list;
    }

    public static Song getSongFromPath(String path, Context context) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {path};
        String[] projection = BASE_PROJECTION;
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            Song song = getSongForCursor(cursor);
            cursor.close();
            return song;
        }
        else return new Song();
    }

    public static long[] getSongListInFolder(Context context, String path) {
        String[] whereArgs = new String[]{path + "%"};
        return getSongListForCursor(makeSongCursor(context, MediaStore.Audio.Media.DATA + " LIKE ?", whereArgs, null));
    }

    public static Song getSongWithId(Context context, long id) {
        return getSongForCursor(makeSongCursor(context, "_id=" + String.valueOf(id), null));
    }

    public static List<Song> searchSongs(Context context, String searchString, int limit) {
        ArrayList<Song> result = getSongsForCursor(makeSongCursor(context, "title LIKE ?", new String[]{searchString + "%"}));
        if (result.size() < limit) {
            result.addAll(getSongsForCursor(makeSongCursor(context, "title LIKE ?", new String[]{"%_" + searchString + "%"})));
        }
        return result.size() < limit ? result : result.subList(0, limit);
    }

    public static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString) {
        final String songSortOrder = PreferencesUtility.getInstance(context).getSongSortOrder();
        return makeSongCursor(context, selection, paramArrayOfString, songSortOrder);
    }

    private static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        String selectionStatement = "is_music=1 AND title != ''";

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, BASE_PROJECTION, selectionStatement, paramArrayOfString, sortOrder);

    }

}
