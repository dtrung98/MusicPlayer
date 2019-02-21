package com.ldt.musicr.fragments.fragmentholder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.ldt.musicr.util.uitool.Animation;
import com.ldt.musicr.ui.main.SupportFragmentPlusActivity;
import com.ldt.musicr.fragments.FragmentPlus;
import com.ldt.musicr.util.Tool;

/**
 * Created by trung on 8/14/2017.
 */

public class RightToLeftTransition {
    private final static long  _DURATION = 270;
    private final static Interpolator _INTERPOLATOR = Animation.getInterpolator(2);
    private final static long _DELAY = 5;

    public static void AddFragmentAndTransform(final SupportFragmentPlusActivity activity, final FragmentPlus ThisFragment, final FragmentPlus BackFragment)
    {
    final int[] screenSize = Tool.getScreenSize(activity);
    //  ValueAnimator va = ValueAnimator.ofFloat(0.25f,1);
    ValueAnimator va = ValueAnimator.ofFloat(0.25f,1);
        va.setDuration(_DURATION);
        va.setInterpolator(_INTERPOLATOR);
        va.setStartDelay(_DELAY);
    final FrameLayout ViewFragment = ThisFragment.getFrameLayout();
    final FrameLayout OldViewFragment = BackFragment.getFrameLayout();
    final FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
       va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float number = (float)animation.getAnimatedValue();
                setPositionAViewInScreen(screenSize,ViewFragment,number-0.5f*(1-number),0);
                setPositionAViewInScreen(screenSize,OldViewFragment,1+number/2,0);
                setScaleBehindView(OldViewFragment,1-1.5f*number,true,number);
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                ViewFragment.setId(Prepare4Fragment.getId());

                ((FrameLayout)OldViewFragment.getParent()).addView(ViewFragment,params1);
                activity.getSupportFragmentManager().beginTransaction().add(ViewFragment.getId(),ThisFragment).commit();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setPositionAViewInScreen(screenSize,OldViewFragment,1,0);
                OldViewFragment.setAlpha(1);
                OldViewFragment.setVisibility(View.GONE);
                ThisFragment.onTransitionComplete();
                SupportFragmentPlusActivity.setInTransition(false);
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
        final FrameLayout OldViewFragment = BackFragment.getFrameLayout();
        OldViewFragment.setVisibility(View.VISIBLE);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float number = (float)animation.getAnimatedValue();
                setPositionAViewInScreen(screenSize,ViewFragment,number-0.5f*(1-number),0);
                setPositionAViewInScreen(screenSize,OldViewFragment,1+number/2,0);
                setScaleBehindView(OldViewFragment,1-1.5f*number,true,number);

                //     setScaleBehindView(OldViewFragment,1-1.5f*number,true,number);
             //  setScaleBehindView(ViewFragment,number-0.5f*(1-number),true,number);

                if(number==0f) {
                    activity.getSupportFragmentManager().beginTransaction().remove(ThisFragment).commit();
                    SupportFragmentPlusActivity.setInTransition(false);
                }
            }
        });

        va.start();
    }
    public static void setPositionAViewInScreen(int[] screenSize, View view, float percentX, float percentY)
    {
        float pxMarginLeft = (1-percentX)*screenSize[0];
        float pxMarginTop = (percentY)*screenSize[1];
        view.setTranslationX(pxMarginLeft);
        view.setTranslationY(pxMarginTop);
    }
    public static void setScaleBehindView(View applyView,float applyAlpha,boolean scale,float applyScale)
    {
        //  float applyScale = 1- 0.1f*number;

        //  applyView.setScaleX(applyScale);
        //  applyView.setScaleY(applyScale);
        applyView.setAlpha(applyAlpha);
    }

}
