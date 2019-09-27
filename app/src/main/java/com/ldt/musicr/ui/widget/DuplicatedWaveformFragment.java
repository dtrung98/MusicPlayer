/*
package com.ldt.NewDefinitionMusicApp.Views;

/**
 * Created by trung on 9/5/2017.



//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

        import android.app.ProgressDialog;
        import android.media.MediaPlayer;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.View.OnClickListener;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.AbsoluteLayout.LayoutParams;

        import com.ldt.NewDefinitionMusicApp.R;
        import com.semantive.waveformandroid.R.id;
        import com.semantive.waveformandroid.R.layout;
        import com.semantive.waveformandroid.R.string;
        import com.semantive.waveformandroid.waveform.Segment;
        import com.semantive.waveformandroid.waveform.soundfile.CheapSoundFile;
        import com.semantive.waveformandroid.waveform.soundfile.CheapSoundFile.ProgressListener;
        import com.semantive.waveformandroid.waveform.view.MarkerView;
        import com.semantive.waveformandroid.waveform.view.WaveformView;
        import com.semantive.waveformandroid.waveform.view.MarkerView.MarkerListener;
        import com.semantive.waveformandroid.waveform.view.WaveformView.WaveformListener;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.util.List;

public abstract class DuplicatedWaveformFragment extends Fragment implements MarkerListener, WaveformListener {
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
    protected Runnable mTimerRunnable = new Runnable() {
        public void run() {
            if(DuplicatedWaveformFragment.this.mStartPos != DuplicatedWaveformFragment.this.mLastDisplayedStartPos && !DuplicatedWaveformFragment.this.mStartText.hasFocus()) {
                DuplicatedWaveformFragment.this.mStartText.setText(DuplicatedWaveformFragment.this.formatTime(DuplicatedWaveformFragment.this.mStartPos));
                DuplicatedWaveformFragment.this.mLastDisplayedStartPos = DuplicatedWaveformFragment.this.mStartPos;
            }

            if(DuplicatedWaveformFragment.this.mEndPos != DuplicatedWaveformFragment.this.mLastDisplayedEndPos && !DuplicatedWaveformFragment.this.mEndText.hasFocus()) {
                DuplicatedWaveformFragment.this.mEndText.setText(DuplicatedWaveformFragment.this.formatTime(DuplicatedWaveformFragment.this.mEndPos));
                DuplicatedWaveformFragment.this.mLastDisplayedEndPos = DuplicatedWaveformFragment.this.mEndPos;
            }

            DuplicatedWaveformFragment.this.mHandler.postDelayed(DuplicatedWaveformFragment.this.mTimerRunnable, 100L);
        }
    };
    protected OnClickListener mPlayListener = new OnClickListener() {
        public void onClick(View sender) {
            DuplicatedWaveformFragment.this.onPlay(DuplicatedWaveformFragment.this.mStartPos);
        }
    };
    protected OnClickListener mRewindListener = new OnClickListener() {
        public void onClick(View sender) {
            if(DuplicatedWaveformFragment.this.mIsPlaying) {
                int newPos = DuplicatedWaveformFragment.this.mPlayer.getCurrentPosition() - 5000;
                if(newPos < DuplicatedWaveformFragment.this.mPlayStartMsec) {
                    newPos = DuplicatedWaveformFragment.this.mPlayStartMsec;
                }

                DuplicatedWaveformFragment.this.mPlayer.seekTo(newPos);
            } else {
                DuplicatedWaveformFragment.this.mStartPos = DuplicatedWaveformFragment.this.trap(DuplicatedWaveformFragment.this.mStartPos - DuplicatedWaveformFragment.this.mWaveformView.secondsToPixels((double) DuplicatedWaveformFragment.this.getStep()));
                DuplicatedWaveformFragment.this.updateDisplay();
                DuplicatedWaveformFragment.this.mStartMarker.requestFocus();
                DuplicatedWaveformFragment.this.markerFocus(DuplicatedWaveformFragment.this.mStartMarker);
            }

        }
    };
    protected OnClickListener mFfwdListener = new OnClickListener() {
        public void onClick(View sender) {
            if(DuplicatedWaveformFragment.this.mIsPlaying) {
                int newPos = 5000 + DuplicatedWaveformFragment.this.mPlayer.getCurrentPosition();
                if(newPos > DuplicatedWaveformFragment.this.mPlayEndMsec) {
                    newPos = DuplicatedWaveformFragment.this.mPlayEndMsec;
                }

                DuplicatedWaveformFragment.this.mPlayer.seekTo(newPos);
            } else {
                DuplicatedWaveformFragment.this.mStartPos = DuplicatedWaveformFragment.this.trap(DuplicatedWaveformFragment.this.mStartPos + DuplicatedWaveformFragment.this.mWaveformView.secondsToPixels((double) DuplicatedWaveformFragment.this.getStep()));
                DuplicatedWaveformFragment.this.updateDisplay();
                DuplicatedWaveformFragment.this.mStartMarker.requestFocus();
                DuplicatedWaveformFragment.this.markerFocus(DuplicatedWaveformFragment.this.mStartMarker);
            }

        }
    };
    protected OnClickListener mMarkStartListener = new OnClickListener() {
        public void onClick(View sender) {
            if(DuplicatedWaveformFragment.this.mIsPlaying) {
                DuplicatedWaveformFragment.this.mStartPos = DuplicatedWaveformFragment.this.mWaveformView.millisecsToPixels(DuplicatedWaveformFragment.this.mPlayer.getCurrentPosition() + DuplicatedWaveformFragment.this.mPlayStartOffset);
                DuplicatedWaveformFragment.this.updateDisplay();
            }

        }
    };
    protected OnClickListener mMarkEndListener = new OnClickListener() {
        public void onClick(View sender) {
            if(DuplicatedWaveformFragment.this.mIsPlaying) {
                DuplicatedWaveformFragment.this.mEndPos = DuplicatedWaveformFragment.this.mWaveformView.millisecsToPixels(DuplicatedWaveformFragment.this.mPlayer.getCurrentPosition() + DuplicatedWaveformFragment.this.mPlayStartOffset);
                DuplicatedWaveformFragment.this.updateDisplay();
                DuplicatedWaveformFragment.this.handlePause();
            }

        }
    };
    protected TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if(DuplicatedWaveformFragment.this.mStartText.hasFocus()) {
                try {
                    DuplicatedWaveformFragment.this.mStartPos = DuplicatedWaveformFragment.this.mWaveformView.secondsToPixels(Double.parseDouble(DuplicatedWaveformFragment.this.mStartText.getText().toString()));
                    DuplicatedWaveformFragment.this.updateDisplay();
                } catch (NumberFormatException var4) {
                    ;
                }
            }

            if(DuplicatedWaveformFragment.this.mEndText.hasFocus()) {
                try {
                    DuplicatedWaveformFragment.this.mEndPos = DuplicatedWaveformFragment.this.mWaveformView.secondsToPixels(Double.parseDouble(DuplicatedWaveformFragment.this.mEndText.getText().toString()));
                    DuplicatedWaveformFragment.this.updateDisplay();
                } catch (NumberFormatException var3) {
                    ;
                }
            }

        }
    };

    public DuplicatedWaveformFragment() {
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(layout.fragment_waveform, container, false);
        this.loadGui(view);
        if(this.mSoundFile == null) {
            this.loadFromFile();
        } else {
            this.mHandler.post(DuplicatedWaveformFragment$$Lambda$1.lambdaFactory$(this));
        }

        return view;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setRetainInstance(true);
        this.mPlayer = null;
        this.mIsPlaying = false;
        this.mFilename = this.getFileName();
        this.mSoundFile = null;
        this.mKeyDown = false;
        this.mHandler = new Handler();
        this.mHandler.postDelayed(this.mTimerRunnable, 100L);
    }

    public void destroy() {
        if(this.mPlayer != null && this.mPlayer.isPlaying()) {
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mPlayer = null;
        }

        this.mSoundFile = null;
        this.mWaveformView = null;
        super.destroy();
    }

    public void waveformDraw() {
        this.mWidth = this.mWaveformView.getMeasuredWidth();
        if(this.mOffsetGoal != this.mOffset && !this.mKeyDown) {
            this.updateDisplay();
        } else if(this.mIsPlaying) {
            this.updateDisplay();
        } else if(this.mFlingVelocity != 0) {
            this.updateDisplay();
        }

    }

    public void waveformTouchStart(float x) {
        this.mTouchDragging = true;
        this.mTouchStart = x;
        this.mTouchInitialOffset = this.mOffset;
        this.mFlingVelocity = 0;
        this.mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    public void waveformTouchMove(float x) {
        this.mOffset = this.trap((int)((float)this.mTouchInitialOffset + (this.mTouchStart - x)));
        this.updateDisplay();
    }

    public void waveformTouchEnd() {
        this.mTouchDragging = false;
        this.mOffsetGoal = this.mOffset;
        long elapsedMsec = System.currentTimeMillis() - this.mWaveformTouchStartMsec;
        if(elapsedMsec < 300L) {
            if(this.mIsPlaying) {
                int seekMsec = this.mWaveformView.pixelsToMillisecs((int)(this.mTouchStart + (float)this.mOffset));
                if(seekMsec >= this.mPlayStartMsec && seekMsec < this.mPlayEndMsec) {
                    this.mPlayer.seekTo(seekMsec - this.mPlayStartOffset);
                } else {
                    this.handlePause();
                }
            } else {
                this.onPlay((int)(this.mTouchStart + (float)this.mOffset));
            }
        }

    }

    public void waveformFling(float vx) {
        this.mTouchDragging = false;
        this.mOffsetGoal = this.mOffset;
        this.mFlingVelocity = (int)(-vx);
        this.updateDisplay();
    }

    public void waveformZoomIn() {
        this.mWaveformView.zoomIn();
        this.mStartPos = this.mWaveformView.getStart();
        this.mEndPos = this.mWaveformView.getEnd();
        this.mMaxPos = this.mWaveformView.maxPos();
        this.mOffset = this.mWaveformView.getOffset();
        this.mOffsetGoal = this.mOffset;
        this.enableZoomButtons();
        this.updateDisplay();
    }

    public void waveformZoomOut() {
        this.mWaveformView.zoomOut();
        this.mStartPos = this.mWaveformView.getStart();
        this.mEndPos = this.mWaveformView.getEnd();
        this.mMaxPos = this.mWaveformView.maxPos();
        this.mOffset = this.mWaveformView.getOffset();
        this.mOffsetGoal = this.mOffset;
        this.enableZoomButtons();
        this.updateDisplay();
    }

    public void markerDraw() {
    }

    public void markerTouchStart(MarkerView marker, float x) {
        this.mTouchDragging = true;
        this.mTouchStart = x;
        this.mTouchInitialStartPos = this.mStartPos;
        this.mTouchInitialEndPos = this.mEndPos;
    }

    public void markerTouchMove(MarkerView marker, float x) {
        float delta = x - this.mTouchStart;
        if(marker == this.mStartMarker) {
            this.mStartPos = this.trap((int)((float)this.mTouchInitialStartPos + delta));
            this.mEndPos = this.trap((int)((float)this.mTouchInitialEndPos + delta));
        } else {
            this.mEndPos = this.trap((int)((float)this.mTouchInitialEndPos + delta));
            if(this.mEndPos < this.mStartPos) {
                this.mEndPos = this.mStartPos;
            }
        }

        this.updateDisplay();
    }

    public void markerTouchEnd(MarkerView marker) {
        this.mTouchDragging = false;
        if(marker == this.mStartMarker) {
            this.setOffsetGoalStart();
        } else {
            this.setOffsetGoalEnd();
        }

    }

    public void markerLeft(MarkerView marker, int velocity) {
        this.mKeyDown = true;
        if(marker == this.mStartMarker) {
            int saveStart = this.mStartPos;
            this.mStartPos = this.trap(this.mStartPos - velocity);
            this.mEndPos = this.trap(this.mEndPos - (saveStart - this.mStartPos));
            this.setOffsetGoalStart();
        }

        if(marker == this.mEndMarker) {
            if(this.mEndPos == this.mStartPos) {
                this.mStartPos = this.trap(this.mStartPos - velocity);
                this.mEndPos = this.mStartPos;
            } else {
                this.mEndPos = this.trap(this.mEndPos - velocity);
            }

            this.setOffsetGoalEnd();
        }

        this.updateDisplay();
    }

    public void markerRight(MarkerView marker, int velocity) {
        this.mKeyDown = true;
        if(marker == this.mStartMarker) {
            int saveStart = this.mStartPos;
            this.mStartPos += velocity;
            if(this.mStartPos > this.mMaxPos) {
                this.mStartPos = this.mMaxPos;
            }

            this.mEndPos += this.mStartPos - saveStart;
            if(this.mEndPos > this.mMaxPos) {
                this.mEndPos = this.mMaxPos;
            }

            this.setOffsetGoalStart();
        }

        if(marker == this.mEndMarker) {
            this.mEndPos += velocity;
            if(this.mEndPos > this.mMaxPos) {
                this.mEndPos = this.mMaxPos;
            }

            this.setOffsetGoalEnd();
        }

        this.updateDisplay();
    }

    public void markerEnter(MarkerView marker) {
    }

    public void markerKeyUp() {
        this.mKeyDown = false;
        this.updateDisplay();
    }

    public void markerFocus(MarkerView marker) {
        this.mKeyDown = false;
        if(marker == this.mStartMarker) {
            this.setOffsetGoalStartNoUpdate();
        } else {
            this.setOffsetGoalEndNoUpdate();
        }

        this.mHandler.postDelayed(DuplicatedWaveformFragment$$Lambda$4.lambdaFactory$(this), 100L);
    }

    protected void loadGui(View view) {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.mDensity = metrics.density;
        this.mMarkerLeftInset = (int)(46.0F * this.mDensity);
        this.mMarkerRightInset = (int)(48.0F * this.mDensity);
        this.mMarkerTopOffset = (int)(10.0F * this.mDensity);
        this.mMarkerBottomOffset = (int)(10.0F * this.mDensity);
        this.mStartText = (TextView)view.findViewById(id.starttext);
        this.mStartText.addTextChangedListener(this.mTextWatcher);
        this.mEndText = (TextView)view.findViewById(id.endtext);
        this.mEndText.addTextChangedListener(this.mTextWatcher);
        this.mPlayButton = (ImageButton)view.findViewById(id.shuffle);
        this.mPlayButton.setOnClickListener(this.mPlayListener);
        this.mRewindButton = (ImageButton)view.findViewById(id.rew);
        this.mRewindButton.setOnClickListener(this.getRewindListener());
        this.mFfwdButton = (ImageButton)view.findViewById(id.ffwd);
        this.mFfwdButton.setOnClickListener(this.getFwdListener());
        TextView markStartButton = (TextView)view.findViewById(id.mark_start);
        markStartButton.setOnClickListener(this.mMarkStartListener);
        TextView markEndButton = (TextView)view.findViewById(id.mark_end);
        markEndButton.setOnClickListener(this.mMarkEndListener);
        this.enableDisableButtons();
        this.mWaveformView = (WaveformView)view.findViewById(id.waveform);
        this.mWaveformView.addBaseListener(this);
        this.mWaveformView.setSegments(this.getSegments());
        this.mInfo = (TextView)view.findViewById(id.info);
        this.mInfo.setText(this.mCaption);
        this.mMaxPos = 0;
        this.mLastDisplayedStartPos = -1;
        this.mLastDisplayedEndPos = -1;
        if(this.mSoundFile != null && !this.mWaveformView.hasSoundFile()) {
            this.mWaveformView.setSoundFile(this.mSoundFile);
            this.mWaveformView.recomputeHeights(this.mDensity);
            this.mMaxPos = this.mWaveformView.maxPos();
        }

        this.mStartMarker = (MarkerView)view.findViewById(id.startmarker);
        this.mStartMarker.addBaseListener(this);
        this.mStartMarker.setImageAlpha(255);
        this.mStartMarker.setFocusable(true);
        this.mStartMarker.setFocusableInTouchMode(true);
        this.mStartVisible = true;
        this.mEndMarker = (MarkerView)view.findViewById(id.endmarker);
        this.mEndMarker.addBaseListener(this);
        this.mEndMarker.setImageAlpha(255);
        this.mEndMarker.setFocusable(true);
        this.mEndMarker.setFocusableInTouchMode(true);
        this.mEndVisible = true;
        this.updateDisplay();
    }

    protected void loadFromFile() {
        this.mFile = new File(this.mFilename);
        this.mLoadingLastUpdateTime = System.currentTimeMillis();
        this.mLoadingKeepGoing = true;
        this.mProgressDialog = new ProgressDialog(this.getActivity());
        this.mProgressDialog.setProgressStyle(1);
        this.mProgressDialog.setTitle(string.progress_dialog_loading);
        this.mProgressDialog.setCancelable(true);
        this.mProgressDialog.setOnCancelListener(DuplicatedWaveformFragment$$Lambda$5.lambdaFactory$(this));
        this.mProgressDialog.show();
        final ProgressListener listener = DuplicatedWaveformFragment$$Lambda$6.lambdaFactory$(this);
        (new Thread() {
            public void run() {
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(DuplicatedWaveformFragment.this.mFile.getAbsolutePath());
                    player.setAudioStreamType(3);
                    player.prepare();
                    DuplicatedWaveformFragment.this.mPlayer = player;
                } catch (IOException var2) {
                    Log.e("WaveformFragment", "Error while creating media player", var2);
                }

            }
        }).start();
        (new Thread() {
            public void run() {
                try {
                    DuplicatedWaveformFragment.this.mSoundFile = CheapSoundFile.create(DuplicatedWaveformFragment.this.mFile.getAbsolutePath(), listener);
                } catch (Exception var2) {
                    Log.e("WaveformFragment", "Error while loading sound file", var2);
                    DuplicatedWaveformFragment.this.mProgressDialog.dismiss();
                    DuplicatedWaveformFragment.this.mInfo.setText(var2.toString());
                    return;
                }

                if(DuplicatedWaveformFragment.this.mLoadingKeepGoing) {
                    DuplicatedWaveformFragment.this.mHandler.post(DuplicatedWaveformFragment$2$$Lambda$1.lambdaFactory$(this));
                }

            }
        }).start();
    }

    protected void finishOpeningSoundFile() {
        this.mWaveformView.setSoundFile(this.mSoundFile);
        this.mWaveformView.recomputeHeights(this.mDensity);
        this.mMaxPos = this.mWaveformView.maxPos();
        this.mLastDisplayedStartPos = -1;
        this.mLastDisplayedEndPos = -1;
        this.mTouchDragging = false;
        this.mOffset = 0;
        this.mOffsetGoal = 0;
        this.mFlingVelocity = 0;
        this.resetPositions();
        this.mCaption = this.mSoundFile.getFiletype() + ", " + this.mSoundFile.getSampleRate() + " Hz, " + this.mSoundFile.getAvgBitrateKbps() + " kbps, " + this.formatTime(this.mMaxPos) + " " + this.getResources().getString(string.time_seconds);
        this.mInfo.setText(this.mCaption);
        this.mProgressDialog.dismiss();
        this.updateDisplay();
    }

    protected synchronized void updateDisplay() {
        int offsetDelta;
        int endX;
        if(this.mIsPlaying) {
            offsetDelta = this.mPlayer.getCurrentPosition() + this.mPlayStartOffset;
            endX = this.mWaveformView.millisecsToPixels(offsetDelta);
            this.mWaveformView.setPlayback(endX);
            this.setOffsetGoalNoUpdate(endX - this.mWidth / 2);
            if(offsetDelta >= this.mPlayEndMsec) {
                this.handlePause();
            }
        }

        if(!this.mTouchDragging) {
            if(this.mFlingVelocity != 0) {
                float saveVel = (float)this.mFlingVelocity;
                offsetDelta = this.mFlingVelocity / 30;
                if(this.mFlingVelocity > 80) {
                    this.mFlingVelocity -= 80;
                } else if(this.mFlingVelocity < -80) {
                    this.mFlingVelocity += 80;
                } else {
                    this.mFlingVelocity = 0;
                }

                this.mOffset += offsetDelta;
                if(this.mOffset + this.mWidth / 2 > this.mMaxPos) {
                    this.mOffset = this.mMaxPos - this.mWidth / 2;
                    this.mFlingVelocity = 0;
                }

                if(this.mOffset < 0) {
                    this.mOffset = 0;
                    this.mFlingVelocity = 0;
                }

                this.mOffsetGoal = this.mOffset;
            } else {
                offsetDelta = this.mOffsetGoal - this.mOffset;
                if(offsetDelta > 10) {
                    offsetDelta /= 10;
                } else if(offsetDelta > 0) {
                    offsetDelta = 1;
                } else if(offsetDelta < -10) {
                    offsetDelta /= 10;
                } else if(offsetDelta < 0) {
                    offsetDelta = -1;
                } else {
                    offsetDelta = 0;
                }

                this.mOffset += offsetDelta;
            }
        }

        this.mWaveformView.setParameters(this.mStartPos, this.mEndPos, this.mOffset);
        this.mWaveformView.invalidate();
        this.mStartMarker.setContentDescription(this.getResources().getText(string.start_marker) + " " + this.formatTime(this.mStartPos));
        this.mEndMarker.setContentDescription(this.getResources().getText(string.end_marker) + " " + this.formatTime(this.mEndPos));
        offsetDelta = this.mStartPos - this.mOffset - this.mMarkerLeftInset;
        if(offsetDelta + this.mStartMarker.getWidth() >= 0) {
            if(!this.mStartVisible) {
                this.mHandler.postDelayed(DuplicatedWaveformFragment$$Lambda$7.lambdaFactory$(this), 0L);
            }
        } else {
            if(this.mStartVisible) {
                this.mStartMarker.setImageAlpha(0);
                this.mStartVisible = false;
            }

            offsetDelta = 0;
        }

        endX = this.mEndPos - this.mOffset - this.mEndMarker.getWidth() + this.mMarkerRightInset;
        if(endX + this.mEndMarker.getWidth() >= 0) {
            if(!this.mEndVisible) {
                this.mHandler.postDelayed(DuplicatedWaveformFragment$$Lambda$8.lambdaFactory$(this), 0L);
            }
        } else {
            if(this.mEndVisible) {
                this.mEndMarker.setImageAlpha(0);
                this.mEndVisible = false;
            }

            endX = 0;
        }

        this.mStartMarker.setLayoutParams(new LayoutParams(-2, -2, offsetDelta, this.mMarkerTopOffset));
        this.mEndMarker.setLayoutParams(new LayoutParams(-2, -2, endX, this.mWaveformView.getMeasuredHeight() - this.mEndMarker.getHeight() - this.mMarkerBottomOffset));
    }

    protected void enableDisableButtons() {
        if(this.mIsPlaying) {
            this.mPlayButton.setImageResource(R.drawable.pause);
            this.mPlayButton.setContentDescription(this.getResources().getText(string.stop));
        } else {
            this.mPlayButton.setImageResource(R.drawable.shuffle);
            this.mPlayButton.setContentDescription(this.getResources().getText(string.shuffle));
        }

    }

    protected void resetPositions() {
        this.mStartPos = 0;
        this.mEndPos = this.mMaxPos;
    }

    protected int trap(int pos) {
        return pos < 0?0:(pos > this.mMaxPos?this.mMaxPos:pos);
    }

    protected void setOffsetGoalStart() {
        this.setOffsetGoal(this.mStartPos - this.mWidth / 2);
    }

    protected void setOffsetGoalStartNoUpdate() {
        this.setOffsetGoalNoUpdate(this.mStartPos - this.mWidth / 2);
    }

    protected void setOffsetGoalEnd() {
        this.setOffsetGoal(this.mEndPos - this.mWidth / 2);
    }

    protected void setOffsetGoalEndNoUpdate() {
        this.setOffsetGoalNoUpdate(this.mEndPos - this.mWidth / 2);
    }

    protected void setOffsetGoal(int offset) {
        this.setOffsetGoalNoUpdate(offset);
        this.updateDisplay();
    }

    protected void setOffsetGoalNoUpdate(int offset) {
        if(!this.mTouchDragging) {
            this.mOffsetGoal = offset;
            if(this.mOffsetGoal + this.mWidth / 2 > this.mMaxPos) {
                this.mOffsetGoal = this.mMaxPos - this.mWidth / 2;
            }

            if(this.mOffsetGoal < 0) {
                this.mOffsetGoal = 0;
            }

        }
    }

    protected String formatTime(int pixels) {
        return this.mWaveformView != null && this.mWaveformView.isInitialized()?this.formatDecimal(this.mWaveformView.pixelsToSeconds(pixels)):"";
    }

    protected String formatDecimal(double x) {
        int xWhole = (int)x;
        int xFrac = (int)(100.0D * (x - (double)xWhole) + 0.5D);
        if(xFrac >= 100) {
            ++xWhole;
            xFrac -= 100;
            if(xFrac < 10) {
                xFrac *= 10;
            }
        }

        return xFrac < 10?xWhole + ".0" + xFrac:xWhole + "." + xFrac;
    }

    protected synchronized void handlePause() {
        if(this.mPlayer != null && this.mPlayer.isPlaying()) {
            this.mPlayer.pause();
        }

        this.mWaveformView.setPlayback(-1);
        this.mIsPlaying = false;
        this.enableDisableButtons();
    }

    protected synchronized void onPlay(int startPosition) {
        if(this.mIsPlaying) {
            this.handlePause();
        } else if(this.mPlayer != null) {
            try {
                this.mPlayStartMsec = this.mWaveformView.pixelsToMillisecs(startPosition);
                if(startPosition < this.mStartPos) {
                    this.mPlayEndMsec = this.mWaveformView.pixelsToMillisecs(this.mStartPos);
                } else if(startPosition > this.mEndPos) {
                    this.mPlayEndMsec = this.mWaveformView.pixelsToMillisecs(this.mMaxPos);
                } else {
                    this.mPlayEndMsec = this.mWaveformView.pixelsToMillisecs(this.mEndPos);
                }

                this.mPlayStartOffset = 0;
                int startFrame = this.mWaveformView.secondsToFrames((double)this.mPlayStartMsec * 0.001D);
                int endFrame = this.mWaveformView.secondsToFrames((double)this.mPlayEndMsec * 0.001D);
                int startByte = this.mSoundFile.getSeekableFrameOffset(startFrame);
                int endByte = this.mSoundFile.getSeekableFrameOffset(endFrame);
                if(startByte >= 0 && endByte >= 0) {
                    try {
                        this.mPlayer.reset();
                        this.mPlayer.setAudioStreamType(3);
                        FileInputStream subsetInputStream = new FileInputStream(this.mFile.getAbsolutePath());
                        this.mPlayer.setDataSource(subsetInputStream.getFD(), (long)startByte, (long)(endByte - startByte));
                        this.mPlayer.prepare();
                        this.mPlayStartOffset = this.mPlayStartMsec;
                    } catch (Exception var7) {
                        Log.e("WaveformFragment", "Exception trying to shuffle file subset", var7);
                        this.mPlayer.reset();
                        this.mPlayer.setAudioStreamType(3);
                        this.mPlayer.setDataSource(this.mFile.getAbsolutePath());
                        this.mPlayer.prepare();
                        this.mPlayStartOffset = 0;
                    }
                }

                this.mPlayer.setOnCompletionListener(DuplicatedWaveformFragment$$Lambda$9.lambdaFactory$(this));
                this.mIsPlaying = true;
                if(this.mPlayStartOffset == 0) {
                    this.mPlayer.seekTo(this.mPlayStartMsec);
                }

                this.mPlayer.start();
                this.updateDisplay();
                this.enableDisableButtons();
            } catch (Exception var8) {
                Log.e("WaveformFragment", "Exception while playing file", var8);
            }

        }
    }

    protected void enableZoomButtons() {
    }

    protected abstract String getFileName();

    protected List<Segment> getSegments() {
        return null;
    }

    protected OnClickListener getFwdListener() {
        return this.mFfwdListener;
    }

    protected OnClickListener getRewindListener() {
        return this.mRewindListener;
    }

    protected int getStep() {
        int maxSeconds = (int)this.mWaveformView.pixelsToSeconds(this.mWaveformView.maxPos());
        return maxSeconds / 3600 > 0?600:(maxSeconds / 1800 > 0?300:(maxSeconds / 300 > 0?60:5));
    }

}
*/


