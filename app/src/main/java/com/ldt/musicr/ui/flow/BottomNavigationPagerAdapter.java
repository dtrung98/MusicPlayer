package com.ldt.musicr.ui.flow;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.flow.feature.FeatureTabFragment;
import com.ldt.musicr.ui.flow.library.LibraryTabFragment;
import com.ldt.musicr.ui.flow.setting.SettingTabFragment;
import com.ldt.musicr.ui.widget.navigate.NavigateFragment;

import java.util.ArrayList;

public class BottomNavigationPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public BottomNavigationPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
        initData();
    }

    public ArrayList<NavigateFragment> mData = new ArrayList<>();

    public boolean onBackPressed(int position) {
        if(position<mData.size())
        return mData.get(position).onBackPressed();
        return false;
    }


    private void initData() {
        mData.add(NavigateFragment.newInstance(new FeatureTabFragment()));
        mData.add(NavigateFragment.newInstance(new LibraryTabFragment()));
        mData.add(NavigateFragment.newInstance(new SettingTabFragment()));
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mData.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        if(position>=mData.size()) return null;
        return mData.get(position);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return mContext.getResources().getString(R.string.feature);
            case 1: return mContext.getResources().getString(R.string.library);
            case 2: return mContext.getResources().getString(R.string.settings);
            default:return null;
        }
    }
}
