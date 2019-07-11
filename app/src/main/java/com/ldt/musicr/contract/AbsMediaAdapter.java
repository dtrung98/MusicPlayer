package com.ldt.musicr.contract;

import android.content.Context;

import com.ldt.musicr.model.Media;

public abstract class AbsMediaAdapter<VH extends AbsDynamicHolder, I extends Media> extends AbsDynamicAdapter<VH, I> {
    private static final String TAG = "AbsMediaAdapter";

    protected Context mContext;
    protected int mMediaPlayDataItem = -1;

    public AbsMediaAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public void destroy() {
        mContext = null;
        super.destroy();
    }

    protected void onPopupMenuItem(AbsDynamicHolder holder, final int position) {
    }

    protected void onLongPressedItem(AbsDynamicHolder holder, final int position) {

    }

    public void notifyOnMediaStateChanged() {
    }

    boolean isMediaPlayItemAvailable() {
        return -1 < mMediaPlayDataItem && mMediaPlayDataItem < getData().size();
    }
}
