package com.ldt.musicr.ui.widget.viewgroup;

import android.content.Context;

import androidx.constraintlayout.motion.widget.MotionLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class SlidingMotionLayout extends MotionLayout {
    private static final String TAG = "SlidingMotionLayout";

    public SlidingMotionLayout(Context context) {
        super(context);
    }

    public SlidingMotionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingMotionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: ev = "+ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }
}
