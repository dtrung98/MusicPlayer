package com.ldt.musicr.views;

import android.content.Context;
import android.graphics.Canvas;

import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.R;
/**
 * Created by trung on 9/18/2017.

 */

public class CustomTitleBar extends View{
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

    public CustomTitleBar(Context context) {
        super(context);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (tieuDe== null) {
            tieuDe = ((View) this.getParent()).findViewById(R.id.tieude);
            if(tieuDe!=null)
               Log.d("Sticky","Catch it!");
        }
    }

    public CustomTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    StickyScrollView stickyScrollView ;
    RelativeLayout tieuDe;
    private boolean scrollDown;
    private float translateOfTieuDe=0;
    private float setTranslateOfTieuDe(int translateY)
    {
        float old = translateOfTieuDe;
        translateOfTieuDe =translateY;
        tieuDe.setTranslationY(translateY);
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
    @Override
    protected  void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        View currentlyStickingView = stickyScrollView.currentlyStickingView;
      //  Log.d("Sticky","topOffset = "+stickyScrollView.stickyViewTopOffset);

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
}
