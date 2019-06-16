package com.ldt.musicr.ui.tabs.library;

import android.content.Context;
import android.os.AsyncTask;
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
import com.ldt.musicr.loader.SongLoader;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.ui.tabs.pager.ResultCallback;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;
import com.ldt.musicr.util.LastFMUtil;
import com.ldt.musicr.util.MusicUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreChildTab extends Fragment {
    public static final String TAG="GenreChildTab";
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.genre_child_tab,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mSwipeRefresh.setOnRefreshListener(this::refresh);
    }

    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefresh ;
    private void refresh() {
    new TestingTask(this).execute();
    }

    private void test() {
        if(getContext()==null) return;
        HashMap<UserKey, Song> hash = new HashMap<>();
        ArrayList<Song> ori = SongLoader.getAllSongs(getContext());

        ArrayList<Song> list = new ArrayList<>(ori);

        for (Song song:
             list) {
            hash.put(new UserKey(song.title,song.id),song);
        }


        Random random = new Random();
        Song[] songs = new Song[ori.size()];
        for (int i = 0; !ori.isEmpty(); i++) {
           songs[i] = ori.remove(random.nextInt(ori.size()));
        }

        int result_one = 0;
        int result_two = 0;
        String one = "";

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {

            for (Song song :
                    songs) {
                Song s = hash.get(new UserKey(song.title, song.id));
                if(i==0) one +=s.title+"\n";
                if (s != null) result_one++;
            }
        }

        long time_one = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {

            for (Song song : songs) {
                int index = list.indexOf(song);
                if (index != -1) {
                    Song s = list.get(index);
                    if (s != null)
                        result_two++;
                }
            }
        }
        long time_two = System.currentTimeMillis();
        Log.d(TAG, "test: total size = "+ songs.length);
        Log.d(TAG, "test: hash = "+(time_one- start)+", found = "+result_one);
        Log.d(TAG, "test: list = "+(time_two-time_one)+", found = "+result_two);
        Log.d(TAG, "test: \n"+one);

        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }
    public static class TestingTask extends AsyncTask<Void,Void,Void> {
        private WeakReference<GenreChildTab> mFragment;
        TestingTask(GenreChildTab tab) {
            mFragment = new WeakReference<>(tab);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(mFragment.get()!=null) {
                mFragment.get().test();
            }

            return null;
        }
    }

    public static class UserKey {
        private final String mUserName;
        private final int mID;

        public UserKey(String mUserName, int mID) {
            this.mUserName = mUserName;
            this.mID = mID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserKey)) return false;
            return (mUserName).equals(((UserKey) o).mUserName) && mID == ((UserKey) o).mID;
        }

        @Override
        public int hashCode() {
            return mUserName.hashCode();
        }
    }

}

