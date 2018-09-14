package com.ldt.musicr.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldt.musicr.InternalTools.Animation;
import com.ldt.musicr.InternalTools.BitmapEditor;
import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.MediaData.Song_OnLoad;
import com.ldt.musicr.activities.SupportFragmentPlusActivity;
import com.ldt.musicr.helpers.MusicPlaybackTrack;
import com.ldt.musicr.services.MusicPlayer;
import com.ldt.musicr.utils.TimberUtils;
import com.ldt.musicr.views.AudioVisualSeekBar;
import com.ldt.musicr.views.BubbleMenu.BubbleMenuCenter;
import com.ldt.musicr.views.DarkenRoundedBackgroundFrameLayout;
import com.ldt.musicr.views.PlayPauseButton;
import com.ldt.musicr.activities.MainActivity;
import com.ldt.musicr.R;
import com.ldt.musicr.views.RoundSeeThroughTextView;
import com.ldt.musicr.views.animated_icon.ToggleButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class MusicControllerFragment extends BaseTabLayerFragment implements BubbleMenuCenter.BubbleMenuViewListener,AudioVisualSeekBar.SeekBarListener, MinimizePlaySwitcher.PlayControlListener, Palette.PaletteAsyncListener, View.OnTouchListener, View.OnClickListener{
    public final static String TAG = "MusicControllerFragment";
    public FragmentPlus.StatusTheme statusTheme = FragmentPlus.StatusTheme.WhiteIcon;
    public int color_in7basic = 0;
    public int colorMostCommon = 0;
    private View navigation ;
    protected DarkenRoundedBackgroundFrameLayout rootView;

    public int[] palette = new int[]{0,0,0,0,0,0};
    /**
     *
     * Ngay trích xuất màu palette xong
     * Ta tiến hành thay đổi màu cho
     * các textView và button
     * @param p Palette
     */
    @Override
    public void onGenerated(@NonNull Palette p) {
        // access palette colors here
        Palette.Swatch psVibrant = p.getVibrantSwatch();
        Palette.Swatch psVibrantLight = p.getLightVibrantSwatch();
        Palette.Swatch psVibrantDark = p.getDarkVibrantSwatch();
        Palette.Swatch psMuted = p.getMutedSwatch();
        Palette.Swatch psMutedLight = p.getLightMutedSwatch();
        Palette.Swatch psMutedDark = p.getDarkMutedSwatch();

        for (int i = 0; i < 6; i++)
            palette[i] = 0;
        if (psVibrant != null) {
            palette[0] = psVibrant.getRgb();
        }
        if (psVibrantLight != null) {
            palette[1] = psVibrantLight.getRgb();
        }
        if (psVibrantDark != null) {
            palette[2] = psVibrantDark.getRgb();
        }
        if (psMuted != null) {
            palette[3] = psMuted.getRgb();
        }
        if (psMutedLight != null) {
            palette[4] = psMutedLight.getRgb();
        }
        if (psMutedDark != null) {
            palette[5] = psMutedDark.getRgb();
        }
        updatePaletteColorView();
        generateColorThemeFromArtBitmap(p);
    }


    private void generateColorThemeFromArtBitmap(@NonNull Palette p) {
          if(alpha_7basic>=0.5f) //  Đủ đậm thì màu mostCommon sẽ là màu song name, màu basic là màu artist
        {
            color1= colorMostCommon;
            alpha1 =1;
            color2 = color_in7basic;
            alpha2 =alpha_7basic;
        }
        else // ngược lại thì màu basic sẽ là màu song name
        {
            color1 = getBestColorFromPalette();
            if(color1==0) color1 = color_in7basic;
            alpha1 = 1;
            color2 =Color.BLACK;
            alpha2 = 0.7f;
        }

        play_pause_custom.setColor(color_in7basic);
        toggle.setColor(color_in7basic);

        time_textView.setBackgroundColor(color_in7basic);
        BitmapEditor.applyNewColor4Bitmap(getActivity(), R.drawable.listblack, playBar_list, color_in7basic, 1);

        nameOfSong.setTextColor(color1);
        BitmapEditor.applyNewColor4Bitmap(getActivity(), R.drawable.listblack, inList, color1, alpha1);
        nameOfArtist.setAlpha(alpha2);
        nameOfArtist.setTextColor(color2);
        BitmapEditor.applyNewColor4Bitmap(getActivity(), R.drawable.pref, inPrev, color2, alpha2);
      //  BitmapEditor.applyNewColor4Bitmap(getActivity(), R.drawable.pauseblack, inPause, color2, alpha2);
        inPause.setColor(color2|((int)alpha2*255)<<24);
        BitmapEditor.applyNewColor4Bitmap(getActivity(), R.drawable.next, inNext, color2, alpha2);
        playBar_TrackBar.setBackgroundColor(color1);
        ParentOfRoot.setBackColor1(Color.argb(10,Color.red(color1),Color.green(color1), Color.blue(color1)));
        visualSeekBar.updateProperties();
    }
    boolean HadSetPalette = false;
    View[] pl = new View[6];

    private void updatePaletteColorView() {
        if (!HadSetPalette) {
            HadSetPalette = true;
            pl[0] = rootView.findViewById(R.id.palette1);
            pl[1] = rootView.findViewById(R.id.palette2);
            pl[2] = rootView.findViewById(R.id.palette3);
            pl[3] = rootView.findViewById(R.id.palette4);
            pl[4] = rootView.findViewById(R.id.palette5);
            pl[5] = rootView.findViewById(R.id.palette6);
            for (int i = 0; i < 6; i++)
                pl[i].setOnClickListener(paletteOnClick);
        }
        for (int i = 0; i < 6; i++) {
            LogColor(palette[i]);

            if (palette[i] != -1)
                    pl[i].setBackgroundColor(palette[i]);
            else pl[i].setBackgroundColor(Color.TRANSPARENT);
        }
    }

    boolean checkOut = true;
    private View.OnClickListener paletteOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int color = Color.TRANSPARENT;
            Drawable background = v.getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();
            if (checkOut) color2 = color;
            else color1 = color;
            checkOut = !checkOut;
            paletteOnClickMainMethod();
        }
    };

    private void paletteOnClickMainMethod() {
        alpha1 = alpha2 = 1;
        nameOfSong.setTextColor(color1);
        BitmapEditor.applyNewColor4Bitmap(getActivity(), R.drawable.listblack, inList, color1, alpha1);
        nameOfArtist.setAlpha(alpha2);
        nameOfArtist.setTextColor(color2);
        BitmapEditor.applyNewColor4Bitmap(getActivity(),
                new int[]{R.drawable.pref, R.drawable.pauseblack, R.drawable.next},
                new ImageView[]{inPrev,inNext},
                color2,
                alpha2);
        playBar_TrackBar.setBackgroundColor(color1);
    }

    int color1 = 0, color2 = 0;
    float alpha1 = 1, alpha2 = 1;

    private void generateColorThemeFromArtBitmap() {
        Palette.from(originalBitmapSong).generate(this);
    }

    float alpha_7basic = 0;
    private int getBestColorFromPalette() {
        int c = 0;
        float[] hsv = new float[3];
        int[] list = new int[]{palette[2], palette[0], palette[5], palette[1], palette[3], palette[4]};
        // theo thứ tự : 2 - 0 - 5 -1 - 3 - 4
        for (int i = 0; i < 6; i++) {
            Color.colorToHSV(list[i], hsv);
            if(hsv[1]>=0.5f) {
                c = list[i];
                return c;
            }
        }
        return 0;
    }
    private int ColorReferTo(int cmc) {
        float[] hsv = new float[3];
        Color.colorToHSV(cmc, hsv);
   //     Log.d(hsv[0] + "|" + hsv[1] + "|" + hsv[2], "ColorMe");
        alpha_7basic = hsv[1];
        float toEight = hsv[0] / 45 + 0.5f;
        if (toEight > 8 | toEight <= 1) return 0xffFF3B30;
        if (toEight <= 2) return 0xffFF9501;
        if (toEight <= 3) return 0xffFFCC00;
        if (toEight <= 4) return 0xff4AD968;
        if (toEight <= 5) return 0xff5AC8FA;
        if (toEight <= 6) return 0xff007AFF;
        if (toEight <= 7) return 0xff5855D6;
        return 0xffFB2C57;
    }

    private void LogColor(int color) {
        float[] colorHSV = new float[3];
        Color.colorToHSV(color, colorHSV);
        int[] colorRGB = new int[3];
        colorRGB[0] = Color.red(color);
        colorRGB[1] = Color.green(color);
        colorRGB[2] = Color.blue(color);
        Log.d(TAG,"hsv = ("+colorHSV[0]+", "+colorHSV[1]+", "+colorHSV[2]+')');
        Log.d(TAG,"rgb = ("+colorRGB[0]+", "+colorRGB[1]+", "+colorRGB[2]+')');
    }

    private int widthFrom = 0;
    private int widthTo = 0;
    View shape_back;
    public void FocusIconAvatarToCenter(boolean zoomIn) {
        int[] location = new int[2];
        shape_back.getLocationOnScreen(location);
        int[] location1 = new int[2];
        iconAvatar.getLocationOnScreen(location1);
        int[] location2 = new int[2];
        rootView.getLocationOnScreen(location2);
         widthTo = shape_back.getHeight();
        int LeftTo = location[0] - location2[0] - widthTo / 2 - 50;
        int TopTo = location[1] - location2[1] - 50 + 6;
        widthTo += 100;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iconAvatar.getLayoutParams();
        //  Log.d("leftMargin = "+params.leftMargin+", leftMargin = "+params.topMargin+", rightMargin = "+params.rightMargin+", bottomMargin = "+params.bottomMargin,"LocateView");
        // params.leftMargin  =LeftTo;
        // params.topMargin = TopTo;
        if (widthFrom == 0)
            widthFrom = params.width;
        Move2CenterIconAvatar_Transform(params, widthFrom, LeftTo, TopTo, widthTo, zoomIn);
    }

    private boolean overDoneZoomInAvatar = true;
    private boolean lastestZoomIn = true;

    private void Move2CenterIconAvatar_Transform(final FrameLayout.LayoutParams params, final float widthFrom, final float LeftTo, final float TopTo, final float WidthTo, final boolean zoomIn) {
        if (zoomIn && params.leftMargin != 0) return;
        if (!zoomIn && params.leftMargin == 0) return;
        lastestZoomIn = zoomIn;
        if (!overDoneZoomInAvatar) return;
        overDoneZoomInAvatar = false;
        ValueAnimator va;
        if (!zoomIn) {
            va = ValueAnimator.ofFloat(1f, 0);
            va.setDuration(400);
        } else {

            va = ValueAnimator.ofFloat(0, 1f);
            va.setDuration(700);
        }

        va.setStartDelay(200);
        if (zoomIn)
            va.setInterpolator(Animation.getInterpolator(4));
        else
            va.setInterpolator(Animation.getInterpolator(3));
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float number = (float) animation.getAnimatedValue();
                 /*
                 oldBitmap = ((BitmapDrawable)iconAvatar.getDrawable()).getBitmap();
                 Bitmap new1 = getBitmapResourceOrPathFromSong(NowPlaying);
                 float circle = 1-number;
                 Bitmap new2= BitmapEditor.getRoundedCornerBitmap(new1, (int)((1+ circle*29)*new1.getWidth()/30));
                 Bitmap new3 = BitmapEditor.GetRoundedBitmapWithBlurShadow(getActivity(),new2,30,30,30,30,-6,180,12,2);
                 iconAvatar.setImageBitmap(new3);
                 oldBitmap.recycle();
                 new1.recycle();
                 new2.recycle();
                 newBitmap = new3;
                */
                float thisLeft = number * LeftTo;
                float thisTop = number * TopTo;
                float thisWidth = number * (WidthTo - widthFrom) + widthFrom;
                params.leftMargin = (int) thisLeft;
                params.topMargin = (int) thisTop;
                params.width = params.height = (int) thisWidth;
                iconAvatar.setLayoutParams(params);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // done
                overDoneZoomInAvatar = true;
                // if status change, run method again.
                if (lastestZoomIn != zoomIn)
                    Move2CenterIconAvatar_Transform(params, widthFrom, LeftTo, TopTo, WidthTo, lastestZoomIn);
            }
        });
        va.start();
    }
    public MusicControllerFragment() {
        // Required empty public constructor
    }

    public ImageView iconAvatar;
    public TextView nameOfSong, nameOfArtist;
    public Song_OnLoad NowPlaying;
    public FrameLayout playBar_overView;
    public ImageView  playBar_list;
    public PlayPauseButton play_pause_custom;
    public TextView playBar_nameOfSong, playBar_artistOfSong;
    public ImageView inList;
    public ToggleButton toggle;
    public ImageView inPrev, inNext;
   public PlayPauseButton inPause;
   public View playPauseButtonWrapper, inPauseWrapper;
    public View playBar_TrackBar;
   public FrameLayout example_frame_layout;
   protected AudioVisualSeekBar visualSeekBar;
   private RoundSeeThroughTextView time_textView;
   private View buttonBar;
    private void MergerUI(View v) {
        // cây thước ẩn xác định kích thước avatar
        shape_back = v.findViewById(R.id.rulerAlign);
        // nút play- pause nằm trên play bar
        play_pause_custom = v.findViewById(R.id.play_pause_custom);

        playBar_TrackBar = v.findViewById(R.id.music_controller_PlayBar_TrackBar);
        // nút Prev nằm bên trong bảng
        inPrev = v.findViewById(R.id.music_controller_InPrev);
        // nút play - pause nằm bên trong bảng
        inPause = v.findViewById(R.id.music_controller_InPause);
        inPauseWrapper = v.findViewById(R.id.in_pause_wrapper);
        playPauseButtonWrapper = v.findViewById(R.id.play_pause_wrapper);
        // nút Next nằm bên trong bảng
        inNext = v.findViewById(R.id.music_controller_InNext);
        // nút playlist nằm bên trong bảng
        inList = v.findViewById(R.id.InMusicController_list);

        toggle = rootView.findViewById(R.id.music_controller_toggle);
        iconAvatar = v.findViewById(R.id.music_controller_iconAvatar);
        nameOfSong = v.findViewById(R.id.music_controller_nameOfSong);
        nameOfArtist = v.findViewById(R.id.music_controller_nameOfArtist);
        playBar_overView = v.findViewById(R.id.music_controller_play_bar_frm);
        playBar_list = v.findViewById(R.id.music_controller_playBar_list);
        playBar_nameOfSong = v.findViewById(R.id.music_controller_playBar_nameOfSong);
        playBar_artistOfSong = v.findViewById(R.id.music_controller_playBar_nameOfArtist);
        navigation = v.findViewById(R.id.navigation_color_control_music_controller);
        example_frame_layout = v.findViewById(R.id.example_frame_layout2);
        time_textView = v.findViewById(R.id.time_textView);
        visualSeekBar = v.findViewById(R.id.visual_seek_bar);
        visualSeekBar.setSeekBarListener(this);
        ParentOfRoot = (DarkenRoundedBackgroundFrameLayout) v;
        buttonBar = v.findViewById(R.id.buttonBar);

        addToBeRipple(R.drawable.ripple_oval,inPrev,inNext,playPauseButtonWrapper,inPauseWrapper,inList,playBar_list);
    }


    /**
     * this is handler y of playbar, it means that if user allTouchEvent the play bar, in any posTop, will call this before all.
     *
     */
   public static void logOnTouchEvent(String mark,MotionEvent event) {
        String log;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN: log="ACTION_DOWN";break;
            case MotionEvent.ACTION_UP: log="ACTION_UP";break;
            case MotionEvent.ACTION_MOVE: log="ACTION_MOVE";break;
            case MotionEvent.ACTION_OUTSIDE: log="ACTION_OUTSIDE";break;
            case MotionEvent.ACTION_POINTER_DOWN: log="ACTION_POINTER_DOWN";break;
            case MotionEvent.ACTION_SCROLL: log="ACTION_SCROLL";break;
            case MotionEvent.ACTION_BUTTON_RELEASE: log="ACTION_BUTTON_RELEASE";break;
            case MotionEvent.ACTION_BUTTON_PRESS: log="ACTION_BUTTON_PRESS";break;
            case MotionEvent.ACTION_CANCEL: log="ACTION_CANCEL";break;
            default: log= "OTHER";
        }
        Log.d(mark,log);

    }
   final String[] menu =  new String[]{"More", "Slide Up", "Close"};
    final int[] image_menu = new int[] {R.drawable.more_black,R.drawable.repeat,R.drawable.exit_controller};

    private BubbleMenuCenter bubbleMenuCenter;
    public void setInform4PlayBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition( rootView.findViewById(R.id.music_controller_parent2OfNameNArtist));
        }
        playBar_nameOfSong.setText(mTitle);
        playBar_artistOfSong.setText(mArtist);
    }

    public boolean IsInitAvatarIcon = true;
    public boolean inAnimator_alpha_pb = false;
  private boolean checkAgain = false;
  private float pc_check = 0;
    private void alpha_playBar_toggle_transition(boolean playBar_toOn,float pc) {
        if(inAnimator_alpha_pb) {checkAgain = true;pc_check = pc;return;}

        ValueAnimator va;
        if(playBar_toOn) va = ValueAnimator.ofFloat(0,1);
        else va = ValueAnimator.ofFloat(1,0);
        va.setDuration(400);
        va.setStartDelay(200);
        va.setInterpolator(Animation.getInterpolator(6));
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float d = (float) animation.getAnimatedValue();
                if (d > 1) d = 1;
                else if (d < 0) d = 0;
                playBar_overView.setAlpha(d);
                if(d==0) playBar_overView.setTranslationY(0);
                else
                playBar_overView.setTranslationY(15*(d-1));
             //   Log.d(TAG,"play bar alpha = " + d);
                toggle.setAlpha(1 - d);
                if(d==1)
                    toggle.setTranslationY(0);
                else
                toggle.setTranslationY(15*d);
            }
        });
        va.start();
    }
    float savedPC = 0;
    public void doTransform(float pc) {

        //  lúc nó mở lên hoàn toàn
        // thì zoom in avatar
        if(savedPC<1f&&pc>=1f) {
            FocusIconAvatarToCenter(true);
        }
        // lúc nó đóng hoàn toàn
        // thì  zoom out avatar
        else if(savedPC>0&&pc<=0) {
            FocusIconAvatarToCenter(false);
        }

        if(savedPC<pc) {
            // this means the controller is on going up to one
        if(savedPC<0.5f&&pc>=0.5f) {
            getMainActivity().blockToWhiteTheme();
            alpha_playBar_toggle_transition(false, pc);
        }
        } else {
            // otherwise the controller is on going down to zero
          if(savedPC>0.5f && pc <=0.5f) {
              getMainActivity().unblockWhiteTheme();
              alpha_playBar_toggle_transition(true,pc);
          }
        }
        savedPC = pc;
    }

    public Bitmap originalBitmapSong = null;
    @Override
    public Bitmap getBitmapRounded(float rounded, Bitmap original) {
        Bitmap new2 = BitmapEditor.getRoundedCornerBitmap(originalBitmapSong, (int)rounded);

         Bitmap ret =  BitmapEditor.GetRoundedBitmapWithBlurShadow(getActivity(), new2, 30, 30, 30, 30, -6, 180, 12, 2);
         new2.recycle();
         return ret;
    }


    Bitmap newBitmap = null;

    public Bitmap getBitmapResourceOrPathFromSong(Song_OnLoad song) {
        if (NowPlaying.AlbumArt_path == "" || NowPlaying.AlbumArt_path == null) {
            Bitmap original = BitmapFactory.decodeResource(getResources(), NowPlaying.Id_Image);
            if (original.getWidth() <= 310) return original;
            Bitmap resize = BitmapEditor.getResizedBitmap(original, 300, 300);
            original.recycle();
            return resize;
        }   // imageView.setImageResource(R.drawable.walle);
        return BitmapFactory.decodeFile(NowPlaying.AlbumArt_path);
    }

    private void makeIconAvatarRotateOrStop() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 150);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(Animation.getInterpolator(4));
        rotateAnimation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {

            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                iconAvatar.setImageBitmap(newBitmap);
                RotateAnimation rotateAnimation1 = new RotateAnimation(270, 360);
                rotateAnimation1.setDuration(1000);
                rotateAnimation1.setInterpolator(Animation.getInterpolator(4));
                iconAvatar.startAnimation(rotateAnimation1);
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

            }
        });
        iconAvatar.startAnimation(rotateAnimation);
    }

    private boolean over5 = false;

    private void setIconAvatarTransform() {
        over5 = false;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setInterpolator(Animation.getInterpolator(4));
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float number = (float) animation.getAnimatedValue();
                setStatusIconAvatar(number, number);
                if (number >= 0.5 && !over5) {
                    iconAvatar.setImageBitmap(newBitmap);
                    over5 = true;
                }
            }
        });
        valueAnimator.start();
    }

    private void setStatusIconAvatar(float rotate, float scale) {

        //   iconAvatar.setRotationY(iconAvatar.getHeight()/2);
        iconAvatar.setRotation(rotate * 360);

        //   iconAvatar.setScaleX(scale);
        //    iconAvatar.setScaleY(scale);
    }
    private ArrayList<Song_OnLoad> list;
    private int position = -1;
    private void log(float i) {
        Log.d(TAG, "-> i = " + i);
    }

    private void setNavigationHeight()
  {
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) navigation.getLayoutParams();
      params.height=((SupportFragmentPlusActivity)getActivity()).NavigationHeight;
      navigation.setLayoutParams(params);
  }
  @Override
  public int getNavigationHeight() {
        return ((SupportFragmentPlusActivity)getActivity()).NavigationHeight;
  }
  @Override
  public int getBarHeight() {
        return playBar_overView.getHeight();
  }
  @Override
  public int getBarWidth() {
        return navigation.getHeight();
    }
    private MainActivity activity;
    public MainActivity getMainActivity(){
        if(activity==null) activity = (MainActivity)getActivity();
        return activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (DarkenRoundedBackgroundFrameLayout) inflater.inflate(R.layout.music_controller, container, false);
        //  ImageView exit_view = (ImageView) fragment.findViewById(R.id.exit_playlist_controller);
        //   exit_view.setOnClickListener(exit_click);

        MergerUI(rootView);
        makeBackground();
        setOnClickOrOnTouch4AllButton();
        setNavigationHeight();
        //   makeIconAvatarRotateOrStop();
        getMainActivity().setMusicStateListenerListener(this);
        new loadPlaying().execute();
        return rootView;
    }
    private DarkenRoundedBackgroundFrameLayout ParentOfRoot;
    private void makeBackground()
    {
       // if(ParentOfRoot==null) Tool.showToast(getActivity(),"Parent null",5000);
      ParentOfRoot.setBackColor2(0xccffffff);
   //   ParentOfRoot.setBackgroundColor(Color.BLUE);
    }

    private void setAudioWaveFragmentView(String file)
    {
        visualSeekBar.Visualize(file);
    }

    public void setRoundNumber(float number) {
       ParentOfRoot.setRoundNumber(number,true);
    }


    @Override
    public ImageView getImageView(String command) {
        return null;
    }

    @Override
    public String[] getStringCommand(String command) {
        return menu;
    }

    @Override
    public int[] getImageResources(String command) {
        return image_menu;
    }

    @Override
    public void onReturnResult(String command, int result) {
        switch (result) {
            case 1 :Tool.showToast(getActivity(),menu[0],1000);break;
            case  2:Tool.showToast(getActivity(),menu[1],1000);break;
            case 3 :Tool.showToast(getActivity(),menu[2],1000);break;
            default : Tool.showToast(getActivity(),"Cancel",1000);break;
        }
    }

    @Override
    public boolean onSeekBarSeekTo(float posTime) {
        return false;
    }

    @Override
    public void onSeekBarTouchDown() {

    }

    @Override
    public void onSeekBarTouchUp() {

    }



    @Override
    public float getCurrentRadius() {
        return originalBitmapSong.getWidth()/16 ;
    }

    @Override
    public int getOutlineColor() {
        return color_in7basic;
    }

    @Override
    public int getBackColor() {
        return ParentOfRoot.getBackgroundColor();
    }

    @Override
    public Bitmap getOriginalBitmap() {
        return originalBitmapSong;
    }

    @Override
    public ImageView getCurrentView() {
        return iconAvatar;
    }

    @Override
    public View getRootLayout() {
        return ((MainActivity)getActivity()).container;
    }

    @Override
    public void beginSwitchMinimize() {
        ((MainActivity)getActivity()).musicControllerFragment.rootView.setVisibility(View.GONE);
    }

    @Override
    public void finishSwitchNormal() {
        ((MainActivity)getActivity()).musicControllerFragment.rootView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onUpdateLayer(TabLayerController.Attr attr, float pcOnTopLayer, int active_i) {
        if(active_i==0) {
            rootView.setRoundNumber(attr.Pc,true);
            doTransform(attr.Pc);
        }
        else if(active_i>=1) {
            rootView.setDarken(pcOnTopLayer*0.3f,false);
            rootView.setRoundNumber(1,true);
        }
    }

    @Override
    public boolean onTouchParentView(boolean handled) {
        return false;
    }


    @Override
    public void onAddedToContainer(TabLayerController.Attr attr) {
          }

    @Override
    public MarginValue defaultBottomMargin() {
        return MarginValue.BELOW_NAVIGATION;
    }

    @Override
    public MarginValue maxPosition() {
        return MarginValue.STATUS_HEIGHT;
    }

    @Override
    public MarginValue minPosition() {
        return MarginValue.VALUE_50_DIP;
    }

    @Override
    public String tag() {
        return TAG;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClickOrOnTouch4AllButton() {
        // Thêm view vào đây để đăng ký sự kiện chạm
        // sau đó thêm 1 case xử lý vào hàm onTouch()
        View[] onTouch = new View[] {
                rootView,
                playBar_overView,
                inList
        };


        // Thêm view vào đây để đăng ký sự kiện click
        // sau đó thêm 1 case xử lý vào hàm onClick()
        View[] onClick = new View[] {
                playBar_overView,
                playPauseButtonWrapper,
                inPrev,
                inPauseWrapper,
                inNext,
                buttonBar
        };

        for (View anOnTouch : onTouch) anOnTouch.setOnTouchListener(this);
        for (View anOnClick : onClick) anOnClick.setOnClickListener(this);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch(v.getId()) {
            case R.id.music_controller_play_bar_frm:
                if (bubbleMenuCenter == null) bubbleMenuCenter = getMainActivity().bubbleMenuCenter;
                //  logOnTouchEvent("MSF",event);
                bubbleMenuCenter.detectLongPress(MusicControllerFragment.this, "",v,event);
               return bubbleMenuCenter.run(v, event) || tabLayerController.streamOnTouchEvent(TAG,v,event);

            case /* rootView */     R.id.music_controller_root_frm     : return rootView_OnTouch(v, event);
            case /* playBarList */  R.id.music_controller_playBar_list :
            case /* inList */            R.id.InMusicController_list             :return tabLayerController.streamOnTouchEvent(PlaylistControllerFragment.TAG,v,event);
            default                                                                                   : return tabLayerController.streamOnTouchEvent(TAG,v,event);
        }
    }

    private boolean rootView_OnTouch(View v, MotionEvent event) {
        v.performClick();
        if (bubbleMenuCenter == null) bubbleMenuCenter = getMainActivity().bubbleMenuCenter;
        //  logOnTouchEvent("MSF",event);
        bubbleMenuCenter.detectLongPress(MusicControllerFragment.this, "",v,event);
        return bubbleMenuCenter.run(v, event) || tabLayerController.streamOnTouchEvent(TAG,v,event);
    }

    public void ButtonControl(int position) {
      switch (position) {
          case -1 : MusicPlayer.previous(getActivity(),true); break;
          case 0: MusicPlayer.playOrPause(); break;
          case 1: MusicPlayer.next(); break;
      }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case /* play_pause_custom */  R.id.play_pause_custom : break;
            case /* inPrev */  R.id.music_controller_InPrev:  ButtonControl(-1);break;
            case /* pause_play_bar*/ R.id.play_pause_wrapper:
            case /* inPause */  R.id.in_pause_wrapper:  ButtonControl(0); break;
            case /* inPrev */  R.id.music_controller_InNext:  ButtonControl(1);break;
            case /*buttonBar*/ R.id.buttonBar: Tool.showToast(getActivity(),"click",500);
        }
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }
    MusicPlaybackTrack currentTrack;
    long mAlbumID;
    String mTitle, mArtist;
    @Override
    public void onMetaChanged() {
    new loadPlaying().execute();
    }

    @Override
    public void onArtWorkChanged() {
        colorMostCommon = Tool.getGlobalColor();
        color_in7basic= Tool.getSurfaceColor();
        float[] hsv = new float[3];
        Color.colorToHSV(colorMostCommon, hsv);
        //     Log.d(hsv[0] + "|" + hsv[1] + "|" + hsv[2], "ColorMe");
        alpha_7basic = hsv[1];
        applyRippleColor(color_in7basic);
    }

    private long mID=0;
    private class loadPlaying extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {

           long newID = MusicPlayer.getCurrentAudioId();
           String result="";
           if(newID!=mID) {
               mID = newID;
               currentTrack = MusicPlayer.getCurrentTrack();
               mTitle = MusicPlayer.getTrackName();
               mArtist = MusicPlayer.getArtistName();
               mAlbumID = MusicPlayer.getCurrentAlbumId();

               originalBitmapSong = ImageLoader.getInstance().loadImageSync(TimberUtils.getAlbumArtUri(mAlbumID).toString());
               // if bitmap can't be loaded, load default image
               if (originalBitmapSong == null) {
                   originalBitmapSong = BitmapFactory.decodeResource(getResources(), R.drawable.default_image2);
               }
                   return "song_change";
               } else  // play pause state changed
                   return "play_pause";
        }


        @Override
        protected void onPostExecute(String result) {
            if (result.equals("song_change")) {
                nameOfSong.setText(mTitle);
                nameOfArtist.setText(mArtist);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(rootView.findViewById(R.id.music_controller_parent2OfNameNArtist));
                }
                playBar_nameOfSong.setText(mTitle);
                playBar_artistOfSong.setText(mArtist);
                newBitmap = getBitmapRounded(originalBitmapSong.getWidth() / 16, originalBitmapSong);
                if (IsInitAvatarIcon) {
                    iconAvatar.setImageBitmap(newBitmap);
                    IsInitAvatarIcon = false;
                } else
                    makeIconAvatarRotateOrStop();
                String path = MusicPlayer.getPath();
                if(path!=null)
                setAudioWaveFragmentView(path);
        }
            generateColorThemeFromArtBitmap();
           updatePlayPauseButton();

        }
    }
    public void updatePlayPauseButton() {
        if (MusicPlayer.isPlaying()) {
            if (!play_pause_custom.isPlayed()) {
                play_pause_custom.setPlayed(true);
                play_pause_custom.startAnimation();
            }
            if (!inPause.isPlayed()) {
                inPause.setPlayed(true);
                inPause.startAnimation();
            }

        } else {
            if (play_pause_custom.isPlayed()) {
                play_pause_custom.setPlayed(false);
                play_pause_custom.startAnimation();
            }
            if (inPause.isPlayed()) {
                inPause.setPlayed(false);
                inPause.startAnimation();
            }
        }
    }
}
