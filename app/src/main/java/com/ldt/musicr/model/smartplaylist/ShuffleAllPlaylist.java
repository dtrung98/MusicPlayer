package com.ldt.musicr.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.ldt.musicr.R;
import com.ldt.musicr.loader.medialoader.SongLoader;
import com.ldt.musicr.model.Song;

import java.util.ArrayList;

public class ShuffleAllPlaylist extends AbsSmartPlaylist {

    public ShuffleAllPlaylist(@NonNull Context context) {
        super(context.getString(R.string.action_shuffle_all), R.drawable.ic_shuffle_white_36dp);
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NonNull Context context) {
        return SongLoader.getAllSongs(context);
    }

    @Override
    public void clear(@NonNull Context context) {
        // Shuffle all is not a real "Smart Playlist"
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ShuffleAllPlaylist(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<ShuffleAllPlaylist> CREATOR = new Parcelable.Creator<ShuffleAllPlaylist>() {
        public ShuffleAllPlaylist createFromParcel(Parcel source) {
            return new ShuffleAllPlaylist(source);
        }

        public ShuffleAllPlaylist[] newArray(int size) {
            return new ShuffleAllPlaylist[size];
        }
    };
}
