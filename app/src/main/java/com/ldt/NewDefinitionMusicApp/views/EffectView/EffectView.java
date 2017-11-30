package com.ldt.NewDefinitionMusicApp.views.EffectView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
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
            property.setTouch_runtime(event.getRawX(),event.getRawY());
         //   property.setTouch(event.getRawX(),event.getRawY());
            boolean b =  bg_behavior.sync();
            boolean e = emo_behavior.sync(b);
           if(b||e) invalidate();
            return -1;
        }
        else  {
            prepareAndEnd();
            return iChooseThis();
        }
    }

    private void prepareAndEnd(){
        bg_behavior.prepare2End=true;
        invalidate();
    }

    /**
     *
     * @return -1 means still handler event
     * 0 means cancel
     * 1,2,3... means the result
     */
    private int iChooseThis() {
     return emo_behavior.whichSelected +1;
    }
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
            menu_item_width = 70*oneDp;
            int[] s = Tool.getScreenSize(true);
            width = s[0];
            height= s[1];
            rectView= new C_RectF(0,0,s[0],s[1]);
            mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mShadowPaint.setColor(0x70777777);
            mShadowPaint.setStyle(Paint.Style.FILL);

            mShadowPaint.setMaskFilter(new BlurMaskFilter(BLUR_RADIUS, BlurMaskFilter.Blur.NORMAL));
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
        float[] item_angle;

        float[] getTouch_runtime() {
            return touch_runtime;
        }

        void setTouch_runtime(float x, float y) {
            this.touch_runtime[0] =x ;
            this.touch_runtime[1] = y ;
        }

        private float[] touch_runtime;
        String[] menu_string;
        int[] menu_image_id;
        int menu_number = 0;
        Bitmap menu_item_background;
        Bitmap[] menu_item_bitmap;

        Paint mShadowPaint ,solidPaint;
        float mShadowDepth = 5;
        float BLUR_RADIUS = 5;
        void setMenu(String[] menu_string,int[] menu_image_id) {
            this.menu_string = menu_string;
            this.menu_image_id = menu_image_id;
            this.menu_number = menu_image_id.length;
            delta_width = new float[menu_number];
            menu_item_bitmap = new Bitmap[menu_number];
            Resources resources = getResources();
            menu_item_background = getMenu_item_background();
            for (int i = 0; i < menu_number;i++) {
                delta_width[i] = menu_item_width;
                menu_item_bitmap[i] = BitmapFactory.decodeResource(resources,menu_image_id[i]);
            }
            setItemPos();
        }
        Bitmap getMenu_item_background() {
            Bitmap bitmap = Bitmap.createBitmap((int)menu_item_width,(int)menu_item_width, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
         //   c.drawColor(0xff5CD315);
            solidPaint.setColor(Color.WHITE);
            solidPaint.setStyle(Paint.Style.FILL);

           // c.translate(BLUR_RADIUS, BLUR_RADIUS);
            //     c.drawRoundRect(sShadowRectF, sShadowRectF.width() / 40,
//                sShadowRectF.height() / 40, mShadowPaint);
            float miw_2 = menu_item_width/2;
            c.drawCircle(miw_2-1,miw_2+3,miw_2-20,mShadowPaint);
            c.drawCircle(miw_2-1,miw_2,miw_2-20,solidPaint);
            return bitmap;
        }
        boolean  above,left;
        void setItemPos() {
            item_pos = new C_PointF[menu_number];
            delta_item_pos = new C_PointF[menu_number];
            item_angle = new float[menu_number];
            // touchX, touchY
            above = menuIsAboveOrBelow();
            left = (touchX<width/2);
            float measureDegree = getTotalAngle();
            item_pos[0] = getFirstPoint(measureDegree,left,above);
            float first_deg = touch.fromCPointFToDegree_From0h(item_pos[0]);
            Log.d("EffectView","first_deg = "+first_deg);
            setFinalItemAngle(above,left,first_deg);
            setFinalItemPos();
        }
        private void setFinalItemAngle(boolean above, boolean left, float first_deg) {
            item_angle[0] = first_deg;
            if      ((above&&!left)
                    ||   // căn ngựoc chiều kim đồng hồ
                    (!above&&left))
            {

                for(int i=1;i<menu_number;i++)
                    item_angle[i] = first_deg - i* eachAngle;
            }
            else { // căn xuoi chiều kim đồng
                for(int i=1;i<menu_number;i++)
                    item_angle[i] = first_deg + i* eachAngle;

            }
        }
        private void setFinalItemPos() {
            for(int i=1;i<menu_number;i++)
                item_pos[i] = touch.getPointAround(menu_satellite_radius,item_angle[i]);

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

        final float eachAngle =40.0f;

        private float getTotalAngle() {
            return eachAngle *(menu_number-1);
        }

        private float getMinDistance() {
            return menu_satellite_radius + menu_item_width/2 +length_dp_50;
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
