package com.ldt.musicr.ui.main.navigate;

import android.view.View;

public interface OnClickItemListener<T> {
    void onItemClick(View view, T item, int position);
}