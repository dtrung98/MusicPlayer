package com.ldt.musicr.ui.widget.avsb;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.ldt.musicr.interactors.AppExecutors;
import com.ldt.musicr.model.Song;

import java.io.FileNotFoundException;

class VisualSeekBarParser implements ProgressListener {
    private final AudioVisualSeekBar mSeekBar;

    /* Parser response message: use to check the result */
    private String mMessage = "";

    protected com.ldt.musicr.ui.widget.soundfile.SoundFile mSoundFile;
    protected boolean mInitialized;
    protected float range;
    protected float scaleFactor;
    protected float minGain;
    protected double mSampleRate;
    protected double mSamplesPerFrame;
    protected double mNumFrames;
    protected double mParseDuration;
    protected int mIntDuration;
    protected int mMaxGain, mMinGain;
    protected int[] mFrameGain;

    private int mNumberFrameInAPen;
    int mNumberFrameAppearInScreen;
    int mNumberPensAppearInScreen;

    int mTotalPens;
    double[] mSmoothedPenGain;

    private float mViewWidth = 0;
    private float mViewHeight = 0;

    private double getDeclaredDuration() {
        return mSeekBar.mSongDeclareDuration;
    }

    private boolean isSoundFileDataAvailable() {
        return mNumFrames != 0;
    }

    private boolean isViewSizeAvailable() {
        return mViewWidth != 0;
    }

    void viewSizeChanged(float newW, float newH) {
        if (newW != mViewWidth || newH != mViewHeight) {
            mViewWidth = newW;
            mViewHeight = newH;
            AppExecutors.getInstance().networkIO().execute(this::parseVisualData);
        }
    }

    public VisualSeekBarParser(@NonNull AudioVisualSeekBar seekBar) {
        mSeekBar = seekBar;
    }

    /**
     * Step 1: Parse the sound file
     * Step 2: When the view size available, or change, use the parsed data to update the visual data
     * Variable changed: if sound file changed -> restart from step 1. if view size changed -> restart from step 2.
     *
     * @param song
     */
    void parse(Song song) {
        AppExecutors.getInstance().networkIO().execute(() -> {
                    mMessage = "";
                    /* notify the seek bar that parser is starting to parse a file */
                    AppExecutors.getInstance().mainThread().execute(mSeekBar::startParsingFile);

                    try {
                        mSoundFile = com.ldt.musicr.ui.widget.soundfile.SoundFile.create(song, this);
                        //Thread.sleep(2500);
                    } catch (FileNotFoundException e) {
                        mMessage = "File is not found";
                    } catch (Exception e) {
                        mMessage = "Could not parse the audio";
                    }

                    if (mMessage.isEmpty())
                        parse();

                    /* notify the seek bar that parsing process had finish */
                    AppExecutors.getInstance().mainThread().execute(mSeekBar::finishParsingFile);
                }
        );
    }

    @WorkerThread
    private void parse() {
        // run in the background
        parseSoundFileData();
        parseVisualData();

    }

    @WorkerThread
    private void parseSoundFileData() {
        mNumFrames = mSoundFile.getNumFrames();
        //Log.d(TAG, "calculateSound: "+mNumFrames);
        mSampleRate = mSoundFile.getSampleRate();
        mSamplesPerFrame = mSoundFile.getSamplesPerFrame();
        mParseDuration = mNumFrames * mSamplesPerFrame / mSampleRate + 0.0f;
        mIntDuration = (int) mParseDuration;
        mFrameGain = mSoundFile.getFrameGains();
        mMaxGain = 0;
        mMinGain = 255;
        for (int i = 0; i < mNumFrames; i++) {
            if (mMaxGain < mFrameGain[i]) mMaxGain = mFrameGain[i];
            if (mMinGain > mFrameGain[i]) mMinGain = mFrameGain[i];
        }
    }

