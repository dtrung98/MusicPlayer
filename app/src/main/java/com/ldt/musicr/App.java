package com.ldt.musicr;

import android.app.Application;

import com.ldt.musicr.permission.Nammu;
import com.ldt.musicr.util.PreferencesUtility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.L;

import java.io.IOException;
import java.io.InputStream;

public class App extends Application {
    private static App mInstance;

    public static synchronized App getInstance() {
        return mInstance;
    }

    public PreferencesUtility getPreferencesUtility() {
        return PreferencesUtility.getInstance(App.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).imageDownloader(new BaseImageDownloader(this) {
            PreferencesUtility prefs = PreferencesUtility.getInstance(App.this);

            @Override
            protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
                if (prefs.loadArtistImages()) return super.getStreamFromNetwork(imageUri, extra);
                throw new IOException();
            }
        }).build();

        ImageLoader.getInstance().init(localImageLoaderConfiguration);
        L.writeLogs(true);
     //   L.disableLogging();
        L.writeDebugLogs(true);
        Nammu.init(this);

    }
}