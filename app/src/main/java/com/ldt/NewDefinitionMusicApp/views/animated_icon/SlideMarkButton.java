package com.ldt.NewDefinitionMusicApp.views.animated_icon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ldt.NewDefinitionMusicApp.InternalTools.ImageEditor;
import com.ldt.NewDefinitionMusicApp.InternalTools.Tool;

/**
 * Created by trung on 12/2/2017.
 */

public class SlideMarkButton extends View {

    public SlideMarkButton(Context context) {
        super(context);
        init();
    }

    public SlideMarkButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMarkButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint solidPaint;
    private int[] measuredSize,realSize;
    private int[] wishingSize_InDP;
    private int[] wishingSize;
    private int color = 0;

    private int oneDp = 0;
    private int[] wishingPos;

    public void init() {
        oneDp = Tool.getOneDps(getContext());

        solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        solidPaint.setStyle(Paint.Style.FILL);
        color = 0xFF888888;
        solidPaint.setColor(color);
        measuredSize = new int[2];
        realSize = new int[2];
        wishingSize_InDP = new int[2];
        wishingSize = new int[2];
        wishingPos = new int[2];
        measuredSize[0] = getMeasuredWidth();
        measuredSize[1] = getMeasuredHeight();
        realSize[0] = getWidth();
        realSize[1] = getHeight();

        wishingSize_InDP[0] = 50;
        wishingSize_InDP[1] = 50;
        wishingSize[0] = wishingSize_InDP[0]*oneDp;
        wishingSize[1] = wishingSize_InDP[1]*oneDp;

        wishingPos[0] = measuredSize[0]/2 - wishingSize[0];
        wishingPos[1] = measuredSize[1]/2 - wishingSize[1];

        path = ImageEditor.RoundedRect(wishingPos[0],wishingPos[1],wishingPos[0]+ wishingSize[0],wishingPos[1]+wishingSize[1],10*oneDp,10*oneDp,false);

    }
    Path path;
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path,solidPaint);

        }
}
