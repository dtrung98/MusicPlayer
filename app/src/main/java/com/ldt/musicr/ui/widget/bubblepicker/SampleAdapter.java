package com.ldt.musicr.ui.widget.bubblepicker;

import android.content.Context;

import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.PickerAdapter;

public class SampleAdapter extends PickerAdapter<String> {

    public SampleAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean onBindItem(PickerItem item, boolean create, int i) {
        super.onBindItem(item,create,i);
        String name = mData.get(i);
        item.setTitle(name);
        item.setRadiusUnit(PickerItem.SIZE_RANDOM);

        return true;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
