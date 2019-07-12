package com.ldt.musicr.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ldt.musicr.R;


public class CircularPlayPauseProgressBar extends View {
    private static final String TAG ="CPPProgressBar";

    public CircularPlayPauseProgressBar(Context context) {
        super(context);
        init(null);

    }
    Drawable mWaveDrawable;
    Drawable mPauseDrawable;
    public CircularPlayPauseProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CircularPlayPauseProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    float oneDp =1;
    Paint mPaint;
    private int mColor;

    private void init(AttributeSet attrs) {
        if(attrs!=null) {
            TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.CircularPlayPauseProgressBar);
            mColor = t.getColor(R.styleable.CircularPlayPauseProgressBar_iconTint,getResources().getColor(R.color.FlatWhite));
            t.recycle();
        }
            mWaveDrawable = getResources().getDrawable(R.drawable.wave_metro);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWaveDrawable.setTint(mColor);
        }


        mPauseDrawable = getResources().getDrawable(R.drawable.ic_pause_black_24dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPauseDrawable.setTint(mColor);
        }

        oneDp = getResources().getDimension(R.dimen.oneDP);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.FlatWhite));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(oneDp*2f);

        //setWillNotDraw(false);
    }

    int mLeftWithPadding = 0;
    int mTopWithPadding = 0;
    int mRightWidthPadding = 0;
    int mBottomWidthPadding = 0;
    public final static int RESET = 0;
    public final static int PLAYING = 1;
    protected int mMode = RESET;
    protected ValueAnimator mValueAnimator = null;
    protected float mAnimateValue = 0;
    public void resetProgress() {
        if(mValueAnimator!=null&&mValueAnimator.isRunning()) mValueAnimator.cancel();
        mMode = RESET;
        mValueAnimator = null;
        mAnimateValue = 0;
        invalidate();
    }
    public int getMode() {
        return mMode;
    }

    public void setProgress(int percent) {
        mAnimateValue = percent;
        mMode = PLAYING;
        invalidate();
    }

    public void startProgress(int duration) {
        syncProgress(duration,0);
    }

    public void syncProgress(int duration, int played) {
        if(mValueAnimator!=null&&mValueAnimator.isRunning()) mValueAnimator.cancel();

        if(duration<=0||played>=duration) {
            resetProgress();
            return;
        }
        mMode = PLAYING;
        Log.d(TAG, "syncProgress: duration = "+duration);
        float from = played/(duration+0f);
        mValueAnimator = ValueAnimator.ofFloat(from,1);
        mValueAnimator.setDuration(duration- played);
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mValueAnimator = null;
                resetProgress();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mValueAnimator = null;
                resetProgress();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
               mAnimateValue = (float) animation.getAnimatedValue();
               invalidate();
            }
        });
        mValueAnimator.start();
    }
    private RectF mRectF;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mLeftWithPadding = getPaddingStart();
        mTopWithPadding = getPaddingTop();
        mRightWidthPadding = getWidth() - getPaddingRight();
        mBottomWidthPadding = getHeight() - getPaddingBottom();

        int radius = Math.min(mRightWidthPadding-mLeftWithPadding,mBottomWidthPadding-mTopWithPadding)/2;
        int centerX = (mRightWidthPadding + mLeftWithPadding)/2;
        int centerY = (mBottomWidthPadding + mTopWithPadding)/2;

        if(mMode==RESET) {
            if (mWaveDrawable != null) {
                int iconRadius = (int) (radius*5/6f);
                mWaveDrawable.setBounds(centerX-iconRadius,centerY-iconRadius,centerX+iconRadius,centerY+iconRadius);
                mWaveDrawable.draw(canvas);

            }
        } else {

            int iconRadius = (int) (radius*2/3f);

            if(mPauseDrawable !=null) {
                mPauseDrawable.setBounds(centerX-iconRadius,centerY-iconRadius,centerX+iconRadius,centerY+iconRadius);
                mPauseDrawable.draw(canvas);
            }
            mPaint.setAlpha(60);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawArc(centerX-radius,centerY-radius,centerX+radius,centerY+radius,-90,360,false,mPaint);
                mPaint.setAlpha(205);
                canvas.drawArc(centerX-radius,centerY-radius,centerX+radius,centerY+radius,-90,mAnimateValue*360f,false,mPaint);

            } else {
                if (mRectF==null)
                    mRectF = new RectF(centerX - radius,centerY - radius, centerX + radius,centerY + radius);
                else {
                    mRectF.left = centerX - radius;
                    mRectF.top = centerY - radius;
                    mRectF.right = centerX + radius;
                    mRectF.bottom = centerY + radius;
                }
                canvas.drawArc(mRectF, -90, 360, false, mPaint);
                mPaint.setAlpha(205);
                canvas.drawArc(mRectF,-90,mAnimateValue*360f,false,mPaint);

            }
        }

    }

}
