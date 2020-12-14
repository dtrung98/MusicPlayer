package com.ldt.musicr.ui.base.presentationstyle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.ldt.musicr.ui.base.PresentationStyle;

public class FullscreenStyle extends PresentationStyle {
    @Override
    public void setContentView(View view) {
        getHostView().addView(view);
    }

    public FullscreenStyle(@NonNull ViewGroup appRootView) {
        super(appRootView);
    }

    @Override
    public String getName() {
        return "fullscreen";
    }
}
