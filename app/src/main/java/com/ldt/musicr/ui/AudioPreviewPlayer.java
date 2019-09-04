package com.ldt.musicr.ui;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.service.MusicServiceEventListener;
import com.ldt.musicr.ui.widget.soundfile.CheapSoundFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AudioPreviewPlayer implements  MediaPlayer.OnCompletionListener, MusicServiceEventListener {
    private static final String TAG ="AudioPreviewPlayer";

    String mPath;
    File mFile;
    CheapSoundFile mSoundFile;
    private AudioPreviewerListener mListener;
    private SoundFileLoader mLoadSoundFile;
    private boolean mSavedPlayState = false;
    private boolean mIsStateLocked = false;

    public AudioPreviewPlayer() {
        mPath ="";

    }

    public AudioPreviewPlayer setListener(AudioPreviewerListener listener) {
        mListener = listener;
        return this;
    }
    public void removeListener() {
        mListener = null;
    }


    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onQueueChanged() {

    }

    @Override
    public void onPlayingMetaChanged() {
        checkingMusicState();
    }

    @Override
    public void onPlayStateChanged() {

    }

    @Override
    public void onRepeatModeChanged() {

    }

    @Override
    public void onShuffleModeChanged() {

    }

    @Override
    public void onMediaStoreChanged() {

    }

    @Override
    public void onPaletteChanged() {

    }

    private void checkingMusicState() {
        if(MusicPlayerRemote.isPlaying())
            forceStop();
    }

    public interface AudioPreviewerListener {
        void onPreviewStart(int totalTime);
        void onPreviewDestroy();
    }

    public void previewThisFile(AudioPreviewerListener listener , String path) {
        Log.d(TAG, "previewThisFile");
        dismiss();

        setListener(listener);

        if(path==null||path.isEmpty()) return;
        mPath= path;

        mFile = new File(mPath);
        if(!mFile.exists()) return;

        mLoadSoundFile = new SoundFileLoader(this);
        mLoadSoundFile.execute(mFile);
    }

    private MediaPlayer mMediaPlayer;

    void dismiss() {
       if(mLoadSoundFile!=null)  {
           mLoadSoundFile.cancelAndUnBind();
           mLoadSoundFile = null;
       }
       removeListener();
    }

    private double mPlayFrom=0;
    private double mPlayTo = 0;

    private double mSampleRate;
    private double mSamplesPerFrame;
    private int mNumFrames;
    private double mDuration;
    private int mIntDuration;

    private int[] mFrameGain;

    private synchronized void calculateSound() {
        // run in the background
        mNumFrames = mSoundFile.getNumFrames();
        mSampleRate = mSoundFile.getSampleRate();
        mSamplesPerFrame = mSoundFile.getSamplesPerFrame();
        mDuration = mNumFrames * mSamplesPerFrame / mSampleRate + 0.0f;
        mIntDuration = (int) mDuration;
        mFrameGain = mSoundFile.getFrameGains();


        double durationZone;
        double timeFromZone = 0;

        if(mDuration<40) {
            durationZone = 40;
            timeFromZone = 5;
        } else if(mDuration<60) {
            timeFromZone = 5;
            durationZone = mIntDuration - 5 - 5;
        } else {
            timeFromZone = 13;
            durationZone = mIntDuration - timeFromZone - 8;
        }

        int frameFromZone = (int) (mNumFrames*timeFromZone/mDuration);
     //   int frameToZone = (int) (frameFromZone + durationZone*2);
     //   int frameToZone = (int) (mNumFrames-1);
       int frameToZone = (int) (frameFromZone + durationZone*mNumFrames/mDuration);
        if(frameToZone>mNumFrames-1) frameToZone = (int) (mNumFrames-1);

        int[] frameGainInPlayingZone = Arrays.copyOfRange(mFrameGain,frameFromZone,frameToZone);

        int numberFrameGainPlayingZone = frameGainInPlayingZone.length;


        // reduce the FrameGain to PenGain
        // 1 pen gain = 0.5s
        float penGainSecond_Guessing = 0.5f;
        durationZone = ((frameToZone-frameFromZone)/(mNumFrames+0f))*mDuration;
        int numberPenGain_Guess = (int) (Math.floor(((frameToZone-frameFromZone)/(mNumFrames+0f))*(mDuration/penGainSecond_Guessing))); // each 500ms
        int numberFrameEachPenGain = numberFrameGainPlayingZone/numberPenGain_Guess;

        numberPenGain_Guess = (int) Math.ceil((numberFrameGainPlayingZone+0f)/numberFrameEachPenGain);
        float penGainSecond = (float) (durationZone/numberPenGain_Guess);

        double[] originalPenGain = new double[numberPenGain_Guess];

        originalPenGain[0] = 0;
        int iPen = 0;
        int pos = 0;
        for(int iFrame = 0;iFrame < numberFrameGainPlayingZone;iFrame++) {
            originalPenGain[iPen] +=frameGainInPlayingZone[iFrame];
            pos++;
            if(iPen==numberPenGain_Guess-1) Log.d(TAG, "last : frame["+iFrame+"] = "+frameGainInPlayingZone[iFrame]+", pos = "+ pos );

            if(iFrame == numberFrameGainPlayingZone - 1) {
                originalPenGain[iPen] /=pos;
                Log.d(TAG, "last : ipen = "+iPen+", average = "+ originalPenGain[iPen]+", from "+ pos+" element" +" / "+numberFrameEachPenGain);
            } else if (pos==numberFrameEachPenGain) {
                originalPenGain[iPen]/=numberFrameEachPenGain;

                pos = 0;
                iPen++;
            }
        }
        Log.d(TAG, "calculateSound: duration = "+ mDuration);

        // make pen gains smoothly
       // computeDoublesForAllZoomLevels(numberPenGain_Guess, originalPenGain);
       double[] SmoothedPenGain =originalPenGain;// new double[numberPenGain_Guess];
//        for (int i_pen = 0; i_pen < numberPenGain_Guess; i_pen++)
//            SmoothedPenGain[i_pen] = getHeight(i_pen, numberPenGain_Guess, originalPenGain, scaleFactor, minGain, range);

        // now explore the smoothedPenGain

        /*
        double maxAverage = findMaxAverage(SmoothedPenGain,15*numberFrameEachPenGain);
         */

        final int staticPenSize = 24;
        final int staticEdge = 22;

         int startPen = findMaxAverage(SmoothedPenGain, SmoothedPenGain.length, staticPenSize);
         int endPen = startPen + staticPenSize;

         int newStartPen = startPen;
         int newEndPen = endPen;

         double minPenBeforeStartPen = SmoothedPenGain[startPen];
         int item = (startPen <= staticEdge) ? 0: startPen - staticEdge;
         for(;item<startPen;item++)
             if(SmoothedPenGain[item] < minPenBeforeStartPen) {
                 minPenBeforeStartPen = SmoothedPenGain[item];
                 newStartPen = item;
             }

         double minPenAfterEndPen = SmoothedPenGain[endPen];
         for(int i = endPen+1;i<=endPen+staticEdge&&i<SmoothedPenGain.length;i++)
       if(SmoothedPenGain[i]<minPenAfterEndPen) {
           minPenAfterEndPen = SmoothedPenGain[i];
           newEndPen = i;
       }

       Log.d(TAG, "calculateSound: start = "+ newStartPen+", penNum = "+ (newEndPen- newStartPen) );

        mPlayFrom =  timeFromZone + newStartPen*penGainSecond;
        mPlayTo   =  timeFromZone + newEndPen*penGainSecond;
        Log.d(TAG, "calculateSound: time from "+ mPlayFrom+" to "+mPlayTo);
    }


    // Returns beginning index of maximum average
    // subarray of length 'k'
    static int findMaxAverage(double arr[], int n, int k)
    {

        // Check if 'k' is valid
        if (k > n)
            return -1;

        // Compute sum of first 'k' elements
        double sum = arr[0];
        for (int i = 1; i < k; i++)
            sum += arr[i];

        double max_sum = sum;
        int max_end = k-1;

        // Compute sum of remaining subarrays
        for (int i = k; i < n; i++)
        {
            sum = sum + arr[i] - arr[i-k];
            if (sum > max_sum)
            {
                max_sum = sum;
                max_end = i;
            }
        }

        // Return starting index
        //return max_end - k + 1;
        return max_end - k;
    }
    protected double getHeight(int i, int totalPens, double[] penGain, float scaleFactor, float minGain, float range) {
        double value = (getGain(i, totalPens, penGain) * scaleFactor - minGain) / range;

        if (value < 0.0)
            value = 0.0f;
        if (value > 1.0)
            value = 1.0f;
        value = (value + 0.05) / 1.05f;
        return value;
    }

    private Random mRandom = new Random();
    protected boolean mInitialized;
    protected float range;
    protected float scaleFactor;
    protected float minGain;

    /**
     * Called once when a new sound file is added
     */
    protected void computeDoublesForAllZoomLevels(int totalPenGains, double[] orginPenGain) {
        // Make sure the range is no more than 0 - 255
        float maxGain = 1.0f;
        for (int i = 0; i < totalPenGains; i++) {
            float gain = (float) getGain(i, totalPenGains, orginPenGain);
            if (gain > maxGain) {
                maxGain = gain;
            }
        }
        scaleFactor = 1.0f;
        if (maxGain > 255.0) {
            scaleFactor = 255 / maxGain;
        }

        // Build histogram of 256 bins and figure out the new scaled max
        maxGain = 0;
        int gainHist[] = new int[256];
        for (int i = 0; i < totalPenGains; i++) {
            int smoothedGain = (int)(getGain(i, totalPenGains, orginPenGain) * scaleFactor);
            if (smoothedGain < 0)
                smoothedGain = 0;
            if (smoothedGain > 255)
                smoothedGain = 255;

            if (smoothedGain > maxGain)
                maxGain = smoothedGain;

            gainHist[smoothedGain]++;
        }

        // Re-calibrate the min to be 5%
        minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < totalPenGains / 20) {
            sum += gainHist[(int) minGain];
            minGain++;
        }

        // Re-calibrate the max to be 99%
        sum = 0;
        while (maxGain > 2 && sum < totalPenGains / 100) {
            sum += gainHist[(int) maxGain];
            maxGain--;
        }

        range = maxGain - minGain;

        mInitialized = true;
    }

    protected double getGain(int i, int totalPens, double[] penGain) {
        int x = Math.min(i, totalPens - 1);
        if (totalPens < 2) {
            return penGain[x];
        } else {
            if (x == 0) {
                return (penGain[0] / 2.0f) + (penGain[1] / 2.0f);
            } else if (x == totalPens - 1) {
                return (penGain[totalPens - 2] / 2.0f) + (penGain[totalPens - 1] / 2.0f);
            } else {
                return (penGain[x - 1] / 3.0f) + (penGain[x] / 3.0f) + (penGain[x + 1] / 3.0f);
            }
        }
    }
    public void forceStop() {
        if(mListener!=null) mListener.onPreviewDestroy();;
        // release old media player
        if(mMediaPlayer!=null) {
            try {
                if (mMediaPlayer.isPlaying()) fadeOutAndRelease(mMediaPlayer);
                else mMediaPlayer.release();
            } catch (Exception ignore) {}
        }
    }
    private void releaseOldMediaPlayer() {

        // release old media player
        if(mMediaPlayer!=null) {
            try {
                if (mMediaPlayer.isPlaying()) fadeOutAndRelease(mMediaPlayer);
                else mMediaPlayer.release();
            } catch (Exception ignore) {}
        }
    }

    private void onSoundFileReady() {
        if(!mIsStateLocked) mSavedPlayState = MusicPlayerRemote.isPlaying();

        releaseOldMediaPlayer();

        if(mPlayTo<=mPlayFrom) {return;}

        // create new media player
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mPath);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo((int) (mPlayFrom*1000));

            int duration = mMediaPlayer.getDuration();

            int timePlayed = (int) ((mPlayTo - mPlayFrom)*1000);
            mMediaPlayer.start();
            if(mListener!=null) mListener.onPreviewStart(timePlayed);

            Handler handler = new Handler();
           final MediaPlayer mediaPlayer = mMediaPlayer;
            if(timePlayed>FADE_DURATION) {
                handler.postDelayed(() -> fadeOutAndRelease(mediaPlayer), (long) timePlayed - FADE_DURATION);
            } else {
                handler.postDelayed(mediaPlayer::release,timePlayed);
            }

        } catch (IOException e) {
            Log.d(TAG, "onSoundFileReady error : "+ e.getMessage());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        dismiss();
    }
    private static class SoundFileLoader extends AsyncTask< File, Void, Void > implements CheapSoundFile.ProgressListener {
        AudioPreviewPlayer mAudioPreviewPlayer;
        boolean mKeeping = true;
        boolean isFinished = false;
        public SoundFileLoader(AudioPreviewPlayer bs) {
            mAudioPreviewPlayer = bs;
        }

        public boolean cancelAndUnBind() {
            mKeeping = false;
            if(!isFinished)
            cancel(true);
            mAudioPreviewPlayer = null;
            return isFinished;
        }

        @Override
        protected Void doInBackground(File...file) {
            try {
                mAudioPreviewPlayer.mSoundFile = CheapSoundFile.create(file[0].getAbsolutePath(), this);
                     mAudioPreviewPlayer.calculateSound();
            } catch (final Exception e) {
                Log.e(TAG, "Error while loading sound file", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isFinished = true;
            if(mAudioPreviewPlayer !=null)
            mAudioPreviewPlayer.onSoundFileReady();

        }

        @Override
        public boolean reportProgress(double fractionComplete) {
         //   Log.d(TAG, "reportProgress: "+fractionComplete);
            return mKeeping;
        }
    }

    float out_volume = 1;
    float in_volume = 0;
    final int FADE_DURATION = 450;

    private void startAndFadeIn(MediaPlayer mediaPlayer) {
        mediaPlayer.setVolume(0,0);
        mediaPlayer.start();

        in_volume = 0;

        //The amount of time between out_volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 10;
        final int MAX_VOLUME = 1; //The out_volume will increase from 0 to 1
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the out_volume changes each step
        final float deltaVolume = MAX_VOLUME / (float)numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeInStep(mediaPlayer, deltaVolume); //Do a fade step
                //Cancel and Purge the Timer if the desired out_volume has been reached
                if(in_volume >=1){
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }


    private void fadeOutAndRelease(MediaPlayer mediaPlayer){
        out_volume = 1;

        //The amount of time between out_volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 10;
        final int MAX_VOLUME = 1; //The out_volume will increase from 0 to 1
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the out_volume changes each step
        final float deltaVolume = MAX_VOLUME / (float)numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    fadeOutStep(mediaPlayer, deltaVolume); //Do a fade step
                } catch (Exception e) {
                    timer.cancel();
                    timer.purge();
                    return;
                }
                //Cancel and Purge the Timer if the desired out_volume has been reached
                if(out_volume <=0.1f){
                    timer.cancel();
                    timer.purge();
                    removeListener();
                    try {

                        mediaPlayer.release();
                    } catch (Exception ignore) {}
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void fadeOutStep(MediaPlayer mediaPlayer,float deltaVolume){
        mediaPlayer.setVolume(out_volume, out_volume);
        out_volume -= deltaVolume;
    }
    private void fadeInStep(MediaPlayer mediaPlayer,float deltaVolume){
        mediaPlayer.setVolume(in_volume, in_volume);
        in_volume += deltaVolume;
    }
}
