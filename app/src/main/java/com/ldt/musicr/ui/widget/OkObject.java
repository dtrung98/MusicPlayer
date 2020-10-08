package com.ldt.musicr.ui.widget;

/**
 * Created by trung on 11/10/2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class OkObject extends View {
        private static final Paint  p  = new Paint();
        private static final Paint  ps = new Paint();
        private static final Path   t  = new Path();
        private static final Matrix m  = new Matrix();
        private static float od;
        protected static ColorFilter cf = null;

    public OkObject(Context context) {
        super(context);
    }

    public OkObject(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OkObject(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
         *  IMPORTANT: Due to the static usage of this class this
         *  method sets the tint color statically. So it is highly
         *  recommended to call the clearColorTint method when you
         *  have finished drawing.
         *
         *  Sets the color to use when drawing the SVG. This replaces
         *  all parts of the drawable which are not completely
         *  transparent with this color.
         */
        public static void setColorTint(int color){
            cf = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        public static void clearColorTint(int color){
            cf = null;
        }

    @Override
    protected void onDraw(Canvas canvas) {
      draw(canvas,getMeasuredWidth(),getMeasuredHeight());
    }

    public static void draw(Canvas c, int w, int h){
            draw(c, w, h, 0, 0);
        }

        public static void draw(Canvas c, int w, int h, int dx, int dy){
            float ow = 64f;
            float oh = 64f;

            od = (w / ow < h / oh) ? w / ow : h / oh;

            r();
            c.save();
            c.translate((w - od * ow) / 2f + dx, (h - od * oh) / 2f + dy);

            m.reset();
            m.setScale(od, od);

            c.save();
            ps.setColor(Color.argb(0,0,0,0));
            ps.setStrokeCap(Paint.Cap.BUTT);
            ps.setStrokeJoin(Paint.Join.MITER);
            ps.setStrokeMiter(4.0f*od);
            c.scale(1.0f,1.0f);
            c.save();
            p.setColor(Color.parseColor("#444444"));
            c.save();
            p.setColor(Color.argb(0,0,0,0));
            ps.setColor(Color.parseColor("#444444"));
            ps.setStrokeWidth(2.0f*od);
            ps.setStrokeMiter(10.0f*od);
            t.reset();
            t.moveTo(54.0f,24.0f);
            t.cubicTo(55.3f,27.1f,56.0f,30.5f,56.0f,34.0f);
            t.cubicTo(56.0f,48.4f,44.4f,60.0f,30.0f,60.0f);
            t.cubicTo(15.6f,60.0f,4.0f,48.4f,4.0f,34.0f);
            t.cubicTo(4.0f,19.6f,15.6f,8.0f,30.0f,8.0f);
            t.cubicTo(35.1f,8.0f,39.9f,9.5f,43.9f,12.0f);
            t.transform(m);
            c.drawPath(t, p);
            c.drawPath(t, ps);
            c.restore();
            r(4,6,7,8,3);
            p.setColor(Color.argb(0,0,0,0));
            ps.setColor(Color.parseColor("#444444"));
            ps.setStrokeWidth(2.0f*od);
            ps.setStrokeMiter(10.0f*od);
            c.save();
            ps.setStrokeCap(Paint.Cap.SQUARE);
            t.reset();
            t.moveTo(20.0f,26.0f);
            t.lineTo(30.0f,36.0f);
            t.lineTo(60.0f,6.0f);
            t.transform(m);
            c.drawPath(t, p);
            c.drawPath(t, ps);
            c.restore();
            r(4,6,7,8,3,5,2,0,1);
            ps.setStrokeCap(Paint.Cap.SQUARE);
            c.restore();
            r(4,6,7,8);
            p.setColor(Color.argb(0,0,0,0));
            ps.setColor(Color.parseColor("#444444"));
            ps.setStrokeWidth(2.0f*od);
            ps.setStrokeCap(Paint.Cap.SQUARE);
            ps.setStrokeMiter(10.0f*od);
            c.restore();
            r();

            c.restore();
        }

        private static void r(Integer... o){
            p.reset();
            ps.reset();
            if(cf != null){
                p.setColorFilter(cf);
                ps.setColorFilter(cf);
            }
            p.setAntiAlias(true);
            ps.setAntiAlias(true);
            p.setStyle(Paint.Style.FILL);
            ps.setStyle(Paint.Style.STROKE);
            for(Integer i : o){
                switch (i){
                    case 0: ps.setStrokeWidth(2.0f*od); break;
                    case 1: ps.setStrokeMiter(10.0f*od); break;
                    case 2: ps.setColor(Color.parseColor("#444444")); break;
                    case 3: p.setColor(Color.parseColor("#444444")); break;
                    case 4: ps.setColor(Color.argb(0,0,0,0)); break;
                    case 5: p.setColor(Color.argb(0,0,0,0)); break;
                    case 6: ps.setStrokeCap(Paint.Cap.BUTT); break;
                    case 7: ps.setStrokeJoin(Paint.Join.MITER); break;
                    case 8: ps.setStrokeMiter(4.0f*od); break;
                }
            }
        }
    }
