package com.ldt.musicr.ui.widget.rounded;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ldt.musicr.util.BitmapEditor;
import com.ldt.musicr.util.Tool;

/**
 * Created by trung on 9/30/2017.
 */

public class DarkenRoundedBackgroundFrameLayout extends FrameLayout {

    public DarkenRoundedBackgroundFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public DarkenRoundedBackgroundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private int backColor1=Color.WHITE,backColor2 =Color.WHITE;
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
    public void setDarken(float darken, boolean shouldDraw)
    {
        if(darken>=0&&darken<=1) {
            this.darken = darken;
            if(shouldDraw) invalidate();
        }
    }
    public float getDarken()
    {
        return darken;
    }
    private Paint drawDarkenPaint;

    private float eachDP =0;
    private final  float maxRy=14;
    private final  float maxRx = 14;
    private int notARGB=0xffffffff;
    public void updateColor() {
        int tempMix1 = ColorUtils.blendARGB(backColor2,backColor1,0.5f);
        int mixColor = ColorUtils.blendARGB(tempMix1, Color.WHITE, 0.5F);
        notARGB = ColorUtils.compositeColors(backColor1, Color.WHITE);
        int notARGB2 = ColorUtils.compositeColors(backColor2,Color.WHITE);
        notARGB = ColorUtils.blendARGB(notARGB,notARGB2,0.5f);
    }
    public int getColorDrawn() {
        return notARGB;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
     //   canvas.drawColor(Color.WHITE);
     //   if(backColor1!=0) canvas.drawColor(backColor1);
     //   if(backColor2!=0) canvas.drawColor(backColor2);
        updateColor();
        if(notARGB==0) return;
        //canvas.drawColor(notARGB);
        mPaint.setColor(Color.WHITE);
        drawContent(canvas,mPaint);
        mPaint.setColor(backColor1);
        drawContent(canvas,mPaint);
    }
    public int getBackgroundColor() {
        return notARGB;
    }
    private float number=0;
    public void setRoundNumber(float number, boolean shouldDraw)
    {
        if(this.number!=number) {
            this.number = number;
        }
        if(shouldDraw) invalidate();
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

canvas.drawPath(BitmapEditor.RoundedRect(0,0,canvas.getWidth(),canvas.getHeight(),maxRx*eachDP*number,maxRy*eachDP*number,true),paint);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);

       // int color4White = (int)( darken * 255.0f);
      //  if(color4White>255) color4White = 255; else if(color4White<0) color4White =0;

        int color4Black = (int) (255.0f*darken);

        if(color4Black>255) color4Black=255;
        drawDarkenPaint.setColor(color4Black<<24);
        drawContent(canvas,drawDarkenPaint);

    }
}
