package com.ldt.musicr.contract;

import android.support.annotation.NonNull;
import android.view.View;

import com.ldt.musicr.model.Media;

public abstract class AbsMediaHolder<I extends Media> extends AbsDynamicHolder<I> implements View.OnClickListener, View.OnLongClickListener {
    public AbsMediaHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
