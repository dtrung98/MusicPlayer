package com.ldt.NewDefinitionMusicApp.views.EffectView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

import android.util.Log;
import android.widget.ImageView;

import com.ldt.NewDefinitionMusicApp.InternalTools.ImageEditor;
import com.ldt.NewDefinitionMusicApp.MediaData.FormatDefinition.C_RectF;

import static android.view.View.VISIBLE;


/**
 * Created by trung on 11/2/2017.
 */

public class EmotionBehavior {
    private final static String Tag = "EmotionBehavior";
    private Shader gradient_shadow_shader;
    private Paint gradient_shadow_Paint;
    private Paint solid_Paint;
    ImageView iconView;
    private Bitmap icon_remix,icon_org;
    private float wish_width;
    private C_RectF from_rect;
    private boolean haveIcon = false;
    private EffectView.Property attr;
    private EffectView effectView;
    public void invalidate() {
        effectView.invalidate();
    }

    int whichSelected = -1;
    public float deltaSelected;
    EmotionBehavior(EffectView effectView)
    {
        this.effectView = effectView;
        attr = effectView.property;
        wish_width = attr.length_dp_50/2;
        from_rect = new C_RectF(0,0,0,0);
        gradient_shadow_shader = new RadialGradient(0,0, attr.length_dp_50/4, 0x10ffffff,0x50ffffff, Shader.TileMode.MIRROR);
        gradient_shadow_Paint = new Paint();
        gradient_shadow_Paint.setAntiAlias(true);
        gradient_shadow_Paint.setShader(gradient_shadow_shader);

        solid_Paint= new Paint();
        solid_Paint.setAntiAlias(true);
        solid_Paint.setColor(0xaaaaaaaa);
    }
    private float biggerForSelected(int whichNewItem, double distance) {
        if(whichNewItem==-1) return 0;
        float ratio =((float)( 1 - Math.abs(1 - distance / attr.menu_satellite_radius)));
        // 0.7 mean 0.3 >> 0.7,
        // 0.3 mean 0.7 >> 0.3,
        // 1.3 mean 0.3 >> 0.7
        if(ratio<=2/3) return 0;
        return  ( ratio - 2/3);
    }
    private boolean doAlgorithm() {
        float[] runtime = attr.getTouch_runtime();
        float cur_x = runtime[0],
                cur_y = runtime[1];
        float angle = attr.touch.fromCPointFToDegree_From0h(cur_x,cur_y);
        int whichNewItem =-1;
        for(int i = 0; i< attr.menu_number; i++)
            if ( attr.item_angle[i] - attr.eachAngle/2<=angle && angle < attr.item_angle[i] + attr.eachAngle/2) {
            // angle of each item        angle range of each item
            whichNewItem = i;
            break;
            }

        double distance  = attr.touch.getDistance(cur_x,cur_y);
      deltaSelected = biggerForSelected(whichNewItem,distance); // delta of size for the selected item
        Log.d(Tag,"which Selected  = "+ whichNewItem);
        whichSelected = whichNewItem;
      return true;
    }
    boolean sync(boolean b) {
       return doAlgorithm();
    }
    void set_source(float x, float y, float w, float h) {
       from_rect.x = x;
       from_rect.y = y;
       from_rect.width =w;
       from_rect.height = h;
    }
    public void draw(Canvas canvas) {
        draw_symbol_icon(canvas,solid_Paint,gradient_shadow_Paint);
        draw_menu_item(canvas,solid_Paint);
        draw_touch_runtime(canvas,solid_Paint);
    }
    private void draw_menu_item(Canvas canvas, Paint solid_Paint) {
        float pc = effectView.getBackgroundAlphaPercent();
        if (attr.menu_number <= 0||pc<=0) return;


        //  Paint.Style style = solid_Paint.getStyle();
        // solid_Paint.setStyle(Paint.Style.STROKE);
        // canvas.drawRect(attr.length_dp_50,0,attr.width- attr.length_dp_50,attr.height,solid_Paint);
        canvas.translate(attr.touchX, attr.touchY);


        int alpha  = solid_Paint.getAlpha();
        float alpha_after = (pc>1) ? 255 : pc*255;
        solid_Paint.setAlpha((int) alpha_after);
       double padding = 0.5f/Math.sqrt(2);
        for (int i = 0; i < attr.menu_number; i++) {
            canvas.save();
            if(i==whichSelected) canvas.scale(pc+deltaSelected,pc+ deltaSelected);
            else  canvas.scale(pc,pc);
            float centerX = attr.item_pos[i].x - attr.touchX;
            float centerY = attr.item_pos[i].y - attr.touchY;
            float cur_x = centerX*pc - attr.menu_item_width/2 ;
            float cur_y = centerY*pc - attr.menu_item_width/2;

            canvas.drawBitmap(attr.menu_item_background,cur_x,cur_y,solid_Paint);
            float _distance = (float) (attr.menu_item_width*padding/2);
            float des_l = centerX*pc- _distance,
                    des_t =centerY*pc - _distance,
                    des_r = des_l + 2*_distance,
                     des_b =des_t + 2*_distance;
          //  canvas.drawRect(des_l,des_t,des_r,des_b,solid_Paint);
            canvas.drawBitmap(
                    attr.menu_item_bitmap[i],
                    new Rect(0,0, attr.menu_item_bitmap[i].getWidth(), attr.menu_item_bitmap[i].getHeight()),
                    new Rect(
                            (int)  des_l
                            ,(int) des_t
                            ,(int) des_r
                            ,(int) des_b)
                    ,solid_Paint);
            canvas.restore();

            }
       // canvas.translate(0,0);
        solid_Paint.setAlpha(alpha);


    }


