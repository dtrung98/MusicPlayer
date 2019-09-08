package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.ldt.musicr.R;

public class RoundSeeThroughTextView extends SeeThroughTranslucentTextView {
    private static final String TAG = "RoundSeeThroughTextView";
    public RoundSeeThroughTextView(Context context) {
        super(context);
        init();
    }

    public RoundSeeThroughTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundSeeThroughTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    float oneDp =1;
    private void init() {
        oneDp = getResources().getDimension(R.dimen.oneDP);
        setWillNotDraw(false);
    }
    RoundDrawable drawable;
    class RoundDrawable extends Drawable {
        Path path=null;
        Paint paint;
        int color;
        public void setColor(int color) {
            this.color = color;
        }
        RoundDrawable(int color ) {
            this.color = color;
        }
        @Override
        public void draw(@NonNull Canvas canvas) {
//            if(data ==null) {
//                data = BitmapEditor.RoundedRect(0,0,getBounds().width(),getBounds().height(),15,15,false);
//                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//
//                paint.setStyle(Paint.Style.FILL);
//            }
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
//            canvas.drawPath(data,paint);
            canvas.drawRoundRect(0,0,getBounds().width(),getBounds().height(),4*oneDp,4*oneDp,paint);
        }

        @Override
        public void setAlpha(int i) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }
    @Override
    public void setBackgroundColor(int color) {
        if(drawable==null) {
            drawable = new RoundDrawable(color);
        }
        else drawable.setColor(color);
       setBackgroundDrawable(drawable);
    }

}
