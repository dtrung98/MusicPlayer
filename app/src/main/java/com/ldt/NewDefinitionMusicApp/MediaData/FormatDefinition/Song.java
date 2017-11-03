package com.ldt.NewDefinitionMusicApp.MediaData.FormatDefinition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ldt.NewDefinitionMusicApp.R;

/**
 * Created by Le Dinh Trung on 8/11/2017.
 */

public class Song {
    public Song()
    {

    }

    int Id_Resource =0 ;
    String Path_Bitmap="";
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
    public Bitmap getNewBitmap(Context context)
    {
        Bitmap or;
        if(HasBitmap()) {
            or= BitmapFactory.decodeFile(Path_Bitmap);
        }
        else {
            or = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image2);
        }
        return  or;
    }

    String Data;
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
    String Title;
    public void setTitle(String title)
    {
        Title = title;
    }
    String Artist;
    public String getArtist()
    {
        return Artist;
    }
    public void setArtist(String artist)
    {
        Artist = artist;
    }
    int TimeCreate;
    public void setTimeCreate(int timeCreate)
    {
        TimeCreate =timeCreate;
    }
    String Genres;
    public String getGenres()
    {
        return Genres;
    }
    public void setGenres(String genres)
    {
        Genres = genres;
    }
    String Album;
    public String getAlbum()
    {
        return Album;
    }
    public void setAlbum(String album)
    {
        Album = album;
    }
    int IDKey_InApp;
    int IDKey_Global;
    int Time = 0;
    public int getTime()
    {
        return Time;
    }
    public void setTime(int time)
    {
        Time = time;
    }
    public void setIDKey_Global(int id_Global)
    {
        IDKey_Global = id_Global;
    }
    public void setID_InApp(int id_inApp)
    {
        IDKey_InApp = id_inApp;
    }
    public int getIDKey_Global()
    {
        return IDKey_Global;
    }
    public int getID_InApp()
    {
        return IDKey_InApp;
    }
    String Composer="";
    public void setComposer(String composer)
    {
        Composer = composer;
    }

    String RealName;
    String RealArtist;

    public boolean HAD_SET_REAL_ARTIST = false;
    public boolean HAD_SET_REAL_NAME = false;
    public void setRealName(String realName)
    {
        HAD_SET_REAL_NAME = true;
        RealName = realName;
    }
    public void setRealArtist(String realArtist)
    {
        HAD_SET_REAL_ARTIST =true;
        RealArtist = realArtist;
    }
    int Number_Played=0;
    public  boolean HAD_PLAYED_BEFORE = false;
    public boolean LOVED=false;
    public boolean FREEZE = false;
    public void increaseNumberPlayedByOne()
    {
        Number_Played++;
        if(!HAD_PLAYED_BEFORE) HAD_PLAYED_BEFORE = true;
    }
    public void DeleteNumberPlayed(int number_played)
    {
        if(number_played!= Number_Played) return;
        Number_Played =0;
    }
}
