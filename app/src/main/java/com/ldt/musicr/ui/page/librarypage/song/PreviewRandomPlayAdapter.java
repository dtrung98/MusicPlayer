package com.ldt.musicr.ui.page.librarypage.song;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;

import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.util.Util;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Random;

import butterknife.OnClick;

public class PreviewRandomPlayAdapter extends RecyclerView.Adapter<PreviewRandomPlayAdapter.ItemHolder> {
    private static final String TAG ="PreviewRandomPlayAdapter";
    private Context mContext;

    private ArrayList<Song> mBackUp = new ArrayList<>();
    private ArrayList<Song> mData = new ArrayList<>();


    private Random mRandom = new Random();
    private long[] songIDs;

    public PreviewRandomPlayAdapter(Context context) {
        mContext = context;
    }

    public void shuffle() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            MusicPlayerRemote.openQueue(mData,0,true);
            //MusicPlayer.playAll(mContext, getRealSongIds(), 0, -1, Util.IdType.NA, false);
            randommize();
        },100);
    }

    public interface FirstItemCallBack {
        void onFirstItemCreated(Song song);
    }

    public FirstItemCallBack mCallBack;
    public void setFirstItemCallBack(FirstItemCallBack callBack) {
        mCallBack = callBack;
    }

    public void removeCallBack() {
        mCallBack = null;
    }

    public void setData(ArrayList<Song> songs) {
        mBackUp.clear();
        if(songs!=null) mBackUp.addAll(songs);
        randommize();
    }

    @OnClick(R.id.refresh)
    public void randommize() {
        ArrayList<Song> songs = new ArrayList<>(mBackUp);
        mData.clear();

        for (int i = 0; i < songs.size(); i++) {
            int pos = mRandom.nextInt(songs.size());
            mData.add(songs.remove(pos));
        }

        this.songIDs = getSongIds();

        if(mCallBack!=null&&mData.size()>0) mCallBack.onFirstItemCreated(mData.get(mData.size()-1));
        notifyDataSetChanged();

    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = mData.get(i).id;
        }

        return ret;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_preview_random_play,viewGroup,false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        itemHolder.bind(mData.get(i));
    }

    @Override
    public int getItemCount() {
        return  Math.max(0,mData.size() - 1);
    }
    private long[] getRealSongIds() {
        long[] real = new long[songIDs.length];
        if(real.length!=0) real[0] = songIDs[songIDs.length-1];
        if (songIDs.length - 1 >= 0) {
            System.arraycopy(songIDs, 0, real, 1, songIDs.length - 1);
        }
        return real;
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RoundedImageView mImage;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mImage = (RoundedImageView) itemView;
            mImage.setOnClickListener(this);
        }

        public void bind(Song song) {
            RequestManager requestManager;
            if(mCallBack instanceof Fragment)
               requestManager =  Glide.with((Fragment) mCallBack);
            else if(mContext !=null)
                requestManager = Glide.with(mContext);
            else requestManager = Glide.with(itemView.getContext());

            requestManager.load(Util.getAlbumArtUri(song.albumId))
                    .placeholder(R.drawable.music_empty)
                    .error(R.drawable.music_empty)
                    .into(mImage);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayerRemote.openQueue(mData,getAdapterPosition(),true);
                //MusicPlayer.playAll(mContext, getRealSongIds(), getAdapterPosition(), -1, Util.IdType.NA, false);
                randommize();
            },100);
        }
    }
}
