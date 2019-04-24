package com.ldt.musicr.ui.nowplaying;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.ItemHolder> implements Callback {
    private static final String TAG ="NowPlayingAdapter";
    private ArrayList<Song> mData = new ArrayList<>();
    private Context mContext;
    public NowPlayingAdapter(Context context) {
        mContext = context;
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

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_art_now_playing,viewGroup,false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        itemHolder.bind(mData.get(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(Exception e) {

    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView mImage;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        private void bind(Song song) {
            Picasso.get().load(Util.getAlbumArtUri(song.albumId)).error(R.drawable.speaker2).placeholder(R.drawable.speaker2).stableKey("album_id="+song.albumId+"_"+song.dateModified).into(mImage,NowPlayingAdapter.this);
         /*   SongGlideRequest.Builder.from(GlideApp.with(mContext), song)
                    .ignoreMediaStore(false)
                    .build().into(mImage);*/

      /*   RequestOptions options = RequestOptions.overrideOf(Tool.getScreenSize(mContext)[0],Tool.getScreenSize(mContext)[1]).skipMemoryCache(true);
            Glide.with(mContext).load(Util.getAlbumArtUri(song.albumId)).apply(options).error(R.drawable.speaker2).placeholder(R.drawable.speaker2).into(mImage);*/
        }
    }
}
