package com.ldt.musicr.fragments.FragmentHolder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.ldt.musicr.InternalTools.Animation;
import com.ldt.musicr.activities.SupportFragmentActivity;
import com.ldt.musicr.fragments.FragmentPlus;
import com.ldt.musicr.InternalTools.Tool;

/**
 * Created by trung on 8/14/2017.
 */

public class Right2LeftTransition {
    private final static long  _DURATION = 300;
    private final static Interpolator _INTERPOLATOR = Animation.getInterpolator(2);
    private final static long _DELAY = 0;
    public static void AddFragmentAndTransform(final SupportFragmentActivity activity, final FragmentPlus ThisFragment, final FragmentPlus BackFragment)
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
        final FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) OldViewFragment.getLayoutParams();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float number = (float)animation.getAnimatedValue();
                setPositionAViewInScreen(screenSize,ViewFragment,params1,2*number-1,1);
                setPositionAViewInScreen(screenSize,OldViewFragment,params2,1+2*number,1);
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
                setPositionAViewInScreen(screenSize,OldViewFragment,params2,1,1);
                ThisFragment.onTransitionComplete();
                OldViewFragment.setAlpha(1);
                OldViewFragment.setVisibility(View.GONE);
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
        final SupportFragmentActivity activity = (SupportFragmentActivity) ThisFragment.getActivity();
        final int[] screenSize = Tool.getScreenSize(activity);
        ValueAnimator va = ValueAnimator.ofFloat(0.75f,0f);
        va.setDuration(_DURATION);
        va.setInterpolator(_INTERPOLATOR);
        va.setStartDelay(_DELAY);
        final  FrameLayout ViewFragment = ThisFragment.getFrameLayout();
        final FrameLayout OldViewFragment = BackFragment.getFrameLayout();
        final FrameLayout.LayoutParams params1 = ( FrameLayout.LayoutParams)ViewFragment.getLayoutParams();
        final  FrameLayout.LayoutParams params2 = ( FrameLayout.LayoutParams) OldViewFragment.getLayoutParams();
        OldViewFragment.setVisibility(View.VISIBLE);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float number = (float)animation.getAnimatedValue();
                setPositionAViewInScreen(screenSize,ViewFragment,params1,number-0.5f*(1-number),1);
                setPositionAViewInScreen(screenSize,OldViewFragment,params2,1+number/2,1);
                setScaleBehindView(OldViewFragment,1-1.5f*number,true,1);
                setScaleBehindView(ViewFragment,number-0.5f*(1-number),true,number);

                if(number==0f) {
                    activity.getSupportFragmentManager().beginTransaction().remove(ThisFragment).commit();
                }
            }
        });

        va.start();
    }
    public static void setPositionAViewInScreen(int[] screenSize, View view, FrameLayout.LayoutParams params, float percentX, float percentY)
    {
        float pxMarginLeft = (1-percentX)*screenSize[0];
        //  float pxMarginRight = - pxMarginLeft;

        //float pxMarginTop = (1-percentY)*screenSize[1];
        //   float pxMarginBottom = -pxMarginTop;
        //  params.setMargins((int)pxMarginLeft,(int)pxMarginTop,(int)pxMarginRight,(int)pxMarginBottom);
        //  view.setLayoutParams(params);
        view.setTranslationX(pxMarginLeft);
    }
    public static void setScaleBehindView(View applyView,float applyAlpha,boolean scale,float applyScale)
    {
        //  float applyScale = 1- 0.1f*number;

        //  applyView.setScaleX(applyScale);
        //  applyView.setScaleY(applyScale);
        applyView.setAlpha(applyAlpha);
    }

}
