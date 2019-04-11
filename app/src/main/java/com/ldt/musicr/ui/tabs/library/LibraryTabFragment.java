package com.ldt.musicr.ui.tabs.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.motion.MotionLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;
import com.ldt.musicr.util.Tool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LibraryTabFragment extends SupportFragment {
    private static final String TAG ="LibraryTabFragment";

    @BindView(R.id.back_image)
    ImageView mBackImage;
    @BindView(R.id.search_view)
    SearchView mSearchView;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    LibraryPagerAdapter mPagerAdapter;
    @BindView(R.id.status_bar) View mStatusView;
    @BindView(R.id.root)
    MotionLayout mMotionLayout;

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.library_tab,container,false);
    }

    @OnClick(R.id.search_view)
    void searchViewClicked() {
        mSearchView.onActionViewExpanded();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mStatusView.getLayoutParams().height = Tool.getStatusHeight(getResources());
        mStatusView.requestLayout();
      //  mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();


        //if(true) return;
        mViewPager.setOnTouchListener((v, event) -> getMainActivity().backStackStreamOnTouchEvent(event));
        mPagerAdapter = new LibraryPagerAdapter(getActivity(),getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        // mTabLayout.setTabsFromPagerAdapter(mTabAdapter);//deprecated
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onResume() {
        super.onResume();
        mSearchView.onActionViewExpanded();
        mSearchView.clearFocus();
    }

    public void goToSongTab() {
        mViewPager.setCurrentItem(0,false);
    }
}
