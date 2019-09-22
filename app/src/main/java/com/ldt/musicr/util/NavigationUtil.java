/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.ldt.musicr.util;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import androidx.annotation.NonNull;

import com.ldt.musicr.R;
import com.ldt.musicr.loader.medialoader.ArtistLoader;
import com.ldt.musicr.model.Genre;
import com.ldt.musicr.model.Playlist;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.ui.LayerController;
import com.ldt.musicr.ui.MainActivity;
import com.ldt.musicr.ui.page.librarypage.LibraryTabFragment;
import com.ldt.musicr.ui.page.subpages.ArtistPagerFragment;
import com.ldt.musicr.ui.page.subpages.PlaylistPagerFragment;

import es.dmoral.toasty.Toasty;

public class NavigationUtil {

    public static void navigateToBackStackController(@NonNull final  MainActivity activity) {
        final LayerController.Attr playingQueueAttr = activity.getLayerController().getMyAttr(activity.getPlayingQueueController());
        final LayerController.Attr nowPlayingAttr = activity.getLayerController().getMyAttr(activity.getNowPlayingController());

        if(playingQueueAttr.getState()!= LayerController.Attr.MINIMIZED&&nowPlayingAttr.getState()!= LayerController.Attr.MINIMIZED) {
            // 2 layer is maximized
            playingQueueAttr.animateToMin();
            playingQueueAttr.getParent().postDelayed(nowPlayingAttr::animateToMin,550);
        } else if(playingQueueAttr.getState()!= LayerController.Attr.MINIMIZED) {
            // only playing queue
            playingQueueAttr.animateToMin();
        } else if(nowPlayingAttr.getState()!= LayerController.Attr.MINIMIZED) {
            // only now playing
            nowPlayingAttr.animateToMin();
        }
    }

    public static void navigateToNowPlayingController(@NonNull final MainActivity activity) {
        final LayerController.Attr playingQueueAttr = activity.getLayerController().getMyAttr(activity.getPlayingQueueController());
        final LayerController.Attr nowPlayingAttr = activity.getLayerController().getMyAttr(activity.getNowPlayingController());

        if(playingQueueAttr.getState()!= LayerController.Attr.MINIMIZED && nowPlayingAttr.getState() != LayerController.Attr.MINIMIZED) {
            // 2 layer is maximized
            playingQueueAttr.animateToMin();
        } else if(playingQueueAttr.getState()!= LayerController.Attr.MINIMIZED) {
            // playing queue is maximize, while now playing is minimize
            playingQueueAttr.animateToMin();
            playingQueueAttr.getParent().postDelayed(nowPlayingAttr::animateToMax,550);
        } else if(nowPlayingAttr.getState()== LayerController.Attr.MINIMIZED) {
            // both are minimized
            nowPlayingAttr.animateToMax();
        }
    }

    public static void navigateToPlayingQueueController(@NonNull final MainActivity activity) {
        final LayerController.Attr playingQueueAttr = activity.getLayerController().getMyAttr(activity.getPlayingQueueController());
        final LayerController.Attr nowPlayingAttr = activity.getLayerController().getMyAttr(activity.getNowPlayingController());

        if (playingQueueAttr.getState() == LayerController.Attr.MINIMIZED) {
            // playing queue is minimize, while now playing is maximize
            playingQueueAttr.animateToMax();
        }
    }

    public static void navigateToArtist(@NonNull final Activity activity, final int artistId) {
        if (activity instanceof MainActivity) {
            final MainActivity mainActivity = (MainActivity) activity;

            LibraryTabFragment fragment = mainActivity.getBackStackController().navigateToLibraryTab(true);
            if (fragment != null)
                fragment.getNavigationController().presentFragment(ArtistPagerFragment.newInstance(ArtistLoader.getArtist(activity, artistId)));

            navigateToBackStackController(mainActivity);
        }
    }

    public static LibraryTabFragment getLibraryTab( Activity activity) {
        if(activity instanceof  MainActivity) {
            final  MainActivity mainActivity = (MainActivity) activity;
            return mainActivity.getBackStackController().navigateToLibraryTab(false);
        }
        return null;
    }

    public static void navigateToAlbum(@NonNull final Activity activity, final int albumId) {

    }

    public static void navigateToGenre(@NonNull final Activity activity, final Genre genre) {

    }

    public static void navigateToPlaylist(@NonNull final Activity activity, final Playlist playlist) {
        if (activity instanceof MainActivity) {
            final MainActivity mainActivity = (MainActivity) activity;

            LibraryTabFragment fragment = mainActivity.getBackStackController().navigateToLibraryTab(true);
            if (fragment != null)
                fragment.getNavigationController().presentFragment(PlaylistPagerFragment.newInstance(activity,playlist,null));
            navigateToBackStackController(mainActivity);
        }
    }

    public static void openEqualizer(@NonNull final Activity activity) {
        final int sessionId = MusicPlayerRemote.getAudioSessionId();
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            Toasty.error(activity, activity.getResources().getString(R.string.no_audio_ID)).show();
        } else {
            try {
                final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId);
                effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                activity.startActivityForResult(effects, 0);
            } catch (@NonNull final ActivityNotFoundException notFound) {
                Toasty.error(activity, activity.getResources().getString(R.string.no_equalizer)).show();
            }
        }
    }
}
