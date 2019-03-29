package com.ldt.musicr.ui.tabs;

import android.view.View;

public interface OnClickItemListener<T> {
    void onItemClick(View view, T item, int position);
}