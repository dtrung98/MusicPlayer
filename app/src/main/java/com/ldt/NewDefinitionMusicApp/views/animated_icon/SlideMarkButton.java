package com.ldt.NewDefinitionMusicApp.views.animated_icon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.ldt.NewDefinitionMusicApp.InternalTools.MToaDo.MPoint;
import com.ldt.NewDefinitionMusicApp.InternalTools.MToaDo.MLine;

import com.ldt.NewDefinitionMusicApp.InternalTools.Tool;

/**
 * Created by trung on 12/2/2017.
 */

public class SlideMarkButton extends View {
    public static final String TAG = "SlideMarkButton";

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
    private int[] center = new int[2];
    private int oneDp = 0;
    private int[] wishingPos;

    private double canhA;
    private double gocLech,phuGocLech;
    private double doRongNet;
    public void init() {
        oneDp = Tool.getOneDps(getContext());

        canhA = oneDp*20;
        gocLech = -30*Math.PI/180; // -45 -> 45
        phuGocLech = Math.PI/2  - Math.abs(gocLech);
        doRongNet = oneDp*4;

        solidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        solidPaint.setStyle(Paint.Style.FILL);

        color = 0xFFFF9500;
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
        if(!isSet) {
            isSet = true;
            measuredSize[0] = getMeasuredWidth();
            measuredSize[1] = getMeasuredHeight();
            realSize[0] = getWidth();
            realSize[1] = getHeight();

            wishingSize_InDP[0] = 100;
            wishingSize_InDP[1] = 50;
            wishingSize[0] = wishingSize_InDP[0]*oneDp;
            wishingSize[1] = wishingSize_InDP[1]*oneDp;

            wishingPos[0] = measuredSize[0]/2 - wishingSize[0]/2;
            wishingPos[1] = measuredSize[1]/2 - wishingSize[1]/2;
            center[0] = realSize[0]/2;
            center[1] = realSize[1]/2;
          //  path = ImageEditor.RoundedRect(wishingPos[0],wishingPos[1],wishingPos[0]+ wishingSize[0],wishingPos[1]+wishingSize[1],10*oneDp,10*oneDp,false);


        }
        Log.d(TAG,"onDraw");
     //   canvas.drawRect(center[0]- wishingSize[0]/2,center[1] - wishingSize[1]/2,center[0]+ wishingSize[0]/2,center[1] + wishingSize[1]/2,solidPaint);


        path = new Path();
        try {

            path.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        gocDuong = (gocLech<0) ? -1 : 1;
        /*
        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);//y-right corner
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry); //y-x corner
        path.rLineTo(0, heightMinusCorners);
         */
        p1.X = center[0] - canhA *Math.cos(gocLech);
        p1.Y = center[1] - canhA /2*Math.sin(gocLech);
        p2.X = center[0];
        p2.Y = center[1] + canhA /2*Math.sin(gocLech);
        p3.X = center[0] + canhA *Math.cos(gocLech);
        p3.Y = center[1] - canhA /2*Math.sin(gocLech);
        MPoint mp1= new MPoint(p2.X,p2.Y-doRongNet);
        MLine lineInside1 = new MLine(p1,p2);
        MLine lineOut1 = new MLine(lineInside1,mp1);
        MLine lineVG1 = lineInside1.DuongThangVuongGoc(p1);
        MPoint mp2 = lineVG1.GiaoDiem(lineOut1); // diem 2

        MPoint mp4 = new MPoint(p2.X,p2.Y+ doRongNet);  // diem 4
        MLine lineOut2 = new MLine(lineInside1,mp4);
        MPoint mp3 = lineOut2.GiaoDiem(lineVG1); // diem 3

        MLine lineInside2 = new MLine(p2,p3);
        MLine lineOut4 = new MLine(lineInside2,mp1);
        MLine lineOut3 = new MLine(lineInside2,mp4);
        MLine lineVG2 = lineInside2.DuongThangVuongGoc(p3);
        MPoint mp5 = lineVG2.GiaoDiem(lineOut3); // diem 5
        MPoint mp6 = lineVG2.GiaoDiem(lineOut4); // diem 6

        canvas.drawCircle(center[0],center[1],(int)doRongNet,solidPaint);
       // canvas.drawCircle((float)p1.getX(),(float)p1.getY(),(int)doRongNet,solidPaint);
       // canvas.drawCircle((float)p2.getX(),(float)p2.getY(),(int)doRongNet,solidPaint);
       // canvas.drawCircle((float)p3.getX(),(float)p3.getY(),(int)doRongNet,solidPaint);

        canvas.drawCircle((float)mp1.getX(),(float)mp1.getY(),(int)doRongNet,solidPaint);
        canvas.drawCircle((float)mp2.getX(),(float)mp2.getY(),(int)doRongNet,solidPaint);
        canvas.drawCircle((float)mp3.getX(),(float)mp3.getY(),(int)doRongNet,solidPaint);
        canvas.drawCircle((float)mp4.getX(),(float)mp4.getY(),(int)doRongNet,solidPaint);
        canvas.drawCircle((float)mp5.getX(),(float)mp5.getY(),(int)doRongNet,solidPaint);
        canvas.drawCircle((float)mp6.getX(),(float)mp6.getY(),(int)doRongNet,solidPaint);

        path.moveTo((float)mp1.X,(float)mp1.Y);
        path.rLineTo((float) mp2.X,(float) mp2.Y);
        MPoint mpQuad1 = new MPoint(
                p1.X- doRongNet*Math.cos(gocLech),
                p1.Y- doRongNet*Math.sin(gocLech));
        path.rQuadTo((float)(mpQuad1.X),(float)(mpQuad1.Y),(float)(mp3.X),(float)(mp3.Y));
        path.rLineTo((float) mp4.X,(float) mp4.Y);
        path.rLineTo((float) mp5.X,(float) mp5.Y);
        MPoint mpQuad2 = new MPoint(
                p3.X + doRongNet*Math.cos(gocLech),
                p3.Y- doRongNet*Math.sin(gocLech));
        path.rQuadTo((float)(mpQuad2.X ),(float)(mpQuad2.Y ),(float)(mp6.X ),(float) (mp6.Y));
        path.rLineTo((float)mp1.X,(float)mp1.Y);
        path.close();
        canvas.drawPath(path,solidPaint);
        }






}
