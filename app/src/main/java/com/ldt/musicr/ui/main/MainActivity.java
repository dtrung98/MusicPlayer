package com.ldt.musicr.ui.main;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import android.widget.FrameLayout;


import com.ldt.musicr.R;
import com.ldt.musicr.ui.main.intro.IntroController;
import com.ldt.musicr.ui.main.navigate.BackStackController;
import com.ldt.musicr.ui.main.nowplaying.NowPlayingController;
import com.ldt.musicr.ui.main.playinglist.PlayingListController;
import com.ldt.musicr.ui.widget.bubblepopupmenu.BubbleMenuCenter;
import com.ldt.musicr.ui.widget.RoundClippingFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_READ_STORAGE = 1;

    @BindView(R.id.rootEveryThing) public ConstraintLayout mRootEverything;
    @BindView(R.id.layer_container) public FrameLayout mLayerContainer;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;
    AudioPreviewer mAudioPreviewer = new AudioPreviewer();
    public AudioPreviewer getAudioPreviewer() {
        return mAudioPreviewer;
    }

    public BubbleMenuCenter bubbleMenuCenter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_STORAGE: {
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // Granted
                        onPermissionGranted();
                    } else onPermissionDenied();
                }
            }

        }
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

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
        bubbleMenuCenter = new BubbleMenuCenter(this);

        mRootEverything.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!checkSelfPermission()) {
                    mIntroController = new IntroController();
                    mIntroController.Init(MainActivity.this,savedInstanceState);
                } else startGUI();
            }
        },100);

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
    LayerControllerV2 mLayerControllerV2;

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
        mPlaylistController = new PlayingListController();
        mBackStackController.attachBottomNavigationView(this);
        mLayerController.init(mLayerContainer,mBackStackController,mNowPlayingController, mPlaylistController);
    }



    public boolean checkSelfPermission() {
     return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                }, MY_PERMISSIONS_READ_STORAGE);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_READ_STORAGE);

            }
        } else onPermissionGranted();
    }

    public void onPermissionOk() {

    }

     public LayerController mLayerController;
     RoundClippingFrameLayout TabSwitcherFrameLayout;
//    private void runLoading()
//    {
//
//        mLayerController = new LayerController(this, Tool.getStatusHeight(getResources()),Tool.getNavigationHeight(this), mLayerContainer);
//
//        musicControllerFragment = new MusicControllerFragment();
//        playlistControllerFragment = new PlaylistControllerFragment();
//        LayerController.BaseLayer tabSwitcher = new LayerController.BaseLayer() {
//            @Override
//            public void onUpdateLayer(LayerController.Attr attr, float pcOnTopLayer, int active_i) {
//
//                if(active_i==0) {  // onTopLayer
//                    TabSwitcherFrameLayout.setDarken(0.3f*(1-pcOnTopLayer),false);
//                    TabSwitcherFrameLayout.setRoundNumber(1-pcOnTopLayer,true);
//
//                    TabSwitcherFrameLayout.setScaleX(0.9f+0.1f*pcOnTopLayer);
//                    TabSwitcherFrameLayout.setScaleY(0.9f+0.1f*pcOnTopLayer);
//                    TabSwitcherFrameLayout.setTranslationY(-mLayerController.ScreenSize[1]*(1- 0.9f -0.1f*pcOnTopLayer)/2.0f);
//                }
//                else if(active_i==1) { // just behind onTopLayer
//                    TabSwitcherFrameLayout.setDarken(0.3f*pcOnTopLayer,false);
//                    TabSwitcherFrameLayout.setRoundNumber(pcOnTopLayer,true);
//                }
//                else { //  other, active_i >1
//                    // min = 0.3
//                    // max = 0.45
//                    float min = 0.3f, max =0.9f;
//                    float hieu = max - min;
//                    float heSo_sau = (active_i-1.0f)/(active_i -0.75f); // 1/2, 2/3,3/4, 4/5, 5/6 ...
//                    float heSo_truoc =  (active_i-2.0f)/(active_i-0.75f); // 0/1, 1/2, 2/3, ...
//                    float darken = min + hieu*heSo_truoc + hieu*(heSo_sau - heSo_truoc)*pcOnTopLayer;
//                   // Log.d(TAG, "darken = " + darken);
//                    TabSwitcherFrameLayout.setDarken(darken,false);
//                 //   TabSwitcherFrameLayout.setDarken(0.3f + 0.6f*pcOnTopLayer,false);
//                    TabSwitcherFrameLayout.setRoundNumber(1,true);
//                }
//            }
//
//            @Override
//            public boolean onTouchParentView(boolean handled) {
//                return false;
//            }
//
//            @Override
//            public FrameLayout parentView() {
//                return TabSwitcherFrameLayout;
//            }
//
//            @Override
//            public void onAddedToContainer(LayerController.Attr attr) {
//                // not called
//            }
//
//            @Override
//            public MarginValue maxPosition() {
//                return MarginValue.ZERO;
//            }
//
//            @Override
//            public boolean onBackPressed() {
//               return false;
//            }
//
//            @Override
//            public MarginValue minPosition() {
//                return MarginValue.VALUE_STT_50DIP;
//            }
//
//            @Override
//            public String tag() {
//                return "TabSwitcherFrameLayout";
//            }
//
//        };
//        mLayerController.addBaseListener(tabSwitcher,0);
//        mLayerController.getMyAttr(tabSwitcher).upInterpolator = 5;
//        mLayerController.addTabLayerFragment(musicControllerFragment,0);
//        mLayerController.addTabLayerFragment(playlistControllerFragment,0);
//
//        onHideLayoutAfterNavigation();
//    }

    private void onHideLayoutAfterNavigation()
    {
      //  getWindow().addFlags( SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION| SYSTEM_UI_FLAG_LAYOUT_STABLE);

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

    public BackStackController mBackStackController;
    public NowPlayingController mNowPlayingController;
    public PlayingListController mPlaylistController;

}
