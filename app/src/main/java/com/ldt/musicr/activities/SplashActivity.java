package com.ldt.musicr.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.splash);
       View f= findViewById(R.id.rootEveryThing);
       View viewOneDp = f.findViewById(R.id.ViewOneDp);
       Tool.setOneDps(viewOneDp.getWidth());
       f.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); // trong suốt status bar và thanh navigation màu đen.

      //  getWindow().addFlags( SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION| SYSTEM_UI_FLAG_LAYOUT_STABLE);
        //getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); // ẩn status bar và thanh navigation màu đen
        ValueAnimator timeAnimator = ValueAnimator.ofFloat(0,1);
        timeAnimator.setDuration(5500);
        timeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                runMainActivity();
                }
        });
        timeAnimator.start();
    }
    private void runMainActivity()
    {
        Tool.setDrawn(true);
    }
}
