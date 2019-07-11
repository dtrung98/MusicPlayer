package com.ldt.musicr;

import android.app.Application;

import com.ldt.musicr.permission.Nammu;
import com.ldt.musicr.util.PreferenceUtil;

import com.nostra13.universalimageloader.utils.L;


public class App extends Application {
    private static App mInstance;

    public static synchronized App getInstance() {
        return mInstance;
    }

    public PreferenceUtil getPreferencesUtility() {
        return PreferenceUtil.getInstance(App.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        L.writeLogs(true);
     //   L.disableLogging();
        L.writeDebugLogs(true);
    //    Nammu.init(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}