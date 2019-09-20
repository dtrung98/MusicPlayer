package com.ldt.musicr.ui.page.librarypage;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ldt.bubblepicker.BubblePickerListener;
import com.ldt.bubblepicker.adapter.BubblePickerAdapter;
import com.ldt.bubblepicker.model.BubbleGradient;
import com.ldt.bubblepicker.model.PickerItem;
import com.ldt.bubblepicker.rendering.BubblePicker;
import com.ldt.bubblepicker.rendering.Item;
import com.ldt.bubblepicker.rendering.java.gltexture.TextureBubblePicker;
import com.ldt.musicr.R;
import com.ldt.musicr.loader.medialoader.GenreLoader;
import com.ldt.musicr.loader.medialoader.SongLoader;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.model.Genre;
import com.ldt.musicr.model.Song;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenreChildTab extends Fragment {
    public static final String TAG="GenreChildTab";

    private final static String ROBOTO_BOLD = "roboto_bold.ttf";
    private final static String ROBOTO_MEDIUM = "roboto_medium.ttf";
    private final static String ROBOTO_REGULAR = "roboto_regular.ttf";

    public Typeface mBoldTypeface;
    public Typeface mMediumTypeface;
    public Typeface mRegularTypeface;

    private void initTypefaces() {
        Context context = getContext();
        if(context !=null) {
            AssetManager assets = context.getAssets();
            mBoldTypeface = Typeface.createFromAsset(assets,ROBOTO_BOLD);
            mMediumTypeface = Typeface.createFromAsset(assets,ROBOTO_MEDIUM);
            mRegularTypeface = Typeface.createFromAsset(assets,ROBOTO_REGULAR);
        }
    }

    @BindView(R.id.bubble_picker)
    TextureBubblePicker mBubblePicker;

    public static GenreChildTab newInstance() {
        return new GenreChildTab();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.genre_child_tab,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        initTypefaces();
        initBubblePicker();
    }

    private BubblePickerAdapter mAdapter;
    private ArrayList<Genre> mGenres = new ArrayList<>();

    public void refreshData() {
        if(getContext()!=null) {
            mGenres.clear();
            mGenres.addAll(GenreLoader.getAllGenres(getContext()));
        }
    }

    public void initBubblePicker() {
        if(mBubblePicker==null) return;
        refreshData();

        String[] titles = getResources().getStringArray(R.array.countries);

        TypedArray colors = getResources().obtainTypedArray(R.array.colors);

        mBubblePicker.setBubbleSize(18);
        Item.Companion.setBitmapSize(128f);
        Item.Companion.setTextSizeRatio(40f/280);

        mAdapter = new BubblePickerAdapter() {

            @Override
            public int getTotalCount() {
                return mGenres.size();
            }

            @Override
            @NonNull
            public PickerItem getItem(int position) {
                PickerItem item = new PickerItem();
                item.setTitle(mGenres.get(position).name);
                item.setGradient( new BubbleGradient(colors.getColor((position * 2) % 8, 0),
                            colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL));
                item.setTypeface(mMediumTypeface);

                 item.setTextColor(ContextCompat.getColor(mBubblePicker.getContext(), android.R.color.white));
                    return item;
                }
            };
        mBubblePicker.setAdapter(mAdapter);

        colors.recycle();


        mBubblePicker.setListener(new BubblePickerListener() {
            @Override
            public void onBubbleSelected(@NotNull PickerItem pickerItem) {

            }

            @Override
            public void onBubbleDeselected(@NotNull PickerItem pickerItem) {

            }

        });
    }

}

