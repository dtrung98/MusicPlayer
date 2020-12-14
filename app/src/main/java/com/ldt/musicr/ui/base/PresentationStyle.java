package com.ldt.musicr.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Like {@link Dialog}, but created from View only
 */
public abstract class PresentationStyle extends ContentViewContainer {

    public PresentationStyle(@NonNull ViewGroup appRootView) {
        super(appRootView);
    }

    public abstract String getName();

    @Override
    public ViewGroup onCreateHostView(Context context) {
        return super.onCreateHostView(context);
    }

    @Override
    public abstract void setContentView(View view);
}
