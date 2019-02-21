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

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Playlists;

import com.ldt.musicr.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongLoader {

    private static Cursor mCursor;

    private static long mPlaylistID;
    private static Context context;


    public static List<Song> getSongsInPlaylist(Context mContext, long playlistID) {
        ArrayList<Song> mSongList = new ArrayList<>();

        context = mContext;
        mPlaylistID = playlistID;

        final int playlistCount = countPlaylist(context, mPlaylistID);

        mCursor = makePlaylistSongCursor(context, mPlaylistID);

        if (mCursor != null) {
            boolean runCleanup = false;
            if (mCursor.getCount() != playlistCount) {
                runCleanup = true;
            }

            if (!runCleanup && mCursor.moveToFirst()) {
                final int playOrderCol = mCursor.getColumnIndexOrThrow(Playlists.Members.PLAY_ORDER);

                int lastPlayOrder = -1;
                do {
                    int playOrder = mCursor.getInt(playOrderCol);
                    if (playOrder == lastPlayOrder) {
                        runCleanup = true;
                        break;
                    }
                    lastPlayOrder = playOrder;
                } while (mCursor.moveToNext());
            }

            if (runCleanup) {

                cleanupPlaylist(context, mPlaylistID, mCursor);

                mCursor.close();
                mCursor = makePlaylistSongCursor(context, mPlaylistID);
                if (mCursor != null) {
                }
            }
        }

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final long id = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(Playlists.Members.AUDIO_ID));

                final String songName = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(AudioColumns.TITLE));

                final String artist = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(AudioColumns.ARTIST));

                final long albumId = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(AudioColumns.ALBUM_ID));

                final long artistId = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(AudioColumns.ARTIST_ID));

                final String album = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(AudioColumns.ALBUM));

                final long duration = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(AudioColumns.DURATION));

                final int durationInSecs = (int) duration / 1000;

                final int tracknumber = mCursor.getInt(mCursor
                        .getColumnIndexOrThrow(AudioColumns.TRACK));
                final String path = mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.DATA));

                final Song song = new Song(id, albumId, artistId, songName, artist, album, durationInSecs, tracknumber,path);

                mSongList.add(song);
            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

    private static void cleanupPlaylist(final Context context, final long playlistId,
                                        final Cursor cursor) {
        final int idCol = cursor.getColumnIndexOrThrow(Playlists.Members.AUDIO_ID);
        final Uri uri = Playlists.Members.getContentUri("external", playlistId);

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newDelete(uri).build());

        final int YIELD_FREQUENCY = 100;

        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                final ContentProviderOperation.Builder builder =
                        ContentProviderOperation.newInsert(uri)
                                .withValue(Playlists.Members.PLAY_ORDER, cursor.getPosition())
                                .withValue(Playlists.Members.AUDIO_ID, cursor.getLong(idCol));

                if ((cursor.getPosition() + 1) % YIELD_FREQUENCY == 0) {
                    builder.withYieldAllowed(true);
                }
                ops.add(builder.build());
            } while (cursor.moveToNext());
        }

        try {
            context.getContentResolver().applyBatch(MediaStore.AUTHORITY, ops);
        } catch (RemoteException e) {
        } catch (OperationApplicationException e) {
        }
    }


    private static int countPlaylist(final Context context, final long playlistId) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    Playlists.Members.getContentUri("external", playlistId),
                    new String[]{
                            Playlists.Members.AUDIO_ID,
                    }, null, null,
                    Playlists.Members.DEFAULT_SORT_ORDER);

            if (c != null) {
                return c.getCount();
            }
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

        return 0;
    }


    public static final Cursor makePlaylistSongCursor(final Context context, final Long playlistID) {
        final StringBuilder mSelection = new StringBuilder();
        mSelection.append(AudioColumns.IS_MUSIC + "=1");
        mSelection.append(" AND " + AudioColumns.TITLE + " != ''");
        return context.getContentResolver().query(
                Playlists.Members.getContentUri("external", playlistID),
                new String[]{
                        Playlists.Members._ID,
                        Playlists.Members.AUDIO_ID,
                        AudioColumns.TITLE,
                        AudioColumns.ARTIST,
                        AudioColumns.ALBUM_ID,
                        AudioColumns.ARTIST_ID,
                        AudioColumns.ALBUM,
                        AudioColumns.DURATION,
                        AudioColumns.TRACK,
                        Playlists.Members.PLAY_ORDER,
                        AudioColumns.DATA
                }, mSelection.toString(), null,
                Playlists.Members.DEFAULT_SORT_ORDER);
    }
    public static List<Song> getPlaylistWithListID(Context mContext, int position, long id) {
        if(mContext!=null) {
            if(true) {
                switch (position) {
                    case 0: return  LastAddedLoader.getLastAddedSongs(mContext);
                    case 1:
                        TopTracksLoader recentloader = new TopTracksLoader(mContext,TopTracksLoader.QueryType.RecentSongs);
                        return SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                    case 2:
                        TopTracksLoader topTracksLoader = new TopTracksLoader(mContext,TopTracksLoader.QueryType.TopTracks);
                        return SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                    default:
                        return PlaylistSongLoader.getSongsInPlaylist(mContext, id);
                }
            } else PlaylistSongLoader.getSongsInPlaylist(mContext, id);
        }
        return null;
    }
}
