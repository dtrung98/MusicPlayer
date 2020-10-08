package com.ldt.musicr.loader.manager;

import java.util.concurrent.ThreadFactory;
import android.os.Process;

public class PriorityThreadFactory implements ThreadFactory {

    private final int mThreadPriority;

    public PriorityThreadFactory(int threadPriority) {
        mThreadPriority = threadPriority;
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(mThreadPriority);
                } catch (Throwable ignored) {

                }
                runnable.run();
            }
        };
        return new Thread(wrapperRunnable);
    }

}