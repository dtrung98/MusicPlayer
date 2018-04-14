package com.ldt.musicr.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ldt.musicr.InternalTools.ImageEditor;
import com.ldt.musicr.InternalTools.Tool;

/**
 * Created by trung on 9/30/2017.
 */

public class CustomSDFL4MusicController extends FrameLayout {

    public CustomSDFL4MusicController(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomSDFL4MusicController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private int backColor1=0,backColor2 =0;
    public void setBackColor1(int color)
    {
        backColor1 = color;
        invalidate();
    }
    public void setBackColor2(int color)
    {
        backColor2 = color;
        invalidate();
    }
    private Paint mPaint;
    private void init()
    {
        drawDarkenPaint = new Paint();
        drawDarkenPaint.setStyle(Paint.Style.FILL);

        drawDarkenPaint.setAntiAlias(true);
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }
    private float darken=0f;
    public float maxDarken =0.4f;
    public void setDarken(float darken, float MaxDarken)
    {
        if(darken>=0&&darken<=1) this.darken = darken;
        maxDarken = MaxDarken;
        invalidate();

    }
    public float getDarken()
    {
        return darken;
    }
    private Paint drawDarkenPaint;

    private float eachDP =0;
    private final  float maxRy=14;
    private final  float maxRx = 14;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
     //   canvas.drawColor(Color.WHITE);
     //   if(backColor1!=0) canvas.drawColor(backColor1);
     //   if(backColor2!=0) canvas.drawColor(backColor2);
        int tempMix1 = ColorUtils.blendARGB(backColor2,backColor1,0.5f);
        int mixColor = ColorUtils.blendARGB(tempMix1, Color.WHITE, 0.5F);
        int notARGB = ColorUtils.compositeColors(mixColor, Color.WHITE);
        if(notARGB==0) return;
        //canvas.drawColor(notARGB);
        mPaint.setColor(notARGB);
    drawContent(canvas,mPaint);
    }
    private float number=0;
    public void setNumber(float number1)
    {
        if(number!=number1) {
            number = number1;
            invalidate();
        }
    }
    private void drawContent(Canvas canvas,Paint paint)
    {
        if(eachDP==0) eachDP = Tool.getOneDps(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&&false) {

        //    drawDarkenPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            canvas.drawRoundRect(0,0,canvas.getWidth(),canvas.getHeight()+50*eachDP,maxRx*eachDP*number,maxRy*eachDP*number,paint);
         //   drawDarkenPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST));
           // canvas.drawRect(0,25*eachDP,canvas.getWidth(),canvas.getHeight(),paint);
        }
        else
        {
canvas.drawPath(ImageEditor.RoundedRect(0,0,canvas.getWidth(),canvas.getHeight(),maxRx*eachDP*number,maxRy*eachDP*number,true),paint);
        }
    }
    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);

        int color4White = (int)( darken/maxDarken * 255.0f);
        if(color4White>255) color4White = 255; else if(color4White<0) color4White =0;

        int color4Black = (int) (255.0f*darken);

        if(color4Black>255) color4Black=255;
        drawDarkenPaint.setColor(color4Black<<24);
        drawContent(canvas,drawDarkenPaint);

    }
}
