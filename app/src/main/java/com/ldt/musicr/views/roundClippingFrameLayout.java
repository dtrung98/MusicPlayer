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
    private static final String TAG = "roundClippingFL";
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
       canvas.drawColor((number==0)? flatWhite : 0);
    }
    int flatWhite =0xfff5f5f5;

    public void setFlatColor(int color)
    {
        flatWhite = color;
    }

    protected void _dispatchDraw(Canvas canvas) {
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
    private float maxRx = 18;
    private float maxRy = 18;
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

   // Bitmap bitmap;
//    Canvas c;
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
