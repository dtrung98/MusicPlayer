package com.ldt.musicr.ui.widget.rounded;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;

import com.ldt.musicr.R;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.util.BitmapEditor;

public class DarkenAndRoundedBackgroundContraintLayout extends ConstraintLayout implements RoundColorable{

    public DarkenAndRoundedBackgroundContraintLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public DarkenAndRoundedBackgroundContraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DarkenAndRoundedBackgroundContraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected Paint mSolidPaint;
    private void init( AttributeSet attrs)
    {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.DarkenAndRoundedBackgroundConstraintLayout);

        color_background = t.getColor(R.styleable.DarkenAndRoundedBackgroundConstraintLayout_round_back_color,Color.WHITE);
        round_type = (t.getInt(R.styleable.DarkenAndRoundedBackgroundConstraintLayout_round_type,0)==1) ? ROUND_TYPE.ROUND_ALL : ROUND_TYPE.ROUND_TOP;
        number = t.getFloat(R.styleable.DarkenAndRoundedBackgroundConstraintLayout_round_number,0);
        alpha = t.getFloat(R.styleable.DarkenAndRoundedBackgroundConstraintLayout_background_alpha,1);

        drawDarkenPaint = new Paint();
        drawDarkenPaint.setStyle(Paint.Style.FILL);

        drawDarkenPaint.setAntiAlias(true);
        mSolidPaint = new Paint();

        mSolidPaint.setStyle(Paint.Style.FILL);
        mSolidPaint.setAntiAlias(true);
        t.recycle();
        setWillNotDraw(false);
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

    protected float eachDP =0;
    protected final  float maxRy=18;
    protected final  float maxRx = 18;

    public enum ROUND_TYPE {
        ROUND_ALL,
        ROUND_TOP
    }
    protected ROUND_TYPE round_type = ROUND_TYPE.ROUND_ALL;
    public void setRoundType(ROUND_TYPE type, boolean shouldInvalidate) {
        round_type = type;
        if(shouldInvalidate) invalidate();
    }
    private int color_background;
    @Override
    public void setBackColor(int color) {
        color_background = color;
    }
    protected float alpha =1;
    public void setAlphaBackground(float value) {
        alpha = value;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //   canvas.drawColor(Color.WHITE);
        //   if(backColor1!=0) canvas.drawColor(backColor1);
        //   if(backColor2!=0) canvas.drawColor(backColor2);

        mSolidPaint.setColor(color_background);
        mSolidPaint.setAlpha((int) (alpha*255f));

        drawContent(canvas, mSolidPaint);
    }

    protected float number=0;
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

            canvas.drawPath(BitmapEditor.RoundedRect(0,0,getWidth(),getHeight(),maxRx*eachDP*number,maxRy*eachDP*number,round_type==ROUND_TYPE.ROUND_TOP),paint);
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        // int color4White = (int)( darken * 255.0f);
        //  if(color4White>255) color4White = 255; else if(color4White<0) color4White =0;

        int color4Black = (int) (255.0f*darken);

        if(color4Black>255) color4Black=255;
        drawDarkenPaint.setColor(color4Black<<24);

        drawContent(canvas,drawDarkenPaint);

    }
}
