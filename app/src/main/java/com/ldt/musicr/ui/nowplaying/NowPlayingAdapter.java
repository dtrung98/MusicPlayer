package com.ldt.musicr.ui.nowplaying;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldt.musicr.R;
import com.ldt.musicr.common.MediaManager;
import com.ldt.musicr.glide.ArtistGlideRequest;
import com.ldt.musicr.glide.GlideApp;
import com.ldt.musicr.loader.medialoader.ArtistLoader;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.utils.ArtworkUtils;
import com.squareup.picasso.Callback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.ItemHolder> implements Callback {
    private static final String TAG ="NowPlayingAdapter";
    private ArrayList<Song> mData = new ArrayList<>();
    private Context mContext;
    public Context getContext() {
        return mContext;
    }
    public void init(Context context) {
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
 /*           Picasso.get().load(Util.getAlbumArtUri(song.albumId))
                    .error(R.drawable.speaker2)
                    .placeholder(R.drawable.music_style)
                    .stableKey("album_id="+song.albumId+"_"+song.dateModified)
                    .into(mImage,NowPlayingAdapter.this);*/

            Artist artist = MediaManager.INSTANCE.getArtist(song.artistId);
            int[] screen = Tool.getScreenSize(mContext);

            ArtworkUtils.getBitmapRequestBuilder(itemView.getContext(), song)
                    .override(screen[1])
                    .placeholder(R.drawable.speaker2)
                    .error(
                            ArtistGlideRequest.Builder.from(GlideApp.with(mContext), artist).requestHighResolutionArt(true).whichImage(1).generateBuilder(mContext).build()
                                    .error(ArtistGlideRequest.Builder.from(GlideApp.with(mContext),artist).requestHighResolutionArt(false).whichImage(1).generateBuilder(mContext).build().error(R.drawable.speaker2)
                    ))
                    .into(mImage);
     /*       ArtistGlideRequest.Builder.from(GlideApp.with(getContext()), mArtist)
                    .tryToLoadOriginal(true)
                    .generateBuilder(getContext())
                    .build()
                    .error(
                                ArtistGlideRequest
                                        .Builder
                                        .from(GlideApp.with(getContext()),mArtist)
                                        .tryToLoadOriginal(false)
                                        .generateBuilder(getContext())
                                        .build())
                    .thumbnail(
                            ArtistGlideRequest
                                    .Builder
                                    .from(GlideApp.with(getContext()), mArtist)
                                    .tryToLoadOriginal(false)
                                    .generateBuilder(getContext())
                                    .build())
                    .into(mBigImage);

            Glide.with(mContext).load(Util.getAlbumArtUri(song.albumId)).apply(options).error(R.drawable.speaker2).placeholder(R.drawable.speaker2).into(mImage);*/


        }
    }
}
