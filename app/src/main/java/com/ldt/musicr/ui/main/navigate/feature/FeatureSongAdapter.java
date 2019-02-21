package com.ldt.musicr.ui.main.navigate.feature;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ldt.musicr.R;
import com.ldt.musicr.fragments.SongOptionBottomSheet;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.services.MusicPlayer;
import com.ldt.musicr.util.TimberUtils;
import com.ldt.musicr.util.Tool;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeatureSongAdapter extends RecyclerView.Adapter<FeatureSongAdapter.ItemHolder>{
    private static final String TAG = "SongAdapter";
    public ArrayList<Song> mAllSongs = new ArrayList<>();
    public ArrayList<Song> mData = new ArrayList<>();
    public int currentlyPlayingPosition = 0;
    private Context mContext;
    private long[] songIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private long playlistId;

    private Random mRandom = new Random();

    public FeatureSongAdapter(Context context) {
        this.mContext = context;
    }


    public void setData(List<Song> data) {
        mAllSongs.clear();
        if(data!=null) mAllSongs.addAll(data);

        initializeSong();
    }

    public void initializeSong() {
        mData.clear();
        ArrayList<Song> mTemp = new ArrayList<>(mAllSongs);
        if(mAllSongs.size()<=5) mData.addAll(mAllSongs);
        else for (int i = 0; i < 5; i++) {
            int pos = mRandom.nextInt(mTemp.size());
            mData.add(mTemp.remove(pos));
        }

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
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = mData.get(i).id;
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
            SongOptionBottomSheet sheet =  SongOptionBottomSheet.newInstance();
            sheet.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                    "song_popup_menu");
            sheet.setListener(new SongOptionBottomSheet.BottomSheetListener() {
                                  @Override
                                  public boolean onButtonClick(int id) {
                                      switch (id) {
                                          case R.id.popup_song_remove_playlist:
                                              TimberUtils.removeFromPlaylist(mContext, mData.get(position).id, playlistId);
                                              removeSongAt(position);
                                              notifyItemRemoved(position);
                                              break;
                                          case R.id.popup_song_play:
                                              MusicPlayer.playAll(mContext, songIDs, position, -1, TimberUtils.IdType.NA, false);
                                              break;
                                          case R.id.popup_song_play_next:
                                              long[] ids = new long[1];
                                              ids[0] = mData.get(position).id;
                                              MusicPlayer.playNext(mContext, ids, -1, TimberUtils.IdType.NA);
                                              break;
                                          case R.id.popup_song_goto_album:
                                              //TODO:   NavigationUtils.goToAlbum(mContext, mData.get(position).albumId);
                                              break;
                                          case R.id.popup_song_goto_artist:
                                              //TODO: NavigationUtils.goToArtist(mContext, mData.get(position).artistId);
                                              break;
                                          case R.id.popup_song_addto_queue:
                                              long[] _id = new long[1];
                                              _id[0] = mData.get(position).id;
                                              MusicPlayer.addToQueue(mContext, _id, -1, TimberUtils.IdType.NA);
                                              break;
                                          case R.id.popup_song_addto_playlist:
                                              //TODO: AddPlaylistDialog.newInstance(mData.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                                              break;
                                          case R.id.popup_song_share:
                                              TimberUtils.shareTrack(mContext, mData.get(position).id);
                                              break;
                                          case R.id.popup_song_delete:
                                              long[] deleteIds = {mData.get(position).id};
                                              TimberUtils.showDeleteDialog(mContext, mData.get(position).title, deleteIds, FeatureSongAdapter.this, position);
                                              break;
                                      }
                                      return true;
                                  }
                              }
            );
            ;
            if(true) return;
            final PopupMenu menu = new PopupMenu(mContext, v);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.popup_song_remove_playlist:
                            TimberUtils.removeFromPlaylist(mContext, mData.get(position).id, playlistId);
                            removeSongAt(position);
                            notifyItemRemoved(position);
                            break;
                        case R.id.popup_song_play:
                            MusicPlayer.playAll(mContext, songIDs, position, -1, TimberUtils.IdType.NA, false);
                            break;
                        case R.id.popup_song_play_next:
                            long[] ids = new long[1];
                            ids[0] = mData.get(position).id;
                            MusicPlayer.playNext(mContext, ids, -1, TimberUtils.IdType.NA);
                            break;
                        case R.id.popup_song_goto_album:
                            //TODO:   NavigationUtils.goToAlbum(mContext, mData.get(position).albumId);
                            break;
                        case R.id.popup_song_goto_artist:
                            //TODO: NavigationUtils.goToArtist(mContext, mData.get(position).artistId);
                            break;
                        case R.id.popup_song_addto_queue:
                            long[] id = new long[1];
                            id[0] = mData.get(position).id;
                            MusicPlayer.addToQueue(mContext, id, -1, TimberUtils.IdType.NA);
                            break;
                        case R.id.popup_song_addto_playlist:
                            //TODO: AddPlaylistDialog.newInstance(mData.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                            break;
                        case R.id.popup_song_share:
                            TimberUtils.shareTrack(mContext, mData.get(position).id);
                            break;
                        case R.id.popup_song_delete:
                            long[] deleteIds = {mData.get(position).id};
                            TimberUtils.showDeleteDialog(mContext, mData.get(position).title, deleteIds, FeatureSongAdapter.this, position);
                            break;
                    }
                    return false;
                }
            });
            menu.inflate(R.menu.popup_song);

            menu.show();
            if(isPlaylist)
                menu.getMenu().findItem(R.id.popup_song_remove_playlist).setVisible(true);
        });
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title) TextView mTitle;
        @BindView(R.id.artist) TextView mArtist;
        @BindView(R.id.image) ImageView mImage;
        @BindView(R.id.more) View mMenuButton;
        @BindView(R.id.quick_play_pause) ImageView mQuickPlayPause;

        @OnClick(R.id.quick_play_pause)
        void clickQuickPlayPause() {
            if(MusicPlayer.getCurrentAudioId()==mData.get(getAdapterPosition()).id) {
                MusicPlayer.playOrPause();
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
                MusicPlayer.playAll(mContext, songIDs, getAdapterPosition(), -1,TimberUtils.IdType.NA, false);
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
            mArtist.setText(song.artistName);

            RequestManager requestManager;
            if(mContext instanceof Activity)
                requestManager = Glide.with((Activity) mContext);
            else requestManager = Glide.with(itemView.getContext());

            requestManager.load(TimberUtils.getAlbumArtUri(song.albumId))
                    .placeholder(R.drawable.music_empty)
                    .error(R.drawable.music_empty)
                    .into(mImage);

            ((RippleDrawable) itemView.getBackground()).setColor(ColorStateList.valueOf(Tool.getSurfaceColor()));
            setOnPopupMenuListener(this,getAdapterPosition());
            checkQuickPlayPause();
        }
        public void checkQuickPlayPause() {
            if(MusicPlayer.getCurrentAudioId()==mData.get(getAdapterPosition()).id) {
                currentlyPlayingPosition = getAdapterPosition();
                mTitle.setTextColor(Tool.getSurfaceColor());

                if(MusicPlayer.isPlaying()) {
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
