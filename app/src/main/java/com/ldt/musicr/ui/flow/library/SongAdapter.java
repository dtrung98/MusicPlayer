package com.ldt.musicr.ui.flow.library;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;

import android.graphics.drawable.RippleDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.ldt.musicr.ui.AudioPreviewPlayer;
import com.ldt.musicr.ui.MainActivity;
import com.ldt.musicr.ui.widget.CircularPlayPauseProgressBar;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;
import com.ldt.musicr.ui.flow.SongOptionBottomSheet;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.services.MusicPlayer;
import com.ldt.musicr.util.Utils;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter,
        FastScrollRecyclerView.MeasurableAdapter, AudioPreviewPlayer.AudioPreviewerListener {
    private static final String TAG = "SongAdapter";
    public ArrayList<Song> mData = new ArrayList<>();
    public int currentlyPlayingPosition = 0;
    private Context mContext;
    private long[] songIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private long playlistId;
    private int mSortType = 0;

    public SongAdapter(Context context) {
        this.mContext = context;
    }
    public ArrayList<Song> getData() {
        return mData;
    }

    public void setData(List<Song> data) {
        initializeSong(data);
    }
    public int mSelected = 0;
    private Random mRandom = new Random();

    private void initializeSong(List<Song> data) {
        mData.clear();

        if(data!=null) mData.addAll(data);
        this.songIDs = getSongIds();
        randommize();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
       if(position==0) return R.layout.item_sort_song_child;
       return R.layout.item_song_child;
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    private long[] getSongIds() {
        long[] ret = new long[mData.size()];
        for (int i = 0; i < mData.size(); i++) {
            ret[i] = mData.get(i).id;
        }

        return ret;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(viewType,viewGroup,false);

        if(viewType==R.layout.item_sort_song_child)
            return new SongAdapter.SortHolder(v);

        return new SongAdapter.ItemHolder(v);
    }
    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder itemHolder, int position) {
        if(itemHolder instanceof ItemHolder)
            ((ItemHolder)itemHolder).bind(mData.get(position-1));
        else ((SortHolder)itemHolder).bind();
    }

    private int getRealAdapterPosition(ItemHolder holder) {
        return holder.getAdapterPosition() - 1;
    }


    private void setOnPopupMenuListener(SongAdapter.ItemHolder itemHolder, final int position) {
        itemHolder.mMenuButton.setOnClickListener(v -> {
            SongOptionBottomSheet sheet =  SongOptionBottomSheet.newInstance();
            sheet.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                    "song_popup_menu");
            sheet.setListener(id -> {
                switch (id) {
                    case R.id.popup_song_remove_playlist:
                        Utils.removeFromPlaylist(mContext, mData.get(position).id, playlistId);
                        removeSongAt(position);
                        notifyItemRemoved(position);
                        break;
                    case R.id.popup_song_play:
                        MusicPlayer.playAll(mContext, songIDs, position, -1, Utils.IdType.NA, false);
                        break;
                    case R.id.popup_song_play_next:
                        long[] ids = new long[1];
                        ids[0] = mData.get(position).id;
                        MusicPlayer.playNext(mContext, ids, -1, Utils.IdType.NA);
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
                        MusicPlayer.addToQueue(mContext, _id, -1, Utils.IdType.NA);
                        break;
                    case R.id.popup_song_addto_playlist:
                        //TODO: AddPlaylistDialog.newInstance(mData.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                        break;
                    case R.id.popup_song_share:
                        Utils.shareTrack(mContext, mData.get(position).id);
                        break;
                    case R.id.popup_song_delete:
                        long[] deleteIds = {mData.get(position).id};
                        Utils.showDeleteDialog(mContext, mData.get(position).title, deleteIds, SongAdapter.this, position);
                        break;
                }
                return true;
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
                            Utils.removeFromPlaylist(mContext, mData.get(position).id, playlistId);
                            removeSongAt(position);
                            notifyItemRemoved(position);
                            break;
                        case R.id.popup_song_play:
                            MusicPlayer.playAll(mContext, songIDs, position, -1, Utils.IdType.NA, false);
                            break;
                        case R.id.popup_song_play_next:
                            long[] ids = new long[1];
                            ids[0] = mData.get(position).id;
                            MusicPlayer.playNext(mContext, ids, -1, Utils.IdType.NA);
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
                            MusicPlayer.addToQueue(mContext, id, -1, Utils.IdType.NA);
                            break;
                        case R.id.popup_song_addto_playlist:
                            //TODO: AddPlaylistDialog.newInstance(mData.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                            break;
                        case R.id.popup_song_share:
                            Utils.shareTrack(mContext, mData.get(position).id);
                            break;
                        case R.id.popup_song_delete:
                            long[] deleteIds = {mData.get(position).id};
                            Utils.showDeleteDialog(mContext, mData.get(position).title, deleteIds, SongAdapter.this, position);
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

    public void randommize() {
        mSelected = mRandom.nextInt(mData.size());
        if(mCallBack!=null) mCallBack.onFirstItemCreated(mData.get(mSelected));
    }

    public SongAdapter setCallBack(PreviewRandomPlayAdapter.FirstItemCallBack callBack) {
        mCallBack = callBack;
        return this;
    }

    private PreviewRandomPlayAdapter.FirstItemCallBack mCallBack;

    public void shuffle() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            MusicPlayer.playAll(mContext, songIDs, mSelected, -1, Utils.IdType.NA, false);
            Handler handler1 = new Handler() ;
            handler1.postDelayed(() -> {
                notifyItemChanged(currentlyPlayingPosition);
                notifyItemChanged(mSelected);
                currentlyPlayingPosition = mSelected;
                randommize();
            },50);
        },100);
    }

    @Override
    public int getViewTypeHeight(RecyclerView recyclerView, @Nullable RecyclerView.ViewHolder viewHolder, int viewType) {
        if (viewType == R.layout.item_sort_song_child) {
            return recyclerView.getResources().getDimensionPixelSize(R.dimen.item_sort_song_child_height);
        } else if (viewType == R.layout.item_song_child) {
            return recyclerView.getResources().getDimensionPixelSize(R.dimen.item_song_child_height);
        }
        return 0;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        if(position==0) return "A";
        if(mData.get(position-1).title.isEmpty())
        return "";
        return mData.get(position-1).title.substring(0,1);
    }

    private int mPreviewItem = -1;

    private void playPreviewThisItem(ItemHolder itemHolder) {

        if(mPreviewItem!=-1) notifyItemChanged(mPreviewItem,false);
        String path = mData.get(getRealAdapterPosition(itemHolder)).path;
        mPreviewItem = itemHolder.getAdapterPosition();
        ((MainActivity)mContext).getAudioPreviewPlayer().previewThisFile(SongAdapter.this,path);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
       if(holder instanceof ItemHolder && !payloads.isEmpty() && payloads.get(0) instanceof Boolean) {
          ((ItemHolder)holder).resetProgress();
      }
          super.onBindViewHolder(holder,position,payloads);
    }
    private long mNotifyDuration =0;
    private long mNotifyTime = System.currentTimeMillis();

    @Override
    public void notifyAudioPreviewDuration(int time) {
        if(mPreviewItem!=-1) {
            mNotifyDuration = time;
            mNotifyTime = System.currentTimeMillis();
            notifyItemChanged(mPreviewItem,time);
        }
    }

    public void forceStopPreview() {
        mPreviewItem = -1;
        ((MainActivity)mContext).getAudioPreviewPlayer().forceStop();
    }

    public void playAll() {
        if(!mData.isEmpty()) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayer.playAll(mContext, songIDs, 0, -1, Utils.IdType.NA, false);
                Handler handler1 = new Handler();
                handler1.postDelayed(() -> {
                    notifyItemChanged(currentlyPlayingPosition);
                    notifyItemChanged(0);
                    currentlyPlayingPosition = 0;
                }, 50);
            }, 100);
        }
    }

    public class SortHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.sort_image) ImageView mImageView;
        @BindView(R.id.sort_text) TextView mSortText;

        public SortHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view.setOnClickListener(this);
        }

        public void bind() {

        }

        @Override
        public void onClick(View v) {

        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnAttachStateChangeListener {

        @BindView(R.id.title) TextView mTitle;
        @BindView(R.id.artist) TextView mArtist;
        @BindView(R.id.image) ImageView mImage;
        @BindView(R.id.more) View mMenuButton;
        @BindView(R.id.quick_play_pause) ImageView mQuickPlayPause;
        @BindView(R.id.number) TextView mNumber;

        @BindView(R.id.loader) View mLoader;
        @BindView(R.id.present) View mPresentButton;

        public ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view.setOnClickListener(this);
            mLoader.addOnAttachStateChangeListener(this);
        }

        @Override
        public void onClick(View view) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayer.playAll(mContext, songIDs, getRealAdapterPosition(this), -1, Utils.IdType.NA, false);
                Handler handler1 = new Handler() ;
                handler1.postDelayed(() -> {
                    notifyItemChanged(currentlyPlayingPosition);
                    notifyItemChanged(getRealAdapterPosition(this));
                    currentlyPlayingPosition = getRealAdapterPosition(this);
                },50);
            },100);
        }

        public void resetProgress() {
            if(mPresentButton instanceof CircularPlayPauseProgressBar)
                ((CircularPlayPauseProgressBar)mPresentButton).resetProgress();

        }

        public void bind(Song song) {
            mNumber.setText(""+(getRealAdapterPosition(this)+1));
            mTitle.setText(song.title);
            mArtist.setText(song.artistName);
            //   ImageLoader.getInstance().displayImage(Utils.getAlbumArtUri(song.albumId).toString(),mImage,new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(R.drawable.music_empty).resetViewBeforeLoading(true).build());

//            Picasso.get()
//                    .load(Utils.getAlbumArtUri(song.albumId))
//                    .placeholder(R.drawable.music_empty)
//                    .error(R.drawable.music_empty)
//                    .into(mImage);
            Glide.with((Activity) mContext)
                    .load(Utils.getAlbumArtUri(song.albumId))
                    .placeholder(R.drawable.music_empty)
                    .error(R.drawable.music_empty)
                    .into(mImage);

            ((RippleDrawable) itemView.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
            setOnPopupMenuListener(this,getRealAdapterPosition(this));
            checkQuickPlayPause();
            bindPresent(song);
        }

        public void bindPresent(Song song) {
            //Log.d(TAG, "bindPresent");
            if(mPresentButton instanceof CircularPlayPauseProgressBar) {
                if(getAdapterPosition()!=mPreviewItem&&((CircularPlayPauseProgressBar) mPresentButton).getMode()==CircularPlayPauseProgressBar.PLAYING)
                ((CircularPlayPauseProgressBar) mPresentButton).resetProgress();
                else if(getAdapterPosition()==mPreviewItem) {
                    long timePlayed = System.currentTimeMillis() - mNotifyTime;
                    if(timePlayed<=mNotifyDuration)
                    ((CircularPlayPauseProgressBar) mPresentButton).syncProgress((int)mNotifyDuration,(int)timePlayed);
                }
            }
        }

        public void checkQuickPlayPause() {
            if(MusicPlayer.getCurrentAudioId()==mData.get(getRealAdapterPosition(this)).id) {
                currentlyPlayingPosition = getRealAdapterPosition(this);
                mTitle.setTextColor(Tool.getBaseColor());

                if(MusicPlayer.isPlaying()) {
                    mQuickPlayPause.setImageResource(R.drawable.ic_volume_up_black_24dp);
                } else {
                    mQuickPlayPause.setImageResource(R.drawable.ic_volume_mute_black_24dp);
                }
            }
            else {
                mQuickPlayPause.setImageDrawable(null);
                mTitle.setTextColor(mContext.getResources().getColor(R.color.FlatWhite));
            }
        }

        @OnClick(R.id.present)
        void clickPresent() {
            if(mPreviewItem!=getAdapterPosition())
                playPreviewThisItem(this);
            else {
                resetProgress();
                forceStopPreview();
            }
        }

        @Override
        public void onViewAttachedToWindow(View v) {
            mLoader.clearAnimation();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            mLoader.clearAnimation();
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
