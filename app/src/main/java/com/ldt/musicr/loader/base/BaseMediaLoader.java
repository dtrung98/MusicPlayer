package com.ldt.musicr.loader.base;

import android.os.Process;
import android.content.Context;
import androidx.annotation.NonNull;

import com.ldt.musicr.loader.manager.PriorityThreadFactory;
import com.ldt.musicr.util.PreferenceUtil;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BaseMediaLoader {
    private static final String TAG = "BaseMediaStoreManager";

    private Context mContext;
    public BaseMediaLoader() {
    }

    private ThreadPoolExecutor mExecutor;
    private int mThreadNumber = 6;

    public void init(@NonNull Context context) {
        mContext = context;
        mThreadNumber = PreferenceUtil.getInstance().getThreadNumber();
        if(mExecutor != null) {
            ThreadFactory factory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);
            mExecutor = new ThreadPoolExecutor(
                    mThreadNumber,
                    mThreadNumber * 2,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    factory);
        }

    }

    public void destroy() {
        mContext = null;
        if(mExecutor!=null)
        mExecutor.shutdown();
        mExecutor  = null;
    }
}
