package com.ldt.musicr.ui.page.featurepage;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.R;
import com.ldt.musicr.service.MusicServiceEventListener;
import com.ldt.musicr.ui.AppActivity;
import com.ldt.musicr.ui.CardLayerController;
import com.ldt.musicr.ui.page.CardLayerFragment;
import com.ldt.musicr.ui.page.MusicServiceNavigationFragment;
import com.ldt.musicr.ui.page.subpages.singleplaylist.SinglePlaylistCardLayerFragment;
import com.ldt.musicr.loader.medialoader.PlaylistLoader;
import com.ldt.musicr.loader.medialoader.SongLoader;
import com.ldt.musicr.model.Playlist;
import com.ldt.musicr.ui.page.subpages.singleplaylist.SinglePlaylistFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeatureTabFragment extends MusicServiceNavigationFragment implements FeaturePlaylistAdapter.PlaylistClickListener, MusicServiceEventListener {
    private static final String TAG = "FeatureTabFragment";

    @BindView(R.id.status_bar)
    View mStatusView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.scroll_view)
    NestedScrollView mNestedScrollView;

    @OnClick(R.id.search)
    void showSearchScreen() {
        if (getActivity() instanceof AppActivity) {
            CardLayerController.CardLayerAttribute attribute = ((AppActivity) getActivity()).getCardLayerController().addCardLayerFragment(SinglePlaylistCardLayerFragment.newInstance(PlaylistLoader.getAllPlaylistsWithAuto(requireContext()).get(0), null), 0);
            attribute.animateToMax();
        }
    }

    FeatureLinearHolder mFeatureLinearHolder;

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.screen_feature_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.flatOrange);
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshData);
        mSwipeRefreshLayout.setEnabled(false);
        ViewCompat.setOnApplyWindowInsetsListener(mNestedScrollView, (v, insets) -> {
            v.setPadding(
                    insets.getSystemWindowInsetLeft(),
                    0,
                    insets.getSystemWindowInsetRight(),
                    (int) (insets.getSystemWindowInsetBottom() + v.getResources().getDimension(R.dimen.bottom_back_stack_spacing)));
            return ViewCompat.onApplyWindowInsets(v, insets);
        });

        mFeatureLinearHolder = new FeatureLinearHolder(getActivity(), mNestedScrollView);
        mFeatureLinearHolder.setPlaylistItemClick(this);

        refreshData();
    }

    private void refreshData() {

        if (getActivity() != null) {
            mFeatureLinearHolder.setSuggestedPlaylists(PlaylistLoader.getAllPlaylistsWithAuto(getActivity()));
            mFeatureLinearHolder.setSuggestedSongs(SongLoader.getAllSongs(getActivity()));
        }

        mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onSetStatusBarMargin(int value) {
        if (mStatusView != null) {
            mStatusView.getLayoutParams().height = value;
            mStatusView.requestLayout();
        }
    }

    @Override
    public void onClickPlaylist(Playlist playlist, @org.jetbrains.annotations.Nullable Bitmap bitmap) {
        boolean showInCardLayer = false;

        if(!showInCardLayer) {
            getNavigationController().presentFragment(SinglePlaylistFragment.newInstance(playlist, bitmap));
        } else if (getActivity() instanceof AppActivity) {
            CardLayerFragment sf = SinglePlaylistCardLayerFragment.newInstance(playlist, bitmap);

            CardLayerController.CardLayerAttribute attribute = ((AppActivity) getActivity()).getCardLayerController().addCardLayerFragment(sf, 0);
            attribute.expandImmediately();
            attribute.animateToMax();
        }
    }

    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onQueueChanged() {

    }

    @Override
    public void onPlayingMetaChanged() {

    }

    @Override
    public void onPlayStateChanged() {

    }

    @Override
    public void onRepeatModeChanged() {

    }

    @Override
    public void onShuffleModeChanged() {

    }

    @Override
    public void onMediaStoreChanged() {
        refreshData();
    }

}
