package com.ldt.musicr.service;

public interface MusicServiceEventListener {
    default void onServiceConnected() {}

    default void onServiceDisconnected() {}

    default void onQueueChanged() {}

    default void onPlayingMetaChanged() {}

    default void onPlayStateChanged() {}

    default void onRepeatModeChanged() {}

    default void onShuffleModeChanged() {}

    default void onMediaStoreChanged() {}

    default void onPaletteChanged() {}
}
