package com.ldt.musicr.ui.bottomnavigationtab.library.song;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.ldt.musicr.helper.songpreview.PreviewSong;
import com.ldt.musicr.helper.songpreview.SongPreviewController;
import com.ldt.musicr.helper.songpreview.SongPreviewListener;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.ui.MainActivity;
import com.ldt.musicr.ui.bottomnavigationtab.SongOptionBottomSheet;
import com.ldt.musicr.ui.bottomnavigationtab.library.artist.ArtistAdapter;
import com.ldt.musicr.ui.widget.CircularPlayPauseProgressBar;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.util.Util;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BasePreviewSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SongPreviewListener {
    private static final String TAG = "BasePreviewSongAdapter";

    private ArrayList<Song> mData = new ArrayList<>();
    private int mPlayingDetectedItemPosition = 0;

    private Context mContext;
    private long[] mSongIDs;



    public BasePreviewSongAdapter(Context context) {
        this.mContext = context;
        if(context instanceof MainActivity) {
           SongPreviewController controller = ((MainActivity)context).getSongPreviewController();
           controller.addSongPreviewListener(this);
        }
    }
    public ArrayList<Song> getData() {
        return mData;
    }

    public void setData(List<Song> data) {
        mData.clear();

        if(data!=null) mData.addAll(data);
        this.mSongIDs = getSongIds();

        initializeSong();

        if(mContext instanceof MainActivity) {
            SongPreviewController controller = ((MainActivity) mContext).getSongPreviewController();
            mPreviewSong = controller.getCurrentPreviewSong();
        }
        notifyDataSetChanged();
    }

    protected abstract void initializeSong();

    public int getPlayingDetectedItemPosition() {
        return mPlayingDetectedItemPosition;
    }

    public void setPlayingDetectedItemPosition(int mPlayingDetectedItemPosition) {
        this.mPlayingDetectedItemPosition = mPlayingDetectedItemPosition;
    }

    public void destroy() {
        if(mContext instanceof MainActivity) {
           SongPreviewController controller  = ((MainActivity)mContext).getSongPreviewController();
           if(controller!=null) controller.removeAudioPreviewerListener(this);
        }
    }

    protected long[] getSongIds() {
        long[] ret = new long[mData.size()];
        for (int i = 0; i < mData.size(); i++) {
            ret[i] = mData.get(i).id;
        }

        return ret;
    }

    private void setOnPopupMenuListener(BasePreviewSongAdapter.ItemHolder itemHolder, final int position) {
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
                    case R.id.popup_song_goto_album:
                        //TODO:   NavigationUtils.goToAlbum(mContext, mData.get(position).albumId);
                        break;
                    case R.id.popup_song_goto_artist:
                        //TODO: NavigationUtils.goToArtist(mContext, mData.get(position).artistId);
                        break;
                    case R.id.popup_song_addto_queue:
                        MusicPlayerRemote.enqueue(mData.get(position));
                        break;
                    case R.id.popup_song_addto_playlist:
                        //TODO: AddPlaylistDialog.newInstance(mData.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                        break;
                    case R.id.popup_song_share:
                        Util.shareTrack(mContext, mData.get(position).id);
                        break;
                    case R.id.popup_song_delete:
                        long[] deleteIds = {mData.get(position).id};
                        Util.showDeleteDialog(mContext, mData.get(position).title, deleteIds, BasePreviewSongAdapter.this, position);
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
                            //MusicPlayer.playNext(mContext, ids, -1, Util.IdType.NA);
                            break;
                        case R.id.popup_song_goto_album:
                            //TODO:   NavigationUtils.goToAlbum(mContext, mData.get(position).albumId);
                            break;
                        case R.id.popup_song_goto_artist:
                            //TODO: NavigationUtils.goToArtist(mContext, mData.get(position).artistId);
                            break;
                        case R.id.popup_song_addto_queue:

                            MusicPlayerRemote.enqueue(mData.get(position));
                            break;
                        case R.id.popup_song_addto_playlist:
                            //TODO: AddPlaylistDialog.newInstance(mData.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                            break;
                        case R.id.popup_song_share:
                            Util.shareTrack(mContext, mData.get(position).id);
                            break;
                        case R.id.popup_song_delete:
                            long[] deleteIds = {mData.get(position).id};
                            Util.showDeleteDialog(mContext, mData.get(position).title, deleteIds, BasePreviewSongAdapter.this, position);
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

    private void playPreviewThisItem(ItemHolder itemHolder) {

        if(mContext instanceof MainActivity) {
           SongPreviewController preview =((MainActivity) mContext).getSongPreviewController();
           if(preview!=null) {
               if (preview.isPlayingPreview()&&preview.isThisSongCurrentPreview(mData.get(getPositionInData(itemHolder))))
                   preview.cancelPreview();
               else {
                  /* ArrayList<Song> data = new ArrayList<>(mData);
                   Collections.shuffle(data);*/
                   preview.previewSongs(mData.get(getPositionInData(itemHolder)));
               }
           }
        }
    }

    public void forceStopPreview() {
        mPreviewSong = null;
        if(mContext instanceof MainActivity) {
            SongPreviewController controller =  ((MainActivity)mContext).getSongPreviewController();
            if(controller!=null) controller.cancelPreview();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {

       if(holder instanceof ItemHolder && !payloads.isEmpty() && payloads.get(0) instanceof Boolean) {
          ((ItemHolder)holder).resetProgress();
      }

       if(holder instanceof ItemHolder)
           for (Object payload :
                   payloads) {
               if(payload instanceof PreviewSong) {
                   ((ItemHolder)holder).bindPresent(mData.get(getPositionInData((ItemHolder) holder)));
               }
               else if(payload instanceof Boolean) {
                   ((ItemHolder)holder).resetProgress();
               }
           }

          super.onBindViewHolder(holder,position,payloads);
    }

    private PreviewSong mPreviewSong = null;
    @Override
    public void onSongPreviewStart(PreviewSong song) {
        int position = mData.indexOf(song.getSong());

        Log.d(TAG, "onSongPreviewStart: position = "+ position);
        if (position != -1) {
            mPreviewSong = song;
            notifyItemChanged(position + 1);
        }
    }

    @Override
    public void onSongPreviewFinish(PreviewSong song) {
        mPreviewSong = null;
        int position = mData.indexOf(song.getSong());
        Log.d(TAG, "onSongPreviewFinish: position = "+ position);

        if(position!=-1)
            notifyItemChanged(position+1);
    }

    public void playAll() {
        if(!mData.isEmpty()) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayerRemote.openQueue(mData,0,true);
                Handler handler1 = new Handler();
                handler1.postDelayed(() -> {
                    notifyItemChanged(mPlayingDetectedItemPosition);
                    notifyItemChanged(0);
                    mPlayingDetectedItemPosition = 0;
                }, 50);
            }, 100);
        }
    }

    public int getViewTypeHeight(RecyclerView recyclerView, @Nullable RecyclerView.ViewHolder viewHolder, int viewType) {
        if (viewType == R.layout.item_sort_song_child) {
            return recyclerView.getResources().getDimensionPixelSize(R.dimen.item_sort_song_child_height);
        } else if (viewType == R.layout.item_song_child) {
            return recyclerView.getResources().getDimensionPixelSize(R.dimen.item_song_child_height);
        }
        return 0;
    }

    @Override
    public int getViewTypeHeight(RecyclerView recyclerView, int i) {
        return recyclerView.getResources().getDimensionPixelSize(R.dimen.item_song_child_height);
    }

    public void notifyMetaChanged() {
        // hightlight truely the playing song
        // check whether old pos is still  the playing song
        long newPlayingID = MusicPlayerRemote.getCurrentSong().id;
        boolean isStillOldPos = false;
        // update old item
        if (-1 < mPlayingDetectedItemPosition && mPlayingDetectedItemPosition < mSongIDs.length) {
            isStillOldPos = mSongIDs[mPlayingDetectedItemPosition] == newPlayingID;
            notifyItemChanged(mPlayingDetectedItemPosition);
        }
        // find new pos
        if (!isStillOldPos) {
            // compare songid with song
            int newPos = mData.indexOf(newPlayingID);
            mPlayingDetectedItemPosition = newPos;
            if (newPos != -1) notifyItemChanged(newPos);
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnAttachStateChangeListener {

        @BindView(R.id.title) TextView mTitle;
        @BindView(R.id.artist) TextView mArtist;
        @BindView(R.id.image) ImageView mImage;
        @BindView(R.id.more) View mMoreButton;
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
                MusicPlayerRemote.openQueue(mData,getPositionInData(this),true);
                //MusicPlayer.playAll(mContext, mSongIDs, getPositionInData(this), -1, Util.IdType.NA, false);
                Handler handler1 = new Handler() ;
                handler1.postDelayed(() -> {
                    notifyItemChanged(mPlayingDetectedItemPosition);
                    notifyItemChanged(getAdapterPosition());
                    mPlayingDetectedItemPosition = getAdapterPosition();
                },50);
            },100);
        }

        public void resetProgress() {
            if(mPresentButton instanceof CircularPlayPauseProgressBar)
                ((CircularPlayPauseProgressBar)mPresentButton).resetProgress();

        }

        public void bind(Song song) {
            mNumber.setText(""+(getPositionInData(this)+1));
            mTitle.setText(song.title);
            mArtist.setText(song.artistName);
               //ImageLoader.getInstance().displayImage(Util.getAlbumArtUri(song.albumId).toString(),mImage,new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(R.drawable.music_empty).resetViewBeforeLoading(true).build());

/*      Picasso.get()
                    .load(MusicUtil.getMediaStoreAlbumCoverUri(song.albumId))
                    .placeholder(R.drawable.music_empty)
                    .error(R.drawable.music_empty)
                    .into(mImage);*/
         /*
          Uri uri = MusicUtil.getMediaStoreAlbumCoverUri(song.albumId);
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
            setOnPopupMenuListener(this, getPositionInData(this));
            highLight();
            bindPresent(song);
        }

        public void bindPresent(Song song) {
            //Log.d(TAG, "bindPresent");
            if(mPresentButton instanceof CircularPlayPauseProgressBar) {
                CircularPlayPauseProgressBar presentButton = (CircularPlayPauseProgressBar) mPresentButton;


                if((mPreviewSong==null || !mPreviewSong.getSong().equals(mData.get(getPositionInData(this))))
                                && presentButton.getMode() == CircularPlayPauseProgressBar.PLAYING)
                presentButton.resetProgress();
                else if(mPreviewSong!=null && mPreviewSong.getSong().equals(mData.get(getPositionInData(this)))) {
                    long timePlayed = mPreviewSong.getTimePlayed();
                    Log.d(TAG, "bindPresent: timePlayed = " + timePlayed);
                    if (timePlayed == -1) presentButton.resetProgress();
                    else {
                        if (timePlayed < 0) timePlayed = 0;
                        if (timePlayed <= mPreviewSong.getTotalPreviewDuration())
                            ((CircularPlayPauseProgressBar) mPresentButton).syncProgress(mPreviewSong.getTotalPreviewDuration(), (int) timePlayed);
                    }
                }
            }
        }

        private void highLight() {
            if(MusicPlayerRemote.getCurrentSong().id==mData.get(getPositionInData(this)).id) {
                mPlayingDetectedItemPosition = getAdapterPosition();
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

        @OnClick(R.id.present)
        void clickPresent() {

         /*   if(mPresentButton instanceof  CircularPlayPauseProgressBar) {
                CircularPlayPauseProgressBar mProgressBar = (CircularPlayPauseProgressBar)mPresentButton;
                if(mPreviewItemInData ==getAdapterPosition()&&mProgressBar.getMode()==CircularPlayPauseProgressBar.RESET)
                    mPreviewItemInData = -1;
            }*/


            if(mPreviewSong!=null&&mPreviewSong.getSong().equals(mData.get(getPositionInData(this))))
            {
                resetProgress();
                forceStopPreview();
            }else {
                playPreviewThisItem(this);
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
