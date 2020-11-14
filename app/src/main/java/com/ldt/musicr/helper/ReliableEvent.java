package com.ldt.musicr.helper;

import androidx.annotation.NonNull;

public class ReliableEvent<T> {
    private final Reliable<T> mReliable;
    private final String mAction;

    public ReliableEvent(Reliable<T> mReliable, String mAction) {
        this.mReliable = mReliable;
        this.mAction = mAction;
    }

    public String getAction() {
        return mAction;
    }

    @NonNull
    public final Reliable<T> getReliable() {
        return mReliable;
    }

}
