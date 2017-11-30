package com.ldt.NewDefinitionMusicApp.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
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

import com.ldt.NewDefinitionMusicApp.InternalTools.Animation;
import com.ldt.NewDefinitionMusicApp.InternalTools.ImageEditor;
import com.ldt.NewDefinitionMusicApp.InternalTools.Tool;
import com.ldt.NewDefinitionMusicApp.MediaData.Song_onload;
import com.ldt.NewDefinitionMusicApp.activities.SupportFragmentActivity;
import com.ldt.NewDefinitionMusicApp.views.CustomAudioWaveView;
import com.ldt.NewDefinitionMusicApp.views.CustomSDFL4MusicController;
import com.ldt.NewDefinitionMusicApp.views.EffectView.EffectViewHolder;
import com.ldt.NewDefinitionMusicApp.views.PlayPauseButton;
import com.ldt.NewDefinitionMusicApp.activities.MainActivity;
import com.ldt.NewDefinitionMusicApp.R;

import java.util.ArrayList;
import java.util.Random;

public class MusicControllerFragment extends Fragment implements EffectViewHolder.EffectViewListener{

    public FragmentPlus.StatusTheme statusTheme = FragmentPlus.StatusTheme.BlackIcon;
    public int color_in7basic = 0;
    public int colorMostCommon = 0;
    private View navigation ;
    protected View rootView;
    public int[] palette = new int[6];
    private boolean generated = false;

