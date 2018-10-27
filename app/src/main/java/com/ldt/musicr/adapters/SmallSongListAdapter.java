package com.ldt.musicr.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.R;
import com.ldt.musicr.activities.BaseActivity;
import com.ldt.musicr.fragments.RoundedBottomSheetDialogFragment;
import com.ldt.musicr.models.Song;
import com.ldt.musicr.services.MusicPlayer;
import com.ldt.musicr.utils.NavigationUtils;
import com.ldt.musicr.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class SmallSongListAdapter extends RecyclerView.Adapter<SmallSongListAdapter.ItemHolder> {
    private static final String TAG = "SmallSongListAdapter";
    public int currentlyPlayingPosition;
    public List<Song> arraylist;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private long playlistId;
    public SmallSongListAdapter(AppCompatActivity context, List<Song> arraylist, boolean isPlaylistSong, boolean animate) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isPlaylist = isPlaylistSong;
        this.songIDs = getSongIds();
        this.animate = animate;

    }
    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
       // if(isPlaylist&&true) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_n1,viewGroup,false);
            ItemHolder ml = new ItemHolder(v);
            return ml;
       // }
    }
    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
      //  StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams)itemHolder.root.getLayoutParams();
       // p.setFullSpan(true);

        Song localItem = arraylist.get(i);
        itemHolder.count.setText((i+1)+"");
        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(localItem.albumId).toString(),itemHolder.albumArt,new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(R.drawable.ic_empty_music2).resetViewBeforeLoading(true).build());

        if(MusicPlayer.getCurrentAudioId()==localItem.id) {
            currentlyPlayingPosition = i;
            itemHolder.title.setTextColor(Tool.getSurfaceColor());
            itemHolder.quickPlayPause.setVisibility(View.VISIBLE);
            if(MusicPlayer.isPlaying()) {
            }
        }
        else {
            itemHolder.quickPlayPause.setVisibility(View.GONE);
            itemHolder.title.setTextColor(Color.BLACK);
        }
        ((RippleDrawable) itemHolder.root.getBackground()).setColor(ColorStateList.valueOf(Tool.getSurfaceColor()));
        setOnPopupMenuListener(itemHolder,i);
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            RoundedBottomSheetDialogFragment sheet =  RoundedBottomSheetDialogFragment.newInstance();
            sheet.show(mContext.getSupportFragmentManager(),
                        "song_popup_menu");
            sheet.setListener(new RoundedBottomSheetDialogFragment.BottomSheetListener() {
                @Override
                public boolean onButtonClick(int id) {
                    switch (id) {
                        case R.id.popup_song_remove_playlist:
                            TimberUtils.removeFromPlaylist(mContext, arraylist.get(position).id, playlistId);
                            removeSongAt(position);
                            notifyItemRemoved(position);
                            break;
                        case R.id.popup_song_play:
                            MusicPlayer.playAll(mContext, songIDs, position, -1, TimberUtils.IdType.NA, false);
                            break;
                        case R.id.popup_song_play_next:
                            long[] ids = new long[1];
                            ids[0] = arraylist.get(position).id;
                            MusicPlayer.playNext(mContext, ids, -1, TimberUtils.IdType.NA);
                            break;
                        case R.id.popup_song_goto_album:
                            //TODO:   NavigationUtils.goToAlbum(mContext, arraylist.get(position).albumId);
                            break;
                        case R.id.popup_song_goto_artist:
                            //TODO: NavigationUtils.goToArtist(mContext, arraylist.get(position).artistId);
                            break;
                        case R.id.popup_song_addto_queue:
                            long[] _id = new long[1];
                            _id[0] = arraylist.get(position).id;
                            MusicPlayer.addToQueue(mContext, _id, -1, TimberUtils.IdType.NA);
                            break;
                        case R.id.popup_song_addto_playlist:
                            //TODO: AddPlaylistDialog.newInstance(arraylist.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                            break;
                        case R.id.popup_song_share:
                            TimberUtils.shareTrack(mContext, arraylist.get(position).id);
                            break;
                        case R.id.popup_song_delete:
                            long[] deleteIds = {arraylist.get(position).id};
                            TimberUtils.showDeleteDialog(mContext, arraylist.get(position).title, deleteIds, SmallSongListAdapter.this, position);
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
                                TimberUtils.removeFromPlaylist(mContext, arraylist.get(position).id, playlistId);
                                removeSongAt(position);
                                notifyItemRemoved(position);
                                break;
                            case R.id.popup_song_play:
                                MusicPlayer.playAll(mContext, songIDs, position, -1, TimberUtils.IdType.NA, false);
                                break;
                            case R.id.popup_song_play_next:
                                long[] ids = new long[1];
                                ids[0] = arraylist.get(position).id;
                                MusicPlayer.playNext(mContext, ids, -1, TimberUtils.IdType.NA);
                                break;
                            case R.id.popup_song_goto_album:
                             //TODO:   NavigationUtils.goToAlbum(mContext, arraylist.get(position).albumId);
                                break;
                            case R.id.popup_song_goto_artist:
                               //TODO: NavigationUtils.goToArtist(mContext, arraylist.get(position).artistId);
                                break;
                            case R.id.popup_song_addto_queue:
                                long[] id = new long[1];
                                id[0] = arraylist.get(position).id;
                                MusicPlayer.addToQueue(mContext, id, -1, TimberUtils.IdType.NA);
                                break;
                            case R.id.popup_song_addto_playlist:
                               //TODO: AddPlaylistDialog.newInstance(arraylist.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                                break;
                            case R.id.popup_song_share:
                                TimberUtils.shareTrack(mContext, arraylist.get(position).id);
                                break;
                            case R.id.popup_song_delete:
                                long[] deleteIds = {arraylist.get(position).id};
                                TimberUtils.showDeleteDialog(mContext,arraylist.get(position).title, deleteIds, SmallSongListAdapter.this, position);
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_song);

                menu.show();
                if(isPlaylist)
                    menu.getMenu().findItem(R.id.popup_song_remove_playlist).setVisible(true);
            }
        });
    }
    public void updateDataSet(List<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
    }
    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, artist;
        protected View root;
        protected ImageView albumArt, popupMenu;
        protected ImageView quickPlayPause;
        protected TextView count;
        //TODO: Add an time circular progress view
        //  Thêm một view hiển thị thời gian chạy hình tròn tại view đang phát
        public ItemHolder(View view) {
            super(view);
            root = view.findViewById(R.id.item_relative_root);
            this.title = view.findViewById(R.id.title_song_list);
            this.artist = view.findViewById(R.id.artist_song_list);
            this.albumArt = view.findViewById(R.id.album_art);
            this.popupMenu = view.findViewById(R.id.popup_menu);
            this.quickPlayPause = view.findViewById(R.id.quick_play_pause_button);
            this.count = view.findViewById(R.id.count);
            // TODO : findViewByID visualizer
            view.setOnClickListener(this);
        }

    @Override
    public void onClick(View view) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playAll(mContext, songIDs, getAdapterPosition(), -1,TimberUtils.IdType.NA, false);
                    Handler handler1 = new Handler() ;
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(currentlyPlayingPosition);
                            notifyItemChanged(getAdapterPosition());
                            currentlyPlayingPosition = getAdapterPosition();
                        }
                    },50);
                }
            },100);
    }
}
    public Song getSongAt(int i) {
        return arraylist.get(i);
    }

    public void addSongTo(int i, Song song) {
        arraylist.add(i, song);
    }

    public void removeSongAt(int i) {
        arraylist.remove(i);
    }
}
