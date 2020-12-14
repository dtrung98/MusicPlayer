package com.ldt.musicr.ui.base;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.musicr.ui.base.presentationstyle.card.DrawerStyle;
import com.ldt.musicr.ui.base.presentationstyle.FullscreenStyle;

/**
 * A {@link FloatingViewFragment} which can be shown in multiple style based on the current configuration.
 */
public class PresentationFragment extends FloatingViewFragment {
    public final PresentationStyleProvider getPresentationStyleProvider() {
        if(mPresentationStyleProvider == null) {
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
        provider.addStyle(new FullscreenStyle(getAppRootView()));
        provider.addStyle(new DrawerStyle(getAppRootView()));
        //provider.addStyle("", new FullscreenStyle());
    }

    /**
     * return the current presentation style based on current configuration
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

    /**
     * Called in {@link PresentationFragment#onCreateViewContainer(Bundle)}, right after the {@link ContentViewContainer} is created
     * @param container the ContentViewContainer
     * @param savedInstanceState the fragment's previous instance state
     */
    public void onViewContainerCreated(@NonNull ContentViewContainer container,@Nullable Bundle savedInstanceState) {}

    @Override
    @NonNull
    public final ContentViewContainer onCreateViewContainer(@Nullable Bundle savedInstanceState) {
        onCreatePresentationStyleProvider();
        final String presentationStyle = retrievePresentationStyle();
        setCurrentPresentationStyle(presentationStyle);

        ContentViewContainer container;

        if (!"".equals(presentationStyle)) {
            container = getPresentationStyleProvider().get(presentationStyle);
            if (container == null) {
                throw new IllegalArgumentException("The container with name :"+presentationStyle+" isn't existed in Container Provider");
            }
        } else {
            container = super.onCreateViewContainer(savedInstanceState);
        }

        onViewContainerCreated(container, savedInstanceState);
        return container;
    }
}
