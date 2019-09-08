package com.ldt.musicr.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by trung on 8/26/2017.
 */

final public class SeeThroughTextView extends AppCompatTextView
{
    private Bitmap mMaskBitmap;
    private Canvas mMaskCanvas;
    private Paint mPaint;
    private Drawable mBackground;
    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundCanvas;

    public SeeThroughTextView(final Context context) {
        super(context);
        init();
    }

    public SeeThroughTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        super.setTextColor(Color.BLACK);
        super.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(final Drawable bg) {
        if (mBackground == bg) {
            return;
        }

        mBackground = bg;

        // Will always drawVisualWave drawable using view bounds. This might be a
        // problem if the drawable should force the view to be bigger, e.g.
        // the view sets its dimensions to wrap_content and the drawable
        // is larger than the text.
        int w = getWidth();
        int h = getHeight();
        if (mBackground != null && w != 0 && h != 0) {
            mBackground.setBounds(0, 0, w, h);
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
        if (mBackground != null) {
            mBackground.setBounds(0, 0, w, h);
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
    protected void onDraw(final Canvas canvas) {
        if (isNothingToDraw()) {
            return;
        }
        drawMask();
        drawBackground();
        canvas.drawBitmap(mBackgroundBitmap, 0.f, 0.f, null);
    }

    private boolean isNothingToDraw() {
        return mBackground == null
                || getWidth() == 0
                || getHeight() == 0;
    }

    // drawVisualWave() calls onDraw() leading to stack overflow
    @SuppressLint("WrongCall")
    private void drawMask() {
        clear(mMaskCanvas);
        super.onDraw(mMaskCanvas);
    }

    private void drawBackground() {
        clear(mBackgroundCanvas);
        mBackground.draw(mBackgroundCanvas);
        mBackgroundCanvas.drawBitmap(mMaskBitmap, 0.f, 0.f, mPaint);
       // backgroundCloudEffectPaint.setStyle(Paint.Style.STROKE);
   //     mBackgroundCanvas.drawRect(0,0,mBackgroundCanvas.getWidth()-1,mBackgroundCanvas.getHeight()-1,backgroundCloudEffectPaint);
    }

    private static void clear(Canvas canvas) {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
    }
}
