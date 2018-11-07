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
import com.ldt.musicr.activities.BaseActivity;
import com.ldt.musicr.activities.MainActivity;
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
    DarkenRoundedBackgroundFrameLayout rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
         rootView = (DarkenRoundedBackgroundFrameLayout) inflater.inflate(R.layout.playlist_controller_fragment,container,false);
        ((BaseActivity)getActivity()).setMusicStateListenerListener(this);
         //v.setOnTouchListener(this);
        return rootView;
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
