package com.ldt.musicr.loader.medialoader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.ldt.musicr.App;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.provider.BlacklistStore;
import com.ldt.musicr.util.PreferenceUtil;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private static String getFileExtension(String path) {
        String extension = "";

        try {
            if (path!=null) {
                String name = path;
                extension = name.substring(name.lastIndexOf(".") +1);
            }
        } catch (Exception e) {
            extension = "";
        }
        return extension;

    }

    public static long[] doFindAudioWayOne(boolean forceOldWay) {
        long start = System.nanoTime();
        ArrayList<File> files = new ArrayList<>();
        ArrayList<File> noMedias = new ArrayList<>();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O&&!forceOldWay) {

            String root = Environment.getExternalStorageDirectory().getAbsolutePath();

            try {
                Files.walk(Paths.get(root)).filter(path -> NO_MEDIA_TAG.equals(path.getFileName().toString())).map(Path::toFile).collect(Collectors.toCollection(() -> noMedias));
            } catch (Exception ignored) {}
            for (int i = 0, noMediaPathsSize = noMedias.size(); i < noMediaPathsSize; i++) {

                try {
                    Files.walk(Paths.get(noMedias.get(i).getParent())).filter(path -> Files.isRegularFile(path) && isAudioExtension(path)).map(Path::toFile).collect(Collectors.toCollection(() -> files));
                } catch (Exception ignored) {
                }
            }
        } else {
            File root = Environment.getExternalStorageDirectory();
            getAllNoMediaDirectories(noMedias,root);
            for (File f :
                    noMedias) {
                getAllMp3FileBelow26(files,f);
            }
        }

        long end = System.nanoTime();

        for (File file : noMedias) {
            Log.d(TAG, "find non media " + file.getAbsolutePath());
        }

        for (File file : files) {
            Log.d(TAG, "find " + file.getAbsolutePath());
        }

        return new long[] {files.size(),end-start};
     }

    private static void getAllMp3FileBelow26(ArrayList<File> list, File directory) {

        File[] files = directory.listFiles();

        for (File file : files)
        {
            if (file.isFile()&&isAudioExtension(file.getAbsolutePath()))
            {
                list.add(file);
            }
            else if (file.isDirectory())
            {
               getAllMp3FileBelow26(list, file);
            }
        }
    }

    private static void getAllNoMediaDirectories(ArrayList<File> list, File directory) {

        File[] files = directory.listFiles();

        for (File file : files)
        {
            if (file.isFile()&&NO_MEDIA_TAG.equals(file.getName()))
            {
                File parent = file.getParentFile();
                list.add(parent);
                break;
            }
            else if (file.isDirectory())
            {
                getAllNoMediaDirectories(list, file);
            }
        }
    }

    private static boolean isAudioExtension(Path path) {
        if(path==null) return false;
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(path.toString()));
        return mime != null && mime.contains("audio");
    }

    private static boolean isAudioExtension(String path) {
        if(path==null) return false;
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(path));
        return mime != null && mime.contains("audio");
    }

    public static long[] doFindAudioWayTwo(@NonNull Context context) {
        long start = System.nanoTime();
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        ArrayList<Path> audioPaths = new ArrayList<>();
        List<Path> noMediaPaths = new ArrayList<>();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Files.walk(Paths.get(root)).filter(path -> NO_MEDIA_TAG.equals(path.getFileName().toString())).collect(Collectors.toCollection(() -> noMediaPaths));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0, noMediaPathsSize = noMediaPaths.size(); i < noMediaPathsSize; i++) {
                Path parent = noMediaPaths.get(i).getParent();
                noMediaPaths.set(i, parent);
            }

                try {
               Files.walk(Paths.get(root)).filter(path -> !Files.isDirectory(path) && noMediaPaths.contains(path.getParent()) && isAudioExtension(path)).collect(Collectors.toCollection(() -> audioPaths));
            } catch (Exception ignored) {}
        }
        long end = System.nanoTime();

        for (Path path : audioPaths) {
            Log.d(TAG, "find "+path);
        }

        return new long[]{audioPaths.size(),end -start};
    }

    public static void doSomething(@NonNull Context context) {
        Log.d(TAG, "find way one");

        long[] w1,w2;
        w1 = doFindAudioWayOne(true);
        w1 = doFindAudioWayOne(true);
        Log.d(TAG, "find way two");
        w2 = doFindAudioWayOne(false);

   /*     w1[1] = 0;
        for(int i= 0;i<100;i++) {
           long[] ew1 = doFindAudioWayOne(context);
           w1[1]+=ew1[1];
        }

        w1[1]/=100;

        w2[1] = 0;
        for(int i= 0;i<100;i++) {
            long[] ew2 = doFindAudioWayTwo(context);
            w2[1]+=ew2[1];
        }

        w2[1]/=100;*/

        Log.d(TAG, "way 1 find "+w1[0]+" files in "+w1[1]);
        Log.d(TAG, "way 2 find "+w2[0]+" files in "+w2[1]);

        if(true) return;
        String where = MediaStore.Files.FileColumns.MIME_TYPE  +" = 'audio/mpeg'" +// MediaStore.Files.FileColumns.MEDIA_TYPE_NONE + " AND " +
               // MediaStore.Files.FileColumns.TITLE + " LIKE %"+"50MB"+"%";
               // MediaStore.Files.FileColumns.
                " AND " +MediaStore.Files.FileColumns.MEDIA_TYPE +" != " +MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO +
                "";
        Log.d(TAG, "find in: "+MediaStore.Files.getContentUri("external").getPath());
        Log.d(TAG, "find in:"+ Environment.getExternalStorageDirectory().getAbsolutePath());
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.equals(NO_MEDIA_TAG);
            }
        };
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        String[] list = root.list (filenameFilter);
        Log.d(TAG, "find list :"+ Arrays.toString(list));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                //Files.walk(Paths.get(root.getAbsolutePath())).filter(path -> Files.isRegularFile(path)&&"audio/mpeg".equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(path)))).forEach(x -> Log.d(TAG, "find file "+x.toAbsolutePath()));
                Files.walk(Paths.get(root.getAbsolutePath())).filter(path -> NO_MEDIA_TAG.equals(path.getFileName().toString())).forEach(x -> Log.d(TAG, "find file "+x.toAbsolutePath()));
            } catch (Exception ignored) {}
        }
        if(true) return;
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
                  //  Log.d(TAG, "find hidden files: \""+path+"\"");
                    file = new File(path);
                    if(file.exists()) {
                        AudioFile audioFile = null;

                        try {
                            audioFile = AudioFileIO.read(file);
                        } catch (Exception ignored) {}

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
