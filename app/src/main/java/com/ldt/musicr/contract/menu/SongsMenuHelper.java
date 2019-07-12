package com.ldt.musicr.contract.menu;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.service.MusicPlayerRemote;

import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 * modified by Le Dinh Trung (dtrung98)
 */
public class SongsMenuHelper {
    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull List<Song> songs, int menuItemId) {
        switch (menuItemId) {
            case R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(songs);
                return true;
            case R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(songs).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case R.id.action_delete_from_device:
                DeleteSongsDialog.create(songs).show(activity.getSupportFragmentManager(), "DELETE_SONGS");
                return true;
        }
        return false;
    }
}
