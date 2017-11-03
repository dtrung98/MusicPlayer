package com.ldt.NewDefinitionMusicApp.views.EffectView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldt.NewDefinitionMusicApp.InternalTools.Tool;


/**
 * Created by trung on 10/17/2017.
 */

public class EffectView extends View {

    public int handlerEvent(View v, MotionEvent event)
    {
        if(event.getAction()!=MotionEvent.ACTION_UP) {
            property.setTouch(event.getRawX(),event.getRawY());
            emo_behavior.sync();
            bg_behavior.sync();
            invalidate();
            return -1;
        }
        else  {
            prepareAndEnd();
            return iChooseThis;
        }
    }

    private void prepareAndEnd(){
        bg_behavior.prepare2End=true;
        invalidate();
    }

    int iChooseThis=0;
    public void removeFromParent() {
        ((ViewGroup)getParent()).removeView(this);
    }

    public EffectView(Context context) {
        super(context);
        init();
    }

    public EffectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EffectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    String[] command;
    Property property = new Property();
    EmotionBehavior emo_behavior;
    BackgroundBehavior bg_behavior;
    SourceViewBehavior sour_view_behavior;
    private void init() {
        property.setDp(Tool.getOneDps(getContext()));
        bg_behavior = new BackgroundBehavior(this);
        emo_behavior = new EmotionBehavior(this);
        sour_view_behavior = new SourceViewBehavior(getContext(),this);
    }

    public void destroy() {
        removeFromParent();
        emo_behavior.iconView.setVisibility(VISIBLE);
        emo_behavior.destroy();
        sour_view_behavior.destroy();
        bg_behavior.destroy();
        emo_behavior = null;
        sour_view_behavior = null;
        bg_behavior = null;
    }
    int[] resDrawable;
    public void set(ImageView symbol,View sourceView, float[] pos_in_screen, String[] command, int[] resDrawable) {
        emo_behavior.setIconView(symbol);
        sour_view_behavior.setSourceView(sourceView);
        property.setTouch(pos_in_screen[0], pos_in_screen[1]);
        this.command = command;
        this.resDrawable = resDrawable;
    }
   float getBackgroundAlphaPercent() {
        return bg_behavior.getBackgroundPc();
   }
    class Property {

        float width,height;
        float oneDp;
        RectF rectView;
        float length_dp_50;
        float length_dp_75;
        float length_dp_100;
        void setDp(float oneDp) {
            this.oneDp = oneDp;
            length_dp_50 = oneDp*50;
            length_dp_75 = oneDp*75;
            length_dp_100 = oneDp*100;
        }
        Property() {
            touchX = touchY = -50;

        }
        float touchX,touchY;
        void setTouch(float touchX,float touchY) {
            this.touchX = touchX;
            this.touchY = touchY;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        Log.d("EffectView","onSizeChanged()");
        property.width = getMeasuredWidth();
        property.height= getMeasuredHeight();
        property.rectView= new RectF(0,0,property.width,property.height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bg_behavior.drawBackgroundAlpha(canvas);
        sour_view_behavior.draw(canvas);
        emo_behavior.draw(canvas);
    }

    @Override
    protected void finalize() {
      Log.d("EffectView","finalize");
    }
}
