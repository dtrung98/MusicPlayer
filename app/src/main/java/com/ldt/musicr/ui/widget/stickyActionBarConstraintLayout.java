package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.graphics.Canvas;

import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ldt.musicr.util.Tool;

/**
 * Created by trung on 9/18/2017.

 */

public class stickyActionBarConstraintLayout extends ConstraintLayout
{
    private static final String TAG = "stickyActionBar";
    public static final String STICKY_TAG = "sticky";

    /**
     * Flag for views that should stick and have non-constant drawing. e.g. Buttons, ProgressBars etc
     */
    public static final String FLAG_NONCONSTANT = "-nonconstant";

    /**
     * Flag for views that have aren't fully opaque
     */
    public static final String FLAG_HASTRANSPARANCY = "-hastransparancy";

    /**
     * Default height of the shadow peeking out below the stuck view.
     */
    private static final int DEFAULT_SHADOW_HEIGHT = 10; // dp;

    public stickyActionBarConstraintLayout(Context context) {
        super(context);

    }


    public stickyActionBarConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public stickyActionBarConstraintLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private StickyScrollView stickyScrollView ;
    public void setStickyScrollView(StickyScrollView s) {
        stickyScrollView = s;
    }
    private boolean scrollDown;
    private float translateOfTieuDe=0;
    private float setTranslateOfTieuDe(int translateY)
    {
        float old = translateOfTieuDe;
        translateOfTieuDe =translateY;
        //Todo: tieuDe.setTranslationY(translateY);
        return old;
    }
    private float getTranslateOfTieuDe()
    {
        return translateOfTieuDe;
    }

    public void connectOnScrollChanged(int toLeft, int toTop, int preLeft, int preTop) {

        if(true) return;
        View currentlyStickingView = stickyScrollView.currentlyStickingView;
        int delta = toTop - preTop;
        float topOffset =0;

        scrollDown = !(delta > 0); // đây là cử chỉ tay, không phải hành vi của scrollView
        Log.d("StickyTitle", preLeft + " -> " + toLeft + ", " + preTop + " -> " + toTop + ", " + scrollDown);
        if (currentlyStickingView != null&&!scrollDown) {
          topOffset =stickyScrollView.stickyViewTopOffset // biến số tối quan trọng, đây chính là transform của canvas đối với ảnh chính
                    + (stickyScrollView.clippingToPadding
                    ? getPaddingTop() : 0)
                    +getHeight()/2
                    -currentlyStickingView.getHeight()/2;
            setTranslateOfTieuDe((int) topOffset);
        }
        if(scrollDown)
        {
           // if(getTranslateOfTieuDe()<0)
             //   setTranslateOfTieuDe(delta);
        }
    }
    private boolean ShouldDrawSticky = true;
    private void drawSticky(Canvas canvas) {
        if(!ShouldDrawSticky) return;
        View currentlyStickingView = stickyScrollView.currentlyStickingView;
          Log.d(TAG,"topOffset = "+stickyScrollView.stickyViewTopOffset);

        if(currentlyStickingView != null){
            canvas.save();
            canvas.translate(
                    getPaddingLeft()
                            + stickyScrollView.stickyViewLeftOffset,

                    stickyScrollView.stickyViewTopOffset // biến số tối quan trọng, đây chính là transform của canvas đối với ảnh chính
                            + (stickyScrollView.clippingToPadding
                            ? getPaddingTop() : 0)
                            +getHeight()/2
                            -currentlyStickingView.getHeight()/2);

            canvas.clipRect(0, (stickyScrollView.clippingToPadding ? -stickyScrollView.stickyViewTopOffset : 0) ,
                    getWidth() - stickyScrollView.stickyViewLeftOffset,
                    currentlyStickingView.getHeight() + stickyScrollView.mShadowHeight + 1);

            if (stickyScrollView.mShadowDrawable != null) {
                int left = 0;
                int right = currentlyStickingView.getWidth();
                int top = currentlyStickingView.getHeight();
                int bottom = currentlyStickingView.getHeight() + stickyScrollView.mShadowHeight;
                stickyScrollView.mShadowDrawable.setBounds(left, top, right, bottom);
                stickyScrollView.mShadowDrawable.draw(canvas);
            }

            canvas.clipRect(0, (stickyScrollView.clippingToPadding ? -stickyScrollView.stickyViewTopOffset : 0), getWidth(), currentlyStickingView.getHeight());
            if(Tool.getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)){
                stickyScrollView.showView(currentlyStickingView);
                currentlyStickingView.draw(canvas);
                stickyScrollView.hideView(currentlyStickingView);
            } else {
                currentlyStickingView.draw(canvas);
            }
            canvas.restore();
        }

    }
    @Override
    protected  void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        canvas.save();
   //     canvas.translate(0,-25*Tool.oneDPs);
        super.dispatchDraw(canvas);
        canvas.restore();
        drawSticky(canvas);
    }
}
