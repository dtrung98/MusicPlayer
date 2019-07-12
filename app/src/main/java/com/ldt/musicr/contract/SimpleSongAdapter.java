package com.ldt.musicr.contract;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.ldt.musicr.ui.BaseActivity;
import com.ldt.musicr.ui.MainActivity;
import com.ldt.musicr.ui.bottomnavigationtab.SongOptionBottomSheet;
import com.ldt.musicr.ui.bottomnavigationtab.library.artist.ArtistAdapter;
import com.ldt.musicr.ui.widget.CircularPlayPauseProgressBar;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.util.Util;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Handle Event:
 * <br>+. Click on item
 * <br>+. Long Click on item
 * <br>+. Click Preview Button on item
 * <br>+. Click Menu Button on item
 */

public abstract class SimpleSongAdapter extends AbsMediaAdapter<AbsDynamicHolder, Song> implements SongPreviewListener {
   private static final String TAG = "SimpleSongAdapter";
   public static final int NORMAL = R.layout.item_song_normal;
   public static final int BIG = R.layout.item_song_big;
   public static final int BIGGER = R.layout.item_song_bigger;

   protected int mLayoutType = NORMAL;

   public void setDataLayoutType(int itemHeightType) {
       mLayoutType = itemHeightType;
   }

    public SimpleSongAdapter(Context context) {
        super(context);

        if(context instanceof BaseActivity) {
           SongPreviewController controller = ((BaseActivity)context).getSongPreviewController();
           controller.addSongPreviewListener(this);
        }
    }

    @Override
    protected void onDataSet() {

        if(mContext instanceof MainActivity) {
            SongPreviewController controller = ((MainActivity) mContext).getSongPreviewController();
            mPreviewSong = controller.getCurrentPreviewSong();
        }
    }

    public void destroy() {
        if(mContext instanceof MainActivity) {
           SongPreviewController controller  = ((MainActivity)mContext).getSongPreviewController();
           if(controller!=null) controller.removeAudioPreviewerListener(this);
        }
    }

    @NotNull
    @Override
    public AbsDynamicHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutType,viewGroup,false);
        return new SimpleSongAdapter.ItemHolder(v);
    }

    private int getPositionInData(ItemHolder holder) {
        return holder.getAdapterPosition() - 1;
    }

    protected void handleMenuClick(@NonNull Song song, int menuItemId) {

    }

    @Override
    protected void onPopupMenuItem(AbsDynamicHolder itemHolder, final int position) {

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
                        Util.showDeleteDialog(mContext, mData.get(position).title, deleteIds, SimpleSongAdapter.this, position);
                        break;
                }
                return true;
            }
            );

            menu.inflate(R.menu.popup_song);

            menu.show();
            if(isPlaylist)
                menu.getMenu().findItem(R.id.popup_song_remove_playlist).setVisible(true);
        });
    }

    private void previewThisSong(ItemHolder itemHolder) {

        if(mContext instanceof MainActivity) {
           SongPreviewController preview =((MainActivity) mContext).getSongPreviewController();
           if(preview!=null) {
               if (preview.isPlayingPreview()&&preview.isThisSongCurrentPreview(getData().get(getPositionInData(itemHolder))))
                   preview.cancelPreview();
               else {
 ArrayList<Song> data = new ArrayList<>(mData);
                   Collections.shuffle(data);

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


    private PreviewSong mPreviewSong = null;
    @Override
    public void onSongPreviewStart(PreviewSong song) {
        int position = getData().indexOf(song.getSong());

        Log.d(TAG, "onSongPreviewStart: position = "+ position);
        if (position != -1) {
            mPreviewSong = song;
            notifyItemChanged(position + 1);
        }
    }

    @Override
    public void onSongPreviewFinish(PreviewSong song) {
        mPreviewSong = null;
        int position = getData().indexOf(song.getSong());
        Log.d(TAG, "onSongPreviewFinish: position = "+ position);

        if(position!=-1)
            notifyItemChanged(position+1);
    }

    public void playAll() {
        if(!getData().isEmpty()) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayerRemote.openQueue(getData(),0,true);
                Handler handler1 = new Handler();
                handler1.postDelayed(() -> {
                    notifyItemChanged(mMediaPlayDataItem);
                    notifyItemChanged(0);
                    mMediaPlayDataItem = 0;
                }, 50);
            }, 100);
        }
    }

    @Override
    public void notifyOnMediaStateChanged() {
        // highlight the playing song
        Song currentPlayingSong = MusicPlayerRemote.getCurrentSong();
        if (currentPlayingSong != null && currentPlayingSong.id != -1) {

            if (isMediaPlayItemAvailable()) {

                notifyItemChanged(getItemHolderPosition(mMediaPlayDataItem));

                boolean isStillOldPos = getData().get(mMediaPlayDataItem).equals(currentPlayingSong);
                if (!isStillOldPos) {
                    mMediaPlayDataItem = getData().indexOf(currentPlayingSong);
                    if (mMediaPlayDataItem != -1)
                        notifyItemChanged(getItemHolderPosition(mMediaPlayDataItem));
                }
            }
        }
    }

    public class ItemHolder extends AbsMediaHolder<Song> {

        @BindView(R.id.number) TextView mNumber;
        @BindView(R.id.title) TextView mTitle;
        @BindView(R.id.description) TextView mDescription;

        @BindView(R.id.image) ImageView mImage;
        @BindView(R.id.menu_button) View mMoreButton;
        @BindView(R.id.quick_play_pause) ImageView mQuickPlayPause;

        @BindView(R.id.preview_button) View mPresentButton;

        public ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }

        @Override
        public void onClick(View view) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayerRemote.openQueue(mData,getPositionInData(this),true);
                //MusicPlayer.playAll(mContext, mSongIDs, getPositionInData(this), -1, Util.IdType.NA, false);
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

        @Override
        public void bind(Song song) {
            mNumber.setText(""+(getPositionInData(this)+1));
            mTitle.setText(song.title);
            mDescription.setText(song.artistName);

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
                mCurrentHighLightPos = getAdapterPosition();
                int baseColor = ArtistAdapter.lighter(Tool.getBaseColor(),0.6f);
                mTitle.setTextColor(ArtistAdapter.lighter(Tool.getBaseColor(),0.25f));
                mDescription.setTextColor(Color.argb(0xAA,Color.red(baseColor),Color.green(baseColor),Color.blue(baseColor)));
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
                mDescription.setTextColor(Color.argb(0xAA,Color.red(flatWhite),Color.green(flatWhite),Color.blue(flatWhite)));
            }
        }

        @OnClick(R.id.preview_button)
        void clickPresent() {

   if(mPresentButton instanceof  CircularPlayPauseProgressBar) {
                CircularPlayPauseProgressBar mProgressBar = (CircularPlayPauseProgressBar)mPresentButton;
                if(mPreviewItemInData ==getAdapterPosition()&&mProgressBar.getMode()==CircularPlayPauseProgressBar.RESET)
                    mPreviewItemInData = -1;
            }



            if(mPreviewSong!=null&&mPreviewSong.getSong().equals(mData.get(getPositionInData(this))))
            {
                resetProgress();
                forceStopPreview();
            }else {
                previewThisSong(this);
            }
        }
    }
}
