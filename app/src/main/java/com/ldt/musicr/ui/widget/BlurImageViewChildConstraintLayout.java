package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.ldt.musicr.util.BitmapEditor;
import com.ldt.musicr.R;

public class BlurImageViewChildConstraintLayout extends ConstraintLayout {
    private static final String TAG = "DeepShadowConstraint";

    public BlurImageViewChildConstraintLayout(Context context) {
        super(context);
        this.init((AttributeSet) null);
    }

    private int mImageViewId = R.id.art;

    public BlurImageViewChildConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public BlurImageViewChildConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.BlurImageViewChildConstraintLayout);


        mImageViewId = t.getResourceId(R.styleable.BlurImageViewChildConstraintLayout_imageViewId, R.id.art);

        mBlurDelta[0] = (int) (t.getDimension(R.styleable.BlurImageViewChildConstraintLayout_shadowDeltaLeft, 0));
        mBlurDelta[1] = (int) (t.getDimension(R.styleable.BlurImageViewChildConstraintLayout_shadowDeltaTop, 0));
        mBlurDelta[2] = (int) (t.getDimension(R.styleable.BlurImageViewChildConstraintLayout_shadowDeltaRight, 0));
        mBlurDelta[3] = (int) (t.getDimension(R.styleable.BlurImageViewChildConstraintLayout_shadowDeltaBottom, 0));
        t.recycle();
    }

    private final int[] imageRect = new int[4];
    private final int[] mBlurDelta = new int[4];

    public void setShadowDeltaRect(int... value) {

        System.arraycopy(value, 0, mBlurDelta, 0, 4);
        invalidate();
    }

    private Bitmap bitmap = null;
    private Bitmap shadowBitmap = null;
    private Canvas shadowCanvas = null;
    private boolean shadow_drawn = false;
    private Paint solidPaint;
    private final boolean mEnableBlurredImageDrawn = false;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        shadowBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        shadowCanvas = new Canvas(shadowBitmap);
    }

    public void setBitmapImage(Bitmap bitmap) {

        shadow_drawn = false;
        mBitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mEnableBlurredImageDrawn) {
            onDrawShadow(canvas);
        }
    }

    private ImageView imageView;
    private Bitmap mBitmap;


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void onDrawShadow(Canvas canvas) {

        if (bitmap == null) {
            imageView = this.findViewById(mImageViewId);
            if (imageView == null) return;

            imageRect[0] = imageView.getLeft();
            imageRect[1] = imageView.getTop();
            imageRect[2] = imageView.getRight();
            imageRect[3] = imageView.getBottom();
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }

        if (!shadow_drawn) {
            if (mBitmap == null)
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            Log.d(TAG, "imageRect = [" + imageRect[0] + ", " + imageRect[1] + ", " + imageRect[2] + ", " + imageRect[3] + "]");
            shadow_drawn = true;
            Bitmap tempBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(tempBitmap);
            solidPaint.setAlpha(150);
            tempCanvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(imageRect[0] + mBlurDelta[0], imageRect[1] + mBlurDelta[1], imageRect[2] + mBlurDelta[2], imageRect[3] + mBlurDelta[3]), solidPaint);
            //    solidPaint.setColor(Tool.getBaseColor());
            //     solidPaint.setAlpha(80);
            //   tempCanvas.drawRect(imageRect[0]+mBlurDelta[0],imageRect[1]+mBlurDelta[1],imageRect[2]+mBlurDelta[2],imageRect[3]+mBlurDelta[3],solidPaint);
            shadowBitmap = BitmapEditor.getBlurredWithGoodPerformance(getContext(), tempBitmap, 1, 16, 3.5f);
            tempCanvas = null;
            tempBitmap.recycle();
        }
        canvas.drawBitmap(shadowBitmap, null, new Rect(0, 0, getWidth(), getHeight()), null);
    }
}
