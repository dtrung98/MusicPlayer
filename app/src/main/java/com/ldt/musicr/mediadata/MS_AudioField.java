package com.ldt.musicr.mediadata;

import android.provider.MediaStore;

import com.ldt.musicr.mediadata.oldmodel.Field;

import java.util.ArrayList;

/**
 * Created by trung on 8/12/2017.
 */

public class MS_AudioField {

   static boolean INIT_SONG_FIELDS = false;
   static public ArrayList<Field> songFields = new ArrayList<>();
   static public ArrayList<Field> playlistFields = new ArrayList<>();
   static public ArrayList<Field> albumFields = new ArrayList<>();
   static public ArrayList<Field> artistFields = new ArrayList<>();

   static boolean INIT_PLAYLIST_FIELDS = false;
   static boolean INIT_ARTIST_FIELDS = false;
   public static void clearFieldList(ArrayList<ArrayList<String>> arrayList)
    {
        int len = arrayList.size();
        for(int i=0;i<len;i++)
            arrayList.get(i).clear();
        arrayList.clear();
    }
    public static void InitSongFields()
    {
        if(INIT_SONG_FIELDS) return;
        INIT_SONG_FIELDS = true;
        songFields.add(new Field(MediaStore.Audio.Media.DISPLAY_NAME,"Display Name"));
           songFields.add(new Field(MediaStore.Audio.Media.CONTENT_TYPE,"Content type"));
          songFields.add(new Field(MediaStore.Audio.Media.DEFAULT_SORT_ORDER,"Default sort order"));
           songFields.add(new Field(MediaStore.Audio.Media.ENTRY_CONTENT_TYPE,"Entry content type"));
         songFields.add(new Field(MediaStore.Audio.Media._COUNT,"Count"));
        songFields.add(new Field(MediaStore.Audio.Media._ID,"ID"));
            songFields.add(new Field(MediaStore.Audio.Media.CONTENT_TYPE,"Content type"));
           songFields.add(new Field(MediaStore.Audio.Media.EXTRA_MAX_BYTES,"Extra max bytes"));
          songFields.add(new Field(MediaStore.Audio.Media.RECORD_SOUND_ACTION,"Record sound action"));
          songFields.add(new Field(MediaStore.Audio.Media.ALBUM,"Album"));
        songFields.add(new Field(MediaStore.Audio.Media.ALBUM_ID,"Album ID"));
        songFields.add(new Field(MediaStore.Audio.Media.ALBUM_KEY,"Album Key"));
          songFields.add(new Field(MediaStore.Audio.Media.BOOKMARK,"Bookmark"));
        songFields.add(new Field(MediaStore.Audio.Media.COMPOSER,"Composer"));
         songFields.add(new Field(MediaStore.Audio.Media.ARTIST,"Artist"));
        songFields.add(new Field(MediaStore.Audio.Media.ARTIST_ID,"Artist ID"));
        songFields.add(new Field(MediaStore.Audio.Media.ARTIST_KEY,"Artist Key"));
          songFields.add(new Field(MediaStore.Audio.Media.DATA,"Data"));
        songFields.add(new Field(MediaStore.Audio.Media.DURATION,"Duration"));
           songFields.add(new Field(MediaStore.Audio.Media.DATE_ADDED,"Date Added"));
          songFields.add(new Field(MediaStore.Audio.Media.DATE_MODIFIED,"Date Modified"));
        songFields.add(new Field(MediaStore.Audio.Media.TITLE,"Title"));
        songFields.add(new Field(MediaStore.Audio.Media.TITLE_KEY,"Title Key"));
          songFields.add(new Field(MediaStore.Audio.Media.HEIGHT,"Height"));
         songFields.add(new Field(MediaStore.Audio.Media.WIDTH,"Width"));
         songFields.add(new Field(MediaStore.Audio.Media.TRACK,"Track"));
         songFields.add(new Field(MediaStore.Audio.Media.YEAR,"Year"));
        songFields.add(new Field(MediaStore.Audio.Media.MIME_TYPE,"Mime Type"));
         songFields.add(new Field(MediaStore.Audio.Media.IS_ALARM,"Is Alarm"));
        songFields.add(new Field(MediaStore.Audio.Media.IS_MUSIC,"Is Music"));
        songFields.add(new Field(MediaStore.Audio.Media.IS_NOTIFICATION,"Is Notification"));
        songFields.add(new Field(MediaStore.Audio.Media.IS_PODCAST,"Is PodCast"));
        songFields.add(new Field(MediaStore.Audio.Media.IS_RINGTONE,"Is Ringtone"));
    }
    public void InitPlaylistFields()
    {
        if(INIT_PLAYLIST_FIELDS) return; INIT_PLAYLIST_FIELDS = true;
        playlistFields.add(new Field(MediaStore.Audio.Playlists._ID,""));
        playlistFields.add(new Field(MediaStore.Audio.Playlists._COUNT,""));
        playlistFields.add(new Field(MediaStore.Audio.Playlists.CONTENT_TYPE,""));
        playlistFields.add(new Field(MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER,""));
        playlistFields.add(new Field(MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE,""));
        playlistFields.add(new Field(MediaStore.Audio.Playlists.DATA,""));
        playlistFields.add(new Field(MediaStore.Audio.Playlists.DATE_ADDED,""));
        playlistFields.add(new Field(MediaStore.Audio.Playlists.DATE_MODIFIED,""));
        playlistFields.add(new Field(MediaStore.Audio.Playlists.NAME,""));
    }

    public void InitArtistFields()
    {
        if(INIT_ARTIST_FIELDS) return; INIT_ARTIST_FIELDS = true;
        artistFields .add(new Field(MediaStore.Audio.Artists.CONTENT_TYPE,"Content Type"));
        artistFields .add(new Field (MediaStore.Audio.Artists.DEFAULT_SORT_ORDER,"Default Sort Order"));
        artistFields .add(new Field(MediaStore.Audio.Artists._COUNT,"Count"));
        artistFields .add(new Field(MediaStore.Audio.Artists._ID,"ID"));
        artistFields .add(new Field(MediaStore.Audio.Artists.ARTIST,"Artist"));
        artistFields .add(new Field(MediaStore.Audio.Artists.ARTIST_KEY,"Artist Key"));
        artistFields .add(new Field(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,"Number Of Albums"));
        artistFields .add(new Field(MediaStore.Audio.Artists.NUMBER_OF_TRACKS,"Number Of Tracks"));
        artistFields .add(new Field(MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE,"Entry Content Type"));
    }

}
