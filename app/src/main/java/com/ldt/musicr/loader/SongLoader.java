package com.ldt.musicr.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.ldt.musicr.App;
import com.ldt.musicr.model.Media;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.provider.BlacklistStore;
import com.ldt.musicr.util.PreferenceUtil;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class SongLoader {
    private static final String TAG = "SongLoader";

    protected static final String NO_MEDIA_TAG = ".nomedia";

    protected static final String BASE_SELECTION = AudioColumns.IS_MUSIC + "=1" + " AND " + AudioColumns.TITLE + " != ''";
    protected static final String[] BASE_PROJECTION = new String[]{
            BaseColumns._ID,// 0
            AudioColumns.TITLE,// 1
            AudioColumns.TRACK,// 2
            AudioColumns.YEAR,// 3
            AudioColumns.DURATION,// 4
            AudioColumns.DATA,// 5
            AudioColumns.DATE_MODIFIED,// 6
            AudioColumns.ALBUM_ID,// 7
            AudioColumns.ALBUM,// 8
            AudioColumns.ARTIST_ID,// 9
            AudioColumns.ARTIST,// 10
    };

    public static ArrayList<Song> getAllSongsIncludeHidden(@NonNull Context context) {
        ArrayList<Song> list = getAllSongs(context);
        list.addAll(getHiddenSongs(context));
        return list;
    }

    public static ArrayList<Song> getHiddenSongs(@NonNull Context context) {
        ArrayList<Song> list = new ArrayList<>();

        doSomething(context);

        return list;
    }

    public static void doSomething(@NonNull Context context) {

        String where = MediaStore.Files.FileColumns.MIME_TYPE  +" = 'audio/mpeg'" +// MediaStore.Files.FileColumns.MEDIA_TYPE_NONE + " AND " +
               // MediaStore.Files.FileColumns.TITLE + " LIKE %"+"50MB"+"%";
               // MediaStore.Files.FileColumns.
                " AND " +MediaStore.Files.FileColumns.MEDIA_TYPE +" != " +MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO +
                "";

        Log.d(TAG, "find where ["+where+"]");
        ContentResolver resolver = context.getContentResolver();
        if(resolver!=null) {
            Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                    new String[]{MediaStore.Files.FileColumns.DATA}, where, null, null);

            if(cursor!=null) Log.d(TAG, "find "+cursor.getCount()+" hidden folders");
            if (cursor != null && cursor.moveToFirst()) {
                String path;
                File file;
                do {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                  //  Log.d(TAG, "find hidden folder: \""+path+"\"");
                    file = new File(path);
                    if(file.exists()) {
                        AudioFile audioFile = null;

                        try {
                            audioFile = AudioFileIO.read(file);
                        } catch (Exception ignored) {};

                        if(audioFile!=null) {
                            Tag tag = audioFile.getTag();
                            if(tag!=null) {
                              String artist =  tag.getFirst(FieldKey.ARTIST);
                              String title = tag.getFirst(FieldKey.TITLE);
                              String album = tag.getFirst(FieldKey.ALBUM);
                              String genre = tag.getFirst(FieldKey.GENRE);
                                Log.d(TAG, "find hidden song: title = "+title+", artist = "+artist+", album = "+album+", genre = "+genre);
                            }
                        }
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
    }

    @NonNull
    public static ArrayList<Song> getAllSongs(@NonNull Context context) {
        Cursor cursor = makeSongCursor(context, null, null);
        return getSongs(cursor);
    }

    public static ArrayList<Song> getAllSongs(Context context, String sortOrder) {
        Cursor  cursor = makeSongCursor(context, null, null,sortOrder);
        return getSongs(cursor);
    }

    @NonNull
    public static ArrayList<Song> getSongs(@NonNull final Context context, final String query) {
        Cursor cursor = makeSongCursor(context, AudioColumns.TITLE + " LIKE ?", new String[]{"%" + query + "%"});
        return getSongs(cursor);
    }

    @NonNull
    public static Song getSong(@NonNull final Context context, final int queryId) {
        Cursor cursor = makeSongCursor(context, AudioColumns._ID + "=?", new String[]{String.valueOf(queryId)});
        return getSong(cursor);
    }

    @NonNull
    public static ArrayList<Song> getSongs(@Nullable final Cursor cursor) {
        ArrayList<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null)
            cursor.close();
        return songs;
    }

    @NonNull
    public static Song getSong(@Nullable Cursor cursor) {
        Song song;
        if (cursor != null && cursor.moveToFirst()) {
            song = getSongFromCursorImpl(cursor);
        } else {
            song = Song.EMPTY_SONG;
        }
        if (cursor != null) {
            cursor.close();
        }
        return song;
    }

    @NonNull
    private static Song getSongFromCursorImpl(@NonNull Cursor cursor) {
        final int id = cursor.getInt(0);
        final String title = cursor.getString(1);
        final int trackNumber = cursor.getInt(2);
        final int year = cursor.getInt(3);
        final long duration = cursor.getLong(4);
        final String data = cursor.getString(5);
        final long dateModified = cursor.getLong(6);
        final int albumId = cursor.getInt(7);
        final String albumName = cursor.getString(8);
        final int artistId = cursor.getInt(9);
        final String artistName = cursor.getString(10);

        return new Song(id, title, trackNumber, year, duration, data, dateModified, albumId, albumName, artistId, artistName);
    }

    @Nullable
    public static Cursor makeSongCursor(@NonNull final Context context, @Nullable final String selection, final String[] selectionValues) {
        return makeSongCursor(context, selection, selectionValues, PreferenceUtil.getInstance(context).getSongSortOrder());
    }

    @Nullable
    public static Cursor makeSongCursor(@NonNull final Context context, @Nullable String selection, String[] selectionValues, final String sortOrder) {

        if (selection != null && !selection.trim().equals("")) {
            selection = addMinDurationFilter(BASE_SELECTION)  + " AND " + selection;
        } else {
            selection = addMinDurationFilter(BASE_SELECTION);
        }

        // Blacklist
        ArrayList<String> paths = BlacklistStore.getInstance(context).getPaths();
        if (!paths.isEmpty()) {
            selection = generateBlacklistSelection(selection, paths.size());
            selectionValues = addBlacklistSelectionValues(selectionValues, paths);

            Log.d(TAG, "makeSongCursor: selection ["+selection+"]");

            String values = "";
            for (String value :
                    selectionValues) {
                values += "[" + value + "], ";
            }
            Log.d(TAG, "makeSongCursor: values = "+ values);
        }

        try {
            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    BASE_PROJECTION, selection, selectionValues, sortOrder);
        } catch (SecurityException e) {
            return null;
        }
    }

    private static String addMinDurationFilter(String selection) {
        return selection + " AND "+ AudioColumns.DURATION+" > " + App.getInstance().getPreferencesUtility().getMinDuration();
    }

    private static String generateBlacklistSelection(String selection, int pathCount) {
        String newSelection = selection != null && !selection.trim().equals("") ? selection + " AND " : "";
        newSelection += AudioColumns.DATA + " NOT LIKE ?";
        for (int i = 0; i < pathCount - 1; i++) {
            newSelection += " AND " + AudioColumns.DATA + " NOT LIKE ?";
        }
        return newSelection;
    }

    private static String[] addBlacklistSelectionValues(String[] selectionValues, ArrayList<String> paths) {
        if (selectionValues == null) selectionValues = new String[0];
        String[] newSelectionValues = new String[selectionValues.length + paths.size()];
        System.arraycopy(selectionValues, 0, newSelectionValues, 0, selectionValues.length);
        for (int i = selectionValues.length; i < newSelectionValues.length; i++) {
            newSelectionValues[i] = paths.get(i - selectionValues.length) + "%";
        }
        return newSelectionValues;
    }
}
