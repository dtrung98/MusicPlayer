package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ldt.musicr.addon.fastscrollrecyclerview.FastScrollRecyclerView;

public class FixedFastScrollRecyclerView extends FastScrollRecyclerView {
    public FixedFastScrollRecyclerView(Context context) {
        super(context);
    }

    public FixedFastScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedFastScrollRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected int getAvailableScrollHeight(int adapterHeight, int yOffset) {
        int visibleHeight = this.getHeight();
        int scrollHeight = this.getPaddingTop() + yOffset + adapterHeight + this.getPaddingBottom();
        int availableScrollHeight = scrollHeight - visibleHeight;
        return availableScrollHeight;
    }
}
