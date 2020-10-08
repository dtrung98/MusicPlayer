package com.ldt.musicr.addon.lastfm.rest.service;

import androidx.annotation.Nullable;


import com.ldt.musicr.addon.lastfm.rest.model.LastFmAlbum;
import com.ldt.musicr.addon.lastfm.rest.model.LastFmArtist;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */

public interface LastFMService {
    String API_KEY = "0627c43f6df6bdb7ab193077dec2f98d";
    String BASE_QUERY_PARAMETERS = "?format=json&autocorrect=1&api_key=" + API_KEY;

    @GET(BASE_QUERY_PARAMETERS + "&method=album.getinfo")
    Call<LastFmAlbum> getAlbumInfo(@Query("album") String albumName, @Query("artist") String artistName, @Nullable @Query("lang") String language);

    @GET(BASE_QUERY_PARAMETERS + "&method=artist.getinfo")
    Call<LastFmArtist> getArtistInfo(@Query("artist") String artistName, @Nullable @Query("lang") String language, @Nullable @Header("Cache-Control") String cacheControl);

    @GET
    Call<String> getPhotoPage(@Url String url, @Nullable @Query("lang") String language, @Nullable @Header("Cache-Control") String cacheControl);
}