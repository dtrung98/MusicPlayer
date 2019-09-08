package com.ldt.musicr.ui.page;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import androidx.fragment.app.Fragment;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

public class RuntimeThemeFragment extends Fragment {
    private ArrayList<View> rippleViews = new ArrayList<>();
    private boolean first_time = true;
    public void addToBeRipple(int drawable,View... v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (first_time) {
                first_time = false;
                res = getResources();
            }
            int l = v.length;
            rippleViews.addAll(Arrays.asList(v));
            for (View view : v) {
                view.setBackground((RippleDrawable) res.getDrawable(drawable));
                view.setClickable(true);
            }
        }
    }
    Resources res;
    public void applyRippleColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        for( final View v : rippleViews)
        {

                ((RippleDrawable)v.getBackground()).setColor(ColorStateList.valueOf(color));
            }
        }
    }
}
