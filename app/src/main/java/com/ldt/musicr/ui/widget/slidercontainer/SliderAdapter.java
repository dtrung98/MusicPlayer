package com.ldt.musicr.ui.widget.slidercontainer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.Util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends PagerAdapter {
    private static final String TAG ="SliderAdapter";

    private Context mContext;

    private ArrayList<Song> mData = new ArrayList<>();

    public SliderAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Song> data) {
        if(mData.equals(data)) {
            Log.d(TAG, "setData: equal");
            return;
        }
        mData.clear();
        if(data!=null) {
            mData.addAll(data);
        }
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @NotNull
    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final Song song = mData.get(position);

        View cardLayout =  LayoutInflater.from(mContext).inflate(R.layout.item_art_now_playing, container, false);
        ImageView image =(cardLayout instanceof ImageView) ?((ImageView)cardLayout) : cardLayout.findViewById(R.id.image);

        Glide.with(mContext).load(Util.getAlbumArtUri(song.albumId)).error(R.drawable.music_empty).placeholder(R.drawable.music_empty).into(image);
        container.addView(cardLayout);
        return cardLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
