package com.ldt.musicr.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.WallpaperManager;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;

import com.ldt.musicr.InternalTools.Animation;
import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.MediaData.Song_onload;
import com.ldt.musicr.fragments.FragmentPlus;
import com.ldt.musicr.fragments.PlayControllerFragment;
import com.ldt.musicr.fragments.PlaybackFragment;

import com.ldt.musicr.fragments.MainScreenFragment;
import com.ldt.musicr.R;
import com.ldt.musicr.services.IRTimberService;
import com.ldt.musicr.views.EffectView.MCBubblePopupUIHolder;
import com.ldt.musicr.views.SupportDarkenFrameLayout;

import java.util.ArrayList;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static com.ldt.musicr.services.MusicPlayer.mService;
public class MainActivity extends SupportFragmentActivity implements ServiceConnection {
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
    public MCBubblePopupUIHolder MCBubblePopupUIHolder;
    public FrameLayout music_controller_withFrameLayout;// this is the frameLayout will contain the music controller fragment, it will be added then.
    public FrameLayout playlist_controller_withFrameLayout;// this is the frameLayout will contain the playlist controller fragment, it will be added to music_controller framelayout.
    public int[] ScreenSize;
    public float[] ScreenSizeDP;
    public int Music_Controller_Width=0,Music_Controller_Height=0;
    private int _xDelta;
    private int _yDelta;
    private MainScreenFragment mainScreenFragment;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Permission Order","Reply Permission'");
        mainScreenFragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    public int getColorr()
    {
        return mainScreenFragment.colorr;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MCBubblePopupUIHolder = new MCBubblePopupUIHolder(this);
        setContentView(R.layout.activity_layout);
        View f= findViewById(R.id.activity_layout_root);
        f.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); // trong suốt status bar và thanh navigation màu đen.
        ValueAnimator va = ValueAnimator.ofFloat(0,1);
        va.setDuration(0);
        va.setStartDelay(750);
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                runLoading();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.start();
       // Init_UpDown_musicController();
    }



    public void MergeUI()
    {

    }
    private void runLoading()
    {
        // load the root layout of activity
        FrameLayout parents = findViewById(R.id.activity_layout_root);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.basic_activity_layout, findViewById(R.id.rootEveryThing));
        parents.addView(view,0);
        callSuperMergeUI();
        MergeUI();
        Initialize_PlayController();
        Initialize_PlaylistController();

        onHideLayoutAfterNavigation();
        openAndCloseDrawer();
        setHeightOfNavigation();
    }
    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        mService = IRTimberService.Stub.asInterface(service);

        //onMetaChanged();
    }


    @Override
    public void onServiceDisconnected(final ComponentName name) {
        mService = null;
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

    public void Control_Music_Song_Player(ArrayList<Song_onload> list, int position)
    { // this method will set content for Music Controller, from MainScreenFragment.
        musicControllerFragment.Set_Music_Song_Player(list,position);
    }

    private void setMarginForMusicLayer_ByPercent(float pc)
    {
        float InDp = pc*StatusHeight_DP;
        float scaleMargin = ReturnPercentageScaleFromDps(InDp);
        music_controller_withFrameLayout.setScaleX(scaleMargin);
        music_controller_withFrameLayout.setScaleY(scaleMargin);
        float alpha= max_dark*pc;
       musicControllerFragment.setDarken(alpha,max_dark);
    }
    private boolean black_theme = false;
    private void setMarginForUILayer_ByPercent(float pc, float pc_pl)
    {
      float VirtualStatusHeight_DP = (StatusHeight_DP==0) ? 24 :StatusHeight_DP;
        float InDp = pc*VirtualStatusHeight_DP + pc_pl*VirtualStatusHeight_DP*0.5f;

        if(InDp>=VirtualStatusHeight_DP/2&&!black_theme) {
            musicControllerFragment.statusTheme = FragmentPlus.StatusTheme.WhiteIcon;
          OrderToChangeStatusTheme(musicControllerFragment);
        }
        else if(InDp<VirtualStatusHeight_DP/2&&!black_theme)
        {
            OrderToChangeStatusTheme(musicControllerFragment);
        }

        float scaleMargin = ReturnPercentageScaleFromDps(InDp);
       switch_page_container.setScaleX(scaleMargin);
        switch_page_container.setScaleY(scaleMargin);

        float alpha=max_dark*pc;
        switch_page_container.setDarken(alpha,max_dark);
        switch_page_container.invalidate();
        //   if(alpha<0.7f) alpha = 0.7f;
 //   endarker4SwitchContainer.setAlpha(alpha);

    }
    // Các phân lớp giao diện của ứng dụng
    // Hiện tại có 3 phân lớp
    // Lớp Container chứa Bộ Fragment , điều chuyển giữa các trang giao diện
    // Lớp MusicController là bộ điều khiển nhạc
    // Lớp PlaylistController là bộ điều khiển danh sách phát
    public enum Layer {
        Container,
        MusicController,
        PlaylistController
    }

  //Every when something wants to change transform of some layer, it needs to call this.
    public void onUpdateLayer(Layer whichLayer, float percentOfThisLayer)
    {
        switch (whichLayer)
        {
            case Container: break;
            case MusicController: onUpdateMusicController(percentOfThisLayer); break;
            case PlaylistController: onUpdatePlaylist(percentOfThisLayer); break;
        }
    }

   final float  max_dark = 0.4f;
    private void setAlpha4PlayBar(float pc)
    {
        musicControllerFragment.setAlphaOfPlayBar(pc);
    }
    private float ReturnPercentageScaleFromDps(float Dps)
    {
        // this fuction return the value scaleX and scale Y with the same effect if using Padding in Dps.
        float pcc = (ScreenSizeDP[1]- 2*Dps)/(ScreenSizeDP[1]);
   //     Log.d("Dps = "+Dps+", pcc = "+pcc+", ScreenHeightDp = "+ ScreenHeightDp,"RPSFD");
        return pcc;
    }
    private void Initialize_PlayController()
    {
        if(Init_Music_Controller) return;
        if(musicControllerFragment!=null) return;
    ScreenSize= Tool.getRefreshScreenSize(this);
     ScreenSizeDP = Tool.getScreenSizeInDp(this);
        musicControllerFragment= new PlayControllerFragment();
        // this is the frame contain music controller
        music_controller_withFrameLayout = new SupportDarkenFrameLayout(this);
        float paddingTop = StatusHeight+pixel_unit/5;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        float size = ScreenSize[1]-paddingTop; // kích thước nguyên bản

        params.height = (int)size +NavigationHeight ;
        float maxMove = size - pixel_unit;
        params.topMargin = ScreenSize[1] +NavigationHeight;
        params.bottomMargin = params.topMargin + (int) size -NavigationHeight;
        float Move = size - params.topMargin;
        pc_ms =Move/maxMove; // init pc
        music_controller_withFrameLayout.setId(R.id.music_controller_withFrameLayout);
        control_container.addView(music_controller_withFrameLayout, params); // add

        getFragmentManager().beginTransaction().add(music_controller_withFrameLayout.getId(),musicControllerFragment).commit(); // add fragment to camera
       // Log.d("Param Init : " + params.topMargin+"|"+params.bottomMargin+"|"+params.width+"|"+params.height,"Params");
    }

    public void Initialize_PlaylistController()
    {

        if(music_controller_withFrameLayout==null) Initialize_PlayController();
    //    if(playlistControllerFragment!=null) return;
        // this is the function which init the playlist controller
        Music_Controller_Width = music_controller_withFrameLayout.getWidth();
        Music_Controller_Height = music_controller_withFrameLayout.getHeight();
       playlistControllerFragment = new PlaybackFragment();
        playlist_controller_withFrameLayout = new SupportDarkenFrameLayout(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        params.topMargin =ScreenSize[1] +NavigationHeight;
        params.bottomMargin = ScreenSize[1] - pixel_unit -NavigationHeight;
        playlist_controller_withFrameLayout.setId(R.id.playlist_controller_withFrameLayout);
       control_container.addView(playlist_controller_withFrameLayout,params);
       getFragmentManager().beginTransaction().add(playlist_controller_withFrameLayout.getId(), playlistControllerFragment).commit();
        //  Toast.makeText(this,"Complete!",Toast.LENGTH_SHORT).show();
    }
      boolean Scroll2Top()
    {
     //  return mainScreenFragment.Scroll2Top();
        return false;
    }
    @Override
    public void onBackPressed()
    {
        if(pc_pl>0.5f)
        {
            UpDown_PlaylistController_Transform(1,0);
        }
        else if(pc_ms>0.5f) {
            UpDown_musicController_Transform(1, 0);
        }
           else super.onBackPressed();
    }

    public void SlideDownNShowWallBack() {
      callInBackProgress();
    //    Tool.showToast(this,"End Of Fragment",500);
    }
    private boolean inBackProgress = false;
    private boolean finishIt = false;
    private ValueAnimator va;
    private void callInBackProgress()
    {
        if(inBackProgress) {
            finishIt = true;
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
                  finishIt = false;
            }
        });
        va.start();
    }
    //
    //   don't edit anything in here, because this variable relate to  some method used for Tranform Controllers
    //   from there to there, all variables is used for determine the gesture user when swipe Music Controller
    //
    private boolean onDown = true;
    private boolean block_UpDown = false;
    private boolean over_Done = false;
    private  boolean down = true;
    private boolean Init_Music_Controller = false;
    //
    //  below is Playlist Controller
    //
    private boolean onDown_pl = true;
    private boolean block_UpDown_pl = false;
    private boolean over_Done_pl = false;
    private  boolean down_pl = true;
    private boolean Init_Playlist_Controller = false;
    private float pc_pl = 0f ; // pc_pl is not 0f when init, its values  is smaller than 0, it means that Playlist Controller is completely hide.
    private float pc_ms =0f; // pc is not 0f when init, its value is smaller than 0.
    //
    //  the end
    //
   //static int count = 0;
    public void UpDown_musicController_Transform(float from,final float to)
    {
        if(block_UpDown) return;
        block_UpDown = true;
        ValueAnimator va = ValueAnimator.ofFloat( from,to);
        int mDuration; //in millis
        float pec= Math.abs(from-to);
        if(from>to) {
            mDuration =200+(int)(500.0f*pec);
            va.setInterpolator(Animation.getInterpolator(4));
        //    count = (count==28)? 0 : count+1; va.setInterpolator(Animation.getEasingInterpolator(15)); Log.d(TAG,"count = "+count);
            down = true;
        }
        else {
            down = false;
            mDuration = 200+(int)(400*pec);
            //  va.setInterpolator(new Choose_Playlist.MyBounceInterpolator(0.1, 15));
            va.setInterpolator(Animation.getInterpolator(4));
        }
        if(from<0&&to==0)
        {
            if(!Init_Music_Controller)
            {
                mDuration = 1000;
                Init_Music_Controller = true;
            }
            else
                //    Toast.makeText(this,"Init Music Controller",Toast.LENGTH_SHORT).show();
                mDuration =600;
            va.setInterpolator(Animation.getInterpolator(4));
        }
        va.setDuration(mDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float  number= ((float)(animation.getAnimatedValue()));
              onUpdateLayer(Layer.MusicController,number);
            }
        });
        va.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                // done
                onEndLayer(Layer.MusicController,to);
            }
        });
        va.start();
    }
    public void onEndLayer(Layer layer, float to)
    {
        switch (layer)
        {
            case Container: break;
            case MusicController: onEndMusicer(to);break;
            case PlaylistController : onEndPlaylist();
        }
    }
    public void onEndMusicer(float to)
    {
        block_UpDown = false;
        over_Done =false;
        pc_ms = to;
        if(pc_ms>=0.5f) musicControllerFragment.FocusIconAvatarToCenter(true);
        else  musicControllerFragment.FocusIconAvatarToCenter(false);

    }
    public void onUpdateMusicController(float number)
    {
        UpDown_musicController_UpDate(number);
        setAlpha4PlayBar(number);
        setMarginForUILayer_ByPercent(number, pc_pl);
    }
    public void control_music_controller_up()
    {
        UpDown_musicController_Transform(pc_ms, 1); // đưa nó lên
    }
    public void control_playlist_controller_up()
    {
        UpDown_PlaylistController_Transform(pc_pl, 1); // đưa nó lên
    }
    public void Init_UpDown_musicController()
    {
        // Hàm này đưa musicController dưới cùng đáy đến điểm ghim.
        if(!Init_Music_Controller) {
            Log.d("SFA","ChangeMarginBottom");
            UpDown_musicController_Transform(pc_ms, 0); // đưa nó lên
            ChangeMarginBottom(pixel_unit);
        }
    }

    public void UpDown_musicController_UpDate(float pc)
    {
        musicControllerFragment.setNumber(pc);
        switch_page_container.setNumber(pc);
        float paddingTop = StatusHeight+pixel_unit/5;
       FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) music_controller_withFrameLayout.getLayoutParams();
        float size = ScreenSize[1] - paddingTop; // kích thước nguyên bản
        float maxMove = size - pixel_unit;
        float Move = maxMove*pc; // Move từng này
        params.topMargin = ScreenSize[1] - pixel_unit - (int)Move;
        if(params.topMargin<pixel_unit||over_Done) {
            params.height = ScreenSize[1]- params.topMargin +NavigationHeight;
            params.bottomMargin = -NavigationHeight;
            if(!down) over_Done =true;
        }
        else {
            params.height = (int)size +NavigationHeight;
            params.bottomMargin = params.topMargin + (int) size -NavigationHeight;
            //  params.bottomMargin = 0;
        }
    //    Log.d("Param UpDown : " + params.topMargin+"|"+params.bottomMargin+"|"+params.width+"|"+params.height,"Params");
        music_controller_withFrameLayout.setLayoutParams(params);
    }
    public void UpDown_PlaylistController_Transform(float from,final float to) // this method will control the Playlist Controller
    {

        if(block_UpDown_pl) return;
        block_UpDown_pl = true;

        ValueAnimator animator_Playlist = ValueAnimator.ofFloat( from,to);
        int mDuration; //in millis
        float pec= Math.abs(from-to);
        if(from>to) {
            mDuration =200+(int)(200.0f*pec);
            animator_Playlist.setInterpolator(Animation.getInterpolator(4));
            down_pl = true;
        }
        else {
            down_pl = false;
            mDuration = 400+(int)(200.0f*pec);
            // va.setInterpolator(new Choose_Playlist.MyBounceInterpolator(0.06, 20));
            animator_Playlist.setInterpolator(Animation.getInterpolator(4));
        }
        if(from<0&&to==0)
        { if(!Init_Playlist_Controller)
        {
            mDuration = 1000;
            Init_Playlist_Controller = true;
        }
        else mDuration =600;
            animator_Playlist.setInterpolator(Animation.getInterpolator(4));
        }
        animator_Playlist.setDuration(mDuration);
        pc_pl= to;

        animator_Playlist.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float  number= ((float)(animation.getAnimatedValue()));
               onUpdateLayer(Layer.PlaylistController,number);

            }
        });
        animator_Playlist.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                onEndLayer(Layer.PlaylistController,to);

            }
        });
        animator_Playlist.start();
    }
    private void onEndPlaylist()
    {
        block_UpDown_pl = false;
        over_Done_pl =false;
    }
    private void onUpdatePlaylist(float number)
    {
        UpDown_PlaylistController_Update(number);
        setMarginForMusicLayer_ByPercent(number);
        setMarginForUILayer_ByPercent(pc_ms,number);
    }
    public void UpDown_PlaylistController_Update(float pc_x)
    {
        float paddingTop = StatusHeight+2*pixel_unit/5;
       FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playlist_controller_withFrameLayout.getLayoutParams();
        float size = ScreenSize[1] - paddingTop; // kích thước nguyên bản
        float maxMove = size ;
        float Move = maxMove*pc_x; // Move từng này
        params.topMargin = ScreenSize[1]- (int)Move+NavigationHeight;
        if(params.topMargin<paddingTop||over_Done) {
            params.height = ScreenSize[1]- params.topMargin+NavigationHeight;
            params.bottomMargin = -NavigationHeight;
            if(!down_pl)
                over_Done_pl =true;
        }
        else {
            params.height = (int)size+NavigationHeight;
            params.bottomMargin = params.topMargin + (int) size-NavigationHeight;

        }
        playlist_controller_withFrameLayout.setLayoutParams(params);
    }
    public View.OnTouchListener onTouchListOfPlayBar = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return onTouchToggle_Playlist.onTouch(v,event);
        }
    };
    public View.OnTouchListener onTouchToggle = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) music_controller_withFrameLayout.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;

                    break;
                case MotionEvent.ACTION_UP:

                    if(pc_ms ==1&&(view.getId()==R.id.music_controller_toggle||view.getId()==R.id.music_controller_play_bar_frm))
                        UpDown_musicController_Transform(1,0);
                    else if(pc_ms ==0)
                        UpDown_musicController_Transform(0,1);
                    else if(onDown) // nếu kéo tay xuống
                    {
                        if(pc_ms <=0.8f) UpDown_musicController_Transform(pc_ms,0);
                        else     UpDown_musicController_Transform(pc_ms,1);
                    }
                    else // nếu kéo tay lên
                    {
                        if(pc_ms >=0.2f) UpDown_musicController_Transform(pc_ms,1);
                        else UpDown_musicController_Transform(pc_ms,0);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:

                    break;
                case MotionEvent.ACTION_POINTER_UP:

                    break;
                case MotionEvent.ACTION_MOVE:

                    float paddingTop = StatusHeight + pixel_unit/5;
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) music_controller_withFrameLayout.getLayoutParams();
                    //     layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    if(layoutParams.topMargin<paddingTop) {
                       float move = paddingTop - (Y - _yDelta);
                         float HeSo = move/(move+100);
                        float maxMove = paddingTop;
                        float realMove = maxMove*HeSo;
                        layoutParams.topMargin = (int)(paddingTop- realMove);
                    }

                    float size = ScreenSize[1] - paddingTop; // kích thước nguyên bản
                    //    layoutParams.rightMargin = 0xffffff06;
                    float maxMove = size - pixel_unit;
                    float Move = size - layoutParams.topMargin;
                    float new_pc =Move/maxMove;
                    if(new_pc< pc_ms) // thấp hơn, chứng tỏ đang đi xuống.
                        onDown = true;
                    else onDown = false;
                    if(pc_ms !=new_pc) {
                        setAlpha4PlayBar(new_pc);
                        setMarginForUILayer_ByPercent(new_pc, pc_pl);
                        if (pc_ms >= 1f) musicControllerFragment.FocusIconAvatarToCenter(true);
                        else if (pc_ms <= 0) musicControllerFragment.FocusIconAvatarToCenter(false);
                        pc_ms = new_pc;
                        musicControllerFragment.setNumber(new_pc);
                        switch_page_container.setNumber(new_pc);
                        //     layoutParams.bottomMargin = layoutParams.topMargin +ScreenHeight - pixel_unit;
                        if (layoutParams.topMargin < paddingTop) {

                            layoutParams.height = ScreenSize[1] - layoutParams.topMargin +NavigationHeight ;
                            layoutParams.bottomMargin = -NavigationHeight;
                        } else  {
                            layoutParams.height = (int) size +NavigationHeight;
                            layoutParams.bottomMargin = layoutParams.topMargin + (int) size-NavigationHeight;
                        }
                        music_controller_withFrameLayout.setLayoutParams(layoutParams);
                 //       Log.d("Param Move : " + layoutParams.topMargin+"|"+layoutParams.bottomMargin+"|"+layoutParams.width+"|"+layoutParams.height,"Params");

                    }
                    break;
            }
            //   relat(R.id.root).invalidate();

            return true;
        }
    };
    public View.OnTouchListener onTouchToggle_Playlist = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) playlist_controller_withFrameLayout.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;

                    break;
                case MotionEvent.ACTION_UP:

                    if(pc_pl==1)
                        UpDown_PlaylistController_Transform(1,0);
                    else if(pc_pl==0)
                        UpDown_PlaylistController_Transform(0,1);
                    else if(onDown_pl) // nếu kéo tay xuống
                    {
                        if(pc_pl<=0.75f) UpDown_PlaylistController_Transform(pc_pl,0);
                        else     UpDown_PlaylistController_Transform(pc_pl,1);
                    }
                    else // nếu kéo tay lên
                    {
                        if(pc_pl>=0.25f) UpDown_PlaylistController_Transform(pc_pl,1);
                        else UpDown_PlaylistController_Transform(pc_pl,0);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:

                    break;
                case MotionEvent.ACTION_POINTER_UP:

                    break;
                case MotionEvent.ACTION_MOVE:

                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) playlist_controller_withFrameLayout.getLayoutParams();
                    //     layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    float size =ScreenSize[1] - pixel_unit; // kích thước nguyên bản
                    //    layoutParams.rightMargin = 0xffffff06;
                    float maxMove = size - pixel_unit;
                    float Move = size - layoutParams.topMargin;
                    float new_pc =Move/maxMove;
                    if(new_pc<pc_pl) // thấp hơn, chứng tỏ đang đi xuống.
                        onDown_pl = true;
                    else onDown_pl = false;
                    if(pc_pl!=new_pc) {
                        setMarginForMusicLayer_ByPercent(new_pc);
                        setMarginForUILayer_ByPercent(pc_ms,new_pc);
                    }
                    pc_pl = new_pc;
                    layoutParams.bottomMargin =layoutParams.topMargin +ScreenSize[1] - pixel_unit-NavigationHeight;
                    if(layoutParams.topMargin<pixel_unit) {
                        layoutParams.height = ScreenSize[1]- layoutParams.topMargin+NavigationHeight;
                        layoutParams.bottomMargin = -NavigationHeight;
                    }
                    else {
                        layoutParams.height = (int)size+NavigationHeight;
                        layoutParams.bottomMargin = layoutParams.topMargin + (int) size-NavigationHeight;
                    }
                    playlist_controller_withFrameLayout.setLayoutParams(layoutParams);
                    break;
            }
            //   relat(R.id.root).invalidate();
            return true;
        }
    };


    public PlayControllerFragment musicControllerFragment;
    public PlaybackFragment playlistControllerFragment;

}
