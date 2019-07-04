package com.ldt.musicr.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;


import android.widget.FrameLayout;


import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.ui.intro.IntroController;
import com.ldt.musicr.ui.playingqueue.PlayingQueueController;
import com.ldt.musicr.ui.bottomnavigationtab.BackStackController;
import com.ldt.musicr.ui.nowplaying.NowPlayingController;

import com.ldt.musicr.ui.widget.RoundClippingFrameLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_WRITE_STORAGE = 1;

    @BindView(R.id.rootEveryThing) public ConstraintLayout mRootEverything;
    @BindView(R.id.layer_container) public FrameLayout mLayerContainer;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;
    AudioPreviewPlayer mAudioPreviewPlayer = new AudioPreviewPlayer();

    public AudioPreviewPlayer getAudioPreviewPlayer() {
        return mAudioPreviewPlayer;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_STORAGE: {
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // Granted
                        onPermissionGranted();
                    } else onPermissionDenied();
                }
            }

        }
    }

    public void goToSongTab() {
        mBackStackController.goToSongTab();
    }
    public void goToPlaylistTab() {
        mBackStackController.goToPlaylistTab();
    }

    @Override
    protected void onDestroy() {
        if(mAudioPreviewPlayer!=null) removeMusicServiceEventListener(mAudioPreviewPlayer);
        super.onDestroy();
    }

    public void setDataForPlayingQueue(List<Song> songs2) {
        if(mPlaylistController!=null) mPlaylistController.setData(songs2);
    }




    public interface PermissionListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    private PermissionListener mPermissionListener;
    public  void setPermissionListener(PermissionListener listener) {
        mPermissionListener = listener;
    }

    public void removePermissionListener() {
        mPermissionListener = null;
    }

    private void onPermissionGranted() {
        if(mPermissionListener!=null) mPermissionListener.onPermissionGranted();
    }

    private void onPermissionDenied() {
        if(mPermissionListener!=null) mPermissionListener.onPermissionDenied();
    }
    IntroController mIntroController;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(createBundleNoFragmentRestore(savedInstanceState));

        setContentView(R.layout.basic_activity_layout);
        ButterKnife.bind(this);
        mLayerContainer.setVisibility(View.GONE);
        if(mAudioPreviewPlayer!=null) addMusicServiceEventListener(mAudioPreviewPlayer);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );

        mRootEverything.post(new Runnable() {
            @Override
            public void run() {

                if(!checkSelfPermission()) {
                    mIntroController = new IntroController();
                    mIntroController.Init(MainActivity.this,savedInstanceState);
                } else startGUI();
            }
        });

    }
    /**
     * Improve bundle to prevent restoring of fragments.
     * @param bundle bundle container
     * @return improved bundle with removed "fragments parcelable"
     */
    private static Bundle createBundleNoFragmentRestore(Bundle bundle) {
        if (bundle != null) {
            bundle.remove("android:support:fragments");
        }
        return bundle;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mLayerController!=null) mLayerController. onConfigurationChanged(newConfig);
    }

    public void startGUI() {
      //  Toast.makeText(this,"Start GUI",Toast.LENGTH_SHORT).show();
        if(mIntroController!=null) {
            removePermissionListener();
            mIntroController.getNavigationController().popAllFragments();
        }
        //runLoading();
        mLayerController = new LayerController(this);
        mBackStackController = new BackStackController();
        mNowPlayingController = new NowPlayingController();
        mPlaylistController = new PlayingQueueController();
        mBackStackController.attachBottomNavigationView(this);
        mLayerController.init(mLayerContainer,mBackStackController,mNowPlayingController, mPlaylistController);
    }

    public boolean checkSelfPermission() {
     return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, MY_PERMISSIONS_WRITE_STORAGE);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_WRITE_STORAGE);

            }
        } else onPermissionGranted();
    }

    public void onPermissionOk() {

    }

     public LayerController mLayerController;
     RoundClippingFrameLayout TabSwitcherFrameLayout;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed()
    {
        if(mLayerController !=null&& mLayerController.onBackPressed())
            return;
       super.onBackPressed();
    }

    public void setTheme(boolean white) {
        Log.d(TAG, "setTheme: "+white);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View root = mRootEverything;
            if(root!=null&&!white)
                root.setSystemUiVisibility(0);
            else if(root!=null)
                root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
    public boolean backStackStreamOnTouchEvent(MotionEvent event) {
        if(mBackStackController!=null) return mBackStackController.streamOnTouchEvent(event);
        return false;
    }

    public void setPlaylistColorPalette(int color1, int color2, float alpha1, float alpha2) {
        if(mPlaylistController!=null) mPlaylistController.onColorPaletteReady(color1,color2,alpha1,alpha2);
    }
    public void popUpPlaylistTab() {
        if(mPlaylistController!=null) mPlaylistController.popUp();
    }

    public BackStackController mBackStackController;
    public NowPlayingController mNowPlayingController;
    public PlayingQueueController mPlaylistController;

}
