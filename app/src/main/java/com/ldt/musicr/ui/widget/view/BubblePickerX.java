package com.ldt.musicr.ui.widget.view;

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

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BubblePickerX extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = "BubblePickerX";

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

    public BubblePickerX(Context context) {
        super(context);
        init(context,null);
    }

    public BubblePickerX(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BubblePickerX(Context context, AttributeSet attrs, int defStyleAttr) {
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
            mBallNumber = t.getInteger(R.styleable.JBubblePicker_ballNumber,16);
            mBallColor = t.getInteger(R.styleable.JBubblePicker_ballColor,context.getResources().getColor(R.color.flatOrange));
            mMinBallSize = t.getDimension(R.styleable.JBubblePicker_minBallSize,context.getResources().getDimension(R.dimen._24dp));
            mMaxBallSize = t.getDimension(R.styleable.JBubblePicker_maxBallSize,mMinBallSize * 2);
            mBallDistance = t.getDimension(R.styleable.JBubblePicker_ballDistance,mMinBallSize/8);
            mGravityPosition = t.getFloat(R.styleable.JBubblePicker_gravityPosition,0.5f);

            t.recycle();
        } else if(context!=null){
            mBallNumber = 16;
            mBallColor = 0xFFFF9500;
            mMinBallSize = context.getResources().getDimension(R.dimen._48dp);
            mMaxBallSize = 2 * mMinBallSize;
            mBallDistance = mMinBallSize /8;
            mGravityPosition = 0.5f;
            oneDp = context.getResources().getDimension(R.dimen.oneDP);
        } else {
            mBallNumber = 16;
            mBallColor = 0xFFFF9500;
            mMinBallSize =100;
            mMaxBallSize = 200;
            mBallDistance = mMinBallSize /8;
            mGravityPosition = 0.5f;
        }

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
        initBall();
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

    private static class Ball {
        public float x;
        public float y;
        public float size;
        public float vX;
        public float vY;
        public float aX;
        public float aY;
        public int color;
        public static boolean xvs(Ball a, Ball b) {
            float r = a.size + b.size + a.size*0.25f;
            r *= r;
            return r >= ((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y -b.y))
                    && ((a.vX>0 && b.vX <=0) || (a.vX <=0 && b.vX >0));
        }

        public static boolean yvs(Ball a, Ball b) {
            float r = a.size + b.size;
            r *= r;
            return r >= ((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y -b.y))
                    && ((a.vY>0 && b.vY <=0) || (a.vY <=0 && b.vY>0));
        }

        public static void crossX(Ball a, Ball b) {
         /*   a.vX = -((a.size - b.size)*a.vX + 2* b.size*b.vX) /
                                              (a.size + b.size);

            a.vY = -((a.size - b.size)*a.vY + 2* b.size*b.vY)/
                                              (a.size + b.size);


            b.vX = -((b.size - a.size)*b.vX + 2* a.size*a.vX)/(a.size + b.size);
            b.vY = -((b.size - a.size)*b.vY + 2* a.size*a.vY)/(a.size + b.size);*/
         float vX = a.vX;
         //float vY = a.vY;
         float fms = 0.95f;
         a.vX =  fms*b.vX;
       //  a.vY =  fms*b.vY;
         b.vX =  fms*vX;
       //  b.vY = fms*vY;
        }

        public static void crossY(Ball a, Ball b) {
            float vY = a.vY;
            float fms = 0.95f;
            a.vY =  fms*b.vY;
            b.vY = fms*vY;
        }
    }
    private ArrayList<Ball> mBalls = new ArrayList<>();

    private void initBall() {
        mBalls.clear();
        mMaxAPerSecond = oneDp*15;
        Random random = new Random();
        for (int i = 0; i < mBallNumber; i++) {
            Ball ball = new Ball();
            if(i% 2 == 0)
            ball.x =  0 - random.nextInt(mDrawWidth);
            else ball.x = mDrawRight + random.nextInt(mDrawWidth);
            ball.y = mDrawBottom - random.nextInt(mDrawHeight);
            ball.size = mMinBallSize;
            ball.color = mBallColor;

            if(ball.x<mGravityPoint.x) {
                ball.vX = mMaxAPerSecond*5;
                ball.aX = mMaxAPerSecond;
            }
            else {
                ball.aX = -mMaxAPerSecond;
                ball.vX = -mMaxAPerSecond*5;
            }

            if(ball.y<mGravityPoint.y) {
                ball.vY = mMaxAPerSecond*5;
                ball.aY = mMaxAPerSecond;
            }
            else {
                ball.aY = -mMaxAPerSecond;
                ball.vY = -mMaxAPerSecond*5;
            }

            mBalls.add(ball);
        }
    }

    private float mMaxAPerSecond;

    protected boolean calculate() {
        boolean shouldDraw = false;
        for (int i = 0; i < mBallNumber; i++) {
            Ball ball = mBalls.get(i);

            ArrayList<Integer> crossed = new ArrayList<>();
            for (int j = 0; j < mBallNumber; j++) {
                if (j == i) continue;
                if (Ball.yvs(ball, mBalls.get(j)) && !crossed.contains(j)) {
                    Ball.crossY(ball, mBalls.get(j));
                    crossed.add(j);
                    j=0;
                 //   break;
                }
            }

            crossed.clear();
                for (int j = 0; j < mBallNumber; j++) {
                    if (j == i) continue;
                    if (Ball.xvs(ball, mBalls.get(j)) && !crossed.contains (j)) {
                        Ball.crossX(ball, mBalls.get(j));
                        crossed.add(j);
                        j=0;
                      //  break;
                    }
                }


            float disX = mGravityPoint.x - ball.x;
            float disY = mGravityPoint.y - ball.y;

          /*  if(Math.abs(disX)< (ball.vX - mMaxVPerSecond) * mDelta/1000) {
                ball.x = mGravityPoint.x;
                ball.vX = 0;
            } else*/
                if(disX<0) {
                    ball.aX = -mMaxAPerSecond;
            }
            else {
                ball.aX = mMaxAPerSecond;
            }

            float delta = mDelta/1000f;

            ball.x += ball.vX* delta + 0.5f * ball.aX * delta * delta;
            ball.vX +=ball.aX* (delta);

           /* if(Math.abs(disY) < (ball.vY - mMaxVPerSecond) * mDelta/1000) {
                ball.y = mGravityPoint.y;
                ball.vY = 0;
            }
            else*/ if(disY <0) {
                ball.aY = -mMaxAPerSecond;
            }
            else {
                ball.aY = mMaxAPerSecond;
            }

            ball.y += ball.vY* delta + 0.5f * ball.aY * delta * delta;
            ball.vY +=ball.aY* (delta);



                shouldDraw = true;
        }
       return shouldDraw;
    }

    protected void doDraw(Canvas canvas) {
         canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        for (Ball ball :
                mBalls) {
            mPaint.setColor(ball.color);
            canvas.drawCircle(ball.x,ball.y,ball.size,mPaint);
            canvas.drawCircle(ball.x,ball.y,ball.size,mStrokePaint);
        }

    }


}
