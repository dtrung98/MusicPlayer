package com.ldt.musicr.ui.page.librarypage.genre;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ldt.musicr.R;
import com.ldt.musicr.loader.medialoader.ArtistLoader;
import com.ldt.musicr.loader.medialoader.GenreLoader;
import com.ldt.musicr.loader.medialoader.SongLoader;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.model.Genre;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.ui.page.BaseMusicServiceFragment;
import com.ldt.musicr.ui.page.librarypage.LibraryTabFragment;
import com.ldt.musicr.ui.page.subpages.ArtistPagerFragment;
import com.ldt.musicr.ui.widget.bubblepicker.SampleAdapter;
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.CircleRenderItem;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubblePicker;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.PickerAdapter;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;
import com.ldt.musicr.util.NavigationUtil;

import java.util.Arrays;
import java.util.Random;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GenreChildTab extends BaseMusicServiceFragment {
    public static final String TAG="GenreChildTab";

    public static GenreChildTab newInstance() {
        return new GenreChildTab();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.genre_child_tab,container,false);
    }

    @BindDimen(R.dimen.minimum_bottom_back_stack_margin)
    float mMinBottomPadding;

    @BindDimen(R.dimen._16dp)
    float m16Dp;

    private Unbinder mUnbinder;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this,view);
    }

    @Override
    public void onMediaStoreChanged() {}

    @Override
    public void onDestroyView() {

        if(mUnbinder!=null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        super.onDestroyView();
    }
}

