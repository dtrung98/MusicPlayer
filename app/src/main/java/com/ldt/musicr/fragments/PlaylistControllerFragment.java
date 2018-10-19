package com.ldt.musicr.fragments;

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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldt.musicr.InternalTools.BitmapEditor;
import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.MediaData.Song_OnLoad;
import com.ldt.musicr.R;
import com.ldt.musicr.views.BlurringView;
import com.ldt.musicr.views.DarkenRoundedBackgroundFrameLayout;

import java.util.Random;

/**
 * Created by trung on 7/17/2017.
 */

public class PlaylistControllerFragment extends BaseTabLayerFragment implements  View.OnTouchListener{
    static final String TAG = "PlaylistControllerFragment";
    public FragmentPlus.StatusTheme statusTheme = FragmentPlus.StatusTheme.BlackIcon;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return tabLayerController.streamOnTouchEvent(TAG,view,event);
    }
    private int moveTo=0;
    private BlurringView blurringView;
    DarkenRoundedBackgroundFrameLayout rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
         rootView = (DarkenRoundedBackgroundFrameLayout) inflater.inflate(R.layout.playlist_controller_fragment,container,false);

         //v.setOnTouchListener(this);
        return rootView;
    }
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
    private String getSongTitle(Song_OnLoad song)
    {
        if(song==null||song.Title==null|| song.Title.equals(""))
            return "Unnamed Song";
        return song.Title;
    }
    private String getSongArtist(Song_OnLoad song)
    {
        if(song==null||song.Artist==null||song.Artist==""||song.Artist.contains("<unknown>"))
            return "Unknown Artist";
        return song.Artist;
    }

    private void Log_A_Song(Song_OnLoad song)
    {
        Log.d("Song", "Title = ["+song.Title+"]"+", Artist = ["+song.Artist+"], Image = ["+((song.AlbumArt_path==null||song.AlbumArt_path=="")? "null]" : "vaild]"));
    }
    private void setInformationPlayer(Song_OnLoad song)
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



    @Override
    public void onUpdateLayer(TabLayerController.Attr attr, float pcOnTopLayer, int active_i) {
        // Todo : update layout
        if(active_i==0) rootView.setRoundNumber(attr.getPc(),true);
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
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public MarginValue minPosition() {
        return MarginValue.BELOW_NAVIGATION;
    }

    @Override
    public String tag() {
        return TAG;
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {

    }

    @Override
    public void onArtWorkChanged() {

    }
}
