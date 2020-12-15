package com.ldt.musicr.ui.base.presentationstyle.fullscreen;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.musicr.ui.base.PresentationStyle;
import com.ldt.musicr.ui.base.presentationstyle.Attribute;
import com.ldt.musicr.ui.base.presentationstyle.drawer.DrawerStyleAttribute;

/**
 * A style which always presents in a fullscreen frame.
 */
public class FullscreenStyle extends PresentationStyle {
    @Override
    public void setContentView(View view) {
        getHostView().addView(view);
    }

    public FullscreenStyle(@NonNull ViewGroup appRootView, @Nullable FullscreenStyleAttribute attribute) {
        super(appRootView, attribute);
    }

    @NonNull
    @Override
    public String getName() {
        return "fullscreen";
    }

    public final FullscreenStyleAttribute requireAttribute() {
        Attribute attribute = getAttribute();
        if (!(attribute instanceof FullscreenStyleAttribute)) {
            throw new IllegalStateException("The attribute used in this style is not a DrawerStyleAttribute");
        }
        return (FullscreenStyleAttribute) attribute;
    }
}
