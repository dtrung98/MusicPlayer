package com.ldt.musicr.ui.base.presentationstyle.drawer;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.ldt.musicr.ui.base.presentationstyle.Attribute;

/**
 * The {@link Attribute} uses to configure a {@link DrawerStyle}
 */
public class DrawerStyleAttribute extends Attribute {
    public static final String LEFT_OVER_MARGIN_DP = "left-over-margin-dp";
    private int mLeftOverMarginDp = 0;

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle bundle = super.onSaveInstanceState();
        bundle.putInt(LEFT_OVER_MARGIN_DP, mLeftOverMarginDp);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle bundle) {
        mLeftOverMarginDp = bundle.getInt(LEFT_OVER_MARGIN_DP, mLeftOverMarginDp);
    }

    public int getLeftOverMarginDp() {
        return mLeftOverMarginDp;
    }

    public void setLeftOverMarginDp(int leftOverMarginDp) {
        mLeftOverMarginDp = leftOverMarginDp;
    }
}
