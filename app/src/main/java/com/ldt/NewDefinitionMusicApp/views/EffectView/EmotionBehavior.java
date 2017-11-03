package com.ldt.NewDefinitionMusicApp.views.EffectView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

import android.util.Log;

import android.widget.ImageView;

import com.ldt.NewDefinitionMusicApp.InternalTools.ImageEditor;
import com.ldt.NewDefinitionMusicApp.InternalTools.Tool.C_RectF;

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
    private PointF touchPos;
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
        wish_width = property.length_dp_50/4;
        touchPos = new PointF(0,0);
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
            touchPos.set(property.touchX,property.touchY);
    }
    public void set_source(float x,float y,float w,float h) {
       from_rect.x = x;
       from_rect.y = y;
       from_rect.width =w;
       from_rect.height = h;
    }
    public void draw(Canvas canvas) {
        Log.d("EffectViewHolder", touchPos.x+" | "+ touchPos.y);
        draw_symbol_icon(canvas,solid_Paint,gradient_shadow_Paint);
    }
    private void draw_symbol_icon(Canvas canvas,Paint solid_Paint_X,Paint gradient_shadow_Paint_X)
    {
        if(!haveIcon) return;
        canvas.save();
        float pc = effectView.getBackgroundAlphaPercent();
        if(pc==1) {
            canvas.translate(touchPos.x, touchPos.y);
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

            float dis_x = touchPos.x - from_rect.x;
            float dis_y = touchPos.y - from_rect.y;

            canvas.translate(from_rect.x+pc*dis_x,from_rect.y+pc*dis_y);
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
