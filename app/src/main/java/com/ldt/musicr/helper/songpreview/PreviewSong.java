package com.ldt.musicr.helper.songpreview;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.ldt.musicr.model.Song;

import java.util.Timer;
import java.util.TimerTask;

class PreviewSong {
    private static final String TAG = "PreviewSong";

    public static final int NOT_PLAY = 0;
    public static final int PREPARE_TO_PLAY = 1;
    public static final int PLAYING = 2;
    public static final int PREPARE_TO_FINISH = 3;
    public static final int FINISHED = 4;
    public static final int FAILURE = -1;

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj instanceof PreviewSong) {
            return mSong.equals(((PreviewSong) obj).mSong);
        }
        return false;
    }

    public interface OnPreviewSongStateChangedListener {
        void onPreviewSongStateChanged(PreviewSong song, int newState);
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
        if(mState != state) {
            this.mState = state;
            if(mListener!=null) mListener.onPreviewSongStateChanged(this,state);
        }
        return this;
    }

    private int mState = NOT_PLAY;
    private final int mStart;
    private final int mFinish;
    private MediaPlayer mMediaPlayer;

    public Song getSong() {
            return mSong;
    }

    private final Song mSong;

    public PreviewSong(Song song,int start, int finish) {

        if(start < 0) start =0;
        if(finish > song.duration) finish = (int) song.duration;


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

    public MediaPlayer getPlayer() {
        return mMediaPlayer;
    }

    private Handler mPlayHandler = new Handler();

    public void play() {
        if(mFinish-mStart < 1000) {
            setState(FAILURE);
            return;
        }

        if(mMediaPlayer!=null) release();
        this.mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mSong.data);
            mMediaPlayer.prepare();


            mMediaPlayer.seekTo(mStart);
            setState(PREPARE_TO_PLAY);
            fadeIn();

        } catch (Exception e) {
            setState(FAILURE);
        }

    }

    public void release() {
        if(mMediaPlayer==null)
            return;

        if(getState()==PLAYING) {
            mPlayHandler.removeCallbacks(mFinishPlayRunnable);
            mPlayHandler.post(mFinishPlayRunnable);
        } else destroy();


    }

    private void onAudioStartFadeIn() {
        mMediaPlayer.start();
        setState(PLAYING);
        mPlayHandler.postDelayed(mFinishPlayRunnable,mFinish - mStart - FADE_OUT_DURATION - FADE_IN_DURATION);
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
            if(mMediaPlayer!=null)
            mMediaPlayer.release();
        } catch (Exception ignored) {}

        mMediaPlayer = null;
        if(mPlayHandler!=null)
            mPlayHandler.removeCallbacks(mFinishPlayRunnable);
        mPlayHandler = null;

        mTimeTask.cancel();
    }

    private static final int NOT_FADE = 0;
    private static final int FADE_IN = 1;
    private static final int FADE_OUT = 2;

    private static final float DEFAULT_IN_VOLUME = 0;
    private static final float DEFAULT_OUT_VOLUME = 0.1f;
    private static final float DEFAULT_PLAY_VOLUME = 1;

    private float mCurrentVolume = DEFAULT_PLAY_VOLUME;

    private static final int FADE_IN_DURATION = 450;
    private static final int FADE_OUT_DURATION = 450;

    private static final int FADE_INTERVAL = 10;

    //Calculate the number of fade steps
    private static final int NUMBER_OF_STEPS = FADE_IN_DURATION/FADE_INTERVAL;

    //Calculate by how much the mCurrentOutVolume changes each step
    final float DELTA_VOLUME = DEFAULT_PLAY_VOLUME / (float) NUMBER_OF_STEPS;


    private int mFadeState = NOT_FADE;
    private Timer mTimer = new Timer(true);
    private final TimerTask mTimeTask = new TimerTask() {
        @Override
        public void run() {
            if(!onIntervalUpdate()) {

                if(mFadeState==FADE_OUT) onAudioFinishFadeOut();

                mFadeState = NOT_FADE;
                if(mTimer!=null) {
                    mTimer.cancel();
                    mTimer.purge();
                    mTimer = null;
                }
            }
        }
    };
    private boolean onIntervalUpdate() {
        switch (mFadeState) {
            case FADE_IN:
                if(mCurrentVolume<=DEFAULT_IN_VOLUME) mCurrentVolume = DEFAULT_IN_VOLUME;

                mCurrentVolume += DELTA_VOLUME;

                if(mCurrentVolume>=DEFAULT_PLAY_VOLUME) mCurrentVolume = DEFAULT_PLAY_VOLUME;
                try {
                    mMediaPlayer.setVolume(mCurrentVolume, mCurrentVolume);
                } catch (Exception ignored) {};
                if(mCurrentVolume==DEFAULT_PLAY_VOLUME)
                    return false;
                return true;

            case FADE_OUT:
                if(mCurrentVolume >= DEFAULT_PLAY_VOLUME) mCurrentVolume = DEFAULT_PLAY_VOLUME;

                mCurrentVolume -= DELTA_VOLUME;

                if(mCurrentVolume<=DEFAULT_OUT_VOLUME) mCurrentVolume = DEFAULT_OUT_VOLUME;
                try {
                    mMediaPlayer.setVolume(mCurrentVolume, mCurrentVolume);
                } catch (Exception ignored) {}

                if(mCurrentVolume==DEFAULT_OUT_VOLUME)
                    return false;

                return true;
            default:
                return false;
        }
    }

    private void fadeIn() {

        mCurrentVolume = DEFAULT_IN_VOLUME;
        try {
            mMediaPlayer.setVolume(DEFAULT_IN_VOLUME, DEFAULT_IN_VOLUME);
        } catch (Exception ignored) {}
        onAudioStartFadeIn();
        mFadeState = FADE_IN;
        update();
    }
    private void update() {

        mTimer = new Timer(true);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(!onIntervalUpdate()) {

                    if(mFadeState==FADE_OUT) onAudioFinishFadeOut();

                    mFadeState = NOT_FADE;
                    if(mTimer!=null) {
                        mTimer.cancel();
                        mTimer.purge();
                        mTimer = null;
                    }
                }
            }
        };

        mTimer.schedule(task,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void fadeOut() {
        mCurrentVolume = DEFAULT_PLAY_VOLUME;
        try {
            mMediaPlayer.setVolume(DEFAULT_PLAY_VOLUME, DEFAULT_PLAY_VOLUME);
        } catch (Exception ignored) {}
        mFadeState = FADE_OUT;
        update();
    }

    }