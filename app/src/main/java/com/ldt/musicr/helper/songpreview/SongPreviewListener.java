package com.ldt.musicr.helper.songpreview;

public interface SongPreviewListener {
        void onSongPreviewStart(PreviewSong song);
        void onSongPreviewFinish(PreviewSong song);
    }