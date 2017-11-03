package com.ldt.NewDefinitionMusicApp.MediaData.FormatDefinition;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ldt.NewDefinitionMusicApp.R;

/**
 * Created by trung on 8/20/2017.
 */

public class SimpleSong {

         int WhichSong;
        int Id_Resource ;
        String Path_Bitmap ="";
        String Data;
        String Title;
        String Artist;

        public SimpleSong(int whichSong,@Nullable int id_resource, @Nullable String path_Bitmap, @NonNull String data,@NonNull String title,@NonNull String artist)
        {
              WhichSong = whichSong;
              Id_Resource = id_resource;
             if(path_Bitmap!=null) Path_Bitmap = path_Bitmap;
            Data = data;
            Title = title;
            Artist = artist;
        }
       public static SimpleSong ConvertSong2SimpleSong(Song song)
       {
           return new SimpleSong(song.getIDKey_Global(),song.Id_Resource,song.Path_Bitmap,song.getData(),song.getTitle(),song.Artist);
       }

        public void setId_resource(int id_resource)
        {
            Id_Resource= id_resource;
        }
        public void setPath_Bitmap(String path_bitmap)
        {
            Path_Bitmap = path_bitmap;
        }
        public boolean HasBitmap()
        {
            return !(Path_Bitmap=="");
        }
        public Bitmap getNewBitmap(Activity activity)
        {
            Bitmap or;
            if(HasBitmap()) {
                or= BitmapFactory.decodeFile(Path_Bitmap);
            }
            else {
                or = BitmapFactory.decodeResource(activity.getResources(), R.drawable.default_image2);
            }
            return  or;
        }
        public int getWhichSong() {
        return WhichSong;
        }

        public void setWhichSong(int whichSong) {
        WhichSong = whichSong;
        }

        public String getData()
        {
            return Data;
        }
        public void setData(String data)
        {
            Data = data;
        }
        public String getTitle()
        {
            return Title;
        }

        public void setTitle(String title)
        {
            Title = title;
        }

        public String getArtist()
        {
            return Artist;
        }
        public void setArtist(String artist)
        {
            Artist = artist;
        }

}
