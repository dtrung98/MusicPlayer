package com.ldt.musicr.ui.page.subpages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ldt.musicr.R;
import com.ldt.musicr.loader.medialoader.ArtistLoader;
import com.ldt.musicr.loader.medialoader.GenreLoader;
import com.ldt.musicr.loader.medialoader.SongLoader;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.model.Genre;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.ui.page.BaseMusicServiceSupportFragment;
import com.ldt.musicr.ui.page.librarypage.genre.ArtistPickerAdapter;
import com.ldt.musicr.ui.page.librarypage.genre.GenrePickerAdapter;
import com.ldt.musicr.ui.page.librarypage.genre.SongPickerAdapter;
import com.ldt.musicr.ui.widget.bubblepicker.SampleAdapter;
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubblePicker;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.CircleRenderItem;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.PickerAdapter;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;

import java.util.Arrays;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BubblePickerFragment extends BaseMusicServiceSupportFragment  implements PickerAdapter.PickerListener {
    public static final String TAG="GenreChildTab";

    @BindView(R.id.bubble_picker)
    BubblePicker mBubblePicker;

    public static BubblePickerFragment newInstance() {
        return new BubblePickerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.genre_child_tab,container,false);
    }

    @BindView(R.id.root)
    View mRoot;

    @BindDimen(R.dimen.minimum_bottom_back_stack_margin)
    float mMinBottomPadding;

    @BindDimen(R.dimen._16dp)
    float m16Dp;

    @BindView(R.id.see_in_new_tab)
    View mSeeInNewTabView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mRefreshButton.setVisibility(View.GONE);
        mSeeInNewTabView.setVisibility(View.GONE);
        initBubblePicker();
    }

    private PickerAdapter mAdapter;

    @BindView(R.id.refresh)
    public View mRefreshButton;

    @OnClick(R.id.refresh)
    public void refreshData() {
        if(getContext()!=null) {
            if(mAdapter instanceof GenrePickerAdapter)
                ((GenrePickerAdapter) mAdapter).setData(GenreLoader.getAllGenres(getContext()));
            else if (mAdapter instanceof SongPickerAdapter)
                ((SongPickerAdapter) mAdapter).setData(SongLoader.getAllSongs(getContext()));
            else if (mAdapter instanceof ArtistPickerAdapter)
                ((ArtistPickerAdapter) mAdapter).setData(ArtistLoader.getAllArtists(getContext()));
            else if(mAdapter instanceof SampleAdapter) {
                String[] list = getResources().getStringArray(R.array.genres);
                if(list.length>=30)
                    list = Arrays.copyOf(list,30);
                ((SampleAdapter) mAdapter).setData(Arrays.asList(list));
            }
        }
    }


    public void initBubblePicker() {

        CircleRenderItem.Companion.setBitmapSize(144f);
        CircleRenderItem.Companion.setTextSizeRatio(40f/280);

        mAdapter = new SampleAdapter(getContext());
        mAdapter.setListener(this);
        mBubblePicker.setAdapter(mAdapter);
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
         mBubblePicker.onResume();
    }

    @Override
    public void onPause() {
        mBubblePicker.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mAdapter.destroy();
        mAdapter = null;
        super.onDestroyView();
    }

    @Override
    public void onMediaStoreChanged() {
        refreshData();
    }

    @Override
    public void onPickerSelected(PickerItem item, int position, Object o) {
        if(o instanceof Genre) {

        } else if(o instanceof Artist) {
            SupportFragment sf = ArtistPagerFragment.newInstance((Artist) o);
            /*      SupportFragment sf = ArtistTrialPager.newInstance(artist);*/
            Fragment parentFragment = getParentFragment();
            if(parentFragment instanceof SupportFragment)
                ((SupportFragment)parentFragment).getNavigationController().presentFragment(sf);
        } else if(o instanceof Song) {

        }
    }

    @OnClick(R.id.see_in_new_tab)
    void seeInNewTab() {

    }

    @Override
    public void onPickerDeselected(PickerItem item, int position, Object o) {

    }
}
