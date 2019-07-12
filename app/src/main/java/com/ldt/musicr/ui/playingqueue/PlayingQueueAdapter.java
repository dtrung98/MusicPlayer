package com.ldt.musicr.ui.playingqueue;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
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

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ldt.musicr.R;
import com.ldt.musicr.glide.GlideApp;
import com.ldt.musicr.glide.SongGlideRequest;
import com.ldt.musicr.helper.songpreview.SongPreviewController;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.ui.AudioPreviewPlayer;
import com.ldt.musicr.ui.MainActivity;
import com.ldt.musicr.ui.bottomnavigationtab.SongOptionBottomSheet;
import com.ldt.musicr.ui.bottomnavigationtab.library.artist.ArtistAdapter;
import com.ldt.musicr.ui.widget.CircularPlayPauseProgressBar;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.util.Util;
import com.makeramen.roundedimageview.RoundedImageView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayingQueueAdapter extends RecyclerView.Adapter<PlayingQueueAdapter.ItemHolder> implements AudioPreviewPlayer.AudioPreviewerListener, FastScrollRecyclerView.SectionedAdapter {
    private static final String TAG = "SongAdapter";
    public ArrayList<Song> mData = new ArrayList<>();
    public int mCurrentHighLightPos = 0;
    private Context mContext;
    private long[] mSongIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private long playlistId;
    private int mSortType = 0;

    public PlayingQueueAdapter(Context context) {
        this.mContext = context;
    }
    public ArrayList<Song> getData() {
        return mData;
    }

    public void setData(List<Song> data) {
        initializeSong(data);
    }

    private void initializeSong(List<Song> data) {
        mData.clear();

        if(data!=null) mData.addAll(data);
        this.mSongIDs = getSongIds();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0) return R.layout.item_sort_song_child;
        return R.layout.item_song_normal;
    }

    @Override
    public int getItemCount() {
        return mData.size();
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
    public ItemHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song_big,viewGroup,false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull ItemHolder itemHolder, int position) {
        itemHolder.bind(mData.get(position));
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {
        itemHolder.mMoreButton.setOnClickListener(v -> {
            SongOptionBottomSheet sheet =  SongOptionBottomSheet.newInstance();
            sheet.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                    "song_popup_menu");
            sheet.setListener(id -> {
                        switch (id) {
                            case R.id.popup_song_remove_playlist:
                                Util.removeFromPlaylist(mContext, mData.get(position).id, playlistId);
                                removeSongAt(position);
                                notifyItemRemoved(position);
                                break;
                            case R.id.popup_song_play:
                                MusicPlayerRemote.openQueue(mData,position,true);
                                break;
                            case R.id.popup_song_play_next:
                                MusicPlayerRemote.playNext(mData.get(position));
                                break;
                            case R.id.popup_song_go_to_album:
                                //TODO:   NavigationUtil.goToAlbum(mContext, mData.get(position).albumId);
                                break;
                            case R.id.popup_song_go_to_artist:
                                //TODO: NavigationUtil.goToArtist(mContext, mData.get(position).artistId);
                                break;
                            case R.id.popup_song_add_to_queue:
                                MusicPlayerRemote.enqueue(mData.get(position));
                                break;
                            case R.id.popup_song_add_to_playlist:
                                //TODO: AddPlaylistDialog.newInstance(mData.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                                break;
                            case R.id.popup_song_share:
                                Util.shareTrack(mContext, mData.get(position).id);
                                break;
                            case R.id.popup_song_delete:
                                long[] deleteIds = {mData.get(position).id};
                                Util.showDeleteDialog(mContext, mData.get(position).title, deleteIds, this, position);
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
                            Util.removeFromPlaylist(mContext, mData.get(position).id, playlistId);
                            removeSongAt(position);
                            notifyItemRemoved(position);
                            break;
                        case R.id.popup_song_play:
                            MusicPlayerRemote.openQueue(mData,position,true);
                            break;
                        case R.id.popup_song_play_next:
                            MusicPlayerRemote.playNext(mData.get(position));
                            break;
                        case R.id.popup_song_go_to_album:
                            //TODO:   NavigationUtil.goToAlbum(mContext, mData.get(position).albumId);
                            break;
                        case R.id.popup_song_go_to_artist:
                            //TODO: NavigationUtil.goToArtist(mContext, mData.get(position).artistId);
                            break;
                        case R.id.popup_song_add_to_queue:
                            MusicPlayerRemote.enqueue(mData.get(position));
                            break;
                        case R.id.popup_song_add_to_playlist:
                            //TODO: AddPlaylistDialog.newInstance(mData.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                            break;
                        case R.id.popup_song_share:
                            Util.shareTrack(mContext, mData.get(position).id);
                            break;
                        case R.id.popup_song_delete:
                            long[] deleteIds = {mData.get(position).id};
                            Util.showDeleteDialog(mContext, mData.get(position).title, deleteIds, PlayingQueueAdapter.this, position);
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

    private int mPreviewItem = -1;

    private void playPreviewThisItem(ItemHolder itemHolder) {

        if(mPreviewItem!=-1)
            notifyItemChanged(mPreviewItem,false);

        if(mContext instanceof MainActivity) {
            SongPreviewController preview =((MainActivity) mContext).getSongPreviewController();
            if(preview!=null) {
                if (preview.isPlayingPreview())
                    preview.cancelPreview();
                else {
                    ArrayList<Song> data = new ArrayList<>(mData);
                    Collections.shuffle(data);
                    preview.previewSongs(data);
                }
            }
        }
    }

    public void forceStopPreview() {
        mPreviewItem = -1;
        if(mContext instanceof MainActivity) {
            SongPreviewController controller =  ((MainActivity)mContext).getSongPreviewController();
            if(controller!=null) controller.cancelPreview();
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull List<Object> payloads) {
        if(!payloads.isEmpty() && payloads.get(0) instanceof Boolean) {
        holder.resetProgress();
        }
        super.onBindViewHolder(holder,position,payloads);
    }
    private long mNotifyDuration =0;
    private long mNotifyTime = System.currentTimeMillis();

    @Override
    public void onPreviewStart(int totalTime) {
        if(mPreviewItem!=-1) {
            mNotifyDuration = totalTime;
            mNotifyTime = System.currentTimeMillis();
            notifyItemChanged(mPreviewItem, totalTime);
        }
    }

    @Override
    public void onPreviewDestroy() {
        if(mPreviewItem!=-1) {
            int temp = mPreviewItem;
            mPreviewItem = -1;
            notifyItemChanged(temp, false);
        }
    }

    public void notifyMetaChanged() {
        // hightlight truely the playing song
        // check whether old pos is still  the playing song
        long newPlayingID = MusicPlayerRemote.getCurrentSong().id;
        boolean isStillOldPos = false;
        // update old item
        if(-1 < mCurrentHighLightPos && mCurrentHighLightPos < mSongIDs.length) {
            isStillOldPos = mSongIDs[mCurrentHighLightPos] ==newPlayingID;
            notifyItemChanged(mCurrentHighLightPos);
        }
        // find new pos
        if(!isStillOldPos) {
            // compare songid with song
            int newPos = mData.indexOf(newPlayingID);
            mCurrentHighLightPos = newPos;
            if(newPos!=-1) notifyItemChanged(newPos);
        }
    }

    @NonNull
    @Override
    public String getSectionName(int i) {
        if(mData.get(i).title.isEmpty())
            return "";
        return mData.get(i).title.substring(0,1);
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title) TextView mTitle;
        @BindView(R.id.description) TextView mArtist;
        @BindView(R.id.image)
        ImageView mImage;
        @BindView(R.id.menu_button) View mMoreButton;
        @BindView(R.id.quick_play_pause) ImageView mQuickPlayPause;
        @BindView(R.id.number) TextView mNumber;

        @BindView(R.id.preview_button) View mPresentButton;

        public ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayerRemote.openQueue(mData,getAdapterPosition(),true);
                Handler handler1 = new Handler() ;
                handler1.postDelayed(() -> {
                    notifyItemChanged(mCurrentHighLightPos);
                    notifyItemChanged(getAdapterPosition());
                    mCurrentHighLightPos = getAdapterPosition();
                },50);
            },100);
        }

        public void resetProgress() {
            if(mPresentButton instanceof CircularPlayPauseProgressBar)
                ((CircularPlayPauseProgressBar)mPresentButton).resetProgress();

        }

        public void bind(Song song) {
            mNumber.setText(""+(getAdapterPosition()+1));
            mTitle.setText(song.title);
            mArtist.setText(song.artistName);
            //   ImageLoader.getInstance().displayImage(Util.getAlbumArtUri(song.albumId).toString(),mImage,new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(R.drawable.music_empty).resetViewBeforeLoading(true).build());

        /*    Picasso.get()
                    .load(Util.getAlbumArtUri(song.albumId))
                    .placeholder(R.drawable.music_empty)
                    .error(R.drawable.music_empty)
                    .into(mImage);*/

        /*    Uri uri = Util.getAlbumArtUri(song.albumId);
            Log.d(TAG, "bind: song ["+ song.title+"], AlbumArtPath = "+ uri.toString());
            Glide.with(mContext)
                    .load(uri)
                    .placeholder(R.drawable.music_empty)
                    .error(R.drawable.music_empty)
                    .into(mImage);*/

            SongGlideRequest.Builder.from(GlideApp.with(mContext), song)
                    .ignoreMediaStore(false)
                    .generatePalette(mContext).build()
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            if(mImage instanceof RoundedImageView) ((RoundedImageView)mImage).setBorderWidth(R.dimen.oneDP);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if(mImage instanceof RoundedImageView) ((RoundedImageView)mImage).setBorderWidth(0f);
                            return false;
                        }
                    })
                    .into(mImage);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((RippleDrawable) itemView.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
                ((RippleDrawable) mMoreButton.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
                ((RippleDrawable) mPresentButton.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
            }
            setOnPopupMenuListener(this, getAdapterPosition());
            highLight();
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

        private void highLight() {
            if(MusicPlayerRemote.getCurrentSong().id==mData.get(getAdapterPosition()).id) {
                mCurrentHighLightPos = getAdapterPosition();
                int baseColor = ArtistAdapter.lighter(Tool.getBaseColor(),0.6f);
                mTitle.setTextColor(ArtistAdapter.lighter(Tool.getBaseColor(),0.25f));
                mArtist.setTextColor(Color.argb(0xAA,Color.red(baseColor),Color.green(baseColor),Color.blue(baseColor)));
                mQuickPlayPause.setColorFilter(baseColor);

                if(MusicPlayerRemote.isPlaying()) {
                    mQuickPlayPause.setImageResource(R.drawable.ic_volume_up_black_24dp);
                } else {
                    mQuickPlayPause.setImageResource(R.drawable.ic_volume_mute_black_24dp);
                }
            }
            else {
                mQuickPlayPause.setImageDrawable(null);
                int flatWhite = mContext.getResources().getColor(R.color.FlatWhite);
                mTitle.setTextColor(mContext.getResources().getColor(R.color.FlatWhite));
                mArtist.setTextColor(Color.argb(0xAA,Color.red(flatWhite),Color.green(flatWhite),Color.blue(flatWhite)));
            }
        }

        @OnClick(R.id.preview_button)
        void clickPresent() {

            // set previewItem = -1 if the preview is end
            if(mPresentButton instanceof  CircularPlayPauseProgressBar) {
                CircularPlayPauseProgressBar mProgressBar = (CircularPlayPauseProgressBar)mPresentButton;
                if(mPreviewItem==getAdapterPosition()&&mProgressBar.getMode()==CircularPlayPauseProgressBar.RESET)
                    mPreviewItem = -1;
            }


            if(mPreviewItem!=getAdapterPosition())
                playPreviewThisItem(this);
            else {
                resetProgress();
                playPreviewThisItem(this);
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
