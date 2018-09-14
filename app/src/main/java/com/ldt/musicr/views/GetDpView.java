package com.ldt.musicr.views;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ldt.musicr.InternalTools.Tool;

public class GetDpView extends View {
    private static final String TAG = "GettingDpView";
    public GetDpView(Context context) {
        super(context);
    }

    public GetDpView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GetDpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float oneDp = getWidth()/100.0f;
        Tool.setOneDps(getWidth()/100.0f);
    }
}
