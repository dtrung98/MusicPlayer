package com.ldt.musicr.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ldt.musicr.R;

/**
 * Created by trung on 9/29/2017.
 */

public class SeeThroughFrameLayout extends FrameLayout {
    private Bitmap mMaskBitmap;
    private Canvas mMaskCanvas;
    private Paint mPaint;
    private Paint src_inPaint,standardPaint;
    private Drawable mBackgroundDrawable;
    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundCanvas;

    public SeeThroughFrameLayout(final Context context) {
        super(context);
        init();
    }

    public SeeThroughFrameLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    @Override
    @Deprecated
    public void setBackgroundDrawable(final Drawable bg) {
        if (mBackgroundDrawable == bg) {
            return;
        }

        mBackgroundDrawable = bg;

        // Will always drawVisualWave drawable using view bounds. This might be a
        // problem if the drawable should force the view to be bigger, e.g.
        // the view sets its dimensions to wrap_content and the drawable
        // is larger than the text.
        int w = getWidth();
        int h = getHeight();
        if (mBackgroundDrawable != null && w != 0 && h != 0) {
            mBackgroundDrawable.setBounds(0, 0, w, h);
        }
        requestLayout();
        invalidate();
    }

    @Override
    public void setBackgroundColor(final int color) {
        setBackgroundDrawable(new ColorDrawable(color));
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0) {
            freeBitmaps();
            return;
        }

        createBitmaps(w, h);
        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.setBounds(0, 0, w, h);
        }
    }

    private void createBitmaps(int w, int h) {
        mBackgroundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mBackgroundCanvas = new Canvas(mBackgroundBitmap);
        mMaskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
        mMaskCanvas = new Canvas(mMaskBitmap);
    }

    private void freeBitmaps() {
        mBackgroundBitmap = null;
        mBackgroundCanvas = null;
        mMaskBitmap = null;
        mMaskCanvas = null;
    }

    @Override
    protected void dispatchDraw(final Canvas canvas) {
      //  Log.d("dispatchDraw called","stfl");
        super.dispatchDraw(canvas);

      //  if (isNothingToDraw()) {
     //       return;
     //   }
        //drawMask();
       // drawBackground();
        //canvas.drawBitmap(mBackgroundBitmap, 0.f, 0.f, standardPaint);

        standardPaint.setAlpha(100);
        canvas.drawCircle(canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/4,standardPaint);
    }

    private boolean isNothingToDraw() {
        return mBackgroundDrawable == null
                || getWidth() == 0
                || getHeight() == 0;
    }

    // drawVisualWave() calls onDraw() leading to stack overflow
    @SuppressLint("WrongCall")
    private void drawMask() {
        clear(mMaskCanvas);
        super.onDraw(mMaskCanvas);

    }
    public ImageView applyDrawableView;

    private void drawBackground() {
        clear(mBackgroundCanvas);
        mBackgroundDrawable.draw(mBackgroundCanvas);
       //  Drawable drawable = mBackgroundDrawable.mutate().getConstantState().newDrawable();
       //  drawable.drawVisualWave(mBackgroundCanvas);
      //  mBackgroundCanvas.drawBitmap(mMaskBitmap, 0.f, 0.f, backgroundCloudEffectPaint);

        int[] padding = new int[]
                {
                        getPaddingLeft(),
                        getPaddingTop(),
                        getPaddingRight(),
                        getPaddingBottom()
                };

        //mBackgroundCanvas.drawRect(getRectSupportPadding(padding,0,0,-1,padding[3]),backgroundCloudEffectPaint);
        mBackgroundCanvas.drawCircle(mBackgroundCanvas.getWidth()/2, mBackgroundCanvas.getHeight()/2, mBackgroundCanvas.getWidth()/4,
                mPaint);
     //   if(applyDrawableView!=null)
        //    applyDrawableView.setImageDrawable(mBackgroundDrawable.mutate().getConstantState().newDrawable());
    }

    // -1 meaning match parent
    @NonNull
    private Rect getRectSupportPadding(int[] padding, int left, int top, int width , int height )
    {
        int canvasW = mBackgroundCanvas.getWidth();
        int canvasH = mBackgroundCanvas.getHeight();

        if(width==-1) width = canvasW;
        if(height==-1) width = canvasH;
        int Left = padding[0] +left,Top=padding[1] +top,Right = Left+width,Bottom = Top +height;


        if(Right>canvasW-padding[2]) Right = canvasW-padding[2];
        if(Bottom>canvasH-padding[3]) Bottom = canvasH - padding[3];
        return new Rect(Left,Top,Right,Bottom);
    }
    private void init() {
        src_inPaint = new Paint();
        src_inPaint.setColor(0x70000000);
        src_inPaint.setAntiAlias(true);
        src_inPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        standardPaint = new Paint();
        standardPaint.setColor(getResources().getColor(R.color.colorAccent));
        standardPaint.setAntiAlias(true);

        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mPaint.setAntiAlias(true);
        super.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private static void clear(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }
}
