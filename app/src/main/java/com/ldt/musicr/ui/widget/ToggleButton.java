package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ldt.musicr.R;
import com.ldt.musicr.util.BitmapEditor;
import com.ldt.musicr.coordinate.MCoordinate.MPoint;

import com.ldt.musicr.util.Tool;

/**
 * Created by trung on 12/2/2017.
 */

public class ToggleButton extends View {
    public static final String TAG = "ToggleButton";

    public ToggleButton(Context context) {
        super(context);
        init(null);
    }

    public ToggleButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ToggleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public ToggleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private Paint solidPaint;
    private int[] measuredSize,realSize;
    private int[] wishingSize_InDP;
    private int[] wishingSize;
    private int color = 0;
    private int[] center = new int[2];
    private float oneDp = 0;
    private int[] wishingPos;

    private double canhA;
    private double gocLech,phuGocLech;
    private double doRongNet;
    private RectF rectF;

    public void setColor(int color) {
        this.color = color;
        if(solidPaint!=null) {
            solidPaint.setColor(color);
            invalidate();
        }
    }
    public void init(AttributeSet attr) {
        if(attr!=null) {
            TypedArray t = getContext().obtainStyledAttributes(attr, R.styleable.ToggleButton);
            color = t.getColor(R.styleable.ToggleButton_maskColor, getResources().getColor(R.color.FlatWhite));
            t.recycle();
        }
        oneDp = Tool.getOneDps(getContext());
        canhA = oneDp*20;
        gocLech = -30*Math.PI/180; // -45 -> 45
        phuGocLech = Math.PI/2  - Math.abs(gocLech);
        doRongNet = oneDp*4;

        solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        solidPaint.setStyle(Paint.Style.FILL);
        
        solidPaint.setColor(color);
        measuredSize = new int[2];
        realSize = new int[2];
        wishingSize_InDP = new int[2];
        wishingSize = new int[2];
        wishingPos = new int[2];
    }

    Path path;
    boolean isSet = false;
    double gocDuong = 1;
    MPoint p1 = new MPoint(),p2 = new MPoint(),p3 = new MPoint();
    @Override
    protected void onDraw(Canvas canvas) {
        if(rectF == null) {
            float h = 8*oneDp;
            float w = 40*oneDp;
            rectF = new RectF(getWidth()/2 - w/2,getHeight()/2-h/2,getWidth()/2 + w/2,getHeight()/2+h/2);
        }
        canvas.drawPath(BitmapEditor.RoundedRect(rectF.left,rectF.top,rectF.right,rectF.bottom,rectF.height()/2,rectF.height()/2,false),solidPaint);
        }
}
