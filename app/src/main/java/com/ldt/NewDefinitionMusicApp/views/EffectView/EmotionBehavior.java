package com.ldt.NewDefinitionMusicApp.views.EffectView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

import android.widget.ImageView;

import com.ldt.NewDefinitionMusicApp.InternalTools.ImageEditor;
import com.ldt.NewDefinitionMusicApp.MediaData.FormatDefinition.C_RectF;


/**
 * Created by trung on 11/2/2017.
 */

public class EmotionBehavior {
    private Shader gradient_shadow_shader;
    private Paint gradient_shadow_Paint;
    private Paint solid_Paint;
    ImageView iconView;
    private Bitmap icon_remix,icon_org;
    private float wish_width;
    private C_RectF from_rect;
    private boolean haveIcon = false;
    private EffectView.Property property;
    private EffectView effectView;
    public void invalidate() {
        effectView.invalidate();
    }
    EmotionBehavior(EffectView effectView)
    {
        this.effectView = effectView;
        property = effectView.property;
        wish_width = property.length_dp_50/2;
        from_rect = new C_RectF(0,0,0,0);
        gradient_shadow_shader = new RadialGradient(0,0,property.length_dp_50/4, 0x10ffffff,0x50ffffff, Shader.TileMode.MIRROR);
        gradient_shadow_Paint = new Paint();
        gradient_shadow_Paint.setAntiAlias(true);
        gradient_shadow_Paint.setShader(gradient_shadow_shader);

        solid_Paint= new Paint();
        solid_Paint.setAntiAlias(true);
        solid_Paint.setColor(0xaaaaaaaa);
    }
    void sync() {

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
        if(property.menu_number<=0) return;
     //   for(int i=0;i<property.menu_number;i++)
       //     canvas.drawBitmap(property.menu_bitmap[i],200*i,200*i,null);
        canvas.save();
        canvas.drawRect(property.length_dp_50,0,property.width- property.length_dp_50,property.height,solid_Paint);
        canvas.translate(property.item_pos[0].x,property.item_pos[0].y);
        draw_circle_stroke(canvas,solid_Paint,20*property.oneDp,0,0,6*property.oneDp,0x80000000);
        canvas.restore();
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
        if(property.touch_runtime[0]==-1) return;
        draw_circle_stroke(canvas,solid_Paint,20*property.oneDp,property.touch_runtime[0],property.touch_runtime[1],6*property.oneDp,0x70ffffff);
    }
    private void draw_symbol_icon(Canvas canvas,Paint solid_Paint_X,Paint gradient_shadow_Paint_X)
    {
        if(!haveIcon) return;
        canvas.save();
        float pc = effectView.getBackgroundAlphaPercent();
        if(pc==1) {
            canvas.translate(property.touchX, property.touchY);
            solid_Paint_X.setColor(Color.WHITE);
            solid_Paint_X.setStyle(Paint.Style.FILL);
            canvas.drawCircle(0, 0, property.length_dp_50 / 4, solid_Paint_X); // vẽ nền trắng
            canvas.drawBitmap(icon_remix, -property.length_dp_50 / 4, -property.length_dp_50 / 4, null); // vẽ ảnh
            canvas.drawCircle(0, 0, property.length_dp_50 / 4, gradient_shadow_Paint_X); // đổ bóng

            solid_Paint_X.setColor(0xaaaaaaaa);
            solid_Paint_X.setStyle(Paint.Style.STROKE);
            solid_Paint_X.setStrokeWidth(property.oneDp * 1.5f);
            canvas.drawCircle(0, 0, property.length_dp_50 / 4, solid_Paint_X); // áp
        }
        else {
            float dis_x = property.touchX - from_rect.x-from_rect.width/2;
            float dis_y = property.touchY - from_rect.y-from_rect.height/2;

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
        this.iconView = iconView;

        icon_org = ((BitmapDrawable)iconView.getDrawable()).getBitmap();
        if(icon_org!=null) haveIcon = true;
        if(haveIcon) {
            if(icon_remix !=null ) icon_remix.recycle();
            icon_remix = ImageEditor.getResizedBitmap(icon_org,(int) property.length_dp_50/2,(int) property.length_dp_50/2);
        }
    }

    void destroy() {
        gradient_shadow_shader = null;
        gradient_shadow_Paint = null;
        solid_Paint= null;
        effectView = null;
        property = null;
    }
}
