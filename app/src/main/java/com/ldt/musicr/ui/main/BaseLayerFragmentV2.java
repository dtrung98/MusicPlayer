package com.ldt.musicr.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ldt.musicr.fragments.RuntimeThemeFragment;

public abstract class BaseLayerFragmentV2 extends RuntimeThemeFragment {
    private Attr mAttrLayer = new Attr();
    public Attr getAttr() {
        return  mAttrLayer;
    }

    public void onTranslateLayer(int translate) {

    }

    public void onScaleLayer(float scale) {

    }
    public void controllerSetPercent(float percent, float marginPercent) {

    }

    protected View mRoot;

    public View getRoot() {
        return mRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRoot = view;
    }

    public boolean onBackPressed() {
        return false;
    }
}
