package com.ldt.musicr.ui.maintab.subpages;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ldt.musicr.R;
import com.ldt.musicr.loader.medialoader.SongLoader;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.ui.maintab.MusicServiceNavigationFragment;
import com.ldt.musicr.util.Tool;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class MoreOptionFragment extends MusicServiceNavigationFragment {
    private static final String TAG = "MoreOptionFragment";

    @BindView(R.id.status_bar)
    View mStatusBar;

    @BindView(R.id.root)
    View mRoot;

    public static MoreOptionFragment newInstance() {
        return new MoreOptionFragment();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.screen_setting_advance,container, false);
    }

    private Unbinder mUnbinder;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this,view);
        onPaletteChanged();
    }

    @Override
    public void onDestroyView() {
        if(mUnbinder!=null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        super.onDestroyView();
    }

    @OnClick(R.id.back_button)
    void back() {
        getNavigationController().dismissFragment();
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        mStatusBar.getLayoutParams().height = value;
    }

    @Override
    public void onPaletteChanged() {
        super.onPaletteChanged();
        int color = Tool.getBaseColor();
        ((TextView)mRoot.findViewById(R.id.title)).setTextColor(color);
        ((ImageView)mRoot.findViewById(R.id.back_button)).setColorFilter(color);

    }

    @OnClick(R.id.button_one)
    void ButtonOneClick() {
        if(getContext()!=null) {
            ArrayList<Song> songs = SongLoader.getAllSongs(getContext());
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(songs.get(new Random().nextInt(songs.size())).data);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(1000*25);
                mRoot.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mediaPlayer.release();
                        } catch (Exception ignored) {}
                    }
                },20*1000);
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
                Toasty.error(getContext(),"Couldn't play songs").show();
            }
        }
    }
}
