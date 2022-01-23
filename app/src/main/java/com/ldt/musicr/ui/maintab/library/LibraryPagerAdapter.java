package com.ldt.musicr.ui.maintab.library;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ldt.musicr.App;
import com.ldt.musicr.R;
import com.ldt.musicr.common.AppConfig;
import com.ldt.musicr.ui.maintab.library.artist.ArtistChildTab;
import com.ldt.musicr.ui.maintab.library.genre.GenreChildTab;
import com.ldt.musicr.ui.maintab.library.playlist.PlaylistChildTab;
import com.ldt.musicr.ui.maintab.library.song.LibrarySongTab;

import java.util.ArrayList;

public class LibraryPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public ArrayList<Fragment> getData() {
        return mData;
    }

    private ArrayList<Fragment> mData = new ArrayList<>();

    private void initData() {
        mData.add(new LibrarySongTab());
        mData.add(new PlaylistChildTab());
        mData.add(new ArtistChildTab());

        if (!AppConfig.hideIncompleteFeature) {
            mData.add(new GenreChildTab());
            mData.add(new FolderChildTab());
        }


    }

    public LibraryPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
            case 0:
                return mContext.getResources().getString(R.string.songs);
            case 1:
                return mContext.getResources().getString(R.string.playlists);
            case 2:
                return mContext.getResources().getString(R.string.artists);
            case 3:
                return mContext.getResources().getString(R.string.genres);
            case 4:
                return mContext.getResources().getString(R.string.folders);
            default:
                return null;
        }

    }
}
