package com.ldt.NewDefinitionMusicApp.views.EffectView;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


import com.ldt.NewDefinitionMusicApp.InternalTools.Tool;
import com.ldt.NewDefinitionMusicApp.activities.MainActivity;

/**
 * Created by trung on 10/30/2017.
 */

public class EffectViewHolder {
    public EffectViewHolder(MainActivity activity) {
        this.activity = activity;
        SCROLL_THRESHOLD = Tool.getOneDps(activity)*10;
        handler = new Handler();
        status = Status.THE_FIRST_TIME;
        touch = new float [] {0,0};
        from_local = new int[] {0,0};
        from_size = new int[] {0,0};
    }
    public interface EffectViewListener {
        ImageView getImageView(String command);
        String[] getStringCommand(String command);
        int[] getImageResourses(String command);
        void onReceivedResult(String command,int result);
    }
    private EffectViewListener whichCall;
    private MainActivity activity;
    private EffectView effectView;
    private View sourceView;
    ImageView symbol;
    String[] string_menu;
    int[] image_menu;
    float[] touch;
    int[] from_local;
    int[] from_size;
    private final Handler handler;
    private Status status;
    String command;
    enum Status {
        THE_FIRST_TIME,
        IN_LONG_PRESSED,
        NOPE
    }
    private void setNull() {
        sourceView = null;
        whichCall = null;
        effectView = null;
        status = Status.THE_FIRST_TIME;
    }

    private EffectView create_New_Instance_Of_EffectView() {
        EffectView effectView = new EffectView(activity);
        effectView.set(symbol,sourceView,touch,string_menu,image_menu);
        effectView.emo_behavior.set_source(from_local[0],from_local[1],from_size[0],from_size[1]);
        effectView.emo_behavior.sync();
        effectView.bg_behavior.sync();
        activity.rootEveryThing.addView(effectView);
        return effectView;
    }


    private boolean isTheFirstTime() {
        return status == Status.THE_FIRST_TIME;
    }
    private MotionEvent event;
    private void setResourcesForFirstTime(EffectViewListener whichCall, String command, View v,MotionEvent event) {
        sourceView = v;
        this.command = command;
        this.whichCall = whichCall;
        this.event = event;
    }
    private void setTheFirstTime( MotionEvent event) {

            touch[0] = event.getRawX();
            touch[1] = event.getRawY();

            this.symbol =whichCall.getImageView(command);
            symbol.getLocationOnScreen(from_local);
            from_size[0] =  symbol.getMeasuredWidth();
            from_size[1] = symbol.getMeasuredHeight();
            this.string_menu = whichCall.getStringCommand(command);
            this.image_menu = whichCall.getImageResourses(command);
            status =Status.NOPE;
            this.event = null;
    }
    private float mDownX;
    private float mDownY;
    private boolean isActionMoveEventStored = false;
    private final float SCROLL_THRESHOLD;
    public void detectLongPress(EffectViewListener whichCall,String command,View v,MotionEvent event) {
        int me = event.getAction();
        if (me == MotionEvent.ACTION_DOWN) {
            if(isTheFirstTime()) setResourcesForFirstTime(whichCall,command,v,event);
            mDownX = event.getRawX();
            mDownY = event.getRawY();
            isActionMoveEventStored = true;
            handler.postDelayed(mLongPressed, android.view.ViewConfiguration.getLongPressTimeout());
        }
        else if (me == MotionEvent.ACTION_MOVE) {
            if (!isActionMoveEventStored) {
                isActionMoveEventStored = true;
                mDownX = event.getRawX();
                mDownY = event.getRawY();
            } else {
                float currentX = event.getRawX();
                float currentY = event.getRawY();
                float firstX = mDownX;
                float firstY = mDownY;
                double distance = Math.sqrt(
                        (currentY - firstY) * (currentY - firstY) + ((currentX - firstX) * (currentX - firstX)));
                if (distance > SCROLL_THRESHOLD) {
                    handler.removeCallbacks(mLongPressed);
                }
            }
        }
        else if(me == MotionEvent.ACTION_UP||me==MotionEvent.ACTION_CANCEL) {
            isActionMoveEventStored = false;
            handler.removeCallbacks(mLongPressed);
        }
    }

    Runnable mLongPressed = new Runnable() {
        public void run() {
           if(isTheFirstTime()) setTheFirstTime(event);
            status = Status.IN_LONG_PRESSED;
            symbol.setVisibility(View.INVISIBLE);
            if (effectView == null)
                effectView = create_New_Instance_Of_EffectView();
        }
    };

    private boolean isInLongPressed() {
        return status == Status.IN_LONG_PRESSED;
    }

    public boolean run(View v, MotionEvent event) { // gọi khi đã chắc chắn là long press
        if (!isInLongPressed()) return false;
        int result = effectView.handlerEvent(v, event);
        if (result != -1) {
            // trả về kết quả
            whichCall.onReceivedResult(command,result);
            setNull();
            return true;
        }
        return true; // 0 mean it stills handler the event
    }
}
