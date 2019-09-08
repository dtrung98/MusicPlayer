package com.ldt.musicr.loader.base;

public abstract class BaseTask<T extends  BaseMediaStoreManager> implements Runnable {
    private static final String TAG = "BaseTask";

    private T mManager;
    @Override
    public  final void run() {
        onRunning();
    }
    abstract void onRunning();

}

