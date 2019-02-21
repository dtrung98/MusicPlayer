package com.ldt.musicr.mediadata.oldmodel;

import android.graphics.Bitmap;

/**
 * Created by trung on 7/13/2017.
 */

public class Album {
        private String Title="";
        private String Artist="";
        private Bitmap BitmapImage=null;
        public Album(String title, String artist, Bitmap bitmap)
        {
              Title=title;
              Artist=artist;
            if(BitmapImage!=null)
                  BitmapImage.recycle();
                  BitmapImage= bitmap;
        }
        public void setBitmap(Bitmap bitmap)
        {
            if(BitmapImage!=null)
                BitmapImage.recycle();
            BitmapImage= bitmap;
        }
    public void setTitle(String title)
        {
            Title=title;
        }
    public void setArtist(String artist)
        {
            Artist=artist;
        }
    public Bitmap getBitmap()
        {
            return BitmapImage;
        }
    public String getTitle()
        {
            return Title;
        }
    public String getArtist()
        {
            return Artist;
        }
}
