package com.ldt.musicr.ui.tabs.library;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ldt.musicr.R;

import java.util.ArrayList;

public class LibraryPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private ArrayList<Fragment> mData = new ArrayList<>();
    private void initData() {
        mData.add(new SongChildTab());
        mData.add(new PlaylistChildTab());
        mData.add(new ArtistChildTab());
        mData.add(new GenreChildTab());
        mData.add(new FolderChildTab());

    }

    public LibraryPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
        initData();
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mData.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    // Returns the page mTitle for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return mContext.getResources().getString(R.string.songs);
            case 1: return mContext.getResources().getString(R.string.playlists);
            case 2: return mContext.getResources().getString(R.string.artists);
            case 3: return mContext.getResources().getString(R.string.genres);
            case 4: return mContext.getResources().getString(R.string.folders);
            default: return null;
        }

    }
}
