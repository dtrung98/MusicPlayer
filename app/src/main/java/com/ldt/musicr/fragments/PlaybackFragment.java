package com.ldt.musicr.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldt.musicr.InternalTools.ImageEditor;
import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.activities.MainActivity;
import com.ldt.musicr.MediaData.Song_onload;
import com.ldt.musicr.R;
import com.ldt.musicr.views.BlurringView;
import com.ldt.musicr.views.SeeThroughFrameLayout;

import java.util.Random;

/**
 * Created by trung on 7/17/2017.
 */

public class PlaybackFragment extends Fragment implements  View.OnTouchListener{
    public FragmentPlus.StatusTheme statusTheme = FragmentPlus.StatusTheme.BlackIcon;
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        ((MainActivity)getActivity()).onTouchToggle_Playlist.onTouch(view,event);
        return true;
    }
    private int moveTo=0;
    private BlurringView blurringView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
         final View v = inflater.inflate(R.layout.fragment_manage,container,false);
         SeeThroughFrameLayout stf = (SeeThroughFrameLayout)v.findViewById(R.id.see_throughFrameLayout);
         stf.applyDrawableView = (ImageView) v.findViewById(R.id.image_sample);
         v.setOnTouchListener(this);
        return v;
    }
    private AbsoluteLayout rootView;
    private ImageView imv(int id)
    {
        return (ImageView) getActivity().findViewById(id);
    }
    private ImageView imv(View v,int id)
    {
        return (ImageView)v.findViewById(id);
    }
    private LinearLayout lnr(int id)
    {
        return (LinearLayout)getActivity().findViewById(id);
    }


    private void line(String Tag,int i)
    {
        Log.d(Tag,"Line "+i);
    }
    private TextView getTextView(int id)
    {
        return (TextView)getActivity().findViewById(id);
    }
    private String getSongTitle(Song_onload song)
    {
        if(song==null||song.Title==null||song.Title=="")
            return "Unnamed Song";
        return song.Title;
    }
    private String getSongArtist(Song_onload song)
    {
        if(song==null||song.Artist==null||song.Artist==""||song.Artist.contains("<unknown>"))
            return "Unknown Artist";
        return song.Artist;
    }

    private void Log_A_Song(Song_onload song)
    {
        Log.d("Song", "Title = ["+song.Title+"]"+", Artist = ["+song.Artist+"], Image = ["+((song.AlbumArt_path==null||song.AlbumArt_path=="")? "null]" : "vaild]"));
    }
    private void setInformationPlayer(Song_onload song)
    {

        TextView Text_view_Song_Title = getTextView(R.id.player_song_title_f);
        TextView Text_view_Song_Artist = getTextView(R.id.player_song_artist_f);

        Text_view_Song_Title.setText(getSongTitle(song));
        Text_view_Song_Artist.setText(getSongArtist(song));
    }
    Random rnd = new Random();
    public static int position_song=0;
    private boolean black_theme=false;
    private Bitmap avatar_bitmap;
    private FrameLayout frame(int id)
    {
        return  (FrameLayout)getActivity().findViewById(id);
    }
    private View.OnClickListener avatarOnclick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            if(MainScreenFragment.DanhSachPhat.size()==0) return;

            final BitmapFactory.Options options = new BitmapFactory.Options();
            int number = rnd.nextInt(MainScreenFragment.DanhSachPhat.size());
            position_song=number;
            Song_onload song = MainScreenFragment.DanhSachPhat.get(number);
            Log_A_Song(song);
            setInformationPlayer(song);

            // get and set avatar image
            if (avatar_bitmap != null) avatar_bitmap.recycle();
            Bitmap original=null;
            Bitmap sample = null;
            String albumPath= song.AlbumArt_path;
            line("Song",1);
            if(albumPath!=null&&albumPath!=""&& Tool.Path_Is_Exist(albumPath)==2)
                try {
                    line("Song",2);
                    original = BitmapFactory.decodeFile(albumPath);
                    options.inSampleSize=Tool.Avatar.getDevideSize(38,original);
                    sample = BitmapFactory.decodeFile(albumPath, options);
                }
                catch (Exception e) {
                    line("Song",3);
                    original = BitmapFactory.decodeResource(getResources(),R.drawable.default_image);
                    options.inSampleSize=Tool.Avatar.getDevideSize(38,original);
                    sample = BitmapFactory.decodeResource(getResources(),R.drawable.default_image, options);

                }
            line("Song",4);
            if(original==null)  {line("Song",41); original = BitmapFactory.decodeResource(getResources(),R.drawable.default_image); }
            options.inSampleSize=Tool.Avatar.getDevideSize(38,original);
            Log.d("Song","Original Size = ["+original.getWidth()+"x"+original.getHeight()+"], Sameple Devide = "+ options.inSampleSize);
            if(sample==null)  {line("Song",42);  sample = BitmapFactory.decodeResource(getResources(),R.drawable.default_image,options); }
            Log.d("Song","Sample Size = ["+sample.getWidth()+"x"+sample.getHeight()+"]");

            line("Song",5);
            Bitmap update_sar = ImageEditor.updateSat(original, 1f);
            line("Song",6);
            original.recycle();
            avatar_bitmap = update_sar;
            Bitmap sample_update = ImageEditor.updateSat(sample, 3);

            Bitmap blur_sample = ImageEditor.fastblur(sample_update, 1, 6);
            int[] colorr = ImageEditor.getAverageColorRGB(blur_sample);
            int colorr_int = Color.rgb(colorr[0], colorr[1], colorr[2]);
            lnr(R.id.lnr_z1).setBackgroundColor(colorr_int);
            sample.recycle();
            sample = null;
            sample_update.recycle();
            blur_sample.recycle();
            imv(R.id.album_art_f).setImageBitmap(avatar_bitmap);
            if (Build.VERSION.SDK_INT >= 21) {
                rootView = (AbsoluteLayout) getActivity().findViewById(R.id.rootView_f);
                //   getWindow().setNavigationBarColor(Color.rgb(colorr[0], colorr[1], colorr[2]));
                //    hideSystemUI();
            }
            if(Build.VERSION.SDK_INT>=19)
            {
                black_theme = ImageEditor.PerceivedBrightness(105,colorr);
                if(black_theme) {
                    rootView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                    //      | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    );
                    ((ImageButton)getActivity().findViewById(R.id.image_down_player_f)).setImageResource(R.drawable.down_white);
                }
                else {
                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            //      | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
                    ((ImageButton)getActivity().findViewById(R.id.image_down_player_f)).setImageResource(R.drawable.down_player);

                }
            }
        }

    };


}
