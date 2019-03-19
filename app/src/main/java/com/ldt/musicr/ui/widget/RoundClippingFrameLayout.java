package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.widget.rounded.RoundColorable;
import com.ldt.musicr.util.BitmapEditor;
import com.ldt.musicr.util.Tool;

/**
 * Created by trung on 9/30/2017.
 */

public class RoundClippingFrameLayout extends SupportDarkenFrameLayout implements RoundColorable {
    private static final String TAG = "roundClippingFL";
    private Paint drawPaint;
    private Paint roundPaint;

    private int mCornerRadius = 12;

    private RectF bounds;


    public RoundClippingFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public RoundClippingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RoundClippingFrameLayout(Context context) {
        super(context);
        init(null);
    }

    protected void init(AttributeSet attr) {
        TypedArray t = getContext().obtainStyledAttributes(attr, R.styleable.RoundClippingFrameLayout);
        backColor = t.getColor(R.styleable.RoundClippingFrameLayout_round_clip_back_color, backColor);


        drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawPaint.setColor(0xffffffff);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roundPaint.setColor(0xffffffff);
        t.recycle();
        //  setWillNotDraw(true);
    }

    public enum ROUND_TYPE {
        ROUND_ALL,
        ROUND_TOP
    }
    protected ROUND_TYPE round_type = ROUND_TYPE.ROUND_ALL;
    public void setRoundType(ROUND_TYPE type, boolean shouldInvalidate) {
        round_type = type;
        if(shouldInvalidate) invalidate();
    }

    protected void _onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw && h != oldh) {
            bounds = new RectF(0, 0, w, h);
        }
    }

    Paint paint;
    Bitmap bitmap;
    Canvas c;
    private float number = 0;
    private boolean drawRound = false;
    @Override
    public void setRoundNumber(float number, boolean shouldDraw) {
        if(number>1) number=1;
        else if(number<0) number = 0;

        if (this.number != number) {
            this.number = number;
       //     Log.d(TAG, "number = " + number);
            if(
                    (number==0&&drawRound)  ||
                    (number!=0&&!drawRound)) {
                drawRound = !drawRound;
                shouldDraw = true;
            }
        }
        if(shouldDraw) invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
      // canvas.drawColor(backColor);
       // canvas.drawPath(BitmapEditor.RoundedRect(0,0,getWidth(),getHeight(),maxRx*eachDP*number,maxRy*eachDP*number,false),paint);
    }
    int backColor =0xff111111;
    @Override
    public void setBackColor(int color)
    {
        backColor = color;
    }

    private float maxRx = 18;
    private float eachDP = 0;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(bitmap==null) {
            bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            c = new Canvas(bitmap);
            p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(0xff1ED760);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(getWidth()/16);

            //    c.drawColor(Color.BLACK);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }
        else {
            c.setBitmap(null);
            bitmap.recycle();
            bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
            c.setBitmap(bitmap);
        }
    }
    private void dispatchNormalDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    Paint p;
     @Override
     protected void dispatchDraw(Canvas canvas) {
         if(drawRound) dispatchSupportRoundedDraw(canvas);
         else
             dispatchNormalDraw(canvas);
     }
    protected void dispatchSupportRoundedDraw(Canvas canvas) {
        if(eachDP==0) eachDP = Tool.getOneDps(getContext());
        //   Log.d(TAG, "dispatchDraw");
        if(bitmap==null) {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            c = new Canvas(bitmap);
            p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(0xff1ED760);
            p.setStyle(Paint.Style.STROKE);


            //    c.drawColor(Color.BLACK);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }
        else
            bitmap.eraseColor(0);
        super.dispatchDraw(c);
        // Bitmap bitmap1 =  Bitmap.createBitmap(canvas.getWidth(),canvas.getHeight(),Bitmap.Config.ARGB_8888);
        // c.setBitmap(bitmap1);

//        c.drawPath(BitmapEditor.RoundedRect(-getWidth()/32,-getWidth()/32,getWidth()+getWidth()/32,getHeight()+getWidth()/32,getWidth()/16,getWidth()/16,false),p);

        float value = maxRx*eachDP*number;
        p.setStrokeWidth(value);
        c.drawPath(BitmapEditor.RoundedRect(-value/2,-value/2,getWidth()+value/2,getHeight()+value/2,value,value,false),p);

//        canvas.drawPath(BitmapEditor.RoundedRect(0,0,canvas.getWidth(),canvas.getHeight(),maxRx*eachDP*number,maxRy*eachDP*number,true),paint);

        //   c.drawBitmap(bitmap,0,0,p);
        canvas.drawBitmap(bitmap,0,0,null);
    }

    public float getNumber() {
        return number;
    }
}
