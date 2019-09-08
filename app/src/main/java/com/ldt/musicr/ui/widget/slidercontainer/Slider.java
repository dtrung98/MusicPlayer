package com.ldt.musicr.ui.widget.slidercontainer;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class Slider extends ViewPager {

    private boolean disabled;

    public Slider(Context context) {
        super(context);
        init();
    }

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setOffscreenPageLimit(2);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !this.disabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !this.disabled && super.onInterceptTouchEvent(event);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
