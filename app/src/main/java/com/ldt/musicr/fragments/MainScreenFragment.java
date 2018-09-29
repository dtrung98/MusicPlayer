package com.ldt.musicr.fragments;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.animation.*;
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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldt.musicr.InternalTools.BitmapEditor;
import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.InternalTools.helper;
import com.ldt.musicr.MediaData.Song_OnLoad;
import com.ldt.musicr.Others.OnSwipeTouchListener;
import com.ldt.musicr.R;

import com.ldt.musicr.activities.BaseActivity;
import com.ldt.musicr.activities.SupportFragmentPlusActivity;
import com.ldt.musicr.adapters.SmallPlaylistAdapter;
import com.ldt.musicr.adapters.SmallSongListAdapter;
import com.ldt.musicr.dataloaders.PlaylistLoader;
import com.ldt.musicr.dataloaders.SongLoader;
import com.ldt.musicr.listeners.MusicStateListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import com.ldt.musicr.MediaData.FormatDefinition.Album;
import com.ldt.musicr.views.BubbleMenu.BubbleMenuCenter;
import com.ldt.musicr.views.GridRecyclerIndicator;
import com.ldt.musicr.views.stickyActionBarConstraintLayout;
import com.ldt.musicr.views.StickyScrollView;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ldt.musicr.InternalTools.BitmapEditor.updateSat;

public class MainScreenFragment extends FragmentPlus implements MusicStateListener {
    private static final String TAG = "MainScreenFragment";
    private static final int MY_PERMISSIONS_READ_STORAGE = 1;
    private TextView baiHat;

    private RecyclerView songListRecyclerView,playlistRecyclerView;
    private ListView playlist_BigList;
    private stickyActionBarConstraintLayout stickyActionBar;
    private GridRecyclerIndicator songIndicator;
    private View songs_pieces_relative_header;
    private View playlist_pieces_relative;
    private View main_slider;
    private Toolbar toolbar;
    public static RippleDrawable getPressedColorRippleDrawable(int normalColor, int pressedColor)
    {
        return new RippleDrawable(getPressedColorSelector(normalColor, pressedColor), getColorDrawableFromColor(normalColor), null);
    }

    public static ColorStateList getPressedColorSelector(int normalColor, int pressedColor)
    {
        return new ColorStateList(
                new int[][]
                        {
                                new int[]{android.R.attr.state_pressed},
                                new int[]{android.R.attr.state_focused},
                                new int[]{android.R.attr.state_activated},
                                new int[]{}
                        },
                new int[]
                        {
                                pressedColor,
                                pressedColor,
                                pressedColor,
                                normalColor
                        }
        );
    }

    public static ColorDrawable getColorDrawableFromColor(int color)
    {
        RadioButton radioButton;

        return new ColorDrawable(color);
    }

    private void MergeUI() {
        main_slider = rootView.findViewById(R.id.main_slider);
        blur_background =  rootView.findViewById(R.id.blur_background);
        songs_pieces_relative_header = rootView.findViewById(R.id.songs_piece_header_relative);
        playlist_pieces_relative = rootView.findViewById(R.id.playlist_piece_relative);
  //      songs_pieces_relative_header.setBackground(rippleDrawable);
        songListRecyclerView = rootView.findViewById(R.id.recyclerView_N1);
        playlistRecyclerView = rootView.findViewById(R.id.playlists_recycler_view);

        baiHat =  rootView.findViewById(R.id.baihat);

        stickyActionBar = rootView.findViewById(R.id.stickyActionBar);
        scrollView =rootView.findViewById(R.id.myScroll);
        scrollView.setStickyDrawView(stickyActionBar);
      //  searchButton = rootView.findViewById(R.id.search);
        playlist_BigList =rootView.findViewById(R.id.choosePlaylist_listView);
        songIndicator = rootView.findViewById(R.id.circleIndicator);
        toolbar = rootView.findViewById(R.id.toolbar);
        setupToolbar();
        addToBeRipple(R.drawable.ripple_effect,songs_pieces_relative_header,playlist_pieces_relative);

        //setTimer();
    }
    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    private void SetAllClick() {
        View[] views = new View[]
                {  // put views here
                        baiHat,
                        songs_pieces_relative_header
              //          titleBar,
                    //    searchButton
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
                case R.id.baihat:

                case R.id.songs_piece_header_relative:
                    ((SupportFragmentPlusActivity) getActivity()).pushFragment(ShowMusicSongs.Initialize(getActivity()), true);
                    break;

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
        mainScreenFragment.setFrameLayoutNTransitionType(activity, SupportFragmentPlusActivity.TransitionType.FADE_IN_OUT);
        return mainScreenFragment;
    }
    private boolean initted = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
        MergeUI();
        SetAllClick();
        GetListMusic();
        ((BaseActivity)getActivity()).setMusicStateListenerListener(this);
        initted = true;
        return rootView;
    }

