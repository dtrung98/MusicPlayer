package com.ldt.musicr.MediaData;

/**
 * Created by trung on 8/11/2017.
 */

public class Song_onload {

        private   long ID;
        public  void setID(int id)
        {
            ID = id;
        }
        public long getID()
        {
            return ID;
        }
        public int Id_Image;
        public String Title;
        public void setArtist(String artist) {
            Artist = artist;
        }

        public String Artist;

        public void setData(String data) {
            Data = data;
        }

        public String Data = "";
        public String AlbumArt_path = "";

        public Song_onload(long id, String albumArt_path, String name, String artist, String data) {
            ID = id;
            AlbumArt_path = albumArt_path;
            Title = name;
            Artist = artist;
            Data = data;
        }
        public Song_onload(long id, int id_image, String name, String artist, String data) {
            ID = id;
            Id_Image = id_image;
            Title = name;
            Artist = artist;
            Data = data;
        }
        public void setTitle(String title) {
            Title = title;
        }
}
