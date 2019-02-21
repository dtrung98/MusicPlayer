package com.ldt.musicr.ui.main.navigate.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.R;
import com.ldt.musicr.loader.PlaylistLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistChildTab extends Fragment {
    private static final String TAG ="PlaylistChildTab";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    PlaylistChildAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_child_tab,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mAdapter = new PlaylistChildAdapter(getActivity(),true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        refreshData();
;    }
    private void refreshData() {
      mAdapter.setData(PlaylistLoader.getPlaylists(getActivity(),true));
    }
}
