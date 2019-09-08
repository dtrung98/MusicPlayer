package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.ldt.musicr.util.Tool;

public class ShadowBackgroundImageView extends androidx.appcompat.widget.AppCompatImageView {
    public ShadowBackgroundImageView(Context context) {
        super(context);
    }

    public ShadowBackgroundImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private Bitmap mMaskBitmap;
    private Canvas mMaskCanvas;
    private void createBitmaps(int w, int h) {
        mMaskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mMaskCanvas = new Canvas(mMaskBitmap);
    }

    private void init() {
        oneDP = Tool.getOneDps(getContext());
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

    }

    private float oneDP;
    private Paint paint;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w == 0 || h == 0) {
            freeBitmaps();
            return;
        }
        createBitmaps(w,h);
    }
    private void freeBitmaps() {
        mMaskCanvas = null;
        mMaskBitmap = null;
    }
    public ShadowBackgroundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
       // canvas.drawColor(Color.RED);
        super.dispatchDraw(canvas);
    }
    @Override
    protected void onDraw(Canvas canvas) {
  //   Rect rect =    getClipBounds();
    // rect.inset((int)(-50* oneDP),(int)(-50*oneDP));
     //  super.onDraw(mMaskCanvas);
        super.onDraw(canvas);
    }

}
