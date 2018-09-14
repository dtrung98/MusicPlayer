package com.ldt.musicr.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;

import com.ldt.musicr.InternalTools.BitmapEditor;
import com.ldt.musicr.InternalTools.Tool;

/**
 * Created by trung on 9/30/2017.
 */

public class roundClippingFrameLayout extends SupportDarkenFrameLayout {
    private Paint drawPaint;
    private Paint roundPaint;

    private int mCornerRadius = 12;

    private RectF bounds;


    public roundClippingFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public roundClippingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public roundClippingFrameLayout(Context context) {
        super(context);
        init();
    }

    protected void init() {
        drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawPaint.setColor(0xffffffff);
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        roundPaint.setColor(0xffffffff);

        //  setWillNotDraw(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw && h != oldh) {
            bounds = new RectF(0, 0, w, h);
        }
    }

    Paint paint;
    Bitmap bitmap;
    Canvas c;
    private float number = 0;

    public void setRoundedClippingNumber(float number, boolean shouldDraw) {
        if (this.number != number) {
            this.number = number;
        }
        if(shouldDraw) invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
       canvas.drawColor((number==0)? flatWhite : 0);
    }
    int flatWhite =0xfff5f5f5;

    public void setFlatColor(int color)
    {
        flatWhite = color;
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
              if(number<=0) {
                  super.dispatchDraw(canvas);
                  return;
              }
              if(bitmap==null) {
                  // one time running
             //     Log.d("You set bitmap !","round");
                  bitmap = Bitmap.createBitmap((int) bounds.width(), (int) bounds.height(), Bitmap.Config.ARGB_8888);
                  c = new Canvas(bitmap);
                  paint = new Paint();
                  paint.setAntiAlias(true);
                  BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

                  paint.setShader(shader);
              }
            //  bitmap.eraseColor(0);

           // run when rect is clipping
              super.dispatchDraw(c);

              if(eachDP==0) eachDP = Tool.getOneDps(getContext());
              if(number>1) number =1;
              else if(number<0) number =0;

              canvas.drawPath(BitmapEditor.RoundedRect(bounds.left,bounds.top,bounds.right,bounds.bottom,mCornerRadius*number*eachDP,mCornerRadius*number*eachDP,false),paint);
    }
    private float eachDP = 0;

    public float getNumber() {
        return number;
    }
}
