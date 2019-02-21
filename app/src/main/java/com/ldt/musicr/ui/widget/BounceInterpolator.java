package com.ldt.musicr.ui.widget;

import android.view.animation.Interpolator;

public class BounceInterpolator implements Interpolator {
    private double mAmplitude = 1;
    private double mFrequency = 10;

    public BounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}