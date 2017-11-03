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

/**
 * Created by trung on 11/2/2017.
 */

public class EmotionBehavior {
    private Shader gradient_shadow_shader;
    private Paint gradient_shadow_Paint;
    private Paint solid_Paint;
    ImageView iconView;
    private Bitmap icon;
    private float r=0;
    private PointF ic_symbol_position;
    private PointF ic_source_position;
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
        r= property.length_dp_50;
        ic_symbol_position = new PointF(0,0);
        ic_source_position = new PointF(0,0);
        gradient_shadow_shader = new RadialGradient(0,0,property.length_dp_50/4, 0x10ffffff,0x50ffffff, Shader.TileMode.MIRROR);
        gradient_shadow_Paint = new Paint();
        gradient_shadow_Paint.setAntiAlias(true);
        gradient_shadow_Paint.setShader(gradient_shadow_shader);

        solid_Paint= new Paint();
        solid_Paint.setAntiAlias(true);
        solid_Paint.setColor(0xaaaaaaaa);
    }
    void sync() {
            ic_symbol_position.set(property.touchX,property.touchY);
    }
    public void set_source_position(float x,float y) {
        if (ic_source_position.x!=x|| ic_source_position.y!=y) {
            ic_source_position.set(x,y);
        }
    }
    public void draw(Canvas canvas) {
        Log.d("EffectViewHolder",ic_symbol_position.x+" | "+ic_symbol_position.y);
        draw_symbol_icon(canvas,solid_Paint,gradient_shadow_Paint);
    }
    private void draw_symbol_icon(Canvas canvas,Paint solid_Paint_X,Paint gradient_shadow_Paint_X)
    {
        if(!haveIcon) return;
        canvas.save();
        canvas.translate(ic_symbol_position.x, ic_symbol_position.y);

        solid_Paint_X.setColor(Color.WHITE);
        solid_Paint_X.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0,0,property.length_dp_50/4,solid_Paint_X); // vẽ nền trắng

        canvas.drawBitmap(icon,-property.length_dp_50/4,-property.length_dp_50/4,null); // vẽ ảnh
        canvas.drawCircle(0,0,property.length_dp_50/4,gradient_shadow_Paint_X); // đổ bóng

        solid_Paint_X.setColor(0xaaaaaaaa);
        solid_Paint_X.setStyle(Paint.Style.STROKE);
        solid_Paint_X.setStrokeWidth(property.oneDp*1.5f);
        canvas.drawCircle(0,0,property.length_dp_50/4,solid_Paint_X); // áp
    }
    void setIconView(ImageView iconView)
    {
        this.iconView = iconView;
        Bitmap temp = ((BitmapDrawable)iconView.getDrawable()).getBitmap();
        if(temp!=null) haveIcon = true;
        if(haveIcon) {
            if(icon!=null ) icon.recycle();
            icon = ImageEditor.getResizedBitmap(temp,(int) property.length_dp_50/2,(int) property.length_dp_50/2);
            invalidate();

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
