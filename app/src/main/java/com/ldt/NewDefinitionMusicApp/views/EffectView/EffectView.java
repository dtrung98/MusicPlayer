package com.ldt.NewDefinitionMusicApp.views.EffectView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldt.NewDefinitionMusicApp.InternalTools.Tool;
import com.ldt.NewDefinitionMusicApp.MediaData.FormatDefinition.C_PointF;
import com.ldt.NewDefinitionMusicApp.MediaData.FormatDefinition.C_RectF;

import org.jetbrains.annotations.Contract;


/**
 * Created by trung on 10/17/2017.
 */

public class EffectView extends View {

    public int handlingEvent(View v, MotionEvent event)
    {
        if(event.getAction()!=MotionEvent.ACTION_UP) {
            property.touch_runtime[0] = event.getRawX();
            property.touch_runtime[1] = event.getRawY();
         //   property.setTouch(event.getRawX(),event.getRawY());
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

    Property property = new Property();
    EmotionBehavior emo_behavior;
    BackgroundBehavior bg_behavior;
    SourceViewBehavior sour_view_behavior;

    private void init() {
        property.setDp(Tool.getOneDps(getContext()));

        bg_behavior = new BackgroundBehavior(this);
        emo_behavior = new EmotionBehavior(this);
        sour_view_behavior = new SourceViewBehavior(this);
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

    public void set(ImageView symbol,View sourceView, float[] pos_in_screen, String[] menu_string, int[] menu_image) {
        emo_behavior.setIconView(symbol);
        sour_view_behavior.setSourceView(sourceView);
        property.setTouch(pos_in_screen[0], pos_in_screen[1]);
        property.setMenu(menu_string,menu_image);
    }
   float getBackgroundAlphaPercent() {
        return bg_behavior.getBackgroundPc();
   }
    class Property {

        float width,height;
        float oneDp;
        C_RectF rectView;
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
            touchX = touchY = -1;
            touch_runtime = new float[] {-1,-1};
            oneDp = Tool.getOneDps(getContext());
            menu_satellite_radius = 100*oneDp;
            menu_item_width = 25*oneDp;
            int[] s = Tool.getScreenSize(true);
            width = s[0];
            height= s[1];
            rectView= new C_RectF(0,0,s[0],s[1]);
        }
        float touchX,touchY;
        C_PointF touch;
        void setTouch(float touchX,float touchY) {
            this.touchX = touchX;
            this.touchY = touchY;
            touch = new C_PointF(touchX,touchY);
        }
        final float menu_satellite_radius; //
        final float menu_item_width;
        float[] delta_width;
        C_PointF[] delta_item_pos;
        C_PointF[] item_pos;
        float[] touch_runtime;
        String[] menu_string;
        int[] menu_image_id;
        int menu_number = 0;
        Bitmap[] menu_bitmap;

        void setMenu(String[] menu_string,int[] menu_image_id) {
            this.menu_string = menu_string;
            this.menu_image_id = menu_image_id;
            this.menu_number = menu_image_id.length;
            delta_width = new float[menu_number];
            menu_bitmap = new Bitmap[menu_number];
            Resources resources = getResources();
            for (int i = 0; i < menu_number;i++) {
                delta_width[i] = menu_item_width;
                menu_bitmap[i] = BitmapFactory.decodeResource(resources,menu_image_id[i]);
            }
            setItemPos();
        }
        void setItemPos() {
            item_pos = new C_PointF[menu_number];
            delta_item_pos = new C_PointF[menu_number];
            // touchX, touchY
            Boolean above = menuIsAboveOrBelow();
            Boolean left = (touchX<width/2);
            float measureDegree = getTotalDegree();
            item_pos[0] = getFirstPoint(measureDegree,left,above);
            float first_deg = touch.calculateDegree(item_pos[0]) - 90;
            Log.d("EffectView","first_deg = "+first_deg);
        }

        private C_PointF getFirstPoint(float measureDegree,boolean left,boolean above) {
            float curDeg = measureDegree/2;
            if(left) curDeg = -curDeg;
            C_PointF measure =touch.getPointAround(menu_satellite_radius,curDeg);
       //     Log.d("EffectView",width+"");
      //      Log.d("EffectView","Before : ( "+((left) ? "left": "right")+", "+curDeg +" ) "+measure.x+" : "+measure.y);
        //   if(true) return measure;

            if(measure.x<length_dp_50||measure.x > width - length_dp_50) {
                if (left)
                    measure.x = length_dp_50;
                else
                    measure.x = width - length_dp_50;
                float temp1 = (float) Math.sqrt(menu_satellite_radius * menu_satellite_radius - Math.pow(touchX - measure.x, 2));
                measure.y = (above) ? touchY - temp1 : touchY + temp1;
            } else if (!above) {
                measure.y = 2*touchY - measure.y;
            }
        //    Log.d("EffectView","After : "+measure.x+" : "+measure.y);
            return measure;
        }

        private final float eachDegree =40.0f;

        private float getTotalDegree() {
            return eachDegree*(menu_number-1);
        }

        private float getMinDistance() {
            return menu_satellite_radius + menu_item_width/2 +100*oneDp;
        }

        @Contract(pure = true)
        private boolean menuIsAboveOrBelow() {
            return touchY>getMinDistance();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {

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