    private void getPaletteColor() {
        Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // access palette colors here
                Palette.Swatch psVibrant = p.getVibrantSwatch();
                Palette.Swatch psVibrantLight = p.getLightVibrantSwatch();
                Palette.Swatch psVibrantDark = p.getDarkVibrantSwatch();
                Palette.Swatch psMuted = p.getMutedSwatch();
                Palette.Swatch psMutedLight = p.getLightMutedSwatch();
                Palette.Swatch psMutedDark = p.getDarkMutedSwatch();

                for (int i = 0; i < 6; i++)
                    palette[i] = -1;
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
                generated = true;
                setPalette();
            }
        };
        generated = false;
        Palette.from(originalBitmapSong).generate(paletteListener);

    }

    boolean HadSetPalette = false;
    View[] pl = new View[6];

    private void setPalette() {
        if (!HadSetPalette) {
            HadSetPalette = true;
            pl[0] = getActivity().findViewById(R.id.palette1);
            pl[1] = getActivity().findViewById(R.id.palette2);
            pl[2] = getActivity().findViewById(R.id.palette3);
            pl[3] = getActivity().findViewById(R.id.palette4);
            pl[4] = getActivity().findViewById(R.id.palette5);
            pl[5] = getActivity().findViewById(R.id.palette6);
            for (int i = 0; i < 6; i++)
                pl[i].setOnClickListener(paletteOnClick);
        }
        for (int i = 0; i < 6; i++) {
            {
                LogColor(palette[i]);

                if (palette[i] != -1)
                    pl[i].setBackgroundColor(palette[i]);
                else pl[i].setBackgroundColor(Color.TRANSPARENT);
            }
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
        ImageEditor.applyNewColor4Bitmap(getActivity(), R.drawable.listblack, inList, color1, alpha1);
        nameOfArtist.setAlpha(alpha2);
        nameOfArtist.setTextColor(color2);
        ImageEditor.applyNewColor4Bitmap(getActivity(),
                new int[]{R.drawable.pref, R.drawable.pauseblack, R.drawable.next},
                new ImageView[]{inPrev, inPause, inNext},
                color2,
                alpha2);
        playBar_TrackBar.setBackgroundColor(color1);
    }

    int color1 = 0, color2 = 0;
    float alpha1 = 1, alpha2 = 1;

    private void setColor4ControllerButton() {

        colorMostCommon = 0xff000000|((MainActivity)getActivity()).getColorr();
        color_in7basic= ColorReferTo(colorMostCommon);
          if(alpha_7basic>=0.5f)
          {
              color1= colorMostCommon;
              alpha1 =1;
              color2 = color_in7basic;
              alpha2 =alpha_7basic;
          }
          else
        {
            color1 = color_in7basic;
            alpha1 = 1;
            color2 = Color.BLACK;
            alpha2 = 0.5f;

        }
           getPaletteColor();
        playBar_pause_custom.setColor(color_in7basic);
        playBar_pause_custom.setPlayed(!playBar_pause_custom.isPlayed());
        playBar_pause_custom.startAnimation();
        ImageEditor.applyNewColor4Bitmap(getActivity(), R.drawable.listblack, playBar_list, color_in7basic, 1);


        nameOfSong.setTextColor(color1);
        ImageEditor.applyNewColor4Bitmap(getActivity(), R.drawable.listblack, inList, color1, alpha1);
        nameOfArtist.setAlpha(alpha2);
        nameOfArtist.setTextColor(color2);
        ImageEditor.applyNewColor4Bitmap(getActivity(), R.drawable.pref, inPrev, color2, alpha2);
        ImageEditor.applyNewColor4Bitmap(getActivity(), R.drawable.pauseblack, inPause, color2, alpha2);
        ImageEditor.applyNewColor4Bitmap(getActivity(), R.drawable.next, inNext, color2, alpha2);
        playBar_TrackBar.setBackgroundColor(color1);

 thisIsParentOfRoot.setBackColor1(0x80<<24|((MainActivity)getActivity()).getColorr());
    }

    float alpha_7basic = 0;

    private int ColorReferTo(int cmc) {
        float[] hsv = new float[3];
        Color.colorToHSV(cmc, hsv);
        Log.d(hsv[0] + "|" + hsv[1] + "|" + hsv[2], "ColorMe");
        alpha_7basic = hsv[1];
        float toEight = hsv[0] / 45 + 0.5f;
        if (toEight > 8 | toEight <= 1) return 0xffFF3B30;
        if (toEight <= 2) return 0xffFF9501;
        if (toEight <= 3) return 0xffFFCC00;
        if (toEight <= 4) return 0xff4AD968;
        if (toEight <= 5) return 0xff5BC7F7;
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
        String hex = Integer.toHexString(color);
    }

    private int widthFrom = 0;
    private int widthTo = 0;

    public void FocusIconAvatarToCenter(boolean zoomIn) {
        View shape_back = getActivity().findViewById(R.id.border_shape4avatar);
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
                 Bitmap new2= ImageEditor.getRoundedCornerBitmap(new1, (int)((1+ circle*29)*new1.getWidth()/30));
                 Bitmap new3 = ImageEditor.GetBlurredBackground(getActivity(),new2,30,30,30,30,-6,180,12,2);
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
    public Song_onload NowPlaying;
    public FrameLayout playBar_overView;
    public ImageView  playBar_list;
    public PlayPauseButton playBar_pause_custom;
    public TextView playBar_nameOfSong, playBar_artistOfSong;
    public ImageView inList;
    public ImageView toggle;
    public ImageView inPrev, inNext, inPause;
    public View playBar_TrackBar;
   public FrameLayout example_frame_layout;
    private void Merge_UI_ID(View v) {
        playBar_pause_custom = v.findViewById(R.id.play_pause_custom);
        playBar_TrackBar = v.findViewById(R.id.music_controller_PlayBar_TrackBar);
        inPrev =  v.findViewById(R.id.music_controller_InPrev);
        inPause =  v.findViewById(R.id.music_controller_InPause);
        inNext =  v.findViewById(R.id.music_controller_InNext);
        inList =  v.findViewById(R.id.InMusicController_list);
        toggle =  rootView.findViewById(R.id.music_controller_toggle);
        iconAvatar =  v.findViewById(R.id.music_controller_iconAvatar);
        nameOfSong =v.findViewById(R.id.music_controller_nameOfSong);
        nameOfArtist =  v.findViewById(R.id.music_controller_nameOfArtist);
        playBar_overView = v.findViewById(R.id.music_controller_play_bar_frm);
        playBar_list = v.findViewById(R.id.music_controller_playBar_list);
        playBar_nameOfSong =  v.findViewById(R.id.music_controller_playBar_nameOfSong);
        playBar_artistOfSong =v.findViewById(R.id.music_controller_playBar_nameOfArtist);
        navigation = v.findViewById(R.id.navigation_color_control_music_controller);
        example_frame_layout = v.findViewById(R.id.example_frame_layout);
    }

    private void setOnClickOrOnTouch4AllButton() {
        rootView.setOnTouchListener(touch_play_bar_top1);
        playBar_overView.setOnTouchListener(touch_play_bar_top1);
    }
    /**
     * this is handler y of playbar, it means that if user touch the play bar, in any position, will call this before all.
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
    final int[] image_menu = new int[] {R.drawable.exit_controller,R.drawable.repeat,R.drawable.more_black};

    private EffectViewHolder effectViewHolder;
    private View.OnTouchListener touch_play_bar_top1 = new View.OnTouchListener()
    {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getId() == R.id.music_controller_play_bar_frm||true) {
                v.performClick();
                if (effectViewHolder == null) effectViewHolder = getMainActivity().effectViewHolder;
                logOnTouchEvent("MSF",event);

                effectViewHolder.detectLongPress(MusicControllerFragment.this, "",v,event);

                return effectViewHolder.run(v, event) || getMainActivity().onTouchToggle.onTouch(v, event);
            }
            else return getMainActivity().onTouchToggle.onTouch(v,event);
        }
    };

    public void setInform4PlayBar(Song_onload nowPlaying) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition( rootView.findViewById(R.id.music_controller_parent2OfNameNArtist));
        }
        playBar_nameOfSong.setText(nowPlaying.Title);
        playBar_artistOfSong.setText(nowPlaying.Artist);
    }

    public boolean IsInitAvatarIcon = true;
    public boolean appearBar = true;
    public boolean inAnimator_alpha_pb = false;
  private boolean checkAgain = false;
  private float pc_check = 0;
    private void alpha_playBar_toggle_transition(boolean playBar_toOn,float pc) {
        if(inAnimator_alpha_pb) {checkAgain = true;pc_check = pc;return;}

        ValueAnimator va;
        if(playBar_toOn) va = ValueAnimator.ofFloat(0,1);
        else va = ValueAnimator.ofFloat(1,0);
        va.setDuration(600);
        va.setStartDelay(200);
        va.addUpdateListener(animation -> {
            float d = (float) animation.getAnimatedValue();
            if(d>1) d = 1;
            else if(d<0) d = 0;
            playBar_overView.setAlpha(d);
            toggle.setAlpha(1 - d);
        });

        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                inAnimator_alpha_pb = true;
               if(playBar_overView.getVisibility()==View.INVISIBLE) {
                   playBar_overView.setVisibility(View.VISIBLE);

               }
                else if(toggle.getVisibility()==View.INVISIBLE) {
                   toggle.setVisibility(View.VISIBLE);

               }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(playBar_overView.getAlpha()==0) {
                    playBar_overView.setVisibility(View.INVISIBLE);
                    appearBar = false;
                }
                else if(toggle.getAlpha()==0) {
                    toggle.setVisibility(View.INVISIBLE);
                    appearBar = true;
                }
               inAnimator_alpha_pb = false;
                if(checkAgain) {
                    checkAgain = false;

                    setAlphaOfPB(pc_check);
                }
            }
        });
        va.start();
    }
    public void setAlphaOfPB(float pc) {
        // Setting alpha for play bar.
        if (appearBar&&pc>=0.5f) {
            if (playBar_overView.getAlpha()>0)
                //  disappear the playbar and appear the toggle
                alpha_playBar_toggle_transition(false,pc);
        }
        else if (!appearBar&&pc<0.5f) {
            if (toggle.getAlpha() >0) {
                // disappear the toggle and appear the playbar
                alpha_playBar_toggle_transition(true,pc);
            }
        }
    }
    public void setAlphaOfPlayBar(float pc) {
        setAlphaOfPB(pc);
        if(true) return;
        float ForPlayBar = 1 - 2 * pc;
        float ForToggle = 2 * pc - 1;

        // Setting alpha for play bar.
        if (ForPlayBar <= 0)
            playBar_overView.setVisibility(View.INVISIBLE);
        else {
            playBar_overView.setVisibility(View.VISIBLE);
            if (ForPlayBar <= 1) playBar_overView.setAlpha(ForPlayBar);
        }

        // Setting alpha for toggle
        if (ForToggle <= 0) {
            toggle.setVisibility(View.INVISIBLE);
        } else {
            toggle.setVisibility(View.VISIBLE);
            if (ForToggle <= 1) toggle.setAlpha(ForToggle);
        }
    }

    public Bitmap originalBitmapSong = null;

    public void setBitmap4Avatar(Song_onload NowPlaying) {
        oldBitmap = ((BitmapDrawable) iconAvatar.getDrawable()).getBitmap();
        if (originalBitmapSong != null) originalBitmapSong.recycle();
        originalBitmapSong = getBitmapResourceOrPathFromSong(NowPlaying);
        Bitmap new2 = ImageEditor.getRoundedCornerBitmap(originalBitmapSong, originalBitmapSong.getWidth());
        newBitmap = ImageEditor.GetBlurredBackground(getActivity(), new2, 30, 30, 30, 30, -6, 180, 12, 2);
        new2.recycle();
    }

    Bitmap newBitmap = null;
    Bitmap oldBitmap = null;

    public Bitmap getBitmapResourceOrPathFromSong(Song_onload song) {
        if (NowPlaying.AlbumArt_path == "" || NowPlaying.AlbumArt_path == null) {
            Bitmap original = BitmapFactory.decodeResource(getResources(), NowPlaying.Id_Image);
            if (original.getWidth() <= 310) return original;
            Bitmap resize = ImageEditor.getResizedBitmap(original, 300, 300);
            original.recycle();

            return resize;
        }   // imageView.setImageResource(R.drawable.walle);
        return BitmapFactory.decodeFile(NowPlaying.AlbumArt_path);
    }

    Random rnd = new Random();

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
                oldBitmap.recycle();
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
                    oldBitmap.recycle();
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


    public void Set_Music_Song_Player(ArrayList<Song_onload> list, int position) {
        // this will get the list Song from MainScreenFragment and the position of the play Song, it only use for setting UI, it do not set anything else.
        NowPlaying = list.get(position);

        nameOfSong.setText(NowPlaying.Title);
        nameOfArtist.setText(NowPlaying.Artist);
        setInform4PlayBar(NowPlaying);
        setBitmap4Avatar(NowPlaying);
        setColor4ControllerButton();
        setAudioWaveFragmentView(NowPlaying.Data);
        if (IsInitAvatarIcon) {
            iconAvatar.setImageBitmap(newBitmap);
            oldBitmap.recycle();
            IsInitAvatarIcon = false;
        } else
            makeIconAvatarRotateOrStop();
    }
  private void setNavigationHeight()
  {
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) navigation.getLayoutParams();
      params.height=((SupportFragmentActivity)getActivity()).NavigationHeight;
      navigation.setLayoutParams(params);
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
        rootView = inflater.inflate(R.layout.music_controller, container, false);
        //  ImageView exit_view = (ImageView) fragment.findViewById(R.id.exit_playlist_controller);
        //   exit_view.setOnClickListener(exit_click);
        makeBackground();
        Merge_UI_ID(rootView);
        setOnClickOrOnTouch4AllButton();
        setNavigationHeight();
        //   makeIconAvatarRotateOrStop();
        return rootView;
    }
    private CustomSDFL4MusicController thisIsParentOfRoot;
    private void makeBackground()
    {
      thisIsParentOfRoot = (CustomSDFL4MusicController)rootView;
       // if(thisIsParentOfRoot==null) Tool.showToast(getActivity(),"Parent null",5000);
      thisIsParentOfRoot.setBackColor2(0xccffffff);
   //   thisIsParentOfRoot.setBackgroundColor(Color.BLUE);
    }
    public void setDarken(float darken, float maxDark)
    {
        thisIsParentOfRoot.setDarken(darken,maxDark);
    }
    CustomAudioWaveView cAWV = null;
    private void setAudioWaveFragmentView(String file)
    { if(true) return;
        if(cAWV!=null)
            ((SupportFragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(cAWV).commit();
            cAWV = CustomAudioWaveView.Initialize(file);
            ((SupportFragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction().add(example_frame_layout.getId(), cAWV).commit();
    }

    public void setNumber(float number) {
       thisIsParentOfRoot.setNumber(number);
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
    public void onReceivedResult(String command,int result) {
        switch (result) {
            case 1 :Tool.showToast(getActivity(),"One",1000);break;
            case  2:Tool.showToast(getActivity(),"Two",1000);break;
            case 3 :Tool.showToast(getActivity(),"Three",1000);break;
            default : Tool.showToast(getActivity(),"Cancel",1000);break;
        }
    }
}
