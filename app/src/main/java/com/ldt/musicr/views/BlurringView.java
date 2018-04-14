package com.ldt.musicr.views;

/**
 * Created by trung on 9/29/2017.
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ldt.musicr.R;

/**
 * A custom view for presenting a dynamically blurred version of another view's content.
 * <p/>
 * Use {@link #setBlurredView(android.view.View)} to set up the reference to the view to be blurred.
 * After that, call {@link #invalidate()} to trigger blurring whenever necessary.
 */
public class BlurringView extends View {

    public BlurringView(Context context) {
        this(context, null);
        setUp();
    }
private Paint mPaint;
    private void setUp()
    {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
      //  backgroundCloudEffectPaint.setAlpha(100);
    }
    public BlurringView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
        final Resources res = getResources();
        final int defaultBlurRadius = res.getInteger(R.integer.default_blur_radius);
        final int defaultDownsampleFactor = res.getInteger(R.integer.default_downsample_factor);
        final int defaultOverlayColor = res.getColor(R.color.default_overlay_color);

        initializeRenderScript(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlurringView);
        setBlurRadius(a.getInt(R.styleable.BlurringView_blurRadius, defaultBlurRadius));
        setDownsampleFactor(a.getInt(R.styleable.BlurringView_downsampleFactor,
                defaultDownsampleFactor));
        setOverlayColor(a.getColor(R.styleable.BlurringView_overlayColor, defaultOverlayColor));
        a.recycle();
    }

    public void setBlurredView(View blurredView) {
        mBlurredView = blurredView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("I am onDraw","BlurringView");
        super.onDraw(canvas);
        if (mBlurredView != null&&!blockBlur) {
            if (prepare()) {
                // If the background of the blurred view is a color drawable, we use it to clear
                // the blurring canvas, which ensures that edges of the child views are blurred
                // as well; otherwise we clear the blurring canvas with a transparent color.
                if (mBlurredView.getBackground() != null && mBlurredView.getBackground() instanceof ColorDrawable) {
                    mBitmapToBlur.eraseColor(((ColorDrawable) mBlurredView.getBackground()).getColor());
                } else {
                    mBitmapToBlur.eraseColor(Color.TRANSPARENT);
                };
                cv = canvas;
                mBlurredView.draw(mBlurringCanvas);
               // new AsyncTask<Void,Void,Void>() {@Override protected Void doInBackground(Void... voids) {
                        blur();
                //        return null;}@Override protected void onPreExecute() {
                        ready2Draw();
             //       }}.execute();

            }

        }
        else {
            Log.d("null Blurred View", "BlurringView");
            if(mOverlayInFrontColor!=0)
                canvas.drawColor(mOverlayInFrontColor);
            canvas.drawColor(mOverlayColor);
        }
      //  canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),backgroundCloudEffectPaint);
    }
    Canvas cv;
    private boolean blockBlur =true;
    public void setBlockBlur(boolean blockBlur)
    {
        this.blockBlur = blockBlur;
    }
    private void ready2Draw()
    {
        int[] me = new int[2];
        int[] you = new int[2];
        mBlurredView.getLocationOnScreen(you);
        getLocationOnScreen(me);
      Canvas  canvas = cv;
        cv=null;
        canvas.save();
        //    canvas.translate(mBlurredView.getX() - getX(), mBlurredView.getY() - getY());
        canvas.translate(0- me[0], you[1] - me[1]);
        canvas.scale(mDownsampleFactor, mDownsampleFactor);
        canvas.drawBitmap(mBlurredBitmap, 0, 0, null);
        canvas.restore();
        if(mOverlayInFrontColor!=0)
            canvas.drawColor(mOverlayInFrontColor);
        canvas.drawColor(mOverlayColor);
    }

    public void setBlurRadius(int radius) {
        mBlurScript.setRadius(radius);
    }

    public void setDownsampleFactor(int factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Downsample factor must be greater than 0.");
        }

        if (mDownsampleFactor != factor) {
            mDownsampleFactor = factor;
            mDownsampleFactorChanged = true;
        }
    }

    public void setOverlayColor(int color) {
        mOverlayColor = color;
    }
    public void setmOverlayInFrontColor(int color)
    {
        mOverlayInFrontColor = color;
    }

    private void initializeRenderScript(Context context) {
        mRenderScript = RenderScript.create(context);
        mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
    }

    protected boolean prepare() {
        final int width = mBlurredView.getWidth();
        final int height = mBlurredView.getHeight();

        if (mBlurringCanvas == null || mDownsampleFactorChanged
                || mBlurredViewWidth != width || mBlurredViewHeight != height) {
            mDownsampleFactorChanged = false;

            mBlurredViewWidth = width;
            mBlurredViewHeight = height;

            int scaledWidth = width / mDownsampleFactor;
            int scaledHeight = height / mDownsampleFactor;

            // The following manipulation is to avoid some RenderScript artifacts at the edge.
            scaledWidth = scaledWidth - scaledWidth % 4 + 4;
            scaledHeight = scaledHeight - scaledHeight % 4 + 4;

            if (mBlurredBitmap == null
                    || mBlurredBitmap.getWidth() != scaledWidth
                    || mBlurredBitmap.getHeight() != scaledHeight) {
                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight,
                        Bitmap.Config.ARGB_8888);
                if (mBitmapToBlur == null) {
                    return false;
                }

                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight,
                        Bitmap.Config.ARGB_8888);
                if (mBlurredBitmap == null) {
                    return false;
                }
            }

            mBlurringCanvas = new Canvas(mBitmapToBlur);
            mBlurringCanvas.scale(1f / mDownsampleFactor, 1f / mDownsampleFactor);
            mBlurInput = Allocation.createFromBitmap(mRenderScript, mBitmapToBlur,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());
        }
        return true;
    }

    protected void blur() {
        mBlurInput.copyFrom(mBitmapToBlur);
        mBlurScript.setInput(mBlurInput);
        mBlurScript.forEach(mBlurOutput);
        mBlurOutput.copyTo(mBlurredBitmap);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRenderScript != null) {
            mRenderScript.destroy();
        }
    }

    private int mDownsampleFactor;
    private int mOverlayColor;
    private int mOverlayInFrontColor = 0;
    private View mBlurredView;
    private int mBlurredViewWidth, mBlurredViewHeight;

    private boolean mDownsampleFactorChanged;
    private Bitmap mBitmapToBlur, mBlurredBitmap;
    private Canvas mBlurringCanvas;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;

}