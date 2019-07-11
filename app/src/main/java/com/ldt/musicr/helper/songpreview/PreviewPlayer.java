package com.ldt.musicr.helper.songpreview;

import android.util.Log;

import com.ldt.musicr.service.MusicPlayerRemote;

import java.util.ArrayList;
/**
 * Play a song list with each song has a start and finish position.
 * <br>Fade in/ Fade out audio when prepare to play new song
 *
 */
public class PreviewPlayer implements PreviewSong.OnPreviewSongStateChangedListener {
    private static final String TAG = "PreviewPlayer";

    private ArrayList<PreviewSong> mPreviewSongs = new ArrayList<>();

   private SongPreviewListener mListener;
   public void setSongPreviewListener(SongPreviewListener listener) {
       mListener = listener;
   }

   public void removeSongPreviewListener() {
       mListener = null;
   }

    public void addToQueue(PreviewSong song) {
        Log.d(TAG, "addToQueue: song "+song.getSong().title);
        mPreviewSongs.add(song);
        if(mPreviewSongs.size()==1)
        play(false);
    }

    public void playNew(PreviewSong song) {
        mPreviewSongs.clear();
        mPreviewSongs.add(song);
        song.play();
    }

    private boolean mIsUserPaused = false;

    public void play(boolean stillPlayIfPaused) {
        if(stillPlayIfPaused) mIsUserPaused = true;
        if(!mPreviewSongs.isEmpty() && !mIsUserPaused) {
            Log.d(TAG, "play new preview song "+ mPreviewSongs.get(0).getSong().title);
            mPreviewSongs.get(0).setOnPreviewSongStateChangedListener(this);
            mPreviewSongs.get(0).play();
            if(mListener!=null) mListener.onSongPreviewStart(mPreviewSongs.get(0));
        } else {
            Log.d(TAG, "onPreviewSongStateChanged : isEmpty = " + mPreviewSongs.isEmpty() + ", shouldPlayMS = " + mShouldPlayingMusicServiceOnFinish + ", MPR is playing = " + MusicPlayerRemote.isPlaying());
            if (mPreviewSongs.isEmpty() && mShouldPlayingMusicServiceOnFinish && !MusicPlayerRemote.isPlaying()) {
                MusicPlayerRemote.playOrPause();
                shouldPlayingMusicServiceOnFinish(false);
            }
        }
    }

    public void pause() {
        Log.d(TAG, "paused");
        mIsUserPaused = true;
        shouldPlayingMusicServiceOnFinish(false);
        if(!mPreviewSongs.isEmpty()) mPreviewSongs.get(0).release();
    }

    public void stopSession() {

        if(!mPreviewSongs.isEmpty()) mPreviewSongs.get(0).release();
        mPreviewSongs.clear();
        mIsUserPaused = false;

    }

    @Override
    public void onPreviewSongStateChanged(PreviewSong song, int newState) {
        Log.d(TAG, "onPreviewSongStateChanged: newState = " + newState);
        if(!mPreviewSongs.isEmpty() && mPreviewSongs.get(0).equals(song)&&(newState==PreviewSong.PREPARE_TO_FINISH||newState==PreviewSong.FAILURE)) {
            song.removeListener();
            if(mListener!=null) mListener.onSongPreviewFinish(song);
            PreviewSong previewSong = mPreviewSongs.remove(0);
            Log.d(TAG, "onPreviewSongStateChanged: removing song "+previewSong.getSong().title);
            play(false);
        } else if(mListener!=null && (newState==PreviewSong.PREPARE_TO_FINISH || newState == PreviewSong.FAILURE)) {
            mListener.onSongPreviewFinish(song);
        }
        /*else if(newState == PreviewSong.PREPARE_TO_FINISH|| newState == PreviewSong.FAILURE) {
            Log.d(TAG, "onPreviewSongStateChanged : isEmpty = " + mPreviewSongs.isEmpty() + ", shouldPlayMS = " + mShouldPlayingMusicServiceOnFinish + ", MPR is playing = " + MusicPlayerRemote.isPlaying());
            if (mPreviewSongs.isEmpty() && mShouldPlayingMusicServiceOnFinish && !MusicPlayerRemote.isPlaying()) {
                MusicPlayerRemote.playOrPause();
                Log.d(TAG, "you are about to play service again ");
                shouldPlayingMusicServiceOnFinish(false);
            }
        }*/
    }

    public boolean isPlayingPreview() {
        return (!mPreviewSongs.isEmpty()&&mPreviewSongs.get(0).isPlaying());
    }

    public PreviewSong getCurrentPreviewSong() {
        if(mPreviewSongs.isEmpty()) return null;
        return mPreviewSongs.get(0);
    }
    private boolean mShouldPlayingMusicServiceOnFinish = false;

    public void shouldPlayingMusicServiceOnFinish(boolean b) {
        Log.d(TAG, "shouldPlayingMusicServiceOnFinish = "+ b);
        mShouldPlayingMusicServiceOnFinish = b;
    }
}