    @WorkerThread
    private void parseVisualData() {
        if (!isSoundFileDataAvailable()) return;
        if (!isViewSizeAvailable()) return;


        //30 s for a screen width
        // how many frames appeared in a screen width ?
        // how many pens appeared in a screen width ?
        // >> how many frame for one pen ?
        // duration = 1.5*Width

        mNumberPensAppearInScreen = (int) (((mSeekBar.mWidth + mSeekBar.mPenDistance) / (0.0f + mSeekBar.mOneDp) + 1.0f) / 4.0f);

        double secondsInScreen = (getDeclaredDuration() / 1000f) / 4f;
        //Log.d(TAG, "calculateSound: duration  = "+duration+", sis = "+ secondsInScreen);
        mNumberFrameAppearInScreen = (int) (mNumFrames * secondsInScreen / mParseDuration);
        mNumberFrameInAPen = mNumberFrameAppearInScreen / mNumberPensAppearInScreen;
        double re = (mNumFrames + 0.0f) / mNumberFrameInAPen;
        mTotalPens = (re == ((int) re)) ? (int) re : ((int) re + 1);

        double[] originalPenGain = new double[mTotalPens];

        if (originalPenGain.length > 0)
            originalPenGain[0] = 0;

        //  reduce the frame gains array (large data) into the pen gains with smaller data.
        int iPen = 0;
        int pos = 0;
        for (int iFrame = 0; iFrame < mNumFrames; iFrame++) {
           /* if(iPen>=226)
                Log.d(TAG, "calculateSound");*/
            originalPenGain[iPen] += mFrameGain[iFrame];
            pos++;
            if (iFrame == mNumFrames - 1) {
                originalPenGain[iPen] /= pos;
            } else if (pos == mNumberFrameInAPen) {
                originalPenGain[iPen] /= mNumberFrameInAPen;
                pos = 0;
                iPen++;
            }
        }
        // make pen gains smoothly
        computeDoublesForAllZoomLevels(mTotalPens, originalPenGain);
        mSmoothedPenGain = new double[mTotalPens];
        for (int i_pen = 0; i_pen < mTotalPens; i_pen++)
            mSmoothedPenGain[i_pen] = getHeight(i_pen, mTotalPens, originalPenGain, scaleFactor, minGain, range);
        int x = 0;
    }

    @WorkerThread
    protected double getHeight(int i, int totalPens, double[] penGain, float scaleFactor, float minGain, float range) {
        double value = (getGain(i, totalPens, penGain) * scaleFactor - minGain) / range;
        if (value < 0.0)
            value = 0.0f;
        if (value > 1.0)
            value = 1.0f;
        value = (value + 0.01f) / 1.01f;
        return value;
    }

    /**
     * Called once when a new sound file is added
     */
    protected void computeDoublesForAllZoomLevels(int totalPenGains, double[] originPenGain) {
        // Make sure the range is no more than 0 - 255
        float maxGain = 1.0f;
        for (int i = 0; i < totalPenGains; i++) {
            float gain = (float) getGain(i, totalPenGains, originPenGain);
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
        int[] gainHist = new int[256];
        for (int i = 0; i < totalPenGains; i++) {
            int smoothedGain = (int) (getGain(i, totalPenGains, originPenGain) * scaleFactor);
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

    protected static double getGain(int i, int totalPens, double[] penGain) {
        // if x > size - 1, x = size -1
        int x = Math.min(i, totalPens - 1);

        // if size < 2, do nothing
        if (totalPens < 2) {
            return penGain[x];
        } else {// else  (size > 2)
            //  if x is the first element
            // x = 1/2 itself + 1/2 next element
            if (x == 0) {
                return penGain[0] * 0.7f + penGain[1] * 0.3f;
            }
            // else if x is the last one,
            // x = 1/2 itself + 1/2 previous element
            else if (x == totalPens - 1) {
                return penGain[totalPens - 2] * 0.3f + penGain[totalPens - 1] * 0.7f;
            } else {
                // else
                // x = 1/3 prev + 1/3 itself + 1/3 next
                return penGain[x - 1] * 3 / 13f + penGain[x] * 7 / 13f + penGain[x + 1] * 3 / 13f;
            }
        }
    }


    @Override
    public boolean reportProgress(double fractionComplete) {
        return mSeekBar.updateParsingProgress(fractionComplete);
    }
}
