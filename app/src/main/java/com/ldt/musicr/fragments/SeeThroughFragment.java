package com.ldt.musicr.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldt.musicr.InternalTools.BitmapEditor;
import com.ldt.musicr.R;
import com.ldt.musicr.activities.SupportFragmentPlusActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeeThroughFragment extends FragmentPlus {


    public SeeThroughFragment() {
        // Required empty public constructor
    }

    @Override
    public ApplyMargin IWantApplyMargin() {
        return ApplyMargin.NONE;
    }

    public static SeeThroughFragment Initialize(Activity activity) {
        SeeThroughFragment fragment = new SeeThroughFragment();
        fragment.setFrameLayoutNTransitionType(activity, SupportFragmentPlusActivity.TransitionType.FADE_IN_OUT);
        return fragment;
    }
    ImageView iV_wallpaper;
    SupportFragmentPlusActivity activity;
    View blank_wall_back;
    LinearLayout draw_on;
    View trade_mark;
    ScrollView scrollView;
    private void MergeUI()
    {
        activity = (SupportFragmentPlusActivity)getActivity();
        iV_wallpaper = new ImageView(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((FrameLayout)rootView).addView(iV_wallpaper,0,params);
        blank_wall_back = rootView.findViewById(R.id.back_wall_blank);
        draw_on = rootView.findViewById(R.id.back_wall_drawn_on);
        trade_mark = rootView.findViewById(R.id.trade_mark);
           //OverScrollDecoratorHelper.setUpOverScroll(scrollView);
    }
    private void SetAllClick()
    {
        View[] views = new View[]
                {  // put views here
                        blank_wall_back,trade_mark
                };

        for( View va : views) va.setOnClickListener(Onclick);
    }
    private View.OnClickListener Onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id)
            {
                case R.id.trade_mark :
                case R.id.back_wall_blank :back_wall_blank_onTouch();return;
            }
        }
    };
    private void back_wall_blank_onTouch()
    {
        ((SupportFragmentPlusActivity)getActivity()).pushFragment(HelloScreenFragment.Initialize(getActivity()),true);
    }
    private Bitmap getWallPaper()
    {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        Bitmap bmp = ((BitmapDrawable)wallpaperDrawable).getBitmap();
        if(bmp.getWidth()>0) return bmp.copy(bmp.getConfig(),true);
        return Bitmap.createBitmap(300,300, Bitmap.Config.ARGB_8888);
    }
    private Bitmap applyBackWallEffect4Wallpaper(Bitmap old)
    {
        Bitmap blur  = BitmapEditor.getBlurredWithGoodPerformance(getActivity(),old,1,12,1.4f);
        //     Canvas canvas = new Canvas(blur);
        //    Paint paint= new Paint();
        //   int w=blur.getWidth(),h = blur.getHeight();
        //   paint.setARGB(100,0,0,0);
        //   canvas.drawRect(0,0,w,h,paint);

        return blur;
    }

    private void CreateAnWallImage()
    {
        iV_wallpaper.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final SupportFragmentPlusActivity activity = (SupportFragmentPlusActivity)getActivity();
        activity.setBlurWallpaper(iV_wallpaper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_see_through, container, false);
        return rootView;
    }

    @Override
    public void onTransitionComplete() {
        MergeUI();
        SetAllClick();
        CreateAnWallImage();
       AnimatedFadeInDraw_on();
    }

    @Override
    public StatusTheme setDefaultStatusTheme() {
        return StatusTheme.WhiteIcon;
    }

    private void AnimatedFadeInDraw_on()
    {
        ValueAnimator va = ValueAnimator.ofFloat(0,1);
        va.setDuration(200);
        va.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                // First  Of All
               draw_on.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
             //   blank_wall_back.setVisibility(View.GONE);
            }
        });
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((float)animation.getAnimatedValue());
                rootView.setAlpha(value);
            //    blank_wall_back.setAlpha(1-value);
            }
        });
        va.start();
    }
}