    private void draw_circle_fill_n_stroke(Canvas canvas, Paint solid_Paint,float _stroke, float R, float x, float y, int _fillColor,int _strokeColor) {
        Paint.Style style = solid_Paint.getStyle();
        float stroke = solid_Paint.getStrokeWidth();
        int color = solid_Paint.getColor();
        solid_Paint.setColor(_fillColor);
        solid_Paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x,y,R,solid_Paint);
        solid_Paint.setColor(_strokeColor);
        solid_Paint.setStyle(Paint.Style.STROKE);
        solid_Paint.setStrokeWidth(_stroke);
        canvas.drawCircle(x,y,R,solid_Paint);
        solid_Paint.setStrokeWidth(stroke);
        solid_Paint.setStyle(style);
        solid_Paint.setColor(color);
    }
    private void draw_circle_fill(Canvas canvas, Paint solid_Paint, float R, float x, float y, int _color) {
        Paint.Style style = solid_Paint.getStyle();
        float stroke = solid_Paint.getStrokeWidth();
        int color = solid_Paint.getColor();
        solid_Paint.setColor(_color);
        solid_Paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x,y,R,solid_Paint);
        solid_Paint.setStrokeWidth(stroke);
        solid_Paint.setStyle(style);
        solid_Paint.setColor(color);
    }
    private void draw_circle_stroke(Canvas canvas,Paint solid_Paint, float R,float x,float y,float _stroke,int _color) {
        Paint.Style style = solid_Paint.getStyle();
        float stroke = solid_Paint.getStrokeWidth();
        int color = solid_Paint.getColor();
        solid_Paint.setColor(_color);
        solid_Paint.setStyle(Paint.Style.STROKE);
        solid_Paint.setStrokeWidth(_stroke);
        canvas.drawCircle(x,y,R,solid_Paint);
        solid_Paint.setStrokeWidth(stroke);
        solid_Paint.setStyle(style);
        solid_Paint.setColor(color);
    }
    private void draw_touch_runtime(Canvas canvas, Paint solid_Paint) {
        float[] runtime = attr.getTouch_runtime();
        if(runtime[0]==-1) return;
        draw_circle_stroke(canvas,solid_Paint,20* attr.oneDp,runtime[0],runtime[1],6* attr.oneDp,0x70ffffff);
    }
    private void draw_symbol_icon(Canvas canvas,Paint solid_Paint_X,Paint gradient_shadow_Paint_X)
    {
        if(!haveIcon) return;
        canvas.save();
        float pc = effectView.getBackgroundAlphaPercent();
        if(pc==1) {
            canvas.translate(attr.touchX, attr.touchY);
            solid_Paint_X.setColor(Color.WHITE);
            solid_Paint_X.setStyle(Paint.Style.FILL);
            canvas.drawCircle(0, 0, attr.length_dp_50 / 4, solid_Paint_X); // vẽ nền trắng
            canvas.drawBitmap(icon_remix, -attr.length_dp_50 / 4, -attr.length_dp_50 / 4, null); // vẽ ảnh
            canvas.drawCircle(0, 0, attr.length_dp_50 / 4, gradient_shadow_Paint_X); // đổ bóng

            solid_Paint_X.setColor(0xaaaaaaaa);
            solid_Paint_X.setStyle(Paint.Style.STROKE);
            solid_Paint_X.setStrokeWidth(attr.oneDp * 1.5f);
            canvas.drawCircle(0, 0, attr.length_dp_50 / 4, solid_Paint_X); // áp
        }
        else {
            float dis_x = attr.touchX - from_rect.x-from_rect.width/2;
            float dis_y = attr.touchY - from_rect.y-from_rect.height/2;

            float tyLe_w = wish_width/from_rect.width - 1;
            float tyLe_h = wish_width/from_rect.height - 1;
            float scale_x = 1 + tyLe_w*pc
                    ,scale_y = 1 + tyLe_h*pc;
            canvas.translate(from_rect.x+pc*dis_x,from_rect.y+pc*dis_y);

            canvas.scale(scale_x,scale_y,from_rect.width/2,from_rect.height/2);
            iconView.draw(canvas);
        }
        canvas.restore();
    }
    void setIconView(ImageView iconView)
    {
        if(iconView==null) {haveIcon = false; return;}
        this.iconView = iconView;

        icon_org = ((BitmapDrawable)iconView.getDrawable()).getBitmap();
        if(icon_org!=null) haveIcon = true;
        if(haveIcon) {
            if(icon_remix !=null ) icon_remix.recycle();
            icon_remix = ImageEditor.getResizedBitmap(icon_org,(int) attr.length_dp_50/2,(int) attr.length_dp_50/2);
        }
    }

    void destroy() {
        if(haveIcon)iconView.setVisibility(VISIBLE);
        gradient_shadow_shader = null;
        gradient_shadow_Paint = null;
        solid_Paint= null;
        effectView = null;
        attr = null;
    }
}
