package com.ldt.musicr.views.EffectView;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.ldt.musicr.InternalTools.ImageEditor;

/**
 * Created by trung on 11/3/2017.
 */

public class SourceViewBehavior {
    private View sourceView;
    private int[] local;
    private int[] size;

    private final float MAX_R_XY = 10;
    private final float maxRy;
    private final float maxRx;
    private Paint mPaintFill;
    private Paint mPaintStroke;
    private EffectView effectView;
    SourceViewBehavior(EffectView effectView) {
        local = new int[]{0,0};
        size = new int[] {0,0};
        this.effectView = effectView;

        maxRx = maxRy = effectView.property.oneDp* MAX_R_XY;

        mPaintFill = new Paint();
        mPaintFill.setAntiAlias(true);
        mPaintFill.setColor(0xffF3F3F3);
        mPaintFill.setStyle(Paint.Style.FILL);
        BlurMaskFilter bmf = new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID);
        mPaintFill.setMaskFilter(bmf);
        mPaintStroke = new Paint();
        mPaintStroke.setAntiAlias(true);
        mPaintStroke.setColor(0xff444444);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setStrokeWidth(1);

    }
    void setSourceView(View sourceView) {
        this.sourceView = sourceView;
        sourceView.getLocationOnScreen(local);
        size[0] = sourceView.getMeasuredWidth();
        size[1] = sourceView.getMeasuredHeight();
        sourceView.setVisibility(View.INVISIBLE);
    }
    void draw(Canvas canvas) {
        if(sourceView==null) return;
        canvas.save();
        canvas.translate(local[0],local[1]);
        float pc = effectView.getBackgroundAlphaPercent();
        //int color = mPaintFill.getColor();
       // mPaintFill.setColor(0xffdddddd);
       // canvas.drawRect(0,0,size[0],size[1],mPaintFill);
        canvas.scale(1-0.06f*pc,1-0.06f*pc,size[0]/2,size[1]/2);
       // mPaintFill.setColor(color);
  //     canvas.drawPath(ImageEditor.RoundedRect(0,0,size[0],size[1],maxRx,maxRy,false), mPaintFill);
        int alpha = mPaintStroke.getAlpha();
        mPaintStroke.setAlpha(50+(int)pc*205);
        canvas.drawPath(ImageEditor.RoundedRect(0,0,size[0],size[1],maxRx*pc,maxRy*pc,false),mPaintStroke);
        sourceView.draw(canvas);
        mPaintStroke.setAlpha(alpha);
        canvas.restore();
    }
    void destroy() {
        sourceView.setVisibility(View.VISIBLE);
        sourceView = null;
        effectView = null;
        mPaintFill = null;
    }
}
