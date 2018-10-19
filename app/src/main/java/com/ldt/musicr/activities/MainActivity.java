package com.ldt.musicr.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.Switch;

import com.ldt.musicr.fragments.MusicControllerFragment;
import com.ldt.musicr.fragments.TabLayerController;
import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.fragments.PlaylistControllerFragment;

import com.ldt.musicr.fragments.MainScreenFragment;
import com.ldt.musicr.R;
import com.ldt.musicr.views.BubbleMenu.BubbleMenuCenter;;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class MainActivity extends BaseActivity  {
    private static final String TAG = "MainActivity";

    boolean openedDrawer = false;

    public void openAndCloseDrawer()
    {


        if(!openedDrawer) {
            InitializeBackWall();
            mainScreenFragment =MainScreenFragment.Initialize(this);
           pushFragment(mainScreenFragment, true);
        }
        openedDrawer = true;
    }
    public BubbleMenuCenter bubbleMenuCenter;
    public int[] ScreenSize;
    public float[] ScreenSizeDP;
    private MainScreenFragment mainScreenFragment;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       // Log.d("Permission Order","Reply Permission'");
        mainScreenFragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
    String action;
    public int getColor24bit()
    {
        return mainScreenFragment.color24bit;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        action = getIntent().getAction();
        Log.d(TAG, "action = " + action);
        super.onCreate(savedInstanceState);

        bubbleMenuCenter = new BubbleMenuCenter(this);
        setContentView(R.layout.activity_layout);

        FrameLayout f= findViewById(R.id.activity_layout_root);
        f.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); // trong suốt status bar và thanh navigation màu đen.
        CountDownTimer t = new CountDownTimer(750,750) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                runLoading();
            }
        }.start();
    }



    public void MergeUI()
    {
        container = findViewById(R.id.container_of_container);
    }
   public TabLayerController tabLayerController;
    private void runLoading()
    {
        // load the root layout of activity
        FrameLayout parents = findViewById(R.id.activity_layout_root);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.basic_activity_layout, findViewById(R.id.rootEveryThing));
        View v = findViewById(R.id.ViewOneDp);
        parents.removeView(v);
        parents.addView(view,0);
        callSuperMergeUI();
        MergeUI();
        ScreenSize= Tool.getRefreshScreenSize(this);
        ScreenSizeDP = Tool.getScreenSizeInDp(this);
        tabLayerController = new TabLayerController(this,StatusHeight,NavigationHeight,container);

        musicControllerFragment = new MusicControllerFragment();
        playlistControllerFragment = new PlaylistControllerFragment();
        TabLayerController.BaseTabLayer tabSwitcher = new TabLayerController.BaseTabLayer() {
            @Override
            public void onUpdateLayer(TabLayerController.Attr attr, float pcOnTopLayer, int active_i) {

                if(active_i==0) {  // onTopLayer
                    TabSwitcherFrameLayout.setDarken(0.3f*(1-pcOnTopLayer),false);
                    TabSwitcherFrameLayout.setRoundNumber(1-pcOnTopLayer,true);

                    TabSwitcherFrameLayout.setScaleX(0.9f+0.1f*pcOnTopLayer);
                    TabSwitcherFrameLayout.setScaleY(0.9f+0.1f*pcOnTopLayer);
                    TabSwitcherFrameLayout.setTranslationY(-tabLayerController.ScreenSize[1]*(1- 0.9f -0.1f*pcOnTopLayer)/2.0f);
                }
                else if(active_i==1) { // just behind onTopLayer
                    TabSwitcherFrameLayout.setDarken(0.3f*pcOnTopLayer,false);
                    TabSwitcherFrameLayout.setRoundNumber(pcOnTopLayer,true);
                }
                else { //  other, active_i >1
                    // min = 0.3
                    // max = 0.45
                    float min = 0.3f, max =0.9f;
                    float hieu = max - min;
                    float heSo_sau = (active_i-1.0f)/(active_i -0.75f); // 1/2, 2/3,3/4, 4/5, 5/6 ...
                    float heSo_truoc =  (active_i-2.0f)/(active_i-0.75f); // 0/1, 1/2, 2/3, ...
                    float darken = min + hieu*heSo_truoc + hieu*(heSo_sau - heSo_truoc)*pcOnTopLayer;
                   // Log.d(TAG, "darken = " + darken);
                    TabSwitcherFrameLayout.setDarken(darken,false);
                 //   TabSwitcherFrameLayout.setDarken(0.3f + 0.6f*pcOnTopLayer,false);
                    TabSwitcherFrameLayout.setRoundNumber(1,true);
                }
            }

            @Override
            public boolean onTouchParentView(boolean handled) {
                return false;
            }

            @Override
            public FrameLayout parentView() {
                return TabSwitcherFrameLayout;
            }

            @Override
            public void onAddedToContainer(TabLayerController.Attr attr) {
                // not called
            }

            @Override
            public MarginValue defaultBottomMargin() {
                return MarginValue.BELOW_NAVIGATION;
            }

            @Override
            public MarginValue maxPosition() {
                return MarginValue.ZERO;
            }

            @Override
            public boolean onBackPressed() {
               return onBackUILayer();
            }

            @Override
            public MarginValue minPosition() {
                return MarginValue.VALUE_STT_50DIP;
            }

            @Override
            public String tag() {
                return "TabSwitcherFrameLayout";
            }

        };
        tabLayerController.addBaseListener(tabSwitcher,0);
        tabLayerController.getMyAttr(tabSwitcher).upInterpolator = 5;
        tabLayerController.addTabLayerFragment(musicControllerFragment,0);
        tabLayerController.addTabLayerFragment(playlistControllerFragment,0);
        //TODO: add mainscreenFragment
    //    tabLayerController.setListeners(musicControllerFragment,0);

      // Initialize_PlaylistController();
        onHideLayoutAfterNavigation();
        openAndCloseDrawer();
        setHeightOfNavigation();
    }

    private void onHideLayoutAfterNavigation()
    {
        getWindow().addFlags( SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION| SYSTEM_UI_FLAG_LAYOUT_STABLE);//|WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
   //     getWindow().addFlags( SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
   //             SYSTEM_UI_FLAG_LAYOUT_STABLE );
    //    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    private Bitmap getWallpapers()
    {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        return ((BitmapDrawable)wallpaperDrawable).getBitmap().copy(Bitmap.Config.ARGB_8888,true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    boolean Scroll2Top()
    {
     //  return mainScreenFragment.Scroll2Top();
        return false;
    }
    @Override
    public void onBackPressed()
    {
        if(tabLayerController!=null&&tabLayerController.onBackPressed())
            return;
       super.onBackPressed();
    }

    public void SlideDownNShowWallBack() {
      callInBackProgress();
    //    Tool.showToast(this,"End Of Fragment",500);
    }
    private boolean inBackProgress = false;
    private ValueAnimator va;
    private void callInBackProgress()
    {
        if(inBackProgress) {
            va.end();
            va.removeAllUpdateListeners();
            va= null;
            finish();
            return;
        }
        inBackProgress = true;
        Tool.showToast(this,"Please press back again to exit",500);
        if(va==null) va = ValueAnimator.ofFloat(0,1);
        va.setDuration(1500);
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                  inBackProgress = false;
            }
        });
        va.start();
    }


    public MusicControllerFragment musicControllerFragment;
    public PlaylistControllerFragment playlistControllerFragment;

}
