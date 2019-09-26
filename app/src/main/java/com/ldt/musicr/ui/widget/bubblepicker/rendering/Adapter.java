package com.ldt.musicr.ui.widget.bubblepicker.rendering;

import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;

public abstract class Adapter implements Decorator  {
    public BubblePicker getBubblePicker() {
        return mBubblePicker;
    }

    private BubblePicker mBubblePicker;

    final void attachRenderer(PickerRenderer renderer) {

    }

    final void attach(BubblePicker bubblePicker) {
        mBubblePicker = bubblePicker;
        onAttach(bubblePicker);
    }
    protected void onAttach(BubblePicker picker) {}

    final void detach() {
        mBubblePicker = null;
    }
    public void destroy() {
        detach();
    }

    public abstract boolean onBindItem(PickerItem item, boolean create, int i);

    public final void notifyItemChanged(int i) {
        if(mBubblePicker!=null) mBubblePicker.getRenderer().notifyItemChanged(i);
    }

    public final void notifyBackImageUpdated(int i) {
        if(mBubblePicker!=null) mBubblePicker.getRenderer().notifySelfChanged(i);
    }

    public final void notifyDataSetChanged() {
        if(mBubblePicker!=null) mBubblePicker.getRenderer().notifyDataSetChanged();
        // remove all items (play animation)
        // add new items (play animation)
    }

    public final void notifyAllItemRemoved() {
        if(mBubblePicker!=null) mBubblePicker.getRenderer().notifyAllItemRemove();
    }

    public final void notifyItemInserted(int i) {
        // new item added
        if(mBubblePicker!=null) mBubblePicker.getRenderer().notifyItemInserted(i);
    }

    /**
     *  Call to notify that one item removed
     * @param i position of new item
     */
    public final void notifyItemRemoved(int i) {
        if(mBubblePicker!=null) mBubblePicker.getRenderer().notifyItemRemoved(i);
    }

    public  abstract int getItemCount();

}
