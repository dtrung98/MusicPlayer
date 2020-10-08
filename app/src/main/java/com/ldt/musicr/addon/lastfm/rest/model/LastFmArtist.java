package com.ldt.musicr.addon.lastfm.rest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LastFmArtist {
    @Expose
    private Artist artist;

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "LastFmArtist{" +
                "artist=" + artist +
                '}';
    }

    public static class Artist {
        @Override
        public String toString() {
            return "Artist{" +
                    "mbid='" + mbid + '\'' +
                    ", url='" + url + '\'' +
                    ", image=" + image +
                    ", bio=" + bio +
                    '}';
        }

        public String getMbid() {
            return mbid;
        }

        public void setMbid(String mbid) {
            this.mbid = mbid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String mbid="";
        public String url="";


        @Expose
        private List<Image> image = new ArrayList<>();
        @Expose
        private Bio bio;

        public List<Image> getImage() {
            return image;
        }

        public void setImage(List<Image> image) {
            this.image = image;
        }

        public Bio getBio() {
            return bio;
        }

        public void setBio(Bio bio) {
            this.bio = bio;
        }

        public static class Bio {
            @Expose
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            @Override
            public String toString() {
                return "Bio{" +
                        "content='" + content + '\'' +
                        '}';
            }
        }

        public static class Image {
            @SerializedName("#text")
            @Expose
            private String Text;
            @Expose
            private String size;

            public String getText() {
                return Text;
            }

            public void setText(String Text) {
                this.Text = Text;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            @Override
            public String toString() {
                return "Image{" +
                        "Text='" + Text + '\'' +
                        ", size='" + size + '\'' +
                        '}';
            }
        }
    }
}
