package com.ldt.musicr.ui.page.librarypage.genre;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.Item;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.BubblePicker;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;
import com.ldt.musicr.util.NavigationUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GenreChildTab extends BaseMusicServiceFragment implements PickerAdapter.PickerListener {
    public static final String TAG="GenreChildTab";

    @BindView(R.id.bubble_picker)
    BubblePicker mBubblePicker;

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
        addPageChangedListener();
        initBubblePicker();
    }

    private void addPageChangedListener() {
        LibraryTabFragment libraryTab = NavigationUtil.getLibraryTab(getActivity());
        if(libraryTab!=null) {
           ViewPager  parentViewPager =  libraryTab.getViewPager();
           if(parentViewPager!=null) {
               parentViewPager.addOnPageChangeListener(mPagerChangeListener);
               mTabPosition = libraryTab.getPagerAdapter().getData().indexOf(this);
               mIsTabActivated = mTabPosition!=-1 && parentViewPager.getCurrentItem() == mTabPosition;
           }
        }
    }

    private void removePageChangedListener() {
        LibraryTabFragment libraryTab = NavigationUtil.getLibraryTab(getActivity());
        if(libraryTab!=null) {
            ViewPager  parentViewPager =  libraryTab.getViewPager();
            if(parentViewPager!=null)
                parentViewPager.removeOnPageChangeListener(mPagerChangeListener);
        }
    }

    private boolean mIsFirstTime = true;
    private int mTabPosition = -1;
    private boolean mIsTabActivated = false;

    private ViewPager.OnPageChangeListener mPagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            mIsTabActivated = mTabPosition !=-1 && i == mTabPosition;
            if(mIsTabActivated) {
                mBubblePicker.setCenterImmediately(!mIsFirstTime);
                if(mIsFirstTime) {
                    mIsFirstTime = false;
                }
                mBubblePicker.onResume();
            } else mBubblePicker.onPause();
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private PickerAdapter mAdapter;

    @OnClick(R.id.refresh)
    public void refreshData() {
        if(getContext()!=null) {
            if(mAdapter instanceof GenrePickerAdapter)
            ((GenrePickerAdapter) mAdapter).setData(GenreLoader.getAllGenres(getContext()));
            else if (mAdapter instanceof SongPickerAdapter)
                ((SongPickerAdapter) mAdapter).setData(SongLoader.getAllSongs(getContext()));
            else if (mAdapter instanceof ArtistPickerAdapter)
                ((ArtistPickerAdapter) mAdapter).setData(ArtistLoader.getAllArtists(getContext()));
        }
    }

    public void initBubblePicker() {

        mBubblePicker.setBubbleSize(10);
        Item.Companion.setBitmapSize(144f);
        Item.Companion.setTextSizeRatio(40f/280);

        mAdapter = new ArtistPickerAdapter(getContext());
        mAdapter.setListener(this);
        mBubblePicker.setAdapter(mAdapter);
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mIsTabActivated)
        mBubblePicker.onResume();
        else mBubblePicker.onPause();
    }

    @Override
    public void onPause() {
        mBubblePicker.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        removePageChangedListener();
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

    @Override
    public void onPickerDeselected(PickerItem item, int position, Object o) {

    }
}

