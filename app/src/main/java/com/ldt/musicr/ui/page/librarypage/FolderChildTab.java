package com.ldt.musicr.ui.page.librarypage;

import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.BubbleGradient;
import com.igalata.bubblepicker.model.PickerItem;
import com.igalata.bubblepicker.rendering.java.gltexture.TextureBubblePicker;

import com.ldt.musicr.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FolderChildTab extends Fragment {
    public static final String TAG ="FolderChildTab";

    @BindView(R.id.bubble_picker)
    TextureBubblePicker mBubblePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.folder_child_tab,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        final String[] titles = getResources().getStringArray(R.array.countries);
        final TypedArray colors = getResources().obtainTypedArray(R.array.colors);

        mBubblePicker.setBubbleSize((int) getResources().getDimension(R.dimen.dp_48));
        mBubblePicker.setBackground(0);
        mBubblePicker.setAdapter(new BubblePickerAdapter() {
            @Override
            public int getTotalCount() {
                return titles.length;
            }

            @NonNull
            @Override
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(titles[position]);
                item.setGradient(new BubbleGradient(colors.getColor((position * 2) % 8, 0),
                        colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL));
                //item.setTypeface(mediumTypeface);
                item.setTextColor( 0xFFFFFFFF);
               // item.setBackgroundImage(ContextCompat.getDrawable(DemoActivity.this, images.getResourceId(position, 0)));
                return item;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mBubblePicker.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBubblePicker.onPause();
    }
}
