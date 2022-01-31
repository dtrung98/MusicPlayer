package com.ldt.musicr.ui.maintab.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldt.musicr.R;
import com.ldt.musicr.common.AppConfig;
import com.ldt.musicr.notification.EventKey;
import com.ldt.musicr.ui.floating.SearchFragment;
import com.ldt.musicr.ui.maintab.library.artist.ArtistChildTab;
import com.ldt.musicr.ui.maintab.library.genre.GenreChildTab;
import com.ldt.musicr.ui.maintab.library.playlist.PlaylistChildTab;
import com.ldt.musicr.ui.maintab.library.song.LibrarySongTab;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.NavigationFragment;
import com.zalo.gitlabmobile.notification.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LibraryTabFragment extends NavigationFragment {
    private static final String TAG ="LibraryTabFragment";

    @BindView(R.id.back_image)
    ImageView mBackImage;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    public LibraryPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    private LibraryPagerAdapter mPagerAdapter;
    @BindView(R.id.status_bar) View mStatusView;

    public MotionLayout getMotionLayout() {
        return mMotionLayout;
    }

    @BindView(R.id.root)
    MotionLayout mMotionLayout;

    @BindView(R.id.search_view) View searchView;

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.screen_tab_library,container,false);
    }

    @OnClick(R.id.search_view)
    void searchLikelyViewClicked() {
        new SearchFragment().show(getParentFragmentManager(), "search");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
      //  mSearchView.onActionViewExpanded();
       // mSearchView.clearFocus();
        mStatusView.getLayoutParams().height = AppConfig.getSystemBarsInset()[1];
        mStatusView.requestLayout();
      //  mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();


        //if(true) return;
        mViewPager.setOnTouchListener((v, event) -> getMainActivity().dispatchOnTouchEvent(event));
        mPagerAdapter = new LibraryPagerAdapter(getActivity(),getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        // mTabLayout.setTabsFromPagerAdapter(mTabAdapter);//deprecated
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public Fragment navigateToTab(int item) {
        if(item<mPagerAdapter.getCount()) {
            mViewPager.setCurrentItem(item, false);
           return mPagerAdapter.getItem(item);
        }
        return null;
    }

    public Fragment navigateToTab(final String tag) {
        switch (tag) {
            case LibrarySongTab.TAG:
                 return navigateToTab(0);
            case PlaylistChildTab.TAG:
                return navigateToTab(1);
            case ArtistChildTab.TAG:
                return navigateToTab(2);
            case GenreChildTab.TAG:
                return navigateToTab(3);
            case FolderChildTab.TAG:
                return navigateToTab(4);
             default:
                 return null;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(MessageEvent event) {
        if(event.getKey() == EventKey.OnSearchInterfaceAppeared.INSTANCE) {
            searchView.setVisibility(View.INVISIBLE);
        } else if(event.getKey() == EventKey.OnSearchInterfaceDisappeared.INSTANCE) {
            searchView.setVisibility(View.VISIBLE);
        }
    }
}
