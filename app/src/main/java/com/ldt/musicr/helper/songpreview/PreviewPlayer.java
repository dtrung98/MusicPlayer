package com.ldt.musicr.helper.songpreview;

import android.content.SharedPreferences;
import android.util.Log;

import com.ldt.musicr.App;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.util.PreferenceUtil;

import java.util.ArrayList;

/**
 * Play a song list with each song has a start and finish position.
 * <br>Fade in/ Fade out audio when prepare to play new song
 */
public class PreviewPlayer implements PreviewSong.OnPreviewSongStateChangedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "PreviewPlayer";

    public PreviewPlayer() {
        App.getInstance().getPreferencesUtility().registerOnSharedPreferenceChangedListener(this);
        notifyVolumePrefChanged();
        notifyBalanceChanged();
    }

    private ArrayList<PreviewSong> mPreviewSongs = new ArrayList<>();

    private SongPreviewListener mListener;

    public void setSongPreviewListener(SongPreviewListener listener) {
        mListener = listener;
    }

    public void removeSongPreviewListener() {
        mListener = null;
    }

    public void addToQueue(PreviewSong song) {
        Log.d(TAG, "addToQueue: song " + song.getSong().title);
        mPreviewSongs.add(song);
        if (mPreviewSongs.size() == 1)
            play(false);
    }

    public void playNew(PreviewSong song) {
        mPreviewSongs.clear();
        mPreviewSongs.add(song);
        song.play();
    }

    private boolean mIsUserPaused = false;

    public void play(boolean stillPlayIfPaused) {
        if (stillPlayIfPaused) mIsUserPaused = true;
        if (!mPreviewSongs.isEmpty() && !mIsUserPaused) {
            Log.d(TAG, "play new preview song " + mPreviewSongs.get(0).getSong().title);
            mPreviewSongs.get(0).setOnPreviewSongStateChangedListener(this);
            mPreviewSongs.get(0).play();
            if (mListener != null && !mPreviewSongs.isEmpty()) {
                mListener.onSongPreviewStart(mPreviewSongs.get(0));
            }
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
        if (!mPreviewSongs.isEmpty()) mPreviewSongs.get(0).release();
    }

    public void stopSession() {

        if (!mPreviewSongs.isEmpty()) mPreviewSongs.get(0).release();
        mPreviewSongs.clear();
        mIsUserPaused = false;

    }

    @Override
    public void onPreviewSongStateChanged(PreviewSong song, int newState) {
        Log.d(TAG, "onPreviewSongStateChanged: newState = " + newState);
        if (!mPreviewSongs.isEmpty() && mPreviewSongs.get(0).equals(song) && (newState == PreviewSong.PREPARE_TO_FINISH || newState == PreviewSong.FAILURE)) {
            song.removeListener();
            if (mListener != null) mListener.onSongPreviewFinish(song);
            PreviewSong previewSong = mPreviewSongs.remove(0);
            Log.d(TAG, "onPreviewSongStateChanged: removing song " + previewSong.getSong().title);
            play(false);
        } else if (mListener != null && (newState == PreviewSong.PREPARE_TO_FINISH || newState == PreviewSong.FAILURE)) {
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

    @Override
    public float getInAppVolume() {
        return mInAppVolume;
    }

    @Override
    public float getLeftBalanceValue() {
        return mLeftBalanceValue;
    }

    @Override
    public float getRightBalanceValue() {
        return mRightBalanceValue;
    }

    public boolean isPlayingPreview() {
        return (!mPreviewSongs.isEmpty() && mPreviewSongs.get(0).isPlaying());
    }

    public PreviewSong getCurrentPreviewSong() {
        if (mPreviewSongs.isEmpty()) return null;
        return mPreviewSongs.get(0);
    }

    private boolean mShouldPlayingMusicServiceOnFinish = false;

    public void shouldPlayingMusicServiceOnFinish(boolean b) {
        Log.d(TAG, "shouldPlayingMusicServiceOnFinish = " + b);
        mShouldPlayingMusicServiceOnFinish = b;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PreferenceUtil.IN_APP_VOLUME:
                notifyVolumePrefChanged();
                break;
            case PreferenceUtil.BALANCE_VALUE:
                notifyBalanceChanged();
                break;
        }
    }

    private float mInAppVolume = 1.0f;
    private float mLeftBalanceValue = 0.5f;
    private float mRightBalanceValue = 0.5f;

    private void notifyVolumePrefChanged() {
        synchronized (this) {
            float volume = App.getInstance().getPreferencesUtility().getInAppVolume();
            if (volume < 0) volume = 0;
            else if (volume > 1) volume = 1;
            mInAppVolume = volume;
        }

        try {
            PreviewSong song = getCurrentPreviewSong();
            if (song != null) song.notifyVolumeChanged();
        } catch (Exception ignored) {
        }
    }

    public void notifyBalanceChanged() {
        synchronized (this) {
            float balance = App.getInstance().getPreferencesUtility().getBalanceValue();
            if (balance < 0) balance = 0;
            else if (balance > 1) balance = 1;
            if (balance < 0.5f) {
                mRightBalanceValue = 2 * balance;
                mLeftBalanceValue = 1;
            } else {
                mLeftBalanceValue = 2 - 2 * balance;
                mRightBalanceValue = 1;
            }
        }

        try {
            PreviewSong song = getCurrentPreviewSong();
            if (song != null) song.notifyVolumeChanged();
        } catch (Exception ignored) {
        }

    }

    public void destroy() {
        App.getInstance().getPreferencesUtility().unregisterOnSharedPreferenceChangedListener(this);
        stopSession();
        removeSongPreviewListener();
    }
}