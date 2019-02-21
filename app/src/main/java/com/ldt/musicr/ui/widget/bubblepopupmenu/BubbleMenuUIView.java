package com.ldt.musicr.ui.widget.bubblepopupmenu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldt.musicr.util.Tool;
import com.ldt.musicr.mediadata.oldmodel.CPointF;
import com.ldt.musicr.mediadata.oldmodel.C_RectF;

import org.jetbrains.annotations.Contract;


/**
 * Created by trung on 10/17/2017.
 */

public class BubbleMenuUIView extends View {
private static final String TAG = "BubbleMenuUIView";
    public int handlingEvent(View v, MotionEvent event)
    {
        if(event.getAction()!=MotionEvent.ACTION_UP) {
            mcAttributes.setTouch_runtime(event.getRawX(),event.getRawY());
         //   mcAttributes.setTouch(event.getRawX(),event.getRawY());
            boolean b =  bg_behavior.sync();
            boolean e = emo_behavior.updateTouchRuntime(b);
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
     * 0 means cancelAndUnBind
     * 1,2,3... means the result
     */
    private int iChooseThis() {
     return emo_behavior.whichSelect() +1;
    }
    public void removeFromParent() {
        ((ViewGroup)getParent()).removeView(this);
    }

    public BubbleMenuUIView(Context context) {
        super(context);
        init();
    }

    public BubbleMenuUIView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleMenuUIView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    MCAttributes mcAttributes = new MCAttributes();

    // Class xử lý hành xử của các icon chọn
    // hiệu ứng khi popup và hide
    // hiệu ứng khi di chuột ...
    BaseBubbleIconDrawable emo_behavior;

    // Class xử lý hành xử của nền
    // nền trắng tăng dần độ trong suốt khi view popup
    // và giảm dần khi ẩn view
    BackgroundBehavior bg_behavior;

    // Class xử lý hành xử của view handler touch event ( view nguồn)
    // ẩn view nguồn và vẽ view nguồn lên canvas của MCBP UI
    SourceViewBehavior sour_view_behavior;

    private void init() {
        mcAttributes.setDp(Tool.getOneDps(getContext()));

        bg_behavior = new BackgroundBehavior(this);
        emo_behavior = new BounceAnimateDrawable(this);
        //emo_behavior = new PlanetAnimateDrawable(this);
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

        sour_view_behavior.setSourceView(sourceView);
        mcAttributes.setTouch(pos_in_screen[0], pos_in_screen[1]);
        mcAttributes.setMenu(menu_string,menu_image);
        emo_behavior.setIconView(symbol);
        emo_behavior.set();
    }
   float getBackgroundAlphaPercent() {
        return bg_behavior.getBackgroundPc();
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
   //   Log.d("EffectView","finalize");
    }


    // Class tính và lưu trữ thông số
    class MCAttributes {
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

        MCAttributes() {
            touchX = touchY = -1;
            touch_runtime = new float[] {-1,-1};
            oneDp = Tool.getOneDps(null);
           Log.d(TAG, "oneDp = " + oneDp);
            menu_satellite_radius = 100*oneDp;
            menu_item_width = 65*oneDp;
            int[] s = Tool.getScreenSize(true);
            width = s[0];
            height= s[1];
            rectView= new C_RectF(0,0,s[0],s[1]);
            mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mShadowPaint.setColor(0x77777777);
            mShadowPaint.setStyle(Paint.Style.FILL);

            mShadowPaint.setMaskFilter(new BlurMaskFilter(BLUR_RADIUS, BlurMaskFilter.Blur.NORMAL));
        }
        float touchX,touchY;
        CPointF touch;
        void setTouch(float touchX,float touchY) {
            this.touchX = touchX;
            this.touchY = touchY;
            touch = new CPointF(touchX,touchY);
        }
        float menu_satellite_radius; //
        float menu_item_width;
        float[] delta_width;
        CPointF[] delta_item_pos;
        CPointF[] item_pos;
        float[] item_angle;

        float[] getTouch_runtime() {
            return touch_runtime;
        }

        void setTouch_runtime(float x, float y) {
            this.touch_runtime[0] =x ;
            this.touch_runtime[1] = y ;
        }

        float[] touch_runtime;
        public String[] menu_string;
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
      //      menu_item_background = getMenu_item_background();
            for (int i = 0; i < menu_number;i++) {
                delta_width[i] = menu_item_width;
                menu_item_bitmap[i] = BitmapFactory.decodeResource(resources,menu_image_id[i]);
            }
            setItemPos();
        }

        boolean  above,left;

        // Hàm quan trọng
        // Tính toán vị trí tâm của các icon
        void setItemPos() {
            item_pos = new CPointF[menu_number];
            delta_item_pos = new CPointF[menu_number];
            item_angle = new float[menu_number];
            // touchX, touchY
            above = menuIsAboveOrBelow();
            left = (touchX<width/2);
            float measureDegree = getTotalAngle();
            item_pos[0] = getFirstPoint(measureDegree,left,above);
            float first_deg = touch.fromCPointFToDegree_From0h(item_pos[0]);
          //  Log.d("EffectView","first_deg = "+first_deg);
            setFinalItemAngle(above,left,first_deg);
            setFinalItemPos();
        }
        private void setFinalItemAngle(boolean above, boolean left, float first_deg) {
            if      ((above&&!left)
                    ||   // căn ngựoc chiều kim đồng hồ
                    (!above&&left))
            {
                for(int i=0;i<menu_number;i++)
                    item_angle[menu_number-1-i] = first_deg - i* eachAngle;
            }
            else { // căn xuoi chiều kim đồng
                for(int i=0;i<menu_number;i++)
                    item_angle[i] = first_deg + i* eachAngle;
            }
            //Log.d(TAG, "item_angle[0] = " + item_angle[0]);
           // Log.d(TAG, "item_angle[1] = " + item_angle[1]);
           // Log.d(TAG, "item_angle[2] = " + item_angle[2]);
        }
        private void setFinalItemPos() {
            for(int i=0;i<menu_number;i++)
                item_pos[i] = touch.getPointAround(menu_satellite_radius,item_angle[i]);

        }

        private CPointF getFirstPoint(float measureDegree, boolean left, boolean above) {
            float curDeg = measureDegree/2;
            if(left) curDeg = -curDeg;
            CPointF measure =touch.getPointAround(menu_satellite_radius,curDeg);
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
}
