package com.ldt.musicr.ui.widget.soundfile;

/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.ldt.musicr.R;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * WaveformView is an Android view that displays a visual representation
 * of an audio waveform.  It retrieves the frame gains from a CheapSoundFile
 * object and recomputes the shape contour at several zoom levels.
 * <p/>
 * This class doesn't handle selection or any of the touch interactions
 * directly, so it exposes a listener interface.  The class that embeds
 * this view should add itself as a listener and make the view scroll
 * and respond to other events appropriately.
 * <p/>
 * WaveformView doesn't actually handle selection, but it will just display
 * the selected part of the waveform in a different color.
 * <p>
 * Modified by Anna Stępień <anna.stepien@semantive.com>
 */
public class WaveformView extends View {

    public static final String TAG = "WaveformView";

    public interface WaveformListener {
        void waveformTouchStart(float x);

        void waveformTouchMove(float x);

        void waveformTouchEnd();

        void waveformFling(float x);

        void waveformDraw();

        void waveformZoomIn();

        void waveformZoomOut();
    }

    // Colors
    protected Paint mGridPaint;
    protected Paint mSelectedLinePaint;
    protected Paint mUnselectedLinePaint;
    protected Paint mUnselectedBkgndLinePaint;
    protected Paint mBorderLinePaint;
    protected Paint mPlaybackLinePaint;
    protected Paint mTimecodePaint;

    protected CheapSoundFile mSoundFile;
    protected int[] mLenByZoomLevel;
    protected float[] mZoomFactorByZoomLevel;
    protected int mZoomLevel;
    protected int mNumZoomLevels;
    protected int mSampleRate;
    protected int mSamplesPerFrame;
    protected int mOffset;
    protected int mSelectionStart;
    protected int mSelectionEnd;
    protected int mPlaybackPos;
    protected float mDensity;
    protected float mInitialScaleSpan;
    protected WaveformListener mListener;
    protected GestureDetector mGestureDetector;
    protected ScaleGestureDetector mScaleGestureDetector;
    protected boolean mInitialized;

    protected float range;
    protected float scaleFactor;
    protected float minGain;

    protected NavigableMap<Double, Segment> segmentsMap;
    protected Segment nextSegment;

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We don't want keys, the markers get these
        setFocusable(false);

        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(false);
        mGridPaint.setColor(getResources().getColor(R.color.grid_line));
        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setAntiAlias(false);
        mSelectedLinePaint.setColor(getResources().getColor(R.color.waveform_selected));
        mUnselectedLinePaint = new Paint();
        mUnselectedLinePaint.setAntiAlias(false);
        mUnselectedLinePaint.setColor(getResources().getColor(R.color.waveform_unselected));
        mUnselectedBkgndLinePaint = new Paint();
        mUnselectedBkgndLinePaint.setAntiAlias(false);
        mUnselectedBkgndLinePaint.setColor(getResources().getColor(R.color.waveform_unselected_bkgnd_overlay));
        mBorderLinePaint = new Paint();
        mBorderLinePaint.setAntiAlias(true);
        mBorderLinePaint.setStrokeWidth(1.5f);
        mBorderLinePaint.setPathEffect(new DashPathEffect(new float[]{3.0f, 2.0f}, 0.0f));
        mBorderLinePaint.setColor(getResources().getColor(R.color.selection_border));
        mPlaybackLinePaint = new Paint();
        mPlaybackLinePaint.setAntiAlias(false);
        mPlaybackLinePaint.setColor(getResources().getColor(R.color.playback_indicator));
        mTimecodePaint = new Paint();
        mTimecodePaint.setTextSize(12);
        mTimecodePaint.setAntiAlias(true);
        mTimecodePaint.setColor(getResources().getColor(R.color.timecode));

        mGestureDetector = new GestureDetector(
                context,
                new GestureDetector.SimpleOnGestureListener() {
                    public boolean onFling(

                            MotionEvent e1, MotionEvent e2, float vx, float vy) {
                        mListener.waveformFling(vx);
                        return true;
                    }
                });

        mScaleGestureDetector = new ScaleGestureDetector(
                context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    public boolean onScaleBegin(ScaleGestureDetector d) {
                        mInitialScaleSpan = Math.abs(d.getCurrentSpanX());
                        return true;
                    }

                    public boolean onScale(ScaleGestureDetector d) {
                        float scale = Math.abs(d.getCurrentSpanX());
                        if (scale - mInitialScaleSpan > 40) {
                            mListener.waveformZoomIn();
                            mInitialScaleSpan = scale;
                        }
                        if (scale - mInitialScaleSpan < -40) {
                            mListener.waveformZoomOut();
                            mInitialScaleSpan = scale;
                        }
                        return true;
                    }
                });

        mSoundFile = null;
        mLenByZoomLevel = null;
        mOffset = 0;
        mPlaybackPos = -1;
        mSelectionStart = 0;
        mSelectionEnd = 0;
        mDensity = 1.0f;
        mInitialized = false;
        segmentsMap = new TreeMap<>();
        nextSegment = null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mListener.waveformTouchStart(event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                mListener.waveformTouchMove(event.getX());
                break;
            case MotionEvent.ACTION_UP:
                mListener.waveformTouchEnd();
                break;
        }
        return true;
    }

    public boolean hasSoundFile() {
        return mSoundFile != null;
    }

    public void setSoundFile(CheapSoundFile soundFile) {
        mSoundFile = soundFile;
        mSampleRate = mSoundFile.getSampleRate();
        mSamplesPerFrame = mSoundFile.getSamplesPerFrame();
        computeDoublesForAllZoomLevels();
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public int getZoomLevel() {
        return mZoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        mZoomLevel = zoomLevel;
    }

    public boolean canZoomIn() {
        return (mZoomLevel < mNumZoomLevels - 1);
    }

    public void zoomIn() {
        if (canZoomIn()) {
            mZoomLevel++;
            float factor = mLenByZoomLevel[mZoomLevel] / (float) mLenByZoomLevel[mZoomLevel - 1];
            mSelectionStart *= factor;
            mSelectionEnd *= factor;
            int offsetCenter = mOffset + (int) (getMeasuredWidth() / factor);
            offsetCenter *= factor;
            mOffset = offsetCenter - (int) (getMeasuredWidth() / factor);
            if (mOffset < 0)
                mOffset = 0;
            invalidate();
        }
    }

    public boolean canZoomOut() {
        return (mZoomLevel > 0);
    }

    public void zoomOut() {
        if (canZoomOut()) {
            mZoomLevel--;
            float factor = mLenByZoomLevel[mZoomLevel + 1] / (float) mLenByZoomLevel[mZoomLevel];
            mSelectionStart /= factor;
            mSelectionEnd /= factor;
            int offsetCenter = (int) (mOffset + getMeasuredWidth() / factor);
            offsetCenter /= factor;
            mOffset = offsetCenter - (int) (getMeasuredWidth() / factor);
            if (mOffset < 0)
                mOffset = 0;
            invalidate();
        }
    }

    public int maxPos() {
        return mLenByZoomLevel[mZoomLevel];
    }

    public int secondsToFrames(double seconds) {
        return (int) (1.0
                * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    public int secondsToPixels(double seconds) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) (z * seconds * mSampleRate / mSamplesPerFrame + 0.5);
    }

    public double pixelsToSeconds(int pixels) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (pixels * (double) mSamplesPerFrame / (mSampleRate * z));
    }

    public int millisecsToPixels(int msecs) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) ((msecs * 1.0 * mSampleRate * z) / (1000.0 * mSamplesPerFrame) + 0.5);
    }

    public int pixelsToMillisecs(int pixels) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) (pixels * (1000.0 * mSamplesPerFrame) / (mSampleRate * z) + 0.5);
    }

    public void setParameters(int start, int end, int offset) {
        mSelectionStart = start;
        mSelectionEnd = end;
        mOffset = offset;
    }

    public int getStart() {
        return mSelectionStart;
    }

    public int getEnd() {
        return mSelectionEnd;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setPlayback(int pos) {
        mPlaybackPos = pos;
    }

    public void setListener(WaveformListener listener) {
        mListener = listener;
    }

    public void setSegments(final List<Segment> segments) {
        if (segments != null) {
            for (Segment segment : segments) {
                segmentsMap.put(segment.getStop(), segment);
            }
        }
    }

    public void recomputeHeights(float density) {
        mDensity = density;
        mTimecodePaint.setTextSize((int) (12 * density));

        invalidate();
    }

    protected void drawWaveformLine(Canvas canvas, int x, int y0, int y1, Paint paint) {
        canvas.drawLine(x, y0, x, y1, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSoundFile == null)
            return;

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int start = mOffset;
        int width = mLenByZoomLevel[mZoomLevel] - start;
        int ctr = measuredHeight / 2;

        if (width > measuredWidth)
            width = measuredWidth;

        double onePixelInSecs = pixelsToSeconds(1);
        boolean onlyEveryFiveSecs = (onePixelInSecs > 1.0 / 50.0);
        double fractionalSecs = mOffset * onePixelInSecs;
        int integerSecs = (int) fractionalSecs;

        double timecodeIntervalSecs = 1.0;

        int factor = 1;
        while (timecodeIntervalSecs / onePixelInSecs < 50) {
            timecodeIntervalSecs = 5.0 * factor;
            factor++;
        }

        int integerTimecode = (int) (fractionalSecs / timecodeIntervalSecs);

        int i = 0;
        while (i < width) {
            fractionalSecs += onePixelInSecs;
            int integerSecsNew = (int) fractionalSecs;
            if (integerSecsNew != integerSecs) {
                integerSecs = integerSecsNew;
                if (!onlyEveryFiveSecs || 0 == (integerSecs % 5)) {
                    canvas.drawLine(i + 1, 0, i + 1, measuredHeight, mGridPaint);
                }
            }

            // BackgroundBehavior waveform
            drawWaveform(canvas, i, start, measuredHeight, ctr, selectWaveformPaint(i, start, fractionalSecs));

            i++;
        }

        // If we can see the right edge of the waveform, drawVisualWave the
        // non-waveform area to the right as unselected
        for (i = width; i < measuredWidth; i++) {
            drawWaveformLine(canvas, i, 0, measuredHeight, mUnselectedBkgndLinePaint);
        }

        // BackgroundBehavior borders
        canvas.drawLine(
                mSelectionStart - mOffset + 0.5f, 30,
                mSelectionStart - mOffset + 0.5f, measuredHeight,
                mBorderLinePaint);
        canvas.drawLine(
                mSelectionEnd - mOffset + 0.5f, 0,
                mSelectionEnd - mOffset + 0.5f, measuredHeight - 30,
                mBorderLinePaint);

        // BackgroundBehavior grid
        fractionalSecs = mOffset * onePixelInSecs;
        i = 0;
        while (i < width) {
            i++;
            fractionalSecs += onePixelInSecs;
            int integerSecs2 = (int) fractionalSecs;
            int integerTimecodeNew = (int) (fractionalSecs / timecodeIntervalSecs);
            if (integerTimecodeNew != integerTimecode) {
                integerTimecode = integerTimecodeNew;

                // Turn, e.g. 67 seconds into "1:07"
                String timecodeMinutes = "" + (integerSecs2 / 60);
                String timecodeSeconds = "" + (integerSecs2 % 60);
                if ((integerSecs2 % 60) < 10) {
                    timecodeSeconds = "0" + timecodeSeconds;
                }
                String timecodeStr = timecodeMinutes + ":" + timecodeSeconds;
                float offset = (float) (0.5 * mTimecodePaint.measureText(timecodeStr));
                canvas.drawText(timecodeStr, i - offset, (int) (12 * mDensity), mTimecodePaint);
            }
        }

        if (mListener != null) {
            mListener.waveformDraw();
        }
    }

    protected void drawWaveform(final Canvas canvas, final int i, final int start, final int measuredHeight, final int ctr, final Paint paint) {
        if (i + start < mSelectionStart || i + start >= mSelectionEnd) {
            drawWaveformLine(canvas, i, 0, measuredHeight,
                    mUnselectedBkgndLinePaint);
        }

        int h = (int) (getScaledHeight(mZoomFactorByZoomLevel[mZoomLevel], start + i) * getMeasuredHeight() / 2);
        drawWaveformLine(
                canvas, i,
                ctr - h,
                ctr + 1 + h,
                paint);

        if (i + start == mPlaybackPos) {
            canvas.drawLine(i, 0, i, measuredHeight, mPlaybackLinePaint);
        }
    }

    protected Paint selectWaveformPaint(final int i, final int start, final double fractionalSecs) {
        Paint paint;
        if (i + start >= mSelectionStart && i + start < mSelectionEnd) {
            paint = mSelectedLinePaint;
        } else {
            paint = mUnselectedLinePaint;
        }

        if (segmentsMap != null && !segmentsMap.isEmpty()) {
            if (nextSegment == null) {
                Double key = segmentsMap.ceilingKey(fractionalSecs);
                if (key != null) {
                    nextSegment = segmentsMap.get(segmentsMap.ceilingKey(fractionalSecs));
                }
            }

            if (nextSegment != null) {
                if (nextSegment.getStart().compareTo(fractionalSecs) <= 0 && nextSegment.getStop().compareTo(fractionalSecs) >= 0) {
                    paint = new Paint();
                    paint.setAntiAlias(false);
                    paint.setColor(nextSegment.getColor());
                    return paint;
                } else {
                    Double key = segmentsMap.ceilingKey(fractionalSecs);
                    if (key != null) {
                        nextSegment = segmentsMap.get(segmentsMap.ceilingKey(fractionalSecs));
                    }
                }
            }
        }

        return paint;
    }

    protected float getGain(int i, int numFrames, int[] frameGains) {
        int x = Math.min(i, numFrames - 1);
        if (numFrames < 2) {
            return frameGains[x];
        } else {
            if (x == 0) {
                return (frameGains[0] / 2.0f) + (frameGains[1] / 2.0f);
            } else if (x == numFrames - 1) {
                return (frameGains[numFrames - 2] / 2.0f) + (frameGains[numFrames - 1] / 2.0f);
            } else {
                return (frameGains[x - 1] / 3.0f) + (frameGains[x] / 3.0f) + (frameGains[x + 1] / 3.0f);
            }
        }
    }

    protected float getHeight(int i, int numFrames, int[] frameGains, float scaleFactor, float minGain, float range) {
        float value = (getGain(i, numFrames, frameGains) * scaleFactor - minGain) / range;
        if (value < 0.0)
            value = 0.0f;
        if (value > 1.0)
            value = 1.0f;
        return value;
    }

    /**
     * Called once when a new sound file is added
     */
    protected void computeDoublesForAllZoomLevels() {
        int numFrames = mSoundFile.getNumFrames();

        // Make sure the range is no more than 0 - 255
        float maxGain = 1.0f;
        for (int i = 0; i < numFrames; i++) {
            float gain = getGain(i, numFrames, mSoundFile.getFrameGains());
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
        for (int i = 0; i < numFrames; i++) {
            int smoothedGain = (int) (getGain(i, numFrames, mSoundFile.getFrameGains()) * scaleFactor);
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
        while (minGain < 255 && sum < numFrames / 20) {
            sum += gainHist[(int) minGain];
            minGain++;
        }

        // Re-calibrate the max to be 99%
        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100) {
            sum += gainHist[(int) maxGain];
            maxGain--;
        }

        range = maxGain - minGain;

        mNumZoomLevels = 4;
        mLenByZoomLevel = new int[4];
        mZoomFactorByZoomLevel = new float[4];

        float ratio = getMeasuredWidth() / (float) numFrames;

        if (ratio < 1) {
            mLenByZoomLevel[0] = Math.round(numFrames * ratio);
            mZoomFactorByZoomLevel[0] = ratio;

            mLenByZoomLevel[1] = numFrames;
            mZoomFactorByZoomLevel[1] = 1.0f;

            mLenByZoomLevel[2] = numFrames * 2;
            mZoomFactorByZoomLevel[2] = 2.0f;

            mLenByZoomLevel[3] = numFrames * 3;
            mZoomFactorByZoomLevel[3] = 3.0f;

            mZoomLevel = 0;
        } else {
            mLenByZoomLevel[0] = numFrames;
            mZoomFactorByZoomLevel[0] = 1.0f;

            mLenByZoomLevel[1] = numFrames * 2;
            mZoomFactorByZoomLevel[1] = 2f;

            mLenByZoomLevel[2] = numFrames * 3;
            mZoomFactorByZoomLevel[2] = 3.0f;

            mLenByZoomLevel[3] = numFrames * 4;
            mZoomFactorByZoomLevel[3] = 4.0f;

            mZoomLevel = 0;
            for (int i = 0; i < 4; i++) {
                if (mLenByZoomLevel[mZoomLevel] - getMeasuredWidth() > 0) {
                    break;
                } else {
                    mZoomLevel = i;
                }
            }
        }

        mInitialized = true;
    }

    protected float getZoomedInHeight(float zoomLevel, int i) {
        int f = (int) zoomLevel;
        if (i == 0) {
            return 0.5f * getHeight(0, mSoundFile.getNumFrames(), mSoundFile.getFrameGains(), scaleFactor, minGain, range);
        }
        if (i == 1) {
            return getHeight(0, mSoundFile.getNumFrames(), mSoundFile.getFrameGains(), scaleFactor, minGain, range);
        }
        if (i % f == 0) {
            float x1 = getHeight(i / f - 1, mSoundFile.getNumFrames(), mSoundFile.getFrameGains(), scaleFactor, minGain, range);
            float x2 = getHeight(i / f, mSoundFile.getNumFrames(), mSoundFile.getFrameGains(), scaleFactor, minGain, range);
            return 0.5f * (x1 + x2);
        } else if ((i - 1) % f == 0) {
            return getHeight((i - 1) / f, mSoundFile.getNumFrames(), mSoundFile.getFrameGains(), scaleFactor, minGain, range);
        }
        return 0;
    }

    protected float getZoomedOutHeight(float zoomLevel, int i) {
        int f = (int) (i / zoomLevel);
        float x1 = getHeight(f, mSoundFile.getNumFrames(), mSoundFile.getFrameGains(), scaleFactor, minGain, range);
        float x2 = getHeight(f + 1, mSoundFile.getNumFrames(), mSoundFile.getFrameGains(), scaleFactor, minGain, range);
        return 0.5f * (x1 + x2);
    }

    protected float getNormalHeight(int i) {
        return getHeight(i, mSoundFile.getNumFrames(), mSoundFile.getFrameGains(), scaleFactor, minGain, range);
    }

    protected float getScaledHeight(float zoomLevel, int i) {
        if (zoomLevel == 1.0) {
            return getNormalHeight(i);
        } else if (zoomLevel < 1.0) {
            return getZoomedOutHeight(zoomLevel, i);
        }
        return getZoomedInHeight(zoomLevel, i);
    }
}
