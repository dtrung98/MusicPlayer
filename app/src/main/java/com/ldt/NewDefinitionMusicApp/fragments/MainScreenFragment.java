package com.ldt.NewDefinitionMusicApp.fragments;


import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.IntRange;
import android.view.MotionEvent;
import android.view.animation.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import android.transition.Fade;
import android.util.Log;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldt.NewDefinitionMusicApp.InternalTools.ImageEditor;
import com.ldt.NewDefinitionMusicApp.InternalTools.Tool;
import com.ldt.NewDefinitionMusicApp.InternalTools.helper;
import com.ldt.NewDefinitionMusicApp.MediaData.MediaLoader;
import com.ldt.NewDefinitionMusicApp.MediaData.Song_onload;
import com.ldt.NewDefinitionMusicApp.Others.OnSwipeTouchListener;
import com.ldt.NewDefinitionMusicApp.R;

import com.ldt.NewDefinitionMusicApp.activities.MainActivity;
import com.ldt.NewDefinitionMusicApp.activities.SupportFragmentActivity;
import com.ldt.NewDefinitionMusicApp.recyclerview.chooseOneSong2MakeListAdapter;
import com.ldt.NewDefinitionMusicApp.services.MediaService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Timer;
import com.ldt.NewDefinitionMusicApp.MediaData.FormatDefinition.Album;
import com.ldt.NewDefinitionMusicApp.views.EffectView.EffectViewHolder;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ldt.NewDefinitionMusicApp.InternalTools.ImageEditor.updateSat;

public class MainScreenFragment extends FragmentPlus {

    private static final int MY_PERMISSIONS_READ_STORAGE = 1;
    private TextView baiHat;
    private ImageButton menuButton;
    private ImageView album2;
    private ImageButton searchButton;
    private View statusBar;
    private RecyclerView recyclerView;
    private ListView playlist_BigList;
    private void MergeUI() {
        statusBar = rootView.findViewById(R.id.status_bar);
        recyclerView = rootView.findViewById(R.id.recyclerView_N1);
        menuButton = rootView.findViewById(R.id.menuButton);
        baiHat =  rootView.findViewById(R.id.baihat);
        titleBar = rootView.findViewById(R.id.title_bar);
        scrollView =rootView.findViewById(R.id.myScroll);
        tieuDe =  rootView.findViewById(R.id.tieude);
        line_bottomOftitleBar = rootView.findViewById(R.id.mainActivity_line_titlebar);
        album2 =  rootView.findViewById(R.id.album2);
        searchButton = rootView.findViewById(R.id.search);
        playlist_BigList =rootView.findViewById(R.id.choosePlaylist_listView);
        setTimer();
    }

    private void SetAllClick() {
        View[] views = new View[]
                {  // put views here
                        menuButton,
                        baiHat,
                        titleBar,
                        searchButton
                };
        setOnClick(Onclick, views);
    }

    private void setOnClick(View.OnClickListener onclick, View[] v) {
        int len = v.length;
        for (int i = 0; i < len; i++)
            v[i].setOnClickListener(onclick);
    }

