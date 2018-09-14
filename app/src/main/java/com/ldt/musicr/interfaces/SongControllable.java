package com.ldt.musicr.interfaces;

import com.ldt.musicr.MediaData.Song_OnLoad;

import java.util.ArrayList;

/**
 *  The object which is able to control the now playing song
 *  If it change anything, other songControllable need to be change
 */
public interface SongControllable {
    /**
     *  Used to set to a song inside myself
     * @param position position of the song in the list
     */
    void setSong(int position);

    /**
     *  Used to set to a song from outside - other objects
     * @param Tag tag of the object
     * @param position position of the song in the list
     */
    void switchSong(String Tag, int position);

    /**
     *  Used to set to a new playlist from outside - other objects
     * @param Tag tag of the object
     * @param list
     * @param position
     */
    void switchPlaylist(String Tag, ArrayList<Song_OnLoad> list, int position);

    /**
     *  Used to set to a new playlist inside myself
     *  Auto - update other related objects
     * @param list
     * @param position
     */
    void setPlaylist(ArrayList<Song_OnLoad> list, int position);

    /**
     * Get current playlist
     * @return the array list of the playlist
     */
    ArrayList<Song_OnLoad> getPlaylist();

    /**
     *  Get the now playing song position in the playlist
     * @return the position of the song
     */
    int getPosition();
}
