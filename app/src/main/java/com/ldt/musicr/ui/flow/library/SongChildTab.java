package com.ldt.musicr.ui.flow.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ldt.musicr.R;
import com.ldt.musicr.loader.SongLoader;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.services.MusicPlayer;
import com.ldt.musicr.services.MusicStateListener;
import com.ldt.musicr.ui.BaseActivity;
import com.ldt.musicr.util.Utils;
import com.ldt.musicr.util.Animation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongChildTab extends Fragment implements PreviewRandomPlayAdapter.FirstItemCallBack, MusicStateListener {
    private static final String TAG ="SongChildTab";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

//    @BindView(R.id.preview_shuffle_list)
//    RecyclerView mPreviewRecyclerView;

    @BindView(R.id.refresh)
    ImageView mRefresh;

    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.artist)
    TextView mArtist;



//    @BindView(R.id.top_background) View mTopBackground;
//    @BindView(R.id.bottom_background) View mBottomBackground;
//    @BindView(R.id.random_header) View mRandomHeader;
//    @BindView(R.id.shuffle_button) View ShuffleButton;


    @OnClick({R.id.preview_random_panel})
     void shuffle() {
        mSongRecyclerAdapter.shuffle();
    }


    SongAdapter mSongRecyclerAdapter;
//    PreviewRandomPlayAdapter mPreviewAdapter;

    @OnClick(R.id.refresh)
    void refresh() {
        mRefresh.animate().rotationBy(360).setInterpolator(Animation.getInterpolator(6)).setDuration(650);

        mRefresh.postDelayed(mSongRecyclerAdapter::randommize,300);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.song_child_tab,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        mSongRecyclerAdapter = new SongAdapter(getActivity());
        mSongRecyclerAdapter.setCallBack(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mSongRecyclerAdapter);

        refreshData();
    }
    private void refreshData() {
        ArrayList<Song> songs = SongLoader.getAllSongs(getActivity());
        mSongRecyclerAdapter.setData(songs);
        showOrHidePreview(!songs.isEmpty());

    }
    private void showOrHidePreview(boolean show) {
        int v = show ? View.VISIBLE : View.GONE;

            mImage.setVisibility(v);
            mRefresh.setVisibility(v);
            mTitle.setVisibility(v);
            mArtist.setVisibility(v);
    }

    @Override
    public void onFirstItemCreated(Song song) {
        mTitle.setText(song.title);
        mArtist.setText(song.artistName);

        Glide.with(this)
                .load(Utils.getAlbumArtUri(song.albumId))
                .placeholder(R.drawable.music_empty)
                .error(R.drawable.music_empty)
                .into(mImage);

    }

    @Override
    public void onResume() {
        super.onResume();
       Activity a = getActivity();
       if(a instanceof BaseActivity)
           ((BaseActivity)a).addMusicStateListener(this);
    }

    @Override
    public void onPause() {
        Activity a = getActivity();
        if(a instanceof BaseActivity)
            ((BaseActivity)a).removeMusicStateListener(this);
        super.onPause();
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        Log.d(TAG, "onMetaChanged: is_playing = "+ MusicPlayer.isPlaying()+", song_name = "+MusicPlayer.getTrackName());
    }

}
