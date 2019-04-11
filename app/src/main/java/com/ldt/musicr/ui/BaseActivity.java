package com.ldt.musicr.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;
import com.ldt.musicr.service.MusicStateListener;
import com.ldt.musicr.service.ITimberService;
import com.ldt.musicr.service.MusicPlayer;
import com.ldt.musicr.service.MusicService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.ldt.musicr.service.MusicPlayer.mService;
/**
 *  Create relationship between Activity and Music Player Service
 */

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection, MusicStateListener {
    private static final String TAG = "BaseActivity";
    private final ArrayList<MusicStateListener> mMusicStateListeners = new ArrayList<>();
    private MusicPlayer.ServiceToken mToken;
    private PlaybackStatus mPlaybackStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        mToken = MusicPlayer.bindToService(this, this);

        mPlaybackStatus = new PlaybackStatus(this);
        //make out_volume keys change multimedia out_volume even if music is not playing now
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart");
        super.onStart();
        if(mService==null) mToken = MusicPlayer.bindToService(this, this);
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
        mMusicStateListeners.clear();
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
        // Let the listener know to the meta changed
        for (final MusicStateListener listener : mMusicStateListeners) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    @Override
    public void restartLoader() {
        Log.d(TAG,"restartLoader");
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListeners) {
            if (listener != null) {
                listener.restartLoader();
            }
        }
    }

    @Override
    public void onPlaylistChanged() {
        Log.d(TAG,"onPlaylistChanged");
        // Let the listener know to update a list
        for (final MusicStateListener listener : mMusicStateListeners) {
            if (listener != null) {
                listener.onPlaylistChanged();
            }
        }
    }

    @NonNull
    public int getGlobalColor() {
        return Tool.getMostCommonColor();
    }

    public void addMusicStateListener(final MusicStateListener listener) {
        if (listener == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (listener != null) {
            mMusicStateListeners.add(listener);
        }
    }
    public void addMusicStateListener(final MusicStateListener listener, boolean firstIndex) {
        if (listener == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (listener != null) {
            mMusicStateListeners.add(0,listener);
        }
    }

    public void removeMusicStateListener(final MusicStateListener status) {
        if (status != null) {
            mMusicStateListeners.remove(status);
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!Utils.hasEffectsPanel(BaseActivity.this)) {
            menu.removeMotionLayer(R.id.action_equalizer);
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
            if (baseActivity != null&&action!=null) {
                if (action.equals(MusicService.META_CHANGED)) {
                    baseActivity.onMetaChanged();
                } else if (action.equals(MusicService.PLAYSTATE_CHANGED)) {
               //     baseActivity.mPlayPauseProgressButton.getPlayPauseButton().updateState();
                } else if (action.equals(MusicService.REFRESH)) {
                    baseActivity.restartLoader();
                } else if (action.equals(MusicService.PLAYLIST_CHANGED)) {
                    baseActivity.onPlaylistChanged();
                } else if (action.equals(MusicService.TRACK_ERROR)) {
                    final String errorMsg = context.getString(R.string.error_playing_track)+" : "+
                            intent.getStringExtra(MusicService.TrackErrorExtra.TRACK_NAME);
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
