package com.ldt.musicr.ui.nowplaying;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldt.musicr.R;
import com.ldt.musicr.misc.CustomFragmentStatePagerAdapter;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.Utils;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CoverPagerAdapter extends PagerAdapter {

    private ArrayList<Song> mData = new ArrayList<>();
    private Context mContext;

    CoverPagerAdapter(Context context) {
        mContext = context;
    }

    public CoverPagerAdapter(Context context, List<Song> songs) {
        mContext = context;
        mData.addAll(songs);
    }

    public void setData(List<Song> data) {
        if(mData.equals(data)) {
            return;
        }
        mData.clear();
        if(data!=null) {
            mData.addAll(data);
        }
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_art_now_playing, container, false);
        ImageView image = view.findViewById(R.id.image);
        Picasso.get().load(Utils.getAlbumArtUri(mData.get(position).albumId)).error(R.drawable.speaker2).placeholder(R.drawable.speaker2).into(image);
        container.addView(view);
        return view;
    }

    public static class AlbumCoverFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final String SONG_ARG = "song";

        private Unbinder unbinder;

        @BindView(R.id.image)
        ImageView mImage;

        private boolean isColorReady;
        private int color;
        private Song song;
        private ColorReceiver colorReceiver;
        private int request;

        public static AlbumCoverFragment newInstance(final Song song) {
            AlbumCoverFragment frag = new AlbumCoverFragment();
            final Bundle args = new Bundle();
            args.putParcelable(SONG_ARG, song);
            frag.setArguments(args);
            return frag;
        }



        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(getArguments() !=null)
            song = getArguments().getParcelable(SONG_ARG);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.item_art_now_playing, container, false);
            unbinder = ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            loadAlbumCover();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
            colorReceiver = null;
        }

        private void loadAlbumCover() {
       //     Picasso.get().load(Utils.getAlbumArtUri(song.albumId)).error(R.drawable.speaker2).placeholder(R.drawable.speaker2).into(mImage);

//            SongGlideRequest.Builder.from(Glide.with(this), song)
//                    .checkIgnoreMediaStore(getActivity())
//                    .generatePalette(getActivity()).build()
//                    .into(new PhonographColoredTarget(mImage) {
//                        @Override
//                        public void onColorReady(int color) {
//                            setColor(color);
//                        }
//                    });

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        }

        private void setColor(int color) {
            this.color = color;
            isColorReady = true;
            if (colorReceiver != null) {
                colorReceiver.onColorReady(color, request);
                colorReceiver = null;
            }
        }

        public void receiveColor(ColorReceiver colorReceiver, int request) {
            if (isColorReady) {
                colorReceiver.onColorReady(color, request);
            } else {
                this.colorReceiver = colorReceiver;
                this.request = request;
            }
        }

        public interface ColorReceiver {
            void onColorReady(int color, int request);
        }
    }
}

