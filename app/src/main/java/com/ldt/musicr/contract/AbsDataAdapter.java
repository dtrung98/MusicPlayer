package com.ldt.musicr.contract;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsDataAdapter<VH extends AbsBindAbleHolder, I> extends RecyclerView.Adapter<VH> {
    private static final String TAG = "AbsDataAdapter";

    private final List<I> mData = new ArrayList<>();

    public final List<I> getData() {
        return mData;
    }

    public final void setData(List<I> data) {
        mData.clear();

        if (data != null) {
            mData.addAll(data);
        }

        onDataSet();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    protected abstract void onDataSet();

    public void destroy() {
        mData.clear();
    }

    public I getDataItem(int i) {
        return mData.get(i);
    }

    public void addDataItem(int i, I item) {
        if (item != null) {
            int pos = (i < 0) ? 0 : (i >= mData.size()) ? mData.size() : i;
            mData.add(pos, item);
            notifyItemChanged(pos);
        }
    }

    public void addItem(I item) {
        if (item != null) {
            mData.add(item);
            notifyItemChanged(mData.size() - 1);
        }
    }

    public void removeSongAt(int i) {
        mData.remove(i);
        notifyItemRemoved(i);
    }

    public int getMediaHolderPosition(int dataPosition) {
        return dataPosition;
    }

    protected int getDataPosition(int itemHolderPosition) {
        return itemHolderPosition;
    }

}
