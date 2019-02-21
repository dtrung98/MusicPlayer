package com.ldt.musicr.ui.main.navigate;

import android.view.View;

public interface AdapterOnClickItemListener<T> {
    void onAdapterItemClick(View view, T item, int position);
}