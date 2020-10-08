package com.ldt.musicr.helper.songpreview;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.ldt.musicr.App;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.MusicUtil;

import java.lang.ref.WeakReference;

public class PreviewSong {
    private static final String TAG = "PreviewSong";

    public static final int NO_PLAY = 0;
    public static final int PREPARE_TO_PLAY = 1;
    public static final int PLAYING = 2;
    public static final int PREPARE_TO_FINISH = 3;
    public static final int FINISHED = 4;
    public static final int FAILURE = -1;

    private static final int ACTION_SET_VOLUME = 1;
    private static final int ACTION_FADE_OUT = 2;
    private static final int ACTION_FADE_IN = 3;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof PreviewSong) {
            return mSong.equals(((PreviewSong) obj).mSong);
        }

        return false;
    }

    public boolean isPlaying() {
        boolean result = false;
        if (mMediaPlayer != null)
            try {
                result = mMediaPlayer.isPlaying();
                Log.d(TAG, "isPlaying: result = " + result);
            } catch (Exception e) {
                result = false;
            }
        return result;
    }

    public long getTimePlayed() {
        if (mMediaPlayer != null)
            try {
                long c = mMediaPlayer.getCurrentPosition();
                long s = getStart();
                Log.d(TAG, "getTimePlayed: current = " + c + ", start = " + s + ", played = " + (c - s));
                return c - s;
            } catch (Exception ignored) {
                return -1;
            }
        return -1;
    }


    public void notifyVolumeChanged() {
        if (mListener != null) updateVolume(ACTION_SET_VOLUME);
    }

    public interface OnPreviewSongStateChangedListener {
        void onPreviewSongStateChanged(PreviewSong song, int newState);

        float getInAppVolume();

        float getLeftBalanceValue();

        float getRightBalanceValue();
    }

    private OnPreviewSongStateChangedListener mListener;

    public void setOnPreviewSongStateChangedListener(OnPreviewSongStateChangedListener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    public int getState() {
        return mState;
    }

    private PreviewSong setState(int state) {
        if (mState != state) {
            this.mState = state;
            Log.d(TAG, "set new state: " + state);
            if (mListener != null) mListener.onPreviewSongStateChanged(this, state);
        }
        return this;
    }

    private int mState = NO_PLAY;
    private final int mStart;
    private final int mFinish;
    private MediaPlayer mMediaPlayer;

    public Song getSong() {
        return mSong;
    }

    private final Song mSong;

    public PreviewSong(Song song, int _start, int _finish) {
        int start = _start;
        int finish = _finish;
        if (start < 0) start = 0;
        if (finish > song.duration) finish = (int) song.duration;

        //  if(finish-start>10000) finish = start+10000;

        this.mSong = song;
        this.mStart = start;
        this.mFinish = finish;

    }

    public int getStart() {
        return mStart;
    }

    public int getFinish() {
        return mFinish;
    }

    public int getTotalPreviewDuration() {
        return getFinish() - getStart();
    }

    public MediaPlayer getPlayer() {
        return mMediaPlayer;
    }

    private Handler mPlayHandler = new Handler();

    public void play() {
        if (mFinish - mStart < 1000) {
            setState(FAILURE);
            return;
        }

        long start = System.currentTimeMillis();
        if (mMediaPlayer != null) release();
        mMediaPlayer = new MediaPlayer();
        long time1 = System.currentTimeMillis();
        try {
            final String path = MusicUtil.getSongFileUri(mSong.id).toString();
            if (path.startsWith("content://")) {
                mMediaPlayer.setDataSource(App.getInstance().getApplicationContext(), Uri.parse(path));
            } else {
                mMediaPlayer.setDataSource(path);
            }

            long time2 = System.currentTimeMillis();
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mp -> {
                mMediaPlayer.seekTo(mStart);

                Log.d(TAG, "play: init = " + (time1 - start) + ", setDataSource = " + (time2 - time1) + ", prepare = " + (System.currentTimeMillis() - time2));
                setState(PREPARE_TO_PLAY);
                fadeIn();
            });

        } catch (Exception e) {
            setState(FAILURE);
        }
        Log.d(TAG, "time to prepare : " + (System.currentTimeMillis() - start));

    }


    public void release() {
        if (mMediaPlayer == null)
            return;

        if (getState() == PLAYING) {
            mPlayHandler.removeCallbacks(mFinishPlayRunnable);
            mPlayHandler.post(mFinishPlayRunnable);
        } else
            destroy();
    }

    private void onAudioStartFadeIn() {
        long start = System.currentTimeMillis();
        mMediaPlayer.start();
        long time1 = System.currentTimeMillis();
        setState(PLAYING);
        Log.d(TAG, "media start time = " + (time1 - start) + ", notify time = " + (System.currentTimeMillis() - time1));
    }

    private void onAudioFinishFadeOut() {
        Log.d(TAG, "onAudioFinishFadeOut");
        destroy();
    }

    private Runnable mFinishPlayRunnable = new Runnable() {
        @Override
        public void run() {
            setState(PREPARE_TO_FINISH);
            fadeOut();
        }
    };

    private void destroy() {
        try {
            if (mMediaPlayer != null)
                mMediaPlayer.release();
        } catch (Exception ignored) {
        }

        mMediaPlayer = null;
        if (mPlayHandler != null)
            mPlayHandler.removeCallbacks(mFinishPlayRunnable);
        mPlayHandler = null;

    }

    private static final int NOT_FADE = 0;
    private static final int FADE_IN = 1;
    private static final int FADE_OUT = 2;

    private static final float DEFAULT_IN_VOLUME = 0.1f;
    private static final float DEFAULT_OUT_VOLUME = 0.1f;
    private static final float DEFAULT_PLAY_VOLUME = 1;

    private float mCurrentVolume = DEFAULT_PLAY_VOLUME;

    private static final int FADE_IN_DURATION = 300;
    private static final int FADE_OUT_DURATION = 350;

    private static final int FADE_INTERVAL = 20;

    //Calculate the number of fade steps
    private static final int NUMBER_OF_STEPS_IN = FADE_IN_DURATION / FADE_INTERVAL;
    private static final int NUMBER_OF_STEPS_OUT = FADE_OUT_DURATION / FADE_INTERVAL;

    //Calculate by how much the mCurrentOutVolume changes each step
    private static final float DELTA_IN_VOLUME = (DEFAULT_PLAY_VOLUME - DEFAULT_IN_VOLUME) / (float) NUMBER_OF_STEPS_IN;
    private static final float DELTA_OUT_VOLUME = (DEFAULT_PLAY_VOLUME - DEFAULT_OUT_VOLUME) / (float) NUMBER_OF_STEPS_OUT;

    private int mFadeState = NOT_FADE;

    private boolean onIntervalUpdate() {
        switch (mFadeState) {
            case FADE_IN:
                if (mCurrentVolume <= DEFAULT_IN_VOLUME) mCurrentVolume = DEFAULT_IN_VOLUME;

                mCurrentVolume += DELTA_IN_VOLUME;

                if (mCurrentVolume >= DEFAULT_PLAY_VOLUME)
                    mCurrentVolume = DEFAULT_PLAY_VOLUME;

                updateVolume(ACTION_FADE_IN);

                return mCurrentVolume != DEFAULT_PLAY_VOLUME;

            case ACTION_FADE_OUT:
                if (mCurrentVolume > DEFAULT_PLAY_VOLUME) mCurrentVolume = DEFAULT_PLAY_VOLUME;

                mCurrentVolume -= DELTA_OUT_VOLUME;

                if (mCurrentVolume < DEFAULT_OUT_VOLUME) mCurrentVolume = DEFAULT_OUT_VOLUME;

                updateVolume(ACTION_FADE_OUT);

                return mCurrentVolume != DEFAULT_OUT_VOLUME;
            default:
                return false;
        }
    }

    private void setVolume(float newVolume) {
        mCurrentVolume = newVolume;
        updateVolume(ACTION_SET_VOLUME);
    }

    private float getInAppVolume() {
        if (mListener != null) return mListener.getInAppVolume();
        return 1.0f;
    }

    private float getLeftBalanceValue() {
        if (mListener == null) return 0.5f;
        return mListener.getLeftBalanceValue();
    }

    private float getRightBalanceValue() {
        if (mListener == null) return 0.5f;
        return mListener.getRightBalanceValue();
    }

    private long startFadeOut = -1;

    private void updateVolume(final int why) {
        if (why == ACTION_FADE_OUT && startFadeOut == -1) startFadeOut = System.currentTimeMillis();

        try {
            mMediaPlayer.setVolume(mCurrentVolume * getInAppVolume() * getLeftBalanceValue(), mCurrentVolume * getInAppVolume() * getRightBalanceValue());
            if (startFadeOut != -1)
                Log.d(TAG, why + " : song " + mSong.title + ", update new volume = " + mCurrentVolume + ", thread id " + Thread.currentThread().getId() + ", time fade out = " + (System.currentTimeMillis() - startFadeOut));
            else
                Log.d(TAG, why + " : song " + mSong.title + ", update new volume = " + mCurrentVolume + ", thread id " + Thread.currentThread().getId());
        } catch (Exception ignored) {
            Log.d(TAG, "exception when update song " + mSong.title + "  volume = " + mCurrentVolume);
        }
    }

    private void fadeIn() {
        onAudioStartFadeIn();
        setVolume(DEFAULT_IN_VOLUME);
        mPlayHandler.postDelayed(mFinishPlayRunnable, mFinish - mStart - FADE_OUT_DURATION - FADE_IN_DURATION);
        mFadeState = FADE_IN;
        update();
    }

    private void updateWithHandler() {
        mPlayHandler.postDelayed(mUpdateRunnable, FADE_INTERVAL);
    }

    private UpdateRunnable mUpdateRunnable = new UpdateRunnable(this);

    private static class UpdateRunnable implements Runnable {
        private UpdateRunnable(PreviewSong s) {
            mRefPreviewSong = new WeakReference<>(s);
        }

        private final WeakReference<PreviewSong> mRefPreviewSong;
        PreviewSong s;

        @Override
        public void run() {
            s = mRefPreviewSong.get();
            if (s == null) return;

            // if no need to update anymore
            if (!s.onIntervalUpdate()) {
                if (s.mFadeState == ACTION_FADE_OUT)
                    s.onAudioFinishFadeOut();

                s.mFadeState = NOT_FADE;
            } else {
                // need to update
                s.updateWithHandler();
            }

            s = null;
        }
    }

    private void update() {
        updateWithHandler();
    }

    private void fadeOut() {
        mFadeState = ACTION_FADE_OUT;
        update();
    }

}