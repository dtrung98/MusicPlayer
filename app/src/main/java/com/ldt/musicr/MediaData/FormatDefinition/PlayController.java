package com.ldt.musicr.MediaData.FormatDefinition;

import android.util.Log;

import com.ldt.musicr.fragments.MainScreenFragment;
import com.ldt.musicr.MediaData.Song_OnLoad;

import java.util.ArrayList;

/*
 * Created by trung on 7/24/2017.
 */

public class PlayController {
    private int playing = 0;
    public ArrayList<String> list;
    public  PlayController()
    {
        list = new ArrayList<>();
    }
    public void setList(ArrayList<Song_OnLoad> theSongs) {
        Log.d("setList Service","Service Log");
        list= MainScreenFragment.getData(theSongs);
    }
    public void setData(ArrayList<String> theDataSongs)
    {
        Log.d("setList Service","Service Log");
        list = theDataSongs;
    }
    public  void setPosPlaying(int position)
    {
        if(position<list.size()) playing=position;
    }
    public String getSongs(int pos)
    {
        if(pos<list.size()) return  list.get(pos);
        else if(list.size()!=0) return  list.get(list.size()-1);
        return null;
    }
    public String getNowPlaying()
    {
        return list.get(playing);
    }
    public int Next()
    {
        if(playing!=list.size()-1) {
            playing++;
            return  playing;
        }
        return playing;
    }
    public int Prev() {
        if (playing != 0) {
            playing--;
            return  playing;
        }
        return 0;
    }
}
