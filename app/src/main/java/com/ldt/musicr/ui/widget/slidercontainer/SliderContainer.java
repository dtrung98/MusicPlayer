package com.ldt.musicr.ui.widget.slidercontainer;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SliderContainer extends FrameLayout implements ViewPager.OnPageChangeListener {
    private boolean needsRedraw;

    private Slider slider;

    private Point center = new Point();
    private Point initialTouch = new Point();

    public SliderContainer(@NonNull Context context) {
        this(context, null);
    }

    public SliderContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setClipChildren(false);
        setClipToPadding(false);

        if (Build.VERSION.SDK_INT < 21)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        slider = new Slider(context);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(slider, 0, layoutParams);
        slider.setPageTransformer(false, new SliderTransformer());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        try {
            slider = (Slider) getChildAt(0);
            slider.addOnPageChangeListener(this);
            slider.setPageTransformer(false, new SliderTransformer());

        } catch (Exception e) {
            throw new IllegalStateException("The root child of SlidePagerContainer must be a SlierPager");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Capture any touches not already handled by the ViewPager
        // to implement scrolling from a touch outside the pager bounds.
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouch.x = (int) ev.getX();
                initialTouch.y = (int) ev.getY();
            case MotionEvent.ACTION_UP:
                this.performClick();
            default:
                ev.offsetLocation(center.x - initialTouch.x, center.y - initialTouch.y);
                break;
        }
        return slider.dispatchTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (needsRedraw) invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        needsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
    }

    public Slider getSlider() {
        return slider;
    }

}
