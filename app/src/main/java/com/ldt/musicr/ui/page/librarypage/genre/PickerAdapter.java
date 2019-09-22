package com.ldt.musicr.ui.page.librarypage.genre;

import com.ldt.musicr.model.Media;
import com.ldt.musicr.ui.widget.bubblepicker.BubblePickerListener;
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.Adapter;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubblePicker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PickerAdapter<T extends Media> extends Adapter implements BubblePickerListener {
    protected ArrayList<T> mData = new ArrayList<>();

    public final void setData(List<T> list) {
        mData.clear();
        if(list!=null) mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setListener(PickerListener listener) {
        mListener = listener;
    }

    @Override
    public void destroy() {
        super.destroy();
        removeListener();
    }

    public void removeListener() {
        mListener = null;
       BubblePicker picker = getBubblePicker();
       if(picker!=null) picker.setListener(null);
    }

    private PickerListener mListener;
    @Override
    public void onBubbleSelected(@NotNull PickerItem item, int position) {
        if(mListener!=null) mListener.onPickerSelected(item, position, mData.get(position));
    }

    @Override
    public void onBubbleDeselected(@NotNull PickerItem item, int position) {
        if(mListener!=null) mListener.onPickerDeselected(item, position, mData.get(position));
    }

    public interface PickerListener {
        void onPickerSelected(PickerItem item, int position, Object o);
        void onPickerDeselected(PickerItem item, int position, Object o);
    }

    @Override
    protected void onAttach(BubblePicker picker) {
        super.onAttach(picker);
        picker.setListener(this);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
