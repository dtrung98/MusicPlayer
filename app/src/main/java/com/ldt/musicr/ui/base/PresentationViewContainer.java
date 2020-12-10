package com.ldt.musicr.ui.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Like {@link Dialog}, but created from View only
 */
public class PresentationViewContainer implements DialogInterface {
    private boolean mShowing = false;

    public void setContentView(View view) {
    }

    private static final String DIALOG_SHOWING_TAG = "android:dialogShowing";
    private static final String DIALOG_HIERARCHY_TAG = "android:dialogHierarchy";

    /**
     * Saves the state of the dialog into a bundle.
     * <p>
     * The default implementation saves the state of its view hierarchy, so you'll
     * likely want to call through to super if you override this to save additional
     * state.
     *
     * @return A bundle with the state of the dialog.
     */
    public @NonNull
    Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(DIALOG_SHOWING_TAG, mShowing);
     /*   if (mCreated) {
            bundle.putBundle(DIALOG_HIERARCHY_TAG, mWindow.saveHierarchyState());
        }*/
        return bundle;
    }

    /**
     * Restore the state of the dialog from a previously saved bundle.
     *
     * The default implementation restores the state of the dialog's view
     * hierarchy that was saved in the default implementation of {@link #onSaveInstanceState()},
     * so be sure to call through to super when overriding unless you want to
     * do all restoring of state yourself.
     * @param savedInstanceState The state of the dialog previously saved by
     *     {@link #onSaveInstanceState()}.
     */
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        final Bundle dialogHierarchyState = savedInstanceState.getBundle(DIALOG_HIERARCHY_TAG);
        if (dialogHierarchyState == null) {
            // dialog has never been shown, or onCreated, nothing to restore.
            return;
        }
        //dispatchOnCreate(savedInstanceState);
        //mWindow.restoreHierarchyState(dialogHierarchyState);
        if (savedInstanceState.getBoolean(DIALOG_SHOWING_TAG)) {
            show();
        }
    }

    public void show() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void dismiss() {

    }
}
