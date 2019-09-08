package com.ldt.musicr.ui.widget.slidercontainer;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class SliderTransformer implements ViewPager.PageTransformer {

    private static final float INACTIVE_SCALE = 0.9f;
    private static final float INACTIVE_ALPHA = 0.7f;

    public void transformPage(View view, float position) {
        if (position < -1) {
            view.setAlpha(INACTIVE_ALPHA);
            view.setScaleX(INACTIVE_SCALE);
            view.setScaleY(INACTIVE_SCALE);
        } else if (position <= 1) {
            float scale = INACTIVE_SCALE + (1 - INACTIVE_SCALE) * (1 - Math.abs(position));
            float alpha = INACTIVE_ALPHA + (1 - INACTIVE_ALPHA) * (1 - Math.abs(position));
            view.setScaleX(scale);
            view.setScaleY(scale);
            view.setAlpha(alpha);
        } else {
            view.setAlpha(INACTIVE_ALPHA);
            view.setScaleX(INACTIVE_SCALE);
            view.setScaleY(INACTIVE_SCALE);
        }
    }
}
