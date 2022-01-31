package com.ldt.musicr.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class EventKey {
    sealed class EventKeyParcelable: EventKey(), Parcelable
    @Parcelize
    object Other : EventKeyParcelable()

    // Navigation
    sealed class NavigationKey(): EventKey()

    // Music Service Event
    sealed class MusicServiceEvent: EventKey()
    object OnServiceConnected : MusicServiceEvent()
    object OnServiceDisconnected : MusicServiceEvent()
    object OnQueueChanged: MusicServiceEvent()
    object OnPlayingMetaChanged: MusicServiceEvent()
    object OnPlayStateChanged: MusicServiceEvent()
    object OnRepeatModeChanged: MusicServiceEvent()
    object OnShuffleModeChanged: MusicServiceEvent()
    object OnMediaStoreChanged: MusicServiceEvent()
    object OnPaletteChanged: MusicServiceEvent()

    object OnLoadedSongs: EventKey()
    object OnLoadedPlaylists: EventKey()
    object OnLoadedArtists: EventKey()
    object OnLoadedMedia: EventKey()

    object OnSystemBarsInsetUpdated: EventKey()
    object OnSearchInterfaceAppeared: EventKey()
    object OnSearchInterfaceDisappeared: EventKey()

    sealed class SettingKey: EventKey() {
        object ChangedSetArtistArtworkAsBackground: SettingKey()
    }
}