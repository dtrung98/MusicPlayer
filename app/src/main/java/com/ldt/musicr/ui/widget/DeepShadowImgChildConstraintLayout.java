package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.ldt.musicr.util.uitool.BitmapEditor;
import com.ldt.musicr.R;

public class DeepShadowImgChildConstraintLayout extends ConstraintLayout {
    private static final String TAG = "DeepShadowConstraint";
    public DeepShadowImgChildConstraintLayout(Context context) {
        super(context);
        this.init((AttributeSet)null);
    }

    public DeepShadowImgChildConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public DeepShadowImgChildConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }
    private boolean setted = false;
    private int[] imageRect = new int[4];
    private int[] delta = new int[4];

    public void setShadowDeltaRect(int... value) {
       setted = true;
        for(int i=0;i<4;i++)
            delta[i] = value[i];
       invalidate();
    }
    private Bitmap bitmap = null;
    private Bitmap shadowBitmap = null;
    private Canvas shadowCanvas = null;
    private boolean shadow_drawn = false;
    private Paint solidPaint;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        shadowBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        shadowCanvas = new Canvas(shadowBitmap);
    }

    public void setBitmapImage(Bitmap bitmap) {

        shadow_drawn = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(setted) onDrawShadow(canvas);
    }
    private ImageView imageView;

    private void onDrawShadow(Canvas canvas) {

        if(bitmap==null) {
            imageView = this.findViewById(R.id.Art);
            imageRect[0] = imageView.getLeft();
            imageRect[1] = imageView.getTop();
            imageRect[2] = imageView.getRight();
            imageRect[3] = imageView.getBottom();
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }

        if(!shadow_drawn) {
            bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

            Log.d(TAG, "imageRect = [" + imageRect[0]+", "+imageRect[1]+", "+imageRect[2]+", "+imageRect[3]+"]");
            shadow_drawn = true;
            Bitmap tempBitmap = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(tempBitmap);
            solidPaint.setAlpha(150);
           tempCanvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()), new Rect(imageRect[0]+delta[0],imageRect[1]+delta[1],imageRect[2]+delta[2],imageRect[3]+delta[3]),solidPaint);
        //    solidPaint.setColor(Tool.getSurfaceColor());
       //     solidPaint.setAlpha(80);
       //   tempCanvas.drawRect(imageRect[0]+delta[0],imageRect[1]+delta[1],imageRect[2]+delta[2],imageRect[3]+delta[3],solidPaint);
            shadowBitmap =BitmapEditor.getBlurredWithGoodPerformance(getContext(),tempBitmap,1,14,2);
            tempCanvas = null;
           tempBitmap.recycle();
        }
        canvas.drawBitmap(shadowBitmap,null,new Rect(0,0,getWidth(),getHeight()),null);
    }
}
