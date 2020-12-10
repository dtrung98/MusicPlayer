package com.ldt.musicr.ui.base;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.widget.viewgroup.CardLayerContainerView;

public class CardPresentationFragment extends FloatingViewFragment {
    private int mContainerWrapperId = View.generateViewId();
    private final static int DURATION = 475;
    private TimeInterpolator mTimeInterpolator = new FastOutSlowInInterpolator();

    @Override
    public ViewGroup onCreateContainerView(@Nullable Bundle savedInstanceState) {
        ViewGroup appRootView = getAppRootView();

        WindowInsetsCompat wic = ViewCompat.getRootWindowInsets(appRootView);
        int parentHeight = appRootView.getHeight();
        int topInset = wic == null ? 0 : wic.getSystemWindowInsetTop();
        topInset += 14f * appRootView.getResources().getDimension(R.dimen.oneDP);

        CardLayerContainerView containerView = new CardLayerContainerView(appRootView.getContext());
        containerView.setId(mContainerWrapperId);
        containerView.setRadius(12 * getResources().getDimension(R.dimen.oneDP));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentHeight - topInset);
        params.topMargin = topInset;

        appRootView.addView(containerView, params);
        int count = appRootView.getChildCount();
        View viewBehind = appRootView.getChildAt(count - 2);

        RoundRectDrawable background = null;
        final float radius = 16 * viewBehind.getResources().getDimension(R.dimen.oneDP);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Drawable preBackground = viewBehind.getBackground();
            if(!(preBackground instanceof RoundRectDrawable)) {
                background = new RoundRectDrawable(ColorStateList.valueOf(0),
                        0);
                viewBehind.setBackground(background);
            } else {
                background = (RoundRectDrawable) preBackground;
            }

            viewBehind.setClipToOutline(true);
            viewBehind.setElevation(0);//4 * viewBehind.getResources().getDimension(R.dimen.oneDP));

            final RoundRectDrawable bg = background;
            ValueAnimator roundVA = ValueAnimator.ofFloat(0, 1).setDuration(DURATION);
            roundVA.setInterpolator(mTimeInterpolator);
            roundVA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bg.setRadius((float)animation.getAnimatedValue() * radius);
                }
            });

            roundVA.start();
        }
        containerView.setTranslationY(parentHeight - topInset);
        viewBehind.animate().scaleX(0.92f).scaleY(0.92f).setDuration(DURATION).setInterpolator(mTimeInterpolator).start();

        containerView.animate().translationY(0).setDuration(DURATION).setInterpolator(mTimeInterpolator).start();
        return containerView.getSubContainerView();
    }

    private boolean mOnDismissing = false;

    @Override
    protected void onDismissDialogContainerView() {
        if(mOnDismissing) {
            return;
        }
        mOnDismissing = true;
        ViewGroup appRootView = getAppRootView();

        View containerWrapper = appRootView.findViewById(mContainerWrapperId);
        containerWrapper.animate()
                .translationY(containerWrapper.getHeight())
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(DURATION)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        appRootView.removeView(containerWrapper);
                        mOnDismissing = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

        int count = appRootView.getChildCount();
        View viewBehind = appRootView.getChildAt(0);
        RoundRectDrawable background = null;
        final float radius = 16 * viewBehind.getResources().getDimension(R.dimen.oneDP);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Drawable preBackground = viewBehind.getBackground();
            if(!(preBackground instanceof RoundRectDrawable)) {
                background = new RoundRectDrawable(ColorStateList.valueOf(0),
                        0);
                viewBehind.setBackground(background);
            } else {
                background = (RoundRectDrawable) preBackground;
            }

            viewBehind.setClipToOutline(true);
            viewBehind.setElevation(0);//4 * viewBehind.getResources().getDimension(R.dimen.oneDP));

            final RoundRectDrawable bg = background;
            ValueAnimator roundVA = ValueAnimator.ofFloat(1, 0).setDuration(DURATION);
            roundVA.setInterpolator(mTimeInterpolator);
            roundVA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bg.setRadius((float)animation.getAnimatedValue() * radius);
                }
            });

            roundVA.start();
        }


        viewBehind.animate().scaleX(1f).scaleY(1f).setDuration(DURATION).setInterpolator(mTimeInterpolator).start();


    }
}
