package com.ldt.musicr.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ldt.musicr.util.InterpolatorUtil;
import com.ldt.musicr.R;

/**
 * Created by trung on 9/27/2017.
 */

public class SupportDarkenFrameLayout extends FrameLayout {
    public SupportDarkenFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public SupportDarkenFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addAttr(context,attrs);
        init();
    }

    public SupportDarkenFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addAttr(context,attrs);
        init();
    }
    private float darken=0f;
    public void setDarken(float darken, boolean shouldDraw)
    {
        if(darken>=0&&darken<=1) {
            this.darken = darken;
        }
        if(shouldDraw) invalidate();
    }
    public float getDarken()
    {
        return darken;
    }
    private Paint drawDarkenPaint;
    private void init()
    {
        drawDarkenPaint = new Paint();
        drawDarkenPaint.setColor(Color.BLACK);
        drawDarkenPaint.setStyle(Paint.Style.FILL);
    //
    //      drawDarkenPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

    }
    private void addAttr(Context context,AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SupportDarkenFrameLayout,
                0, 0);
        try {
            darken = a.getInt(R.styleable.SupportDarkenFrameLayout_how_dark, 0)/(255.0f);
        } finally {
            a.recycle();
        }
    //    Log.d("How dark ",darken+"");
    }
    float color4Black_float=0;

    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);

    //    int color4White = (int)( darken* 255.0f);
      //  if(color4White>255) color4White = 255; else if(color4White<0) color4White =0;

        int color4Black = (int) (255.0f*darken);
     //  canvas.drawColor(color4White<<24|0x00ffffff);
       if(color4Black>255) color4Black=255;
       else if(color4Black<0) color4Black =0;
      canvas.drawColor(color4Black<<24);

    }
    private boolean inProcess = false;
    private ValueAnimator va;
    private void turnOnOrOffDark()
    {
        if(inProcess) return;
        if(va == null) {
            va= ValueAnimator.ofFloat(0,1);
            va.setDuration(300);
            va.setInterpolator(InterpolatorUtil.getInterpolator(2));
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                   color4Black_float= (float)animation.getAnimatedValue();
                   invalidate();
                }
            });
            va.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    inProcess = false;
                }

                @Override
                public void onAnimationStart(Animator animation) {

                }
            });

        }
        inProcess = true;
        if(color4Black_float==0)
        va.start();
        else va.reverse();
    }
}