    private void GetListMusic() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
     //   Log.d("Permission Order","Reply Permission'");
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
    private void onPostSongs() {};
    private void onPostAlbums() {};
    private void doInBackground_LoadSongs() {};
    private void doInBackground_LoadAlbums() {};

    private void doStuff() {
        new loadPlaylists().execute();
        new loadSongs().execute();
        onTransitionComplete();
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

    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {
        if(mSongsListAdapter!=null) mSongsListAdapter.notifyDataSetChanged();
    }

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

    StickyScrollView scrollView;
    int titleColor = 0;

    private void AnimatedColorTitle() {
        if (scrollView == null) scrollView =  rootView.findViewById(R.id.myScroll);
        int yy = scrollView.getScrollY();

        if (yy < max_alpha) {
            alphaOfTitleBar = yy / 3;
            if (alphaOfTitleBar < 0) alphaOfTitleBar = 0;

        } else if (alphaOfTitleBar < max_alpha) {
            alphaOfTitleBar = max_alpha / 3;

        } else if (just_change_color) {
            just_change_color = false;
        }
        titleColor = BitmapEditor.mixTwoColors(Color.WHITE,0xff<<24| color24bit,1-alphaOfTitleBar/255.0f);

        stickyActionBar.setBackgroundColor(titleColor);
    }
    public static ArrayList<Song_OnLoad> DanhSachPhat = new ArrayList<Song_OnLoad>();

    public int NowPlaying_int;

    public static ArrayList<String> getData(ArrayList<Song_OnLoad> arrayList) {
        int len = arrayList.size();
        ArrayList<String> arrayList1 = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            arrayList1.add(arrayList.get(i).Data);
        }
        return arrayList1;
    }

    private boolean just_change_color = true;


    Random rnd = new Random();

    @Override
    public void onArtWorkChanged() {
    if(!initted) return;
     //   blur_background.setImageBitmap(getMainActivity().getBlurArtWork());
        int c = Tool.getSurfaceColor();
        applyRippleColor(c);
        main_slider.setBackgroundColor(Color.argb(150,Color.red(c),Color.green(c),Color.blue(c)));
        if(mSongsListAdapter!=null) mSongsListAdapter.notifyDataSetChanged();
        //((RippleDrawable)songs_pieces_relative_header.getBackground()).setColor(ColorStateList.valueOf(Tool.getSurfaceColor()));
    }

    private ImageView blur_background;
    private boolean settedBackground = false;

