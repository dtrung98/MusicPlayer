package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MPSafeAreaView extends View {
    public MPSafeAreaView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MPSafeAreaView(@NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    @NotNull
    @Override
    public WindowInsets onApplyWindowInsets(@Nullable WindowInsets insets) {
        if (insets != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
            layoutParams.setMargins(
                    insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());
            requestLayout();
        }
        return super.onApplyWindowInsets(insets);
    }

    public MPSafeAreaView(@NotNull Context context) {
        super(context);
    }
}
