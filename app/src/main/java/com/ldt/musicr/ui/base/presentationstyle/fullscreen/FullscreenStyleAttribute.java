package com.ldt.musicr.ui.base.presentationstyle.fullscreen;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.ldt.musicr.ui.base.presentationstyle.Attribute;

public class FullscreenStyleAttribute extends Attribute {
    public int getEnterAnimation() {
        return mEnterAnimation;
    }

    public void setEnterAnimation(int enterAnimation) {
        mEnterAnimation = enterAnimation;
    }

    public int getExitAnimation() {
        return mExitAnimation;
    }

    public void setExitAnimation(int exitAnimation) {
        mExitAnimation = exitAnimation;
    }

    private int mEnterAnimation = android.R.animator.fade_in;
    private int mExitAnimation = android.R.animator.fade_out;

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle bundle = super.onSaveInstanceState();
                ;
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle bundle) {

    }
}
