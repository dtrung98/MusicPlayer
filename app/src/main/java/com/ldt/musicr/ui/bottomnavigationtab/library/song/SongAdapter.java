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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ldt.musicr.glide.GlideApp;
import com.ldt.musicr.glide.SongGlideRequest;
import com.ldt.musicr.helper.menu.SongMenuHelper;
import com.ldt.musicr.helper.songpreview.PreviewSong;
import com.ldt.musicr.helper.songpreview.SongPreviewController;
import com.ldt.musicr.helper.songpreview.SongPreviewListener;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.ui.MainActivity;
import com.ldt.musicr.ui.bottomsheet.OptionBottomSheet;
import com.ldt.musicr.ui.bottomsheet.SortOrderBottomSheet;
import com.ldt.musicr.ui.bottomnavigationtab.library.artist.ArtistAdapter;
import com.ldt.musicr.ui.widget.CircularPlayPauseProgressBar;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;

import com.makeramen.roundedimageview.RoundedImageView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter,
     FastScrollRecyclerView.MeasurableAdapter, SongPreviewListener, SortOrderBottomSheet.SortOrderChangedListener {
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

    public void setIsPlaylist(boolean value) {
        isPlaylist = value;
    }

    public SongAdapter(Context context) {
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
        initializeSong(data);
    }
    public int mSelected = 0;
    private Random mRandom = new Random();

    private void initializeSong(List<Song> data) {
        mData.clear();

        if(data!=null) mData.addAll(data);
        this.mSongIDs = getSongIds();
        randomize();

        if(mContext instanceof MainActivity) {
            SongPreviewController controller = ((MainActivity) mContext).getSongPreviewController();
           mPreviewSong = controller.getCurrentPreviewSong();
        }
            notifyDataSetChanged();
    }

    public void destroy() {
        if(mContext instanceof MainActivity) {
           SongPreviewController controller  = ((MainActivity)mContext).getSongPreviewController();
           if(controller!=null) controller.removeAudioPreviewerListener(this);
        }

        removeCallBack();
        removeOrderListener();
    }

    @Override
    public int getItemViewType(int position) {
       if(position==0) return R.layout.item_sort_song_child;
       return R.layout.item_song_normal;
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

    @Override
    public int getSavedOrder() {
        if(mSortOrderListener!=null)
            return mSortOrderListener.getSavedOrder();
        return 0;
    }

    @Override
    public void onOrderChanged(int newType, String name) {
        if(mSortOrderListener!=null) {
            mSortOrderListener.onOrderChanged(newType, name);
            notifyItemChanged(0);
        }
    }
    private SortOrderBottomSheet.SortOrderChangedListener mSortOrderListener;
    public void setSortOrderChangedListener(SortOrderBottomSheet.SortOrderChangedListener listener) {
        mSortOrderListener = listener;
    }
    public void removeOrderListener() {
        mSortOrderListener = null;
    }

    private void sortHolderClicked() {
        if(mContext instanceof AppCompatActivity) {
            SortOrderBottomSheet bs = SortOrderBottomSheet.newInstance(this);
            bs.show(((AppCompatActivity)mContext).getSupportFragmentManager(),TAG);
        }
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

    private int getPositionInData(ItemHolder holder) {
        return holder.getAdapterPosition() - 1;
    }

    private void setOnPopupMenuListener(SongAdapter.ItemHolder itemHolder, final int position) {
        itemHolder.mMoreButton.setOnClickListener(v -> {
            OptionBottomSheet
                    .newInstance(SongMenuHelper.SONG_OPTION,mData.get(position))
                    .show(((AppCompatActivity)mContext).getSupportFragmentManager(), "song_popup_menu");
    });
    }

    public void randomize() {
        if(mData.isEmpty()) return;
        mSelected = mRandom.nextInt(mData.size());
        if(mCallBack!=null) mCallBack.onFirstItemCreated(mData.get(mSelected));
    }

    public SongAdapter setCallBack(PreviewRandomPlayAdapter.FirstItemCallBack callBack) {
        mCallBack = callBack;
        return this;
    }

    public void removeCallBack() {
        mCallBack = null;
    }

    private PreviewRandomPlayAdapter.FirstItemCallBack mCallBack;

    public void shuffle() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            MusicPlayerRemote.openQueue(mData,mSelected,true);
            //MusicPlayer.playAll(mContext, mSongIDs, mSelected, -1, Util.IdType.NA, false);
            Handler handler1 = new Handler() ;
            handler1.postDelayed(() -> {
                notifyItemChanged(mCurrentHighLightPos);
                notifyItemChanged(mSelected);
                mCurrentHighLightPos = mSelected;
                randomize();
            },50);
        },100);
    }


    @NonNull
    @Override
    public String getSectionName(int position) {
        if(position==0) return "A";
        if(mData.get(position-1).title.isEmpty())
        return "A";
        return mData.get(position-1).title.substring(0,1);
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
            notifyItemChanged(position + 1, mPreviewSong);
        }
    }

    @Override
    public void onSongPreviewFinish(PreviewSong song) {
        mPreviewSong = null;
        int position = mData.indexOf(song.getSong());
        Log.d(TAG, "onSongPreviewFinish: position = "+ position);

        if(position!=-1)
            notifyItemChanged(position+1, false);
    }

    public void playAll() {
        if(!mData.isEmpty()) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                MusicPlayerRemote.openQueue(mData,0,true);
                Handler handler1 = new Handler();
                handler1.postDelayed(() -> {
                    notifyItemChanged(mCurrentHighLightPos);
                    notifyItemChanged(0);
                    mCurrentHighLightPos = 0;
                }, 50);
            }, 100);
        }
    }

    public int getViewTypeHeight(RecyclerView recyclerView, @Nullable RecyclerView.ViewHolder viewHolder, int viewType) {
        if (viewType == R.layout.item_sort_song_child) {
            return recyclerView.getResources().getDimensionPixelSize(R.dimen.item_sort_song_child_height);
        } else if (viewType == R.layout.item_song_normal) {
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
        if (-1 < mCurrentHighLightPos && mCurrentHighLightPos < mSongIDs.length) {
            isStillOldPos = mSongIDs[mCurrentHighLightPos] == newPlayingID;
            notifyItemChanged(mCurrentHighLightPos);
        }
        // find new pos
        if (!isStillOldPos) {
            // compare songid with song
            int newPos = mData.indexOf(newPlayingID);
            mCurrentHighLightPos = newPos;
            if (newPos != -1) notifyItemChanged(newPos);
        }
    }

    public class SortHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sort_text) TextView mSortText;
        @OnClick(R.id.sort_parent)
        void sortClicked() {
            sortHolderClicked();
        }

        public SortHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }

        public void bind() {
            if(mSortOrderListener!=null) {
              String str =  mContext.getResources().getString(
                        SortOrderBottomSheet.mSortStringRes[mSortOrderListener.getSavedOrder()]);
              mSortText.setText(str);
            }
            }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.title) TextView mTitle;
        @BindView(R.id.description) TextView mArtist;
        @BindView(R.id.image) ImageView mImage;
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
