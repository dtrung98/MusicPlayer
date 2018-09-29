package com.ldt.musicr.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ldt.musicr.InternalTools.BitmapEditor;
import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.R;
import com.ldt.musicr.listeners.MusicStateListener;
import com.ldt.musicr.permission.Nammu;
import com.ldt.musicr.services.ITimberService;
import com.ldt.musicr.services.MusicPlayer;
import com.ldt.musicr.services.MusicService;
import com.ldt.musicr.utils.TimberUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.ldt.musicr.InternalTools.BitmapEditor.updateSat;
import static com.ldt.musicr.services.MusicPlayer.mService;
/**
 *  Create relationship between Activity and Music Player Service
 */

public abstract class BaseActivity extends SupportFragmentPlusActivity implements ServiceConnection, MusicStateListener {
    private static final String TAG = "BaseActivity";
    private final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();
    private MusicPlayer.ServiceToken mToken;
    private PlaybackStatus mPlaybackStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        mToken = MusicPlayer.bindToService(this, this);

        mPlaybackStatus = new PlaybackStatus(this);
        //make volume keys change multimedia volume even if music is not playing now
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart");
        super.onStart();

        final IntentFilter filter = new IntentFilter();
        // Play and pause changes
        filter.addAction(MusicService.PLAYSTATE_CHANGED);
        // Track changes
        filter.addAction(MusicService.META_CHANGED);
        // Update a list, probably the playlist fragment's
        filter.addAction(MusicService.REFRESH);
        // If a playlist has changed, notify us
        filter.addAction(MusicService.PLAYLIST_CHANGED);
        // If there is an error playing a track
        filter.addAction(MusicService.TRACK_ERROR);

        registerReceiver(mPlaybackStatus, filter);

    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop");
        super.onStop();


    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        onMetaChanged();
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        Log.d(TAG,"onServiceConnected");
        mService = ITimberService.Stub.asInterface(service);

