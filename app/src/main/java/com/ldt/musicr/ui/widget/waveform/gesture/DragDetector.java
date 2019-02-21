package com.ldt.musicr.ui.widget.waveform.gesture;

/**
 * Created by trung on 9/7/2017.
 */

import android.content.Context;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class DragDetector {

    private static final int INVALID_POINTER_ID = -1;

    private final float mTouchSlop;
    private boolean mIsDragging;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private int mActivePointerIndex = 0;
    private OnDragGestureListener mOnDragGestureListener;

    public DragDetector(Context context, OnDragGestureListener dragGestureListener) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mOnDragGestureListener = dragGestureListener;
    }

    public boolean isDragging() {
        return mIsDragging;
    }

    private float getActiveX(MotionEvent ev) {
        try {
            return ev.getX(mActivePointerIndex);
            //return MotionEventCompat.getX(ev, mActivePointerIndex);
        } catch (Exception e) {
            return ev.getX();
        }
    }

    private float getActiveY(MotionEvent ev) {
        try {
            return ev.getY(mActivePointerIndex);
            //return MotionEventCompat.getY(ev, mActivePointerIndex);
        } catch (Exception e) {
            return ev.getY();
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        //final int action = MotionEventCompat.getActionMasked(ev);
        onTouchActivePointer(action, ev);
        onTouchDragEvent(action, ev);
        return true;
    }

    private void onTouchActivePointer(int action, MotionEvent ev) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                //final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = (pointerIndex == 0) ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                }
                break;
        }

        mActivePointerIndex =
                ev.findPointerIndex(mActivePointerId != INVALID_POINTER_ID ? mActivePointerId : 0);
    }

    private void onTouchDragEvent(int action, MotionEvent ev) {
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastTouchX = getActiveX(ev);
                mLastTouchY = getActiveY(ev);

                mIsDragging = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = getActiveX(ev);
                final float y = getActiveY(ev);
                final float dx = x - mLastTouchX, dy = y - mLastTouchY;

                if (!mIsDragging) {
                    mIsDragging = mEnableRect.contains(mLastTouchX, mLastTouchY)
                            && Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
                }

                if (mIsDragging) {
                    mOnDragGestureListener.onDrag(dx, dy);
                    mLastTouchX = x;
                    mLastTouchY = y;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                break;
            }
        }
    }

    private RectF mEnableRect = new RectF();

    public void setEnableRect(float left, float top, float right, float bottom) {
        mEnableRect.set(left, top, right, bottom);
    }
}