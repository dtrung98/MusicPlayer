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
import com.ldt.musicr.ui.intro.IntroController;
import com.ldt.musicr.ui.playingqueue.PlayingQueueController;
import com.ldt.musicr.ui.bottomnavigationtab.BackStackController;
import com.ldt.musicr.ui.nowplaying.NowPlayingController;
import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_WRITE_STORAGE = 1;

    @BindView(R.id.rootEveryThing)
    public ConstraintLayout mRootEverything;

    @BindView(R.id.layer_container)
    public FrameLayout mLayerContainer;

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    private void bindView() {
        mRootEverything = findViewById(R.id.rootEveryThing);
        mLayerContainer = findViewById(R.id.layer_container);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
    }

    public BackStackController mBackStackController;
    public NowPlayingController mNowPlayingController;
    public PlayingQueueController mPlayingQueueController;

    public LayerController getLayerController() {
        return mLayerController;
    }

    public LayerController mLayerController;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        super.onCreate(savedInstanceState);
        assign(0);
        setContentView(R.layout.basic_activity_layout);
        bindView();
        assign("set & bind");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
        assign("set fullscreen");
        mRootEverything.post(new Runnable() {
            @Override
            public void run() {
                assign(2);
                boolean isPermissionGranted = checkSelfPermission();
                assign(3);
                if(!isPermissionGranted) {

                    if(mIntroController ==null)
                    mIntroController = new IntroController();

                    mIntroController.init(MainActivity.this,savedInstanceState);
                    assign(4);
                } else startGUI();
            }
        });

    }
    private long mStart = System.currentTimeMillis();
    private void assign(Object mark) {
        long current = System.currentTimeMillis();
        Log.d(TAG, "logTime: Time "+ mark+" = "+(current - mStart));
        mStart = current;
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

        assign(5);
        //runLoading();
        mLayerController = new LayerController(this);
        mBackStackController = new BackStackController();
        mNowPlayingController = new NowPlayingController();
        mPlayingQueueController = new PlayingQueueController();

        assign(6);
        mBackStackController.attachBottomNavigationView(this);

        assign(7);
        mLayerController.init(mLayerContainer,mBackStackController,mNowPlayingController, mPlayingQueueController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        assign("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        assign("onResume");
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
        if(mPlayingQueueController !=null) mPlayingQueueController.onColorPaletteReady(color1,color2,alpha1,alpha2);
    }
    public void popUpPlaylistTab() {
        if(mPlayingQueueController !=null) mPlayingQueueController.popUp();
    }

    public BackStackController getBackStackController() {
        return mBackStackController;
    }

    public NowPlayingController getNowPlayingController() {
        return mNowPlayingController;
    }

    public PlayingQueueController getPlayingQueueController() {
        return mPlayingQueueController;
    }
}
