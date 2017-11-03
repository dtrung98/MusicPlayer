package com.ldt.NewDefinitionMusicApp.Others;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldt.NewDefinitionMusicApp.R;

public class Suface_Drawer extends Activity {
    /** Called when the activity is first created. */

    private Handler mHandler = new Handler();
    TextView tv;
    DrawCanvas canvasView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surfacethis);
        RelativeLayout wheel = (RelativeLayout) findViewById(R.id.wheel);
        wheel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                     if(DrawCanvas.speed==0) DrawCanvas.speed=1;else DrawCanvas.speed=0;

                return true;
            }
        });
        wheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (DrawCanvas.speed)
                {
                    case 1 : DrawCanvas.speed=2;break;
                    case 2 : DrawCanvas.speed=5;break;
                    case 5 : DrawCanvas.speed=8;break;
                    case 8 : DrawCanvas.speed=12;break;
                    case 12 : DrawCanvas.speed=24;break;
                    case 24 : DrawCanvas.speed=40;break;
                    case 40 : DrawCanvas.speed=72;break;
                    case 72 : DrawCanvas.speed=90;break;
                    case 90 : DrawCanvas.speed=1;break;
                }
            }
        });
        canvasView = new DrawCanvas(this);
        canvasView.screenSize = new DrawCanvas.ScreenSize(getSizeOfScreen());
       canvasView.setZOrderOnTop(true);
        wheel.addView(canvasView);

    }


    private int[] getSizeOfScreen()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return new int[]{width,height};
    }
    @Override
    protected void onResume() {
        //  Auto-generated method stub
        super.onResume();
        canvasView.onResumeMySurfaceView();
    }

    @Override
    protected void onPause() {
        //  Auto-generated method stub
        super.onPause();
        canvasView.onPauseMySurfaceView();
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        long start = SystemClock.uptimeMillis();
        public void run() {
            long millis = SystemClock.uptimeMillis() - start;
            canvasView.slice = millis/20000.0;
            //tv.setText(String.valueOf(canvasView.slice));
            if(millis<20000) mHandler.postAtTime(this, start + millis);
            else{
                mHandler.removeCallbacks(mUpdateTimeTask);
            }
        }
    };

    public void go(View view){
        mHandler.removeCallbacks(mUpdateTimeTask);  //Remove old timer
        mHandler.postDelayed(mUpdateTimeTask, 1);

    }


}