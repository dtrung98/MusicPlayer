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
 * Created by trung on 8/19/2017.
 */

public class FadeInFadeOutTransition {

    private final static long  _DURATION = 250;
    private final static Interpolator _INTERPOLATOR = Animation.getInterpolator(2);
    private final static long _DELAY = 0;
    public static void setScaleBehindView(View applyView,float number,boolean applyApha)
    {
        //  float applyScale = 1- 0.1f*number;

        //  applyView.setScaleX(applyScale);
        //  applyView.setScaleY(applyScale);
        if(applyApha) applyView.setAlpha(number);
    }
    public static void AddFragmentAndTransform(final SupportFragmentPlusActivity activity, final FragmentPlus ThisFragment, final FragmentPlus BackFragment)
    {

        final int[] screenSize = Tool.getScreenSize(activity);
        //  ValueAnimator va = ValueAnimator.ofFloat(0.25f,1);
        ValueAnimator va = ValueAnimator.ofFloat(0,1);
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
                setScaleBehindView(ViewFragment,number,true);
               setScaleBehindView(OldViewFragment,1-number,true);
            }
        });

        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ViewFragment.setId(Prepare4Fragment.getId());
                ViewFragment.setAlpha(0);
                ((FrameLayout)OldViewFragment.getParent()).addView(ViewFragment,params1);
                activity.getSupportFragmentManager().beginTransaction().add(ViewFragment.getId(),ThisFragment).commit();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ThisFragment.onTransitionComplete();
                OldViewFragment.setAlpha(1);
                OldViewFragment.setVisibility(View.GONE);
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
                SupportFragmentPlusActivity.setInTransition(false);
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
                setScaleBehindView(ViewFragment,number,true);
                setScaleBehindView(BackViewFragment,1-number,true);
            }
        });

        va.start();
    }
}
