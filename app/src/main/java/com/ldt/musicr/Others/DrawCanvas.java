package com.ldt.musicr.Others;

/**
 * Created by trung on 7/11/2017.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ldt.musicr.R;

class DrawCanvas extends SurfaceView implements Runnable{

        Thread thread = null;
        SurfaceHolder surfaceHolder;
        volatile boolean running = false;
        public double slice;

        public DrawCanvas(Context context) {
            super(context);
            // Auto-generated constructor stub
            surfaceHolder = getHolder();

        }

        public void onResumeMySurfaceView(){
            running = true;
            thread = new Thread(this);
            thread.start();
        }

        public void onPauseMySurfaceView(){
            boolean retry = true;
            running = false;
            while(retry){
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    //  Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


        public static class ScreenSize {
  public  int Width,Height;
    public ScreenSize (int width,int height)
    {
        Width=width;
        Height=height;
    }
    public ScreenSize(int[] size)
    {
        Width=size[0];
        Height=size[1];
    }
}
public static int speed=1;
public ScreenSize screenSize ;
public static float rotateValue=1;
        public static boolean settup=false;
        public void run() {
            //  Auto-generated method stub
            while(running){
                if(surfaceHolder.getSurface().isValid()){
                    {
                        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
                    }
                    Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                    Paint mPaints = new Paint();
                    mPaints.setAntiAlias(true);
                    //mPaints.setStyle(Paint.Style.FILL);
                    mPaints.setColor(0xff14A5E2);
                  mPaints.setTextSize(50);
                    Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.tfboys);
                    float speedX = easeInOutQuad(rotateValue,0,1,1000);
                    canvas.drawText(speedX+"|"+rotateValue+"|"+screenSize.Width+"|"+screenSize.Height,600,1000,mPaints);
                    float distance = screenSize.Width-200;
                canvas.drawRect(100+speedX*distance,screenSize.Height/2-400,200+speedX*distance,200,mPaints);
                    if(rotateValue<1000) rotateValue+=speed; else rotateValue=0;
              //      canvas.rotate(spe
                    // edX*360,400,400);
                //    canvas.drawBitmap(bmp,new Rect(0,0,bmp.getWidth(),bmp.getHeight()),new Rect(300,300,500,500),mPaints);

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
        public static float easeInOutQuad(float current,float start_value,float change_in_value,float duration) {
            current /= duration/2;
            if (current < 1) return change_in_value/2*current*current + start_value;
            current--;
            return -change_in_value/2 * (current*(current-2) - 1) + start_value;
        };
        public static float easeOutQuad(float t,float b,float c,float d) {
            t /= d;
            return -c * t*(t-2) + b;
        };
        public static float easeInOutExpo(float t,float b,float c,float d) {
            t /= d/2;
            if (t < 1) return (float)( c/2 * Math.pow( 2, 10 * (t - 1) ) + b);
            t--;
            return (float)(c/2 * ( -Math.pow( 2, -10 * t) + 2 ) + b);
        };

    }

