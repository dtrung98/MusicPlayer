package com.ldt.musicr.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.R;
import com.ldt.musicr.activities.MainActivity;
import com.ldt.musicr.activities.SupportFragmentActivity;

/**
 * Created by trung on 8/22/2017.
 */

public class DrawerFragment extends FragmentPlus {

    public static final String TAG = DrawerFragment.class.getSimpleName();
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        rootView.findViewById(R.id.menuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setCurrentMenuOrTab(SupportFragmentActivity.MenuType.CONTAINER);
                mainActivity.pushFragment(new MainScreenFragment(), true);
                mainActivity.openAndCloseDrawer();
            }
        });
        rootView.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setCurrentMenuOrTab(SupportFragmentActivity.MenuType.CONTAINER);
                mainActivity.pushFragment(ShowMusicSongs.Initialize(getActivity()), true);
                mainActivity.openAndCloseDrawer();
            }
        });
        rootView.findViewById(R.id.text_trinhphatnhac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setCurrentMenuOrTab(SupportFragmentActivity.MenuType.CONTAINER);
                mainActivity.pushFragment(new MainScreenFragment(), true);
                mainActivity.openAndCloseDrawer();
            }
        });
        return rootView;
    }

    @Override
    public void onTransitionComplete() {

    }

    @Override
    public StatusTheme setDefaultStatusTheme() {
        return StatusTheme.WhiteIcon;
    }
}