    private void ChangeBackground(int id) {
        // ảnh gốc
        Bitmap original = null;
        // ảnh mẫu
        Bitmap sample = null;
        // ảnh mẫu sau cập nhật
        Bitmap sample_update;
        // ảnh mẫu được làm mờ
        Bitmap sample_blur ;
        // đã từng cài background trước đó rồi
        settedBackground = true;
        // ImageView dùng để hiển thị ảnh blur

        // biến
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // địa chỉ của ảnh
        String albumArt_path = DanhSachPhat.get(id).AlbumArt_path;
        // Nếu địa chỉ khác null, rỗng và tồn tại
        if (albumArt_path != null && albumArt_path != "" && Tool.Path_Is_Exist(albumArt_path) == 2)
            try { // cố gắng lấy bức ảnh
                original = BitmapFactory.decodeFile(albumArt_path);
                options.inSampleSize = Tool.Avatar.getDevideSize(38, original);
                sample = BitmapFactory.decodeFile(albumArt_path, options);
            } catch (Exception e) {
            // nếu không lấy được thì lấy ảnh mặc định
                original = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);
                options.inSampleSize = Tool.Avatar.getDevideSize(38, original);
                sample = BitmapFactory.decodeResource(getResources(), R.drawable.default_image, options);
            }
            // phòng
        if (original == null)
            original = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);

        options.inSampleSize = Tool.Avatar.getDevideSize(38, original);
        if (sample == null)
            sample = BitmapFactory.decodeResource(getResources(), R.drawable.default_image, options);

        sample_update = updateSat(sample, 4);
        sample_blur = BitmapEditor.fastblur(sample_update, 1, 4);
        int[] averageColorRGB = BitmapEditor.getAverageColorRGB(sample_blur);
        black_theme = BitmapEditor.PerceivedBrightness(95, averageColorRGB);

        color24bit = (averageColorRGB[0] << 16 | averageColorRGB[1] << 8 | averageColorRGB[2]);
        Tool.setGlobalColor(0xff << 24 | color24bit);
        just_change_color = true;
        //   changeThemeColor();
        blur_background.setImageBitmap(sample_blur);
    }

    /*
    private void changeThemeColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(0x88000000 | color24bit);
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


    private int alphaOfTitleBar = 0;
    static boolean black_theme = false;
    /*
    Black is true, otherwise is white
     */
    public int color24bit = 255 << 16 | 255 << 8 | 255;

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
         * @param margin  desirable margin size in px between the views in the songListRecyclerView
         * @param columns number of columns of the RecyclerView
         */
        public RecyclerViewMargin(@IntRange(from = 0) int margin, @IntRange(from = 0) int columns) {
            this.margin = margin;
            this.columns = columns;

        }

        /**
         * Set different margins for the items inside the songListRecyclerView: no y margin for the first row
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
    private SmallPlaylistAdapter mPlaylistAdapter;
    private SmallSongListAdapter mSongsListAdapter;
    private class loadPlaylists extends  AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if(getActivity()!=null) mPlaylistAdapter = new SmallPlaylistAdapter(getActivity(),PlaylistLoader.getPlaylists(getActivity(),true),true);
            return "Executed";
        }
        @Override
        protected void onPostExecute(String result){
            playlistRecyclerView.setAdapter(mPlaylistAdapter);
            if(getActivity()!=null) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),1,LinearLayoutManager.HORIZONTAL,false);
                playlistRecyclerView.setLayoutManager(gridLayoutManager);
                OverScrollDecoratorHelper.setUpOverScroll(playlistRecyclerView,OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
            //    SnapHelper snapHelper = new LinearSnapHelper();
              //  snapHelper.attachToRecyclerView(playlistRecyclerView);
            }
        }
    }
    private boolean showAuto = true;
    private class loadSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                mSongsListAdapter = new SmallSongListAdapter((AppCompatActivity) getActivity(), SongLoader.getAllSongs(getActivity()), false, false);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            songListRecyclerView.setAdapter(mSongsListAdapter);
            if (getActivity() != null) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 6, LinearLayoutManager.HORIZONTAL, false);
               songListRecyclerView.setLayoutManager(gridLayoutManager);
                OverScrollDecoratorHelper.setUpOverScroll(songListRecyclerView,OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);
                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(songListRecyclerView);
                songIndicator.setRecyclerView(songListRecyclerView);

            }
        }

    }
    String[] long_press_menu_random_string = new String[] {"Play","Add","More"};
    int[] long_press_menu_random_image_id = new int[] {R.drawable.play,R.drawable.back,R.drawable.more_black};
    public BubbleMenuCenter.BubbleMenuViewListener bubbleMenuViewListener = new BubbleMenuCenter.BubbleMenuViewListener() {
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
        public void onReturnResult(String command, int result) {
            Tool.showToast(getActivity(),getStringCommand(command)[result],500);
        }
    };
    private void setAlbumArrayList_ListView() {

        playlist_BigList.setAdapter(new album_view_adapter());
        helper.getListViewSize(playlist_BigList);
        playlist_BigList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                //       Toast.makeText(Choose_Playlist.this,"I am ListView "+posTop+"th",Toast.LENGTH_SHORT).show();

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
            view = getActivity().getLayoutInflater().inflate(R.layout.item_songs_list, parent, false);
            final ImageView imageView =  view.findViewById(R.id.item_album_view_image);
            TextView titleView =  view.findViewById(R.id.item_album_view_title);
            TextView artistView = view.findViewById(R.id.item_album_view_aritst);
            final Album album = albumArrayList.get(position);
            imageView.setTag(new myTag(position));

            imageView.setImageBitmap(BitmapEditor.GetRoundedBitmapWithBlurShadow(getActivity(), BitmapEditor.getRoundedCornerBitmap(album.getBitmap(), 15), 25, 25, 25, 25, -6, 180, 12, 2));
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

     class MyBounceInterpolator implements android.view.animation.Interpolator {
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
                    if (BitmapEditor.TrueIfBitmapBigger(bitmap1, 300)) {
                        Log.d("Song", "bigger");
                        bitmap = BitmapEditor.getResizedBitmap(bitmap1, 300, 300);
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
