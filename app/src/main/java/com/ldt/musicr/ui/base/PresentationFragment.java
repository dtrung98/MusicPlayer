package com.ldt.musicr.ui.base;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.musicr.ui.base.presentationstyle.drawer.DrawerStyle;
import com.ldt.musicr.ui.base.presentationstyle.drawer.DrawerStyleAttribute;
import com.ldt.musicr.ui.base.presentationstyle.fullscreen.FullscreenStyle;
import com.ldt.musicr.ui.base.presentationstyle.fullscreen.FullscreenStyleAttribute;

import java.util.HashMap;

/**
 * A {@link FloatingViewFragment} which can be shown in multiple style based on the current configuration.
 */
public class PresentationFragment extends FloatingViewFragment {
    private final FullscreenStyleAttribute mFullscreenStyleAttribute = new FullscreenStyleAttribute();
    private final DrawerStyleAttribute mDrawerStyleAttribute = new DrawerStyleAttribute();

    public FullscreenStyleAttribute getFullscreenStyleAttribute() {
        return mFullscreenStyleAttribute;
    }

    public DrawerStyleAttribute getDrawerStyleAttribute() {
        return mDrawerStyleAttribute;
    }


    public final PresentationStyleProvider getPresentationStyleProvider() {
        if (mPresentationStyleProvider == null) {
            mPresentationStyleProvider = new PresentationStyleProvider();
        }
        return mPresentationStyleProvider;
    }

    private PresentationStyleProvider mPresentationStyleProvider;

    public final PresentationStyle getPresentationStyle(String name) {
        return getPresentationStyleProvider().get(name);
    }

    public String getCurrentPresentationStyle() {
        return mCurrentPresentationStyle;
    }

    private void setCurrentPresentationStyle(String currentPresentationStyle) {
        if (currentPresentationStyle == null) {
            mCurrentPresentationStyle = "";
        } else {
            mCurrentPresentationStyle = currentPresentationStyle;
        }
    }

    private String mCurrentPresentationStyle = "";

    /**
     * Called when creating the {@link PresentationStyleProvider}
     * Override this method to add custom {@link PresentationStyle} to the provider
     */
    protected void onCreatePresentationStyleProvider() {
        PresentationStyleProvider provider = getPresentationStyleProvider();
        provider.addStyle(new FullscreenStyle(getAppRootView(), mFullscreenStyleAttribute));
        provider.addStyle(new DrawerStyle(getAppRootView(), mDrawerStyleAttribute));
        //provider.addStyle("", new FullscreenStyle());
    }

    /**
     * return the current presentation style based on current configuration
     *
     * @return the presentation style used for this presentation
     */
    protected String retrievePresentationStyle() {

        Configuration configuration = requireContext().getResources().getConfiguration();
        int wQualifier = configuration.screenWidthDp;
        int hQualifier = configuration.screenHeightDp;

        return "drawer";

     /*   if (wQualifier >= 448 && hQualifier >= 448) {
            return "dialog";
        } else if (hQualifier >= 300 && (float) hQualifier / wQualifier >= 4f / 3) {
            return "bottomsheet";
        } else {
            return "fullscreen";
        }*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save all presentation states
        HashMap<String, PresentationStyle> styleMap = getPresentationStyleProvider().getHashMap();
        for (PresentationStyle style : styleMap.values()) {
            style.onSavePresentationState(outState);
        }
    }

    @Override
    @NonNull
    public final ContentViewContainer onCreateContainer(@Nullable Bundle savedInstanceState) {
        onCreatePresentationStyleProvider();

        // Restore all presentation states
        if (savedInstanceState != null) {
            HashMap<String, PresentationStyle> styleMap = getPresentationStyleProvider().getHashMap();
            for (PresentationStyle style :
                    styleMap.values()) {
                style.onRestorePresentationState(savedInstanceState);
            }
        }

        // get the current presentation style
        final String presentationStyle = retrievePresentationStyle();
        setCurrentPresentationStyle(presentationStyle);

        final ContentViewContainer container;

        if (!"".equals(presentationStyle)) {
            container = getPresentationStyleProvider().get(presentationStyle);
            if (container == null) {
                throw new IllegalArgumentException("The container with name :" + presentationStyle + " isn't existed in Container Provider");
            }
        } else {
            // fall back to default container
            container = super.onCreateContainer(savedInstanceState);
        }
        return container;
    }
}
