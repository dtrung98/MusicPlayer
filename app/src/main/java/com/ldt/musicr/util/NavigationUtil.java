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
import android.support.annotation.NonNull;

import com.ldt.musicr.R;
import com.ldt.musicr.model.Genre;
import com.ldt.musicr.model.Playlist;
import com.ldt.musicr.service.MusicPlayerRemote;

import es.dmoral.toasty.Toasty;

public class NavigationUtil {

    public static void goToArtist(@NonNull final Activity activity, final int artistId) {

    }

    public static void goToAlbum(@NonNull final Activity activity, final int albumId) {

    }

    public static void goToGenre(@NonNull final Activity activity, final Genre genre) {

    }

    public static void goToPlaylist(@NonNull final Activity activity, final Playlist playlist) {

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
