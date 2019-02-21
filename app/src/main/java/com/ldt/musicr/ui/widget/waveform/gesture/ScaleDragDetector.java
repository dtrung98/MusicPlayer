package com.ldt.musicr.ui.widget.waveform.gesture;

/**
 * Created by trung on 9/7/2017.
 */

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class ScaleDragDetector implements ScaleGestureDetector.OnScaleGestureListener {

    private static final int INVALID_POINTER_ID = -1;

    private final float mTouchSlop;
    private final float mMinimumVelocity;
    private final ScaleGestureDetector mScaleDetector;
    private final OnScaleDragGestureListener mScaleDragGestureListener;

    private VelocityTracker mVelocityTracker;
    private boolean mIsDragging;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private int mActivePointerIndex = 0;

    public ScaleDragDetector(Context context, OnScaleDragGestureListener scaleDragGestureListener) {
        mScaleDetector = new ScaleGestureDetector(context, this);
        mScaleDragGestureListener = scaleDragGestureListener;

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();

        if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
            return false;
        }

        mScaleDragGestureListener.onScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
        return true;
    }

    @Override public boolean onScaleBegin(ScaleGestureDetector detector) {
        mScaleDragGestureListener.onScaleBegin();
        return true;
    }

    @Override public void onScaleEnd(ScaleGestureDetector detector) {
        mScaleDragGestureListener.onScaleEnd();
    }

    public boolean isScaling() {
        return mScaleDetector.isInProgress();
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
        mScaleDetector.onTouchEvent(ev);
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
                mVelocityTracker = VelocityTracker.obtain();
                if (null != mVelocityTracker) {
                    mVelocityTracker.addMovement(ev);
                }

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
                    mIsDragging = Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
                }

                if (mIsDragging) {
                    mScaleDragGestureListener.onDrag(dx, dy);
                    mLastTouchX = x;
                    mLastTouchY = y;

                    if (null != mVelocityTracker) {
                        mVelocityTracker.addMovement(ev);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (null != mVelocityTracker) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (mIsDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(ev);
                        mLastTouchY = getActiveY(ev);

                        mVelocityTracker.addMovement(ev);
                        mVelocityTracker.computeCurrentVelocity(1000);

                        final float vX = mVelocityTracker.getXVelocity(), vY =
                                mVelocityTracker.getYVelocity();

                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mScaleDragGestureListener.onFling(mLastTouchX, mLastTouchY, -vX, -vY);
                        }
                    }
                }
                if (null != mVelocityTracker) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            }
        }
    }
}