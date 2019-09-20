package com.ldt.musicr.ui.widget;


        import android.app.ProgressDialog;
        import android.content.DialogInterface;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.os.Bundle;
        import android.os.Handler;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.ViewGroup;
        import android.widget.AbsoluteLayout;
        import android.widget.ImageButton;
        import android.widget.TextView;

        import com.ldt.musicr.R;
        import com.ldt.musicr.ui.widget.soundfile.CheapSoundFile;
        import com.ldt.musicr.ui.widget.soundfile.MarkerView;
        import com.ldt.musicr.ui.widget.soundfile.Segment;
        import com.ldt.musicr.ui.widget.soundfile.WaveformView;

        import java.io.File;
        import java.io.FileInputStream;
        import java.util.List;

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

/**
 * Keeps track of the waveform display, current horizontal offset, marker handles,
 * start / end text boxes, and handles all of the buttons and controls
 *
 * Modified by Anna Stępień <anna.stepien@semantive.com>
 */
public abstract class WaveformFragment extends Fragment implements MarkerView.MarkerListener, WaveformView.WaveformListener {

    public static final String TAG = "WaveformFragment";

    protected long mLoadingLastUpdateTime;
    protected boolean mLoadingKeepGoing;
    protected ProgressDialog mProgressDialog;
    protected CheapSoundFile mSoundFile;
    protected File mFile;
    protected String mFilename;
    protected WaveformView mWaveformView;
    protected MarkerView mStartMarker;
    protected MarkerView mEndMarker;
    protected TextView mStartText;
    protected TextView mEndText;
    protected TextView mInfo;
    protected ImageButton mPlayButton;
    protected ImageButton mRewindButton;
    protected ImageButton mFfwdButton;
    protected boolean mKeyDown;
    protected String mCaption = "";
    protected int mWidth;
    protected int mMaxPos;
    protected int mStartPos;
    protected int mEndPos;
    protected boolean mStartVisible;
    protected boolean mEndVisible;
    protected int mLastDisplayedStartPos;
    protected int mLastDisplayedEndPos;
    protected int mOffset;
    protected int mOffsetGoal;
    protected int mFlingVelocity;
    protected int mPlayStartMsec;
    protected int mPlayStartOffset;
    protected int mPlayEndMsec;
    protected Handler mHandler;
    protected boolean mIsPlaying;
    protected MediaPlayer mPlayer;
    protected boolean mTouchDragging;
    protected float mTouchStart;
    protected int mTouchInitialOffset;
    protected int mTouchInitialStartPos;
    protected int mTouchInitialEndPos;
    protected long mWaveformTouchStartMsec;
    protected float mDensity;
    protected int mMarkerLeftInset;
    protected int mMarkerRightInset;
    protected int mMarkerTopOffset;
    protected int mMarkerBottomOffset;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_custom_waveform_view, container, false);
        loadGui(view);
        if (mSoundFile == null) { // ban dau neu null thi mo progress bar va load file
            loadFromFile();
        } else {
            mHandler.post(this::finishOpeningSoundFile); // cai nay nghia la ?
        }
        return view;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);

        mPlayer = null;
        mIsPlaying = false;

        mFilename = getFileName();
        mSoundFile = null;
        mKeyDown = false;

        mHandler = new Handler();

        mHandler.postDelayed(mTimerRunnable, 100);
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        mSoundFile = null;
        mWaveformView = null;
        super.onDestroy();
    }

    //
    // WaveformListener
    //

    /**
     * Every time we get a message that our waveform drew, see if we need to
     * animate and trigger another doDraw.
     */
    public void waveformDraw() {
        mWidth = mWaveformView.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown)
            updateDisplay();
        else if (mIsPlaying) {
            updateDisplay();
        } else if (mFlingVelocity != 0) {
            updateDisplay();
        }
    }

    public void waveformTouchStart(float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    public void waveformTouchMove(float x) {
        mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
        Log.d (TAG,"onTouchMove : x = "+x+", mOffset = "+mOffset);
        updateDisplay();
    }

    public void waveformTouchEnd() {
        mTouchDragging = false;
        mOffsetGoal = mOffset;

        long elapsedMsec = System.currentTimeMillis() - mWaveformTouchStartMsec;
        if (elapsedMsec < 300) {
            Log.d(TAG,"elapsed");
            if (mIsPlaying) {
                int seekMsec = mWaveformView.pixelsToMillisecs((int) (mTouchStart + mOffset));
                if (seekMsec >= mPlayStartMsec && seekMsec < mPlayEndMsec) {
                    Log.d(TAG,"seekTo");
                    mPlayer.seekTo(seekMsec - mPlayStartOffset);
                } else {
                    handlePause();
                }
            } else {
                onPlay((int) (mTouchStart + mOffset));
            }
        }
    }

    public void waveformFling(float vx) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-vx);
        Log.d (TAG,"onFling : vx = "+vx+", mOffset = "+mOffset);
        updateDisplay();
    }

    public void waveformZoomIn() {
        mWaveformView.zoomIn();
        mStartPos = mWaveformView.getStart();
        mEndPos = mWaveformView.getEnd();
        mMaxPos = mWaveformView.maxPos();
        mOffset = mWaveformView.getOffset();
        mOffsetGoal = mOffset;
        enableZoomButtons();
        updateDisplay();
    }

    public void waveformZoomOut() {
        mWaveformView.zoomOut();
        mStartPos = mWaveformView.getStart();
        mEndPos = mWaveformView.getEnd();
        mMaxPos = mWaveformView.maxPos();
        mOffset = mWaveformView.getOffset();
        mOffsetGoal = mOffset;
        enableZoomButtons();
        updateDisplay();
    }

    //
    // MarkerListener
    //

    public void markerDraw() {
    }

    public void markerTouchStart(MarkerView marker, float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
    }

    public void markerTouchMove(MarkerView marker, float x) {
        float delta = x - mTouchStart;

        if (marker == mStartMarker) {
            mStartPos = trap((int) (mTouchInitialStartPos + delta));
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
        } else {
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
            if (mEndPos < mStartPos)
                mEndPos = mStartPos;
        }

        updateDisplay();
    }

    public void markerTouchEnd(MarkerView marker) {
        mTouchDragging = false;
        if (marker == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    public void markerLeft(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerRight(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos)
                mStartPos = mMaxPos;
            mEndPos += (mStartPos - saveStart);
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalStart();
        }

        if (marker == mEndMarker) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos)
                mEndPos = mMaxPos;

            setOffsetGoalEnd();
        }

        updateDisplay();
    }

    public void markerEnter(MarkerView marker) {
    }

    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    public void markerFocus(MarkerView marker) {
        mKeyDown = false;
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }

        // Delay updaing the display because if this focus was in
        // response to a touch event, we want to receive the touch
        // event too before updating the display.
        mHandler.postDelayed(this::updateDisplay, 100);
    }

    //
    // Internal methods
    //

    protected void loadGui(View view) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;

        mMarkerLeftInset = (int) (46 * mDensity);
        mMarkerRightInset = (int) (48 * mDensity);
        mMarkerTopOffset = (int) (10 * mDensity);
        mMarkerBottomOffset = (int) (10 * mDensity);

        mStartText = view.findViewById(R.id.starttext);
        mStartText.addTextChangedListener(mTextWatcher);
        mEndText = view.findViewById(R.id.endtext);
        mEndText.addTextChangedListener(mTextWatcher);

        mPlayButton = view.findViewById(R.id.play);
        mPlayButton.setOnClickListener(mPlayListener);
        mRewindButton = view.findViewById(R.id.rew);
        mRewindButton.setOnClickListener(getRewindListener());
        mFfwdButton =  view.findViewById(R.id.ffwd);
        mFfwdButton.setOnClickListener(getFwdListener());

        TextView markStartButton =   view.findViewById(R.id.mark_start);
        markStartButton.setOnClickListener(mMarkStartListener);
        TextView markEndButton =   view.findViewById(R.id.mark_end);
        markEndButton.setOnClickListener(mMarkEndListener);

        enableDisableButtons();

        mWaveformView =  view.findViewById(R.id.waveform);
        mWaveformView.setListener(this);
        mWaveformView.setSegments(getSegments());

        mInfo =   view.findViewById(R.id.info);
        mInfo.setText(mCaption);

        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        if (mSoundFile != null && !mWaveformView.hasSoundFile()) {
            mWaveformView.setSoundFile(mSoundFile);
            mWaveformView.recomputeHeights(mDensity);
            mMaxPos = mWaveformView.maxPos();
        }

        mStartMarker = view.findViewById(R.id.startmarker);
        mStartMarker.setListener(this);
        mStartMarker.setImageAlpha(255);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
        mStartVisible = true;

        mEndMarker = view.findViewById(R.id.endmarker);
        mEndMarker.setListener(this);
        mEndMarker.setImageAlpha(255);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
        mEndVisible = true;

        updateDisplay();
    }

    protected void loadFromFile() {
        mFile = new File(mFilename);
        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mLoadingKeepGoing = false;
            }
        });
        mProgressDialog.show();

        final CheapSoundFile.ProgressListener listener = new CheapSoundFile.ProgressListener() {
            @Override
            public boolean reportProgress(double fractionComplete) {
                long now = System.currentTimeMillis();
                if (now - mLoadingLastUpdateTime > 100) { // what does this mean ?
                    mProgressDialog.setProgress(
                            (int) (mProgressDialog.getMax() * fractionComplete));
                    mLoadingLastUpdateTime = now;
                }
                return mLoadingKeepGoing;
            }
        };

        // Create the MediaPlayer in a background thread
        new Thread() {
            public void run() {
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(mFile.getAbsolutePath());
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.prepare();
                    mPlayer = player;
                } catch (final java.io.IOException e) {
                    Log.e(TAG, "Error while creating media player", e);
                }
            }
        }.start();

        // Load the sound file in a background thread
        new Thread() {
            public void run() {
                try {
                    mSoundFile = CheapSoundFile.create(mFile.getAbsolutePath(), listener);
                } catch (final Exception e) {
                    Log.e(TAG, "Error while loading sound file", e);
                    mProgressDialog.dismiss();
                    mInfo.setText(e.toString());
                    return;
                }
                if (mLoadingKeepGoing) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            finishOpeningSoundFile();
                        }
                    });
                }
            }
        }.start();

    }

    protected void finishOpeningSoundFile() {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);

        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        mTouchDragging = false;

        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();

        mCaption = mSoundFile.getFiletype() + ", " +
                mSoundFile.getSampleRate() + " Hz, " +
                mSoundFile.getAvgBitrateKbps() + " kbps, " +
                formatTime(mMaxPos) + " " + getResources().getString(R.string.time_seconds);
        mInfo.setText(mCaption);
        mProgressDialog.dismiss();
        updateDisplay();
    }

    protected synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int frames = mWaveformView.millisecsToPixels(now);
            mWaveformView.setPlayback(frames);
            setOffsetGoalNoUpdate(frames - mWidth / 2);
            if (now >= mPlayEndMsec) {
                handlePause();
            }
        }

        if (!mTouchDragging) {
            int offsetDelta;

            if (mFlingVelocity != 0) {
                float saveVal = mFlingVelocity;


                offsetDelta = (int) ((mFlingVelocity+0.0f) / (30)); // v of offset ("distance")
           //     offsetDelta =(mFlingVelocity>0) ? 160: -160; // v of offset ("distance")
                if (mFlingVelocity > offsetDelta) {  // ?
                    mFlingVelocity -= offsetDelta;
                } else if (mFlingVelocity < -offsetDelta) {
                    mFlingVelocity -= offsetDelta;
                } else {
                    mFlingVelocity = 0;
                }

                mOffset += offsetDelta; // S + v = nextS

                if (mOffset + mWidth / 2 > mMaxPos) { // if
                    mOffset = mMaxPos - mWidth / 2;
                    mFlingVelocity = 0; // Stop animate
                }
                if (mOffset < 0) { // if
                    mOffset = 0;
                    mFlingVelocity = 0; // Stop animate
                }
                mOffsetGoal = mOffset; // ?
            } else {
                offsetDelta = mOffsetGoal - mOffset;

                if (offsetDelta > 10)
                    offsetDelta /= 10;
                else if (offsetDelta > 0)
                    offsetDelta = 1;
                else if (offsetDelta < -10)
                    offsetDelta /= 10;
                else if (offsetDelta < 0)
                    offsetDelta = -1;
                else
                    offsetDelta = 0;

                mOffset += offsetDelta;
            }
        }

        mWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mWaveformView.invalidate();

        mStartMarker.setContentDescription(getResources().getText(R.string.start_marker) + " " + formatTime(mStartPos));
        mEndMarker.setContentDescription(getResources().getText(R.string.end_marker) + " " + formatTime(mEndPos));

        int startX = mStartPos - mOffset - mMarkerLeftInset;
        if (startX + mStartMarker.getWidth() >= 0) {
            if (!mStartVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mStartVisible = true;
                        mStartMarker.setImageAlpha(255);
                    }
                }, 0);
            }
        } else {
            if (mStartVisible) {
                mStartMarker.setImageAlpha(0);
                mStartVisible = false;
            }
            startX = 0;
        }

        int endX = mEndPos - mOffset - mEndMarker.getWidth() + mMarkerRightInset;
        if (endX + mEndMarker.getWidth() >= 0) {
            if (!mEndVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEndVisible = true;
                        mEndMarker.setImageAlpha(255);
                    }
                }, 0);
            }
        } else {
            if (mEndVisible) {
                mEndMarker.setImageAlpha(0);
                mEndVisible = false;
            }
            endX = 0;
        }

        mStartMarker.setLayoutParams(
                new AbsoluteLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        startX, mMarkerTopOffset));

        mEndMarker.setLayoutParams(
                new AbsoluteLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        endX, mWaveformView.getMeasuredHeight() - mEndMarker.getHeight() - mMarkerBottomOffset));
    }

    protected Runnable mTimerRunnable = new Runnable() {
        public void run() {
            // Updating an EditText is slow on Android.  Make sure
            // we only do the update if the text has actually changed.
            if (mStartPos != mLastDisplayedStartPos && !mStartText.hasFocus()) {
                mStartText.setText(formatTime(mStartPos));
                mLastDisplayedStartPos = mStartPos;
            }

            if (mEndPos != mLastDisplayedEndPos && !mEndText.hasFocus()) {
                mEndText.setText(formatTime(mEndPos));
                mLastDisplayedEndPos = mEndPos;
            }

            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };

    protected void enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
            mPlayButton.setContentDescription(getResources().getText(R.string.stop));
        } else {
            mPlayButton.setImageResource(android.R.drawable.ic_media_play);
            mPlayButton.setContentDescription(getResources().getText(R.string.play));
        }
    }

    protected void resetPositions() {
        mStartPos = 0;
        mEndPos = mMaxPos;
    }

    protected int trap(int pos) {
        if (pos < 0)
            return 0;
        if (pos > mMaxPos)
            return mMaxPos;
        return pos;
    }

    protected void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - mWidth / 2);
    }

    protected void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - mWidth / 2);
    }

    protected void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - mWidth / 2);
    }

    protected void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - mWidth / 2);
    }

    protected void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    protected void setOffsetGoalNoUpdate(int offset) {
        if (mTouchDragging) {
            return;
        }

        mOffsetGoal = offset;
        if (mOffsetGoal + mWidth / 2 > mMaxPos)
            mOffsetGoal = mMaxPos - mWidth / 2;
        if (mOffsetGoal < 0)
            mOffsetGoal = 0;
    }

    protected String formatTime(int pixels) {
        if (mWaveformView != null && mWaveformView.isInitialized()) {
            return formatDecimal(mWaveformView.pixelsToSeconds(pixels));
        } else {
            return "";
        }
    }

    protected String formatDecimal(double x) {
        int xWhole = (int) x;
        int xFrac = (int) (100 * (x - xWhole) + 0.5);

        if (xFrac >= 100) {
            xWhole++; //Round up
            xFrac -= 100; //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10; //we need a fraction that is 2 digits long
            }
        }

        if (xFrac < 10)
            return xWhole + ".0" + xFrac;
        else
            return xWhole + "." + xFrac;
    }

    protected synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
    }

    protected synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
            return;
        }

        if (mPlayer == null) {
            // Not initialized yet
            return;
        }

        try {
            mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mEndPos);
            }

            mPlayStartOffset = 0;

            int startFrame = mWaveformView.secondsToFrames(mPlayStartMsec * 0.001);
            int endFrame = mWaveformView.secondsToFrames(mPlayEndMsec * 0.001);
            int startByte = mSoundFile.getSeekableFrameOffset(startFrame);
            int endByte = mSoundFile.getSeekableFrameOffset(endFrame);
            if (startByte >= 0 && endByte >= 0) {
                try {
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    FileInputStream subsetInputStream = new FileInputStream(mFile.getAbsolutePath());
                    mPlayer.setDataSource(subsetInputStream.getFD(),startByte, endByte - startByte);
                    mPlayer.prepare();
                    mPlayStartOffset = mPlayStartMsec;
                } catch (Exception e) {
                    Log.e(TAG, "Exception trying to shuffle file subset", e);
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(mFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayStartOffset = 0;
                }
            }

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    WaveformFragment.this.handlePause();
                }
            });
            mIsPlaying = true;

            if (mPlayStartOffset == 0) {
                mPlayer.seekTo(mPlayStartMsec);
            }
            mPlayer.start();
            updateDisplay();
            enableDisableButtons();
        } catch (Exception e) {
            Log.e(TAG, "Exception while playing file", e);
        }
    }

    protected void enableZoomButtons() {
    }

    protected OnClickListener mPlayListener = new OnClickListener() {
        public void onClick(View sender) {
            onPlay(mStartPos);
        }
    };

    protected OnClickListener mRewindListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() - 5000;
                if (newPos < mPlayStartMsec)
                    newPos = mPlayStartMsec;
                mPlayer.seekTo(newPos);
            } else {
                mStartPos = trap(mStartPos - mWaveformView.secondsToPixels(getStep()));
                updateDisplay();
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);
            }
        }
    };

    protected OnClickListener mFfwdListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = 5000 + mPlayer.getCurrentPosition();
                if (newPos > mPlayEndMsec)
                    newPos = mPlayEndMsec;
                mPlayer.seekTo(newPos);
            } else {
                mStartPos = trap(mStartPos + mWaveformView.secondsToPixels(getStep()));
                updateDisplay();
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);
            }
        }
    };

    protected OnClickListener mMarkStartListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mStartPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
            }
        }
    };

    protected OnClickListener mMarkEndListener = new OnClickListener() {
        public void onClick(View sender) {
            if (mIsPlaying) {
                mEndPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
                handlePause();
            }
        }
    };

    protected TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (mStartText.hasFocus()) {
                try {
                    mStartPos = mWaveformView.secondsToPixels(Double.parseDouble(mStartText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e) {
                }
            }
            if (mEndText.hasFocus()) {
                try {
                    mEndPos = mWaveformView.secondsToPixels(Double.parseDouble(mEndText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e) {
                }
            }
        }
    };

    protected abstract String getFileName();

    protected List<Segment> getSegments() {
        return null;
    }

    protected OnClickListener getFwdListener() {
        return mFfwdListener;
    }

    protected OnClickListener getRewindListener() {
        return mRewindListener;
    }

    protected int getStep() {
        int maxSeconds = (int) mWaveformView.pixelsToSeconds(mWaveformView.maxPos());
        if (maxSeconds / 3600 > 0) {
            return 600;
        } else if (maxSeconds / 1800 > 0) {
            return 300;
        } else if (maxSeconds / 300 > 0) {
            return 60;
        }
        return 5;
    }
}