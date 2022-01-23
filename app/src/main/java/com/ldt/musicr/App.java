package com.ldt.musicr;

import android.app.Application;

import com.ldt.musicr.common.AppStartup;
import com.ldt.musicr.util.PreferenceUtil;


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
        AppStartup.onAppStartup();
    }


}