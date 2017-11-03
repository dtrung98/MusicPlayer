package com.ldt.NewDefinitionMusicApp.useless;

/**
 * Created by trung on 10/18/2017.
 */

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ldt.NewDefinitionMusicApp.InternalTools.Tool;

/**
 * Detects x and right swipes across a view.
 */
public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft() {
        Log.d("MCF","Left");

    }

    public void onSwipeRight() {
        Log.d("MCF","Right");
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        public boolean longPressBefore = false;

        @Override
        public boolean onDown(MotionEvent e) {
            if(longPressBefore)
            {
                Log.d("MCF",e.getX()+" | "+e.getY());
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(!longPressBefore) {
                longPressBefore = true;

            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            longPressBefore = false;
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }
}