        onMetaChanged();
    }


    @Override
    public void onServiceDisconnected(final ComponentName name) {
        Log.d(TAG,"onServiceDisconnected");
        mService = null;
    }

    @Override
    protected void onDestroy() {
Log.d(TAG,"onDestroy");
        super.onDestroy();
        // Unbind from the service
        if (mToken != null) {
            MusicPlayer.unbindFromService(mToken);
            mToken = null;
        }

        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {
        }
        mMusicStateListener.clear();
    }
    private boolean black_theme = false;
    private long mAlbumId = -1;
    private int mColor24Bit = 0;
    public int getColor24Bit() {
        return mColor24Bit;
    }
    private class loadArtWork extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... v) {

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 38;
                Bitmap origin, sample;

            //    origin = BitmapFactory.decodeFile(TimberUtils.getAlbumArtUri(albumID).getEncodedPath());
              origin =  ImageLoader.getInstance().loadImageSync(TimberUtils.getAlbumArtUri(mAlbumId).toString());
                // if bitmap can't be loaded, load default image
                if(origin==null) {
                    origin = BitmapFactory.decodeResource(getResources(), R.drawable.default_image2);
                    sample = BitmapFactory.decodeResource(getResources(),R.drawable.default_image2,options);
                } else {
                 //   sample = BitmapFactory.decodeFile(TimberUtils.getAlbumArtUri(albumID).getEncodedPath());
                    sample = BitmapEditor.getResizedBitmap(origin,origin.getHeight()/38,origin.getWidth()/38);
                 //   sample = origin.copy(origin.getConfig(),false);
                }
                mArtWork = origin.copy(origin.getConfig(),false);

                sample = updateSat(sample, 4);
                sample = BitmapEditor.fastblur(sample, 1, 4);
                int[] averageColorRGB = BitmapEditor.getAverageColorRGB(sample);
                black_theme = BitmapEditor.PerceivedBrightness(95, averageColorRGB);

                mColor24Bit = (averageColorRGB[0] << 16 | averageColorRGB[1] << 8 | averageColorRGB[2]);

                mBlurArtWork = sample.copy(sample.getConfig(),false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Tool.setGlobalColor(0xff << 24 | mColor24Bit);
            Tool.setSurfaceColor(ColorReferTo(Tool.getGlobalColor()));
            onArtWorkChanged();
        }

    }
    private int ColorReferTo(int cmc) {
        float[] hsv = new float[3];
        Color.colorToHSV(cmc, hsv);
        //     Log.d(hsv[0] + "|" + hsv[1] + "|" + hsv[2], "ColorMe");
        float toEight = hsv[0] / 45 + 0.5f;
        if (toEight > 8 | toEight <= 1) return 0xffFF3B30;
        if (toEight <= 2) return 0xffFF9500;
        if (toEight <= 3) return 0xffFFCC00;
        if (toEight <= 4) return 0xff4CD964;
        if (toEight <= 5) return 0xff5AC8FA;
        if (toEight <= 6) return 0xff007AFF;
        if (toEight <= 7) return 0xff5855D6;
        return 0xffFF2D55;
    }

    @Override
    public void onMetaChanged() {
        Log.d(TAG,"onMetaChanged");
            mAlbumId = MusicPlayer.getCurrentAlbumId();
            new loadArtWork().execute();
        // Let the listener know to the meta changed
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    @Override
    public void restartLoader() {
        Log.d(TAG,"restartLoader");
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.restartLoader();
            }
        }
    }

    @Override
    public void onPlaylistChanged() {
        Log.d(TAG,"onPlaylistChanged");
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onPlaylistChanged();
            }
        }
    }

    @Override
    public void onArtWorkChanged() {
        Log.d(TAG,"onArtWorkChanged");
        for(final MusicStateListener listener : mMusicStateListener) {
            if(listener!=null)
                listener.onArtWorkChanged();
        }
    }
    private Bitmap mArtWork, mBlurArtWork;

    @Nullable
    public Bitmap getArtWork() {
        return mArtWork;
    }

    @Nullable
    public Bitmap getBlurArtWork() {
        return mBlurArtWork;
    }

    @NonNull
    public int getGlobalColor() {
        return Tool.getGlobalColor();
    }

    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicStateListener.add(status);
        }
    }

    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicStateListener.remove(status);
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!TimberUtils.hasEffectsPanel(BaseActivity.this)) {
            menu.removeItem(R.id.action_equalizer);
        }
        ATE.applyMenu(this, getATEKey(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_settings:
                NavigationUtils.navigateToSettings(this);
                return true;
            case R.id.action_shuffle:
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.shuffleAll(BaseActivity.this);
                    }
                }, 80);

                return true;
            case R.id.action_search:
                NavigationUtils.navigateToSearch(this);
                return true;
            case R.id.action_equalizer:
                NavigationUtils.navigateToEqualizer(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public String getATEKey() {
        return Helpers.getATEKey(this);
    }

    public void setPanelSlideListeners(SlidingUpPanelLayout panelLayout) {
        panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1);
            }

            @Override
            public void onPanelExpanded(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(0);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }
    */
    private final static class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<BaseActivity> mReference;


        public PlaybackStatus(final BaseActivity activity) {
            mReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {

            final String action = intent.getAction();
            Log.d(TAG,"onReceive, action = "+action);
            BaseActivity baseActivity = mReference.get();
            if (baseActivity != null) {
                if (action.equals(MusicService.META_CHANGED)) {
                    baseActivity.onMetaChanged();
                } else if (action.equals(MusicService.PLAYSTATE_CHANGED)) {
               //     baseActivity.mPlayPauseProgressButton.getPlayPauseButton().updateState();
                } else if (action.equals(MusicService.REFRESH)) {
                    baseActivity.restartLoader();
                } else if (action.equals(MusicService.PLAYLIST_CHANGED)) {
                    baseActivity.onPlaylistChanged();
                } else if (action.equals(MusicService.TRACK_ERROR)) {
                    final String errorMsg = context.getString(R.string.error_playing_track,
                            intent.getStringExtra(MusicService.TrackErrorExtra.TRACK_NAME));
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
/*
    public class initQuickControls extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            QuickControlsFragment fragment1 = new QuickControlsFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.quickcontrols_container, fragment1).commitAllowingStateLoss();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
//            QuickControlsFragment.topContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    NavigationUtils.navigateToNowplaying(BaseActivity.this, false);
//                }
//            });
        }

        @Override
        protected void onPreExecute() {
        }
    }
    */
}