    private View.OnClickListener Onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.search: {
                    Scroll2Top();
                }
                break;
                case R.id.baihat:
                case R.id.menuButton:
                case R.id.title_bar:
                    ((SupportFragmentActivity) getActivity()).pushFragment(ShowMusicSongs.Initialize(getActivity()), true);
            }
        }
    };

    public boolean Scroll2Top() {
        int yy = scrollView.getScrollY();
        if (yy != 0) {
            scrollView.setFocusableInTouchMode(true);
            //scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            scrollView.fullScroll(View.FOCUS_UP);
            return true;
        } else return false;
    }

    @Override
    public ApplyMargin IWantApplyMargin() {
        return ApplyMargin.BOTH;
    }

    @Override
    public void applyFitWindow_Bottom()
    {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)playlist_BigList.getLayoutParams();
        params.bottomMargin = FitWindow_Bottom;
        playlist_BigList.setLayoutParams(params);
        Log.d("SFA","MainScreen received command : "+ FitWindow_Bottom);
    }

    public static MainScreenFragment Initialize(Activity activity) {

        MainScreenFragment mainScreenFragment = new MainScreenFragment();
        mainScreenFragment.setFrameLayoutNTransitionType(activity, SupportFragmentActivity.TransitionType.FADE_IN_OUT);
        return mainScreenFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        MergeUI();
        SetAllClick();
        GetListMusic();
        return rootView;
    }

    private void GetListMusic() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                }, MY_PERMISSIONS_READ_STORAGE);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_READ_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else doStuff();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_STORAGE: {
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // Granted
                        doStuff();
                    } else getActivity().finish();
                }
            }
            return;
        }

    }

    private int dd = 0;

    private void doStuff() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                GetListSong();
                setAlbumArrayList_ListView();
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                onTransitionComplete();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                getMusic();
                getAlbumList_AfterAskedPermission();
                return null;
            }

        }.execute();
    }
    private boolean drawn =false;
    @Override
    public void onTransitionComplete() {
        if(!drawn) {
            drawn = true;
            Tool.setDrawn(drawn);
            getActivity().findViewById(R.id.rootEveryThing).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public StatusTheme setDefaultStatusTheme() {
        return StatusTheme.BlackIcon;
    }


    @Override
    public void applyFitWindow_Top() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)statusBar.getLayoutParams();
        params.height= FitWindow_Top;
        statusBar.setLayoutParams(params);
    }

    class AlbumArt {
        String Name;
        String Art;

        public AlbumArt(String name, String art) {
            Name = name;
            Art = art;
        }
    }

    private Timer timer = new Timer();

    private void setTimer() {
        start();
    }

    private boolean started = false;
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (started) {
                start();
            }
        }
    };

    public void stop() {
        started = false;
        handler.removeCallbacks(runnable);
    }

    private static final int max_alpha = 100 * 3;

    public void start() {
        //Log.d("Runner",number_run+"");
        AnimatedColorTitle();
        started = true;
        handler.postDelayed(runnable, 2);
    }

    ScrollView scrollView;
    RelativeLayout titleBar;
    RelativeLayout tieuDe;
    View line_bottomOftitleBar;

    private void AnimatedColorTitle() {
        if (scrollView == null) scrollView =  rootView.findViewById(R.id.myScroll);
        int yy = scrollView.getScrollY();

        int max_y = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();

        if (yy < max_alpha) {
            alphaOfTitleBar = yy / 3;
            if (alphaOfTitleBar < 0) alphaOfTitleBar = 0;
            titleBar.setBackgroundColor(alphaOfTitleBar << 24 | colorr);
            line_bottomOftitleBar.setAlpha(alphaOfTitleBar/255.0f);
        } else if (alphaOfTitleBar < max_alpha) {
            alphaOfTitleBar = max_alpha / 3;
            titleBar.setBackgroundColor(alphaOfTitleBar << 24 | colorr);
            line_bottomOftitleBar.setAlpha(alphaOfTitleBar/255.0f);
        } else if (just_change_color) {
            just_change_color = false;
            titleBar.setBackgroundColor(alphaOfTitleBar << 24 | colorr);
            line_bottomOftitleBar.setAlpha(alphaOfTitleBar/255.0f);
        }

    }

    /*
       private Timer timer;
   private TimerTask timerTask = new TimerTask() {

       @Override
       public void run() {
           final Random random = new Random();
           int i = random.nextInt(2 - 0 + 1) + 0;
           random_note.setImageResource(image[i]);
       }
   };

   public void start() {
       if(timer != null) {
           return;
       }
       timer = new Timer();
       timer.scheduleAtFixedRate(timerTask, 0, 2000);
   }

   public void stop() {
       timer.cancel();
       timer = null;
   }
        */
    private ArrayList<AlbumArt> albumArts = new ArrayList<>();

    private void getMusic() {
        ContentResolver contentR = getActivity().getContentResolver();
        MediaLoader.StartToRefresh(contentR);
        Uri song = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri album = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentR.query(song, null, null, null, null);
        Cursor albumCursor = contentR.query(album, null, null, null, null);

        if (albumCursor != null && albumCursor.moveToFirst()) {
            DanhSachPhat.clear();
            albumArts.clear();
            //   Toast.makeText(MainScreenFragment.this,albumArts.size()+":"+albumArts.size(),Toast.LENGTH_SHORT).show();
            int x = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int idd = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);

            {
                do {
                    String thisArt = albumCursor.getString(x);
                    String thisId = albumCursor.getString(idd);
                    albumArts.add(new AlbumArt(thisId, thisArt));
                }
                while (albumCursor.moveToNext());
            }
            albumCursor.close();
            //     Toast.makeText(MainScreenFragment.this, albumArts.size() + ":" + albumArts.size(), Toast.LENGTH_SHORT).show();

        }

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtst = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumId = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int data = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int idColumn = songCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            do {
                long thisId = songCursor.getLong(idColumn);
                String idz = songCursor.getString(albumId);
                String curTil = songCursor.getString(songTitle);
                String curArt = songCursor.getString(songArtst);
                String path = songCursor.getString(data);

                // String thisArt = songCursor.getString(album);
                // if(thisArt!=null&&thisArt!="")
                //   listSong.add(new Song_onload(thisArt,curTil,curArt));
                //    else

                String nnn = getAlbumArtWithId(idz);
                if (nnn != null)
                    DanhSachPhat.add(new Song_onload(thisId, nnn, curTil, curArt, path));
                else if (DanhSachPhat.size() != 0)
                    DanhSachPhat.add(new Song_onload(thisId, R.drawable.default_image2, curTil, curArt, path));

            }
            while (songCursor.moveToNext());
            songCursor.close();
        }
        Collections.sort(albumArts, new Comparator<AlbumArt>() {
            public int compare(AlbumArt a, AlbumArt b) {
                return a.Name.compareTo(b.Name);
            }
        });

        Collections.sort(DanhSachPhat, new Comparator<Song_onload>() {
            public int compare(Song_onload a, Song_onload b) {
                return a.Title.compareTo(b.Title);
            }
        });
    }

    /*
      private AdapterView.OnItemClickListener list_view_item_onclick = new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

  Toast.makeText(MainScreenFragment.this,"you clicked listview!",Toast.LENGTH_LONG).show();
          }
      };
      */
    public static ArrayList<Song_onload> DanhSachPhat = new ArrayList<Song_onload>();
    private Intent ex_service_intent = null;

    private void GetListSong() {
        if (DanhSachPhat.size() == 0) return;
        dd = rnd.nextInt(albumArts.size());
        setUpRecyclerView();
       OverScrollDecoratorHelper.setUpOverScroll(scrollView);


    }

    public int NowPlaying_int;

    public void controlPrevOrNext(boolean next) {
        if (next) NowPlaying_int++;
        else NowPlaying_int--;
        if (NowPlaying_int > DanhSachPhat.size() - 1) NowPlaying_int = DanhSachPhat.size() - 1;
        else if (NowPlaying_int < 0) NowPlaying_int = 0;
        ex_service_intent.putExtra("NowPlaying", NowPlaying_int);
        getActivity().startService(ex_service_intent);
        ChangeBackground(NowPlaying_int);
        ((MainActivity) getActivity()).Control_Music_Song_Player(DanhSachPhat, NowPlaying_int);
        ((MainActivity) getActivity()).Init_UpDown_musicController();
        ((MainActivity)getActivity()).control_music_controller_up();
    }


    public static ArrayList<String> getData(ArrayList<Song_onload> arrayList) {
        int len = arrayList.size();
        ArrayList<String> arrayList1 = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            arrayList1.add(arrayList.get(i).Data);
        }
        return arrayList1;
    }

    private boolean just_change_color = true;


    Random rnd = new Random();

    private String getAlbumArtWithId(String id) {
        int len = albumArts.size();
        for (int i = 0; i < len; i++)
            if (id.equals(albumArts.get(i).Name)) {
                return albumArts.get(i).Art;
            }
        return null;
    }

    private ImageView blur_background;
    private boolean settedBackground = false;

    private void ChangeBackground(int id) {
        Bitmap original = null;
        Bitmap sample = null;
        Bitmap sample_update;
        Bitmap sample_blur ;
        settedBackground = true;
        blur_background =  rootView.findViewById(R.id.blur_background);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        String albumArt_path = DanhSachPhat.get(id).AlbumArt_path;

        if (albumArt_path != null && albumArt_path != "" && Tool.Path_Is_Exist(albumArt_path) == 2)
            try {
                original = BitmapFactory.decodeFile(albumArt_path);
                options.inSampleSize = Tool.Avatar.getDevideSize(38, original);
                sample = BitmapFactory.decodeFile(albumArt_path, options);
                album2.setImageBitmap(original);
            } catch (Exception e) {
                original = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);
                options.inSampleSize = Tool.Avatar.getDevideSize(38, original);
                sample = BitmapFactory.decodeResource(getResources(), R.drawable.default_image, options);
            }
        if (original == null)
            original = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);
        options.inSampleSize = Tool.Avatar.getDevideSize(38, original);
        if (sample == null)
            sample = BitmapFactory.decodeResource(getResources(), R.drawable.default_image, options);

        sample_update = updateSat(sample, 4);
        sample_blur = ImageEditor.fastblur(sample_update, 1, 4);
        int[] averageColorRGB = ImageEditor.getAverageColorRGB(sample_blur);
        black_theme = ImageEditor.PerceivedBrightness(95, averageColorRGB);

        colorr = (averageColorRGB[0] << 16 | averageColorRGB[1] << 8 | averageColorRGB[2]);
        Tool.setGlobalColor(0xff << 24 | colorr);
        just_change_color = true;
        //   changeThemeColor();
        blur_background.setImageBitmap(sample_blur);
    }

    /*
    private void changeThemeColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(0x88000000 | colorr);
            root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ibutton(R.id.menuButton).setAlpha(0.7f);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ibutton(R.id.search).setAlpha(0.7f);
        }
        if (black_theme) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                root.setSystemUiVisibility(0);
            }
            text(R.id.text_trinhphatnhac).setTextColor(Color.argb(166, 255, 255, 255));
            ibutton(R.id.menuButton).setImageResource(R.drawable.menu_1white);
            ibutton(R.id.search).setImageResource(R.drawable.search_white);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            text(R.id.text_trinhphatnhac).setTextColor(Color.argb(166, 0, 0, 0));
            ibutton(R.id.menuButton).setImageResource(R.drawable.menu_1);
            ibutton(R.id.search).setImageResource(R.drawable.search);
        }
    }
    */
    private boolean blockChangeStatusBarColor = false;
    private boolean FirstRender = true;

    private void setBackground() {
        int random = rnd.nextInt(albumArts.size());
        ChangeBackground(random);
        //     Log.d((System.nanoTime()-ddd)+"","zzzm");
    }

    private int alphaOfTitleBar = 0;
    static boolean black_theme = false;
    /*
    Black is true, otherwise is white
     */
    public int colorr = 255 << 16 | 255 << 8 | 255;

    /**
     * Calculate the average red, green, blue color values of a bitmap
     *
     * @param //bitmap a {@link Bitmap}
     * @return
     */


    public void HieuUngRipple(View view) {

        //   Intent intent = new Intent(this, Dripple.class);
        // startActivity(intent);
        //  ibutton(R.id.search).setVisibility(View.GONE);
    }

    private OnSwipeTouchListener ostl;


    private ImageButton play_button;
    //  private ImageButton more= findViewById(R.id.more);
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private boolean play_bar_visible = true;
    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener mCorkyListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mFade = new Fade(3);
            }
            //new Fade(3);
            // do something when the button is clicked
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //    TransitionManager.beginDelayedTransition((RelativeLayout) findViewById(R.id.root), mFade);
            }
            play_bar_visible = !play_bar_visible;
        }
    };

    private View.OnLongClickListener avatar_longClick = new View.OnLongClickListener() {
        public boolean onLongClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //   TransitionManager.beginDelayedTransition((RelativeLayout) findViewById(R.id.root));
            }
            play_bar_visible1 = !play_bar_visible1;
            return true;
        }
    };

    private boolean play_bar_visible1 = true;
    private Fade mFade;
    private ViewGroup mRootView;

    /*
      private class SwipeDown extends Transition{
         private static final String PROPNAME_BACKGROUND =
                 "com.example.android.customtransition:CustomTransition:background";
         @Override
         public void captureStartValues(TransitionValues transitionValues) {
             captureValues(transitionValues);
         }
         // For the view in transitionValues.view, get the values you
         // want and put them in transitionValues.values
         private void captureValues(TransitionValues transitionValues) {
             // Get a reference to the view
             View view = transitionValues.view;
             // Store its background property in the values map
             transitionValues.values.put(PROPNAME_BACKGROUND, view.getBackground());
         }
         @Override
         public void captureEndValues(TransitionValues values) {
             captureValues(values);
         }
         /*
         @Override
         public Animator createAnimator(ViewGroup sceneRoot,
                                        TransitionValues startValues,
                                        TransitionValues endValues)
         {

         }
         */
    public class RecyclerViewMargin extends RecyclerView.ItemDecoration {
        private final int columns;
        private int margin;

        /**
         * constructor
         *
         * @param margin  desirable margin size in px between the views in the recyclerView
         * @param columns number of columns of the RecyclerView
         */
        public RecyclerViewMargin(@IntRange(from = 0) int margin, @IntRange(from = 0) int columns) {
            this.margin = margin;
            this.columns = columns;

        }

        /**
         * Set different margins for the items inside the recyclerView: no y margin for the first row
         * and no x margin for the first column.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            int position = parent.getChildLayoutPosition(view);
            //set right margin to all
            outRect.left = margin;
            outRect.right = margin;

        }
    }
    private void setUpRecyclerView() {
        chooseOneSong2MakeListAdapter adapter = new chooseOneSong2MakeListAdapter(getActivity(), DanhSachPhat);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4, LinearLayoutManager.HORIZONTAL, false));

        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                                                @Override
                                                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                                                    MusicControllerFragment.logOnTouchEvent("recyclerView (onIntercept.... )",e);
                                                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                                                    if(recyclerView.getChildLayoutPosition(child)!=-1) {
                                                        getMainActivity().effectViewHolder.detectLongPress(effectViewListener,"",child,e);
                                                        return true;
                                                    }
                                                    return false;
                                                }

                                                @Override
                                                public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                                                    MusicControllerFragment.logOnTouchEvent("recyclerView ( onTouchEvent )",e);
                                                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                                                    getMainActivity().effectViewHolder.detectLongPress(effectViewListener,"",child,e);
                                                    boolean d = getMainActivity().effectViewHolder.run(child,e)|| normal(child,e);

                                                }

                                                @Override
                                                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                                                    Log.d("recyclerView","onRequestDisallowInterceptTouchEvent");
                                                }
                                                public boolean normal(View v,MotionEvent e) {
                                                    // do whatever
                                                  if(true)  return true;
                                                    int position = recyclerView.getChildLayoutPosition(v);
                                                    if(position==-1) return false;
                                                    ex_service_intent = new Intent(getActivity(), MediaService.class);
                                                    ex_service_intent.putExtra("DanhSachPhat_Data", getData(DanhSachPhat));
                                                    ex_service_intent.putExtra("NowPlaying", position);
                                                    //   OverScrollDecoratorHelper.setUpOverScroll(Song_list);

                                                    //     getActivity().startService(ex_service_intent);
                                                    NowPlaying_int = position;
                                                    ChangeBackground(NowPlaying_int);
                                                    ((MainActivity) getActivity()).Control_Music_Song_Player(DanhSachPhat, NowPlaying_int);
                                                    ((MainActivity) getActivity()).Init_UpDown_musicController();
                                                    ((MainActivity)getActivity()).control_music_controller_up();
                                                    return true;
                                                }
                                            });
/*
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        ex_service_intent = new Intent(getActivity(), MediaService.class);
                        ex_service_intent.putExtra("DanhSachPhat_Data", getData(DanhSachPhat));
                        ex_service_intent.putExtra("NowPlaying", position);
                        //   OverScrollDecoratorHelper.setUpOverScroll(Song_list);

                        //     getActivity().startService(ex_service_intent);
                        NowPlaying_int = position;
                        ChangeBackground(NowPlaying_int);
                        ((MainActivity) getActivity()).Control_Music_Song_Player(DanhSachPhat, NowPlaying_int);
                        ((MainActivity) getActivity()).Init_UpDown_musicController();
                       ((MainActivity)getActivity()).control_music_controller_up();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                }));
*/
    }
    String[] long_press_menu_random_string = new String[] {"Play","Add","More"};
    int[] long_press_menu_random_image_id = new int[] {R.drawable.play,R.drawable.back,R.drawable.more_black};
    public EffectViewHolder.EffectViewListener effectViewListener = new EffectViewHolder.EffectViewListener() {
        @Override
        public ImageView getImageView(String command) {
            return null;
        }

        @Override
        public String[] getStringCommand(String command) {
            return long_press_menu_random_string;
        }

        @Override
        public int[] getImageResources(String command) {
            return long_press_menu_random_image_id;
        }

        @Override
        public void onReceivedResult(String command, int result) {
            Tool.showToast(getActivity(),getStringCommand(command)[result],500);
        }
    };
    private void setAlbumArrayList_ListView() {

        playlist_BigList.setAdapter(new album_view_adapter());
        helper.getListViewSize(playlist_BigList);
        playlist_BigList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                //       Toast.makeText(Choose_Playlist.this,"I am ListView "+position+"th",Toast.LENGTH_SHORT).show();

                //  List<View> lv =getAllViews(view);
                View IsimageView = ((ViewGroup) view).getChildAt(0);

                if (IsimageView instanceof ImageView) {
                    //     Toast.makeText(Choose_Playlist.this,"ok",Toast.LENGTH_SHORT).show();
                    int[] location = new int[2];
                    IsimageView.getLocationOnScreen(location);
                    int id_pos = ((myTag) (IsimageView.getTag())).Position;
                    Bitmap bitm = albumArrayList.get(id_pos).getBitmap();
                    //drawBuffer(location[0],location[1],location[0]+ IsimageView.getWidth(),IsimageView.getHeight()+location[1],bitm);
                }
                final android.view.animation.Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce_slow);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 30);
                myAnim.setInterpolator(interpolator);
                view.startAnimation(myAnim);
            }
        });
    }

    public ArrayList<Album> albumArrayList = new ArrayList<>();

    public class myTag {
        int Position;

        public myTag(int id_pos) {
            Position = id_pos;
        }
    }

    private class album_view_adapter extends BaseAdapter {
        @Override
        public int getCount() {
            return albumArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            view = getActivity().getLayoutInflater().inflate(R.layout.item_list_view, parent, false);
            final ImageView imageView =  view.findViewById(R.id.item_album_view_image);
            TextView titleView =  view.findViewById(R.id.item_album_view_title);
            TextView artistView = view.findViewById(R.id.item_album_view_aritst);
            final Album album = albumArrayList.get(position);
            imageView.setTag(new myTag(position));
            imageView.setImageBitmap(ImageEditor.GetBlurredBackground(getActivity(), ImageEditor.getRoundedCornerBitmap(album.getBitmap(), 15), 25, 25, 25, 25, -6, 180, 12, 2));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tool.showToast(getActivity(), "You click on this ImageView!", 1000);

                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ImageView iv = (ImageView) v;
                    Tool.showToast(getActivity(), "You long click on this ImageView!", 1000);


                    return true;
                }
            });
            titleView.setText(album.getTitle());
            artistView.setText(album.getArtist());
            Log.d("Vision ", "ListView getView!");
            return view;
        }
    }

    static class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }

    private void getAlbumList_AfterAskedPermission() {
        ContentResolver contentR = getActivity().getContentResolver();
        Uri album = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor = contentR.query(album, null, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) { // có chạy vòng đầu tiên hay không.
            albumArrayList.clear();  // Xóa hết phần tử cái đã.
            int art = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int title = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int artist = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);

            do {
                String art_path, title_text, artist_text;
                art_path = albumCursor.getString(art);
                title_text = albumCursor.getString(title);
                artist_text = albumCursor.getString(artist);
                Bitmap bitmap = BitmapFactory.decodeFile(art_path);
                if (bitmap == null) // khong ton tai art
                {
                    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.default_image2);
                    if (ImageEditor.TrueIfBitmapBigger(bitmap1, 300)) {
                        Log.d("Song", "bigger");
                        bitmap = ImageEditor.getResizedBitmap(bitmap1, 300, 300);
                        bitmap1.recycle();
                    } else bitmap = bitmap1;
                }
                albumArrayList.add(new Album(title_text, artist_text, bitmap));
            }
            while (albumCursor.moveToNext());
        }
        Collections.sort(albumArrayList, new Comparator<Album>() {
            public int compare(Album a, Album b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }
}
