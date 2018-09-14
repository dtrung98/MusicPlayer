package com.ldt.musicr.fragments;

import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.ldt.musicr.listeners.MusicStateListener;

public abstract class BaseTabLayerFragment extends RuntimeThemeFragment implements TabLayerController.TabLayerCallBack, MusicStateListener {
     TabLayerController tabLayerController;
    public  void setTabLayerController(TabLayerController tabLayerController) {
        this.tabLayerController = tabLayerController;
    }

    @Override
    public FrameLayout parentView() {
        return null;
    }
}
