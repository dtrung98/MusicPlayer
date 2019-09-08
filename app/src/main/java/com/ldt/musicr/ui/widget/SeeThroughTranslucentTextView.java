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
 * Created by trung on 8/27/2017.
 */

public class SeeThroughTranslucentTextView extends AppCompatTextView{
    private static final String TAG = "SeeThroughTTextView";
    private Bitmap mMaskBitmap;
    private Canvas mMaskCanvas;
    private Paint mPaint;
    private Drawable mBackground;
    private Bitmap mBackgroundBitmap;
    private Canvas mBackgroundCanvas;

    public SeeThroughTranslucentTextView(final Context context) {
        super(context);
        init();
    }

    public SeeThroughTranslucentTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeeThroughTranslucentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        super.setTextColor(0xaaffffff);
        super.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
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
        mMaskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
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
   // Cái này thực hiện vẽ Nội dung ( Chữ ) Lên mBackgroundCanvas
       mBackground.draw(mBackgroundCanvas);   // vẽ nền
        mBackgroundCanvas.drawBitmap(mMaskBitmap, 0.f, 0.f, mPaint); // vẽ chữ

        super.setTextColor(getTextColors());
        drawMask();
        mBackgroundCanvas.drawBitmap(mMaskBitmap, 0.f, 0.f, mPaint); // vẽ chữ
        //   backgroundCloudEffectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
       // mBackgroundCanvas.drawBitmap(mMaskBitmap, 0.f, 0.f, backgroundCloudEffectPaint); // vẽ chữ


       // Paint paint = new Paint();
      //  mBackgroundCanvas.drawBitmap(mMaskBitmap, 0.f, 0.f, paint);
    //    mBackground.drawVisualWave(mBackgroundCanvas);

    }

    private static void clear(Canvas canvas) {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
    }
}
