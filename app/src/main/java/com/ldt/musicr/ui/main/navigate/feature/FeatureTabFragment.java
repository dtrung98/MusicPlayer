package com.ldt.musicr.ui.main.navigate.feature;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.R;
import com.ldt.musicr.fragments.PlaylistPagerFragment;
import com.ldt.musicr.loader.PlaylistLoader;
import com.ldt.musicr.loader.SongLoader;
import com.ldt.musicr.model.Playlist;
import com.ldt.musicr.ui.main.navigate.OnClickItemListener;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeatureTabFragment extends SupportFragment implements FeaturePlaylistAdapter.PlaylistClickListener {
    private static final String TAG ="FeatureTabFragment";

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.feature_tab_fragment, container, false);
    }
    @BindView(R.id.scroll_view)
    NestedScrollView mNestedScrollView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.FlatOrange);
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshData);

        mFeatureLinearHolder = new FeatureLinearHolder(getActivity(),mNestedScrollView);
        mFeatureLinearHolder.setPlaylistItemClick(this);

        refreshData();
    }

    private void refreshData() {
        mFeatureLinearHolder.setSuggestedPlaylists(PlaylistLoader.getPlaylists(getActivity(),true));
        mFeatureLinearHolder.setSuggestedSongs(SongLoader.getAllSongs(getActivity()));
        mSwipeRefreshLayout.setRefreshing(false);
    }
    FeatureLinearHolder mFeatureLinearHolder;
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @BindView(R.id.status_bar) View mStatusView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    public void onSetStatusBarMargin(int value) {
        if(mStatusView!=null) {
            mStatusView.getLayoutParams().height = value;
            mStatusView.requestLayout();
        }
    }

    @Override
    public void onClickPlaylist(Playlist playlist, @org.jetbrains.annotations.Nullable Bitmap bitmap) {
        SupportFragment sf = PlaylistPagerFragment.newInstance(playlist,bitmap);
        getNavigationController().presentFragment(sf);
    }
}
