package com.ldt.musicr.helper.songpreview;

import android.media.MediaPlayer;
import android.util.Log;

import java.util.ArrayList;
/**
 * Play a song list with each song has a start and finish position.
 * <br>Fade in/ Fade out audio when prepare to play new song
 *
 */
public class PreviewPlayer implements PreviewSong.OnPreviewSongStateChangedListener {
    private static final String TAG = "PreviewPlayer";


    private ArrayList<PreviewSong> mPreviewSongs = new ArrayList<>();

    public void addToQueue(PreviewSong song) {
        Log.d(TAG, "addToQueue: song "+song.getSong().title);
        mPreviewSongs.add(song);
        if(mPreviewSongs.size()==1)
        play();
    }

    public void playNew(PreviewSong song) {
        mPreviewSongs.clear();
        mPreviewSongs.add(song);
        song.play();
    }

    public void play() {
        if(!mPreviewSongs.isEmpty()) {
            Log.d(TAG, "play new preview song "+ mPreviewSongs.get(0).getSong().title);
            mPreviewSongs.get(0).setOnPreviewSongStateChangedListener(this);
            mPreviewSongs.get(0).play();
        }
    }

    public void pause() {
        if(!mPreviewSongs.isEmpty()) mPreviewSongs.get(0).release();
    }

    public void stopSession() {
        pause();
        mPreviewSongs.clear();
    }


    @Override
    public void onPreviewSongStateChanged(PreviewSong song, int newState) {
        Log.d(TAG, "onPreviewSongStateChanged: newState = " + newState);
        if(!mPreviewSongs.isEmpty() && mPreviewSongs.get(0).equals(song)&&(newState==PreviewSong.PREPARE_TO_FINISH||newState==PreviewSong.FAILURE)) {
            song.removeListener();
            PreviewSong previewSong = mPreviewSongs.remove(0);
            Log.d(TAG, "onPreviewSongStateChanged: removing song "+previewSong.getSong().title);
            play();
        }
    }
}