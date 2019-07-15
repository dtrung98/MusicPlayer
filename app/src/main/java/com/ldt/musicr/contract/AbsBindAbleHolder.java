package com.ldt.musicr.contract;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class AbsBindAbleHolder<I> extends RecyclerView.ViewHolder {

    public AbsBindAbleHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(I item) {};
}
