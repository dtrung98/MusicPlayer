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

public class GenreChildTab extends BaseMusicServiceFragment implements PickerAdapter.PickerListener, View.OnLayoutChangeListener {
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

    @BindView(R.id.root)
    View mRoot;

    @BindDimen(R.dimen.minimum_bottom_back_stack_margin)
    float mMinBottomPadding;

    @BindDimen(R.dimen._16dp)
    float m16Dp;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mRoot.addOnLayoutChangeListener(this);
        addPageChangedListener();
        initBubblePicker();
    }

    private MotionLayout mLibraryMotionLayout;

    private void addPageChangedListener() {

        LibraryTabFragment libraryTab = NavigationUtil.getLibraryTab(getActivity());
        if(libraryTab!=null) {
            mLibraryMotionLayout = libraryTab.getMotionLayout();
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
               if(list.length>=mSampleSize)
               list = Arrays.copyOf(list,mSampleSize);
                ((SampleAdapter) mAdapter).setData(Arrays.asList(list));
            }
        }
    }

    public int mSampleSize = 30;
    public Random rnd = new Random();

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
        mRoot.removeOnLayoutChangeListener(this);
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

    @OnClick(R.id.see_in_new_tab)
    void seeInNewTab() {
      /*  SupportFragment sf = BubblePickerFragment.newInstance();
        *//*      SupportFragment sf = ArtistTrialPager.newInstance(artist);*//*
        Fragment parentFragment = getParentFragment();
        if(parentFragment instanceof SupportFragment)
            ((SupportFragment)parentFragment).getNavigationController().presentFragment(sf);*/
        mAdapter.notifyAllItemRemoved();
    }

    @OnClick(R.id.reorder)
    void reorder() {
        mSampleSize = rnd.nextInt(30);
        refreshData();
    }

    @Override
    public void onPickerDeselected(PickerItem item, int position, Object o) {

    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(mLibraryMotionLayout!=null) {
           float progress =  mLibraryMotionLayout.getProgress();
           float bottomMargin = (int) ((1-progress) * mMinBottomPadding);
            ((ViewGroup.MarginLayoutParams)mBubblePicker.getLayoutParams()).bottomMargin = (int) bottomMargin;
            mBubblePicker.requestLayout();
        }
    }
}

