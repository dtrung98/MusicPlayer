package com.ldt.musicr.ui.widget.gesture;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ldt.musicr.ui.page.CardLayerFragment;
import com.ldt.musicr.ui.CardLayerController;

public class SwipeDetectorGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG ="SwipeDetector";


    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    public int item = -1;
    public CardLayerController.CardLayerAttribute attr;
    public CardLayerFragment layer;
    public void setMotionLayer(int i, CardLayerFragment b, CardLayerController.CardLayerAttribute a) {
        item = i;
        layer = b;
        attr = a;
    }

    public void removeMotionLayer() {
        item = -1;
        layer = null;
        attr = null;
    }

    public boolean isLayerAvailable() {
        return item !=-1;
    }


    protected int id;
    public void setAdaptiveView(View v) {
        id =v.getId();
    }
    public boolean onUp(MotionEvent e) {
        return false;
    }
    public boolean onMove(MotionEvent e) {
        return false;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getRawY() - e1.getRawY();
            float diffX = e2.getRawX() - e1.getRawX();
            Log.d(TAG, "onFling: diffY = "+diffY);
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        result = onSwipeRight(e1,e2,velocityX,velocityY);
                    } else {
                        result = onSwipeLeft(e1,e2,velocityX,velocityY);
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        result = onSwipeBottom(e1,e2,velocityX,velocityY);
                    } else {
                        result = onSwipeTop(e1,e2,velocityX,velocityY);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    public boolean onSwipeRight(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public boolean onSwipeLeft(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public boolean onSwipeTop(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public boolean onSwipeBottom(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
