package com.ldt.musicr.ui.maintab.feature;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldt.musicr.R;
import com.ldt.musicr.helper.menu.SongMenuHelper;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.ui.bottomsheet.OptionBottomSheet;
import com.ldt.musicr.model.Song;

import com.ldt.musicr.util.Tool;
import com.ldt.musicr.utils.ArtworkUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeatureSongAdapter extends RecyclerView.Adapter<FeatureSongAdapter.ItemHolder>{
    private static final String TAG = "SongAdapter";
    public ArrayList<Song> mAllSongs = new ArrayList<>();
    public ArrayList<Song> mData = new ArrayList<>();
    public int currentlyPlayingPosition = 0;

    public Context getContext() {
        return mContext;
    }

    public void init(Context context) {
        mContext = context;
    }

    private Context mContext;
    private long[] songIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private long playlistId;

    public void setData(List<Song> data) {
        mAllSongs.clear();
        if(data!=null) mAllSongs.addAll(data);

        initializeSong();
    }

    public void initializeSong() {
        mData.clear();

        Collections.shuffle(mAllSongs);

        int size = mAllSongs.size();
        for(int i = 0;i<4&&i<size;i++)
            mData.add(mAllSongs.get(i));

        this.songIDs = getSongIds();

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public int getAllItemCount() {
        return mAllSongs.size();
    }

    public long[] getSongIds() {
        long[] ret = new long[mAllSongs.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = mAllSongs.get(i).id;
        }

        return ret;
    }

    @NotNull
    @Override
    public ItemHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feature_song,viewGroup,false);
        return new ItemHolder(v);
    }
    @Override
    public void onBindViewHolder(@NotNull ItemHolder itemHolder, int i) {
        itemHolder.bind(mData.get(i));
    }


    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {
        itemHolder.mMenuButton.setOnClickListener(v -> {
            OptionBottomSheet
                    .newInstance(SongMenuHelper.SONG_OPTION,mData.get(position))
                    .show(((AppCompatActivity)mContext).getSupportFragmentManager(), "song_popup_menu");
        });
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title) TextView mTitle;
        @BindView(R.id.description) TextView mDescription;
        @BindView(R.id.image) ImageView mImage;
        @BindView(R.id.menu_button) View mMenuButton;
        @BindView(R.id.quick_play_pause) ImageView mQuickPlayPause;

        @OnClick(R.id.quick_play_pause)
        void clickQuickPlayPause() {
            if(MusicPlayerRemote.getCurrentSong().id==mData.get(getAdapterPosition()).id) {
                MusicPlayerRemote.playOrPause();
                checkQuickPlayPause();
            } else onClick(itemView);
        }

        public ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayerRemote.openQueue(mAllSongs,getAdapterPosition(),true);
                Handler handler1 = new Handler() ;
                handler1.postDelayed(() -> {
                    notifyItemChanged(currentlyPlayingPosition);
                    notifyItemChanged(getAdapterPosition());
                    currentlyPlayingPosition = getAdapterPosition();
                },50);
            },100);
        }

        public void bind(Song song) {

            mTitle.setText(song.title);
            mDescription.setText(song.artistName);

            ArtworkUtils.getBitmapRequestBuilder(mImage.getContext(), song)
                    .placeholder(R.drawable.music_empty)
                    .error(R.drawable.music_empty)
                    .into(mImage);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((RippleDrawable) itemView.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
            } else {
                //TODO: Below Android L
            }
            setOnPopupMenuListener(this,getAdapterPosition());
            checkQuickPlayPause();
        }
        public void checkQuickPlayPause() {
            if(MusicPlayerRemote.getCurrentSong().id==mData.get(getAdapterPosition()).id) {
                currentlyPlayingPosition = getAdapterPosition();
                mTitle.setTextColor(Tool.getBaseColor());

                if(MusicPlayerRemote.isPlaying()) {
                    mQuickPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                } else {
                    mQuickPlayPause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                }
            }
            else {
                mQuickPlayPause.setImageDrawable(null);
                mTitle.setTextColor(mContext.getResources().getColor(R.color.FlatWhite));
            }
        }
    }

    public Song getSongAt(int i) {
        return mData.get(i);
    }

    public void addSongTo(int i, Song song) {
        mData.add(i, song);
    }

    public void removeSongAt(int i) {
        mData.remove(i);
    }

}
