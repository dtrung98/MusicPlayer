package com.ldt.musicr.ui.maintab.feature;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Build;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import com.ldt.musicr.loader.medialoader.TopAndRecentlyPlayedTracksLoader;
import com.ldt.musicr.model.PlaylistSong;
import com.ldt.musicr.util.PlaylistArtworkGenerator;
import com.ldt.musicr.ui.widget.BounceInterpolator;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;
import com.ldt.musicr.loader.medialoader.LastAddedLoader;
import com.ldt.musicr.loader.medialoader.PlaylistSongLoader;

import com.ldt.musicr.model.Playlist;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.Util;
import com.makeramen.roundedimageview.RoundedDrawable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FeaturePlaylistAdapter extends RecyclerView.Adapter<FeaturePlaylistAdapter.ItemHolder> {
    private static final String TAG = "PlaylistAdapter";
    public ArrayList<Playlist> mPlaylistData = new ArrayList<>();

    public FeaturePlaylistAdapter setListener(PlaylistClickListener listener) {
        mListener = listener;
        return this;
    }

    public PlaylistClickListener mListener;
    private Context mContext;
    private boolean showAuto;
    private int songCountInt;
    private long firstAlbumID = -1;

    public interface PlaylistClickListener {
        void onClickPlaylist(Playlist playlist, @Nullable Bitmap bitmap);
    }

    public FeaturePlaylistAdapter(Context mContext, boolean showAuto) {
        this.mContext = mContext;
        this.showAuto = showAuto;
    }

    public void unBindAdapter() {
        mListener = null;
        mContext = null;
    }

    public void setData(List<Playlist> data) {
        mPlaylistData.clear();
        if (data != null) {
            mPlaylistData.addAll(data);
            notifyDataSetChanged();

        }
    }

    public void addData(ArrayList<Playlist> data) {
        if (data != null) {
            int posBefore = mPlaylistData.size();
            mPlaylistData.addAll(data);
            notifyItemRangeInserted(posBefore, data.size());
        }
    }

    @NotNull
    @Override
    public ItemHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feature_playlist, viewGroup, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull final ItemHolder itemHolder, int i) {
        // Lấy item Playlist thứ i
        final Playlist playlist = mPlaylistData.get(i);
        // tên tương ứng     itemHolder.title.setText(playlist.name);

        // lấy uri của art
        Log.d(TAG, "one");


        new PlaylistBitmapLoader(this, playlist, itemHolder).execute();

        itemHolder.art.setTag(firstAlbumID);
        itemHolder.title.setText(playlist.name);
        if (Util.isLollipop()) itemHolder.art.setTransitionName("transition_album_art" + i);
    }


    private Bitmap getImagePlaylist(int position, Playlist playlist) {
        List<Song> l = getPlaylistWithListId(position, playlist.id);
        return PlaylistArtworkGenerator.getBitmap(mContext, l, true, false);

    }

    @Override
    public int getItemCount() {
        return mPlaylistData.size();
    }


    public List<Song> getPlaylistWithListId(int position, long id) {
        if (mContext != null) {
            firstAlbumID = -1;
            if (showAuto) {
                switch (position) {
                    case 0:
                        return LastAddedLoader.getLastAddedSongs(mContext, null);
                    case 1:
                        //TopTracksLoader recentloader = new TopTracksLoader(mContext,TopTracksLoader.QueryType.RecentSongs);
                        return TopAndRecentlyPlayedTracksLoader.getRecentlyPlayedTracks(mContext);
                    case 2:
                        //TopTracksLoader topTracksLoader = new TopTracksLoader(mContext,TopTracksLoader.QueryType.TopTracks);
                        return TopAndRecentlyPlayedTracksLoader.getTopTracks(mContext);
                    default:
                        return new ArrayList<>(PlaylistSongLoader.getPlaylistSongList(mContext, (int) id));// PlaylistSongLoader.getSongsInPlaylist(mContext, id);
                }
            } else new ArrayList<>(PlaylistSongLoader.getPlaylistSongList(mContext, (int) id));
        }
        return null;
    }

    private String getPlaylistArtUri(int position, long id) {
        if (mContext != null) {
            firstAlbumID = -1;
            if (showAuto) {
                switch (position) {
                    case 0:
                        List<Song> lastAddedSongs = LastAddedLoader.getLastAddedSongs(mContext);
                        songCountInt = lastAddedSongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = lastAddedSongs.get(0).albumId;
                            return Util.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 1:
                        //TopTracksLoader recentloader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
                        List<Song> recentsongs = TopAndRecentlyPlayedTracksLoader.getRecentlyPlayedTracks(mContext);// SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                        songCountInt = recentsongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = recentsongs.get(0).albumId;
                            return Util.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 2:
                        //TopTracksLoader topTracksLoader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.TopTracks);
                        List<Song> topsongs = TopAndRecentlyPlayedTracksLoader.getTopTracks(mContext);// SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                        songCountInt = topsongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = topsongs.get(0).albumId;
                            return Util.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    default:
                        List<PlaylistSong> playlistsongs = PlaylistSongLoader.getPlaylistSongList(mContext, (int) id);
                        songCountInt = playlistsongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = playlistsongs.get(0).albumId;
                            return Util.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";

                }
            } else {
                List<PlaylistSong> playlistsongs = PlaylistSongLoader.getPlaylistSongList(mContext, (int) id);
                songCountInt = playlistsongs.size();

                if (songCountInt != 0) {
                    firstAlbumID = playlistsongs.get(0).albumId;
                    return Util.getAlbumArtUri(firstAlbumID).toString();
                } else return "nosongs";
            }
        }
        return null;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        protected TextView title;
        protected ImageView art;
        View view_over;
        protected View root;
        int currentColor = 0;

        ItemHolder(View view) {
            super(view);
            root = view;
            this.title = view.findViewById(R.id.playlist_title);
            this.art = view.findViewById(R.id.playlist_image);
            view_over = view.findViewById(R.id.playlist_over);
            view_over.setOnClickListener(this);
            view_over.setOnTouchListener(this);
        }

        @Override
        public void onClick(View v) {
            //Todo: Navigate to playlist detail
            final android.view.animation.Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce_slow);
            BounceInterpolator interpolator = new BounceInterpolator(0.1, 30);
            myAnim.setInterpolator(interpolator);
            myAnim.setDuration(350);
            itemView.startAnimation(myAnim);

            if (mListener != null) {
                Bitmap bitmap = null;
                Drawable d = art.getDrawable();
                if (d instanceof BitmapDrawable) bitmap = ((BitmapDrawable) d).getBitmap();
                else if (d instanceof RoundedDrawable)
                    bitmap = ((RoundedDrawable) d).getSourceBitmap();
                mListener.onClickPlaylist(mPlaylistData.get(getAdapterPosition()), bitmap);
            }

        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (currentColor != Tool.getBaseColor()) {
                currentColor = Tool.getBaseColor();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((RippleDrawable) view_over.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
                }
            }
            return false;
        }
    }


    private static class PlaylistBitmapLoader extends AsyncTask<Void, Void, Bitmap> {
        private FeaturePlaylistAdapter mAdapter;
        private ItemHolder mItemHolder;
        private Playlist mPlaylist;

        PlaylistBitmapLoader(FeaturePlaylistAdapter adapter, Playlist playlist, ItemHolder item) {
            mAdapter = adapter;
            mItemHolder = item;
            mPlaylist = playlist;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mItemHolder.art.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(Void... v) {

            List<Song> l = mAdapter.getPlaylistWithListId(mItemHolder.getAdapterPosition(), mPlaylist.id);
            return PlaylistArtworkGenerator.getBitmap(mAdapter.mContext, l, false, false);
        }
    }

}