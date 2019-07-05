package com.ldt.musicr.ui.bottomnavigationtab;

import android.view.View;

public interface OnClickItemListener<T> {
    void onItemClick(View view, T item, int position);
}