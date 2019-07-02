package com.ldt.musicr.ui.tabs.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.ldt.musicr.R;
import com.ldt.musicr.addon.lastfm.rest.LastFMRestClient;
import com.ldt.musicr.addon.lastfm.rest.model.LastFmArtist;
import com.ldt.musicr.glide.artistimage.ArtistImageFetcher;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.ui.tabs.pager.ResultCallback;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;
import com.ldt.musicr.util.LastFMUtil;
import com.ldt.musicr.util.MusicUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreChildTab extends Fragment {
    private static final String TAG ="ArtistTrialPager";
    // we need these very low values to make sure our artist image loading calls doesn't block the image loading queue
    private static final int TIMEOUT = 750;

    private static final String ARTIST = "artist";

    public static GenreChildTab newInstance(Artist artist) {

        Bundle args = new Bundle();
        if(artist!=null)
            args.putParcelable(ARTIST,artist);

        GenreChildTab fragment = new GenreChildTab();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.image)
    ImageView mImage;

    @BindView(R.id.text)
    TextView mText;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.genre_child_tab,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

    }

}

