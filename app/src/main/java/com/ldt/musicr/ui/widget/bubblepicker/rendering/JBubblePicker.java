package com.ldt.musicr.ui.widget.bubblepicker.rendering;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;


import com.ldt.musicr.R;
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JBubblePicker extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = "BubblePickerX";

    public static final int MODE_CLICK = 0;
    public static final int MODE_SELECT  = 1;
    public static final int MODE_SELECT_COUNT = 2;

    private int mMode = MODE_CLICK;

    public final static long FRAME_INTERVAL = 1000/60;
    private ScheduledExecutorService mExecutorService;
    private long mStartTime;
    private long mDelta;
    private long mTotalRunningTime;
    private long mFrames;

    private Paint mPaint;
    private Paint mStrokePaint;
    private int mBallColor;
    private float mMinBallSize;
    private float mMaxBallSize;

    public JBubblePicker(Context context) {
        super(context);
        init(context,null);
    }

    public JBubblePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public JBubblePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int mBallNumber = 16;
    private float mBallDistance;
    private float mGravityPosition = 0.5f;
    private float oneDp = 4;

    private void init(Context context, AttributeSet attrs) {
        if(attrs!=null && context!=null) {
            TypedArray t  = context.obtainStyledAttributes(attrs, R.styleable.JBubblePicker);
            mBallNumber = t.getInteger(R.styleable.JBubblePicker_ballNumber,0);
            mBallColor = t.getInteger(R.styleable.JBubblePicker_ballColor,0xFFFF9500);
            mMinBallSize = t.getDimension(R.styleable.JBubblePicker_minBallSize,context.getResources().getDimension(R.dimen._24dp));
            mMaxBallSize = t.getDimension(R.styleable.JBubblePicker_maxBallSize,mMinBallSize * 2);
            mBallDistance = t.getDimension(R.styleable.JBubblePicker_ballDistance,mMinBallSize/8);
            mGravityPosition = t.getFloat(R.styleable.JBubblePicker_gravityPosition,0.5f);
            mMode = t.getInt(R.styleable.JBubblePicker_mode,MODE_CLICK);

            t.recycle();
        } else {
            mBallNumber = 16;
            mBallColor = 0xFFFF9500;
            if(context!=null)
            mMinBallSize = context.getResources().getDimension(R.dimen._48dp);
            mMaxBallSize = 2 * mMinBallSize;
            mBallDistance = mMinBallSize /8;
            mGravityPosition = 0.5f;

        }

        if(context!=null)
            oneDp = context.getResources().getDimension(R.dimen.oneDP);
        else oneDp = 4;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.flatOrange));

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG );
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(getResources().getColor(R.color.flatPurple));

        mBallRadius = getResources().getDimension(R.dimen._24dp);

        setOpaque(false);
        setSurfaceTextureListener(this);
        setFocusable(false);
        setWillNotDraw(false);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        mFrames = 0;
        mDelta = 0;
        mTotalRunningTime = 0;
        mStartTime = System.currentTimeMillis();
        initDraw();
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        mExecutorService.scheduleAtFixedRate(this::render, FRAME_INTERVAL, FRAME_INTERVAL, TimeUnit.MILLISECONDS);
        Log.d(TAG, "onSurfaceTextureAvailable");

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.d(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mExecutorService.shutdown();
        Log.d(TAG, "onSurfaceTextureDestroyed");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    private void recordValue() {
        long current = System.currentTimeMillis();
        mDelta = current - mStartTime - mTotalRunningTime;

        mTotalRunningTime +=mDelta;
        mFrames++;
    }

    private void render() {
        recordValue();
        if(calculate()) {
            Canvas canvas = null;
            try {
                canvas = lockCanvas();
                doDraw(canvas);
            } finally {
                unlockCanvasAndPost(canvas);
            }
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mDrawTop = getPaddingTop();
        mDrawBottom = h - getPaddingBottom();
        mDrawLeft = getPaddingLeft();
        mDrawRight = w - mDrawLeft - getPaddingRight();
        mDrawHeight = mDrawBottom - mDrawTop;
        mDrawWidth = mDrawRight - mDrawLeft;
        mGravityPoint.x = mDrawLeft + mDrawWidth/2;
        mGravityPoint.y = mDrawTop + mDrawHeight*mGravityPosition;
    }

    private int mDrawTop;
    private int mDrawBottom;
    private int mDrawLeft;
    private int mDrawRight;
    private int mDrawHeight;
    private int mDrawWidth;

    public int getFps() {
        long delta = System.currentTimeMillis() - mStartTime;
        if(delta<1000) return 0;
        return (int) (mFrames/(delta /1000));
    }

    private float mBallPosition = 0;
    private float mBallRadius;

    private PointF mGravityPoint = new PointF(0,0);
    private ArrayList<PickerItem> mItems = new ArrayList<>();

    public Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    private int mMaxSelectedCount;

    private Adapter mAdapter;

    private boolean mIsResuming = true;

    public void onResume() {
        mIsResuming = true;
    }

    public void onPause() {
        mIsResuming = false;
    }

/*    private float mStartTouchX = 0f;
    private float mStartTouchY = 0f;
    private float mPreviousTouchX = 0f;
    private float mPreviousTouchY = 0f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

        }
        return super.onTouchEvent(event);
    }*/

    private void initDraw() {

    }

    protected boolean calculate() {
        boolean shouldDraw = false;
        if(mIsResuming) {

            shouldDraw = true;
        }
        return shouldDraw;
    }

    protected void doDraw(Canvas canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }


}