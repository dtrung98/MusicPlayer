package com.ldt.musicr.fragments.FragmentHolder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;


import com.ldt.musicr.activities.SupportFragmentPlusActivity;
import com.ldt.musicr.fragments.FragmentPlus;
import com.ldt.musicr.InternalTools.Animation;
import com.ldt.musicr.InternalTools.Tool;

/**
 * Created by trung on 8/13/2017.
 */

public class BottomUpTransition {

    private final static long  _DURATION = 250;
    private final static Interpolator _INTERPOLATOR = Animation.getInterpolator(2);
    private final static long _DELAY = 0;
    public static void setPositionAViewInScreen(int[] screenSize, View view, FrameLayout.LayoutParams params, float percentX, float percentY)
    {
        float pxMarginLeft = (1-percentX)*screenSize[0];
        float pxMarginRight = - pxMarginLeft;

        float pxMarginTop = (1-percentY)*screenSize[1];
        float pxMarginBottom = -pxMarginTop;
        params.setMargins((int)pxMarginLeft,(int)pxMarginTop,(int)pxMarginRight,(int)pxMarginBottom);
        view.setLayoutParams(params);
    }
    public static void setScaleBehindView(View applyView,float number,boolean applyApha)
    {
        //  float applyScale = 1- 0.1f*number;

        //  applyView.setScaleX(applyScale);
        //  applyView.setScaleY(applyScale);
        if(applyApha) applyView.setAlpha(1-0.3f*number);
    }

    public static void AddFragmentAndTransform(final SupportFragmentPlusActivity activity, final FragmentPlus ThisFragment, final FragmentPlus BackFragment)
    {
        final int[] screenSize = Tool.getScreenSize(activity);
        ValueAnimator va = ValueAnimator.ofFloat(0.2f,1f);
        va.setDuration(_DURATION+30);
        va.setInterpolator(_INTERPOLATOR);
        va.setStartDelay(_DELAY);
        final  FrameLayout ViewFragment = ThisFragment.getFrameLayout();
        final FrameLayout BackViewFragment = BackFragment.getFrameLayout();
        final FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final  FrameLayout.LayoutParams params2 = ( FrameLayout.LayoutParams) BackViewFragment.getLayoutParams();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float number = (float)animation.getAnimatedValue();
                setPositionAViewInScreen(screenSize,ViewFragment,params1,1,number);
                //    setPositionAViewInScreen(screenSize,BackViewFragment,params2,1,1+number/5);
                //   setScaleBehindView(behindView,number,true);
            }
        });

        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ViewFragment.setId(Prepare4Fragment.getId());
                ((FrameLayout)BackViewFragment.getParent()).addView(ViewFragment,params1);
                activity.getSupportFragmentManager().beginTransaction().add(ViewFragment.getId(),ThisFragment).commit();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ThisFragment.onTransitionComplete();
                BackViewFragment.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        va.start();
    }
    public static void RemoveFragmentAndTransform( final FragmentPlus ThisFragment, final FragmentPlus BackFragment)
    {
        final SupportFragmentPlusActivity activity = (SupportFragmentPlusActivity) ThisFragment.getActivity();
        final int[] screenSize = Tool.getScreenSize(activity);
        ValueAnimator va = ValueAnimator.ofFloat(0.75f,0f);
        va.setDuration(_DURATION);
        va.setInterpolator(_INTERPOLATOR);
        va.setStartDelay(_DELAY);
        final  FrameLayout ViewFragment = ThisFragment.getFrameLayout();
        final FrameLayout BackViewFragment = BackFragment.getFrameLayout();
        final FrameLayout.LayoutParams params1 = ( FrameLayout.LayoutParams)ViewFragment.getLayoutParams();
        final  FrameLayout.LayoutParams params2 = ( FrameLayout.LayoutParams) BackViewFragment.getLayoutParams();
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                BackViewFragment.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                activity.getSupportFragmentManager().beginTransaction().remove(ThisFragment).commit();
                Prepare4Fragment.unUseThisID(ViewFragment.getId());
                ((ViewGroup)ViewFragment.getParent()).removeView(ViewFragment);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float number = (float)animation.getAnimatedValue();
                setPositionAViewInScreen(screenSize,ViewFragment,params1,1,number);
                //   setPositionAViewInScreen(screenSize,behindView,params2,1,1-number/5);
                //   setScaleBehindView(behindView,number,true);

            }
        });

        va.start();
    }

}
