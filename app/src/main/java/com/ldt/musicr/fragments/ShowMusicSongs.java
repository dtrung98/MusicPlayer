package com.ldt.musicr.fragments;


import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.MediaData.FormatDefinition.SimpleSong;
import com.ldt.musicr.MediaData.MS_AudioField;
import com.ldt.musicr.MediaData.MediaLoader;
import com.ldt.musicr.activities.SupportFragmentPlusActivity;
import com.ldt.musicr.activities.MainActivity;
import com.ldt.musicr.R;

import java.time.format.TextStyle;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowMusicSongs#Initialize(Activity )} (ContentResolver)} factory method to
 * create an instance of this fragment.
 */
public class ShowMusicSongs extends FragmentPlus {
    private ContentResolver CR;
    public void setCR(ContentResolver contentResolver)
    {
        CR= contentResolver;
    }

    public static ShowMusicSongs Initialize(Activity activity)
    {
        ShowMusicSongs fragment = new ShowMusicSongs();
        fragment.setFrameLayoutNTransitionType(activity, SupportFragmentPlusActivity.TransitionType.RIGHT_LEFT);
        // The only need for the ShowMusicSongs Fragment is the contentResolver
        //fragment.setCR(activity.getContentResolver());

        return fragment;
    }

    private BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return MediaLoader.simpleSongs.size();
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
        public View getView(int position, View view, ViewGroup parent) {
                view = new LinearLayout(getActivity());
                LinearLayout l_cast = (LinearLayout) view;

                l_cast.setOrientation(LinearLayout.VERTICAL);
                l_cast.setPadding((int) Tool.getPixelsFromDPs(getActivity(), 10), 0, (int) Tool.getPixelsFromDPs(getActivity(), 10), 0);
                ArrayList<String> ar = getValueFromProperty(MediaLoader.simpleSongs.get(position));

                int len = ar.size();
                for (int i = 0; i < len; i++) {
                    TextView t = new TextView(getActivity());

                    t.setTextColor(0xff000000);
                    //   t.setTextSize(label_AlbumList.getTextSize());
                    String text = ar.get(i);
                    if (i == 0)
                    {
                        t.setText(position + " - " + text);
                        t.setTypeface(t.getTypeface(),Typeface.BOLD);
                    }
                    else t.setText(text);
                    l_cast.addView(t);
                    View line = new View(getActivity());

                    line.setBackgroundColor(0xffeeeeee);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Tool.oneDPs);
                    l_cast.addView(line, params);

                }
                View line = new View(getActivity());

                line.setBackgroundColor(0xff444444);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Tool.oneDPs);
                l_cast.addView(line, params);

            return view;
        }
    };
  private ArrayList<String> getValueFromProperty(SimpleSong simpleSong)
  {
      int whichProperty = simpleSong.getWhichSong();
      ArrayList<String> list = new ArrayList<>();
      int len = MS_AudioField.songFields.size();
      for(int i=0;i<len;i++)
          list.add(MediaLoader.getStringFromStringKey(whichProperty,MS_AudioField.songFields.get(i).getName()));
      return list;
  }

    ListView listView;
    TextView label_AlbumList;
     float textSizeOfLabel;
    Button button_click2Show;
    private void MergerUi()
    {
        listView = (ListView) rootView.findViewById(R.id.show_music_song_listView);
        label_AlbumList = (TextView) rootView.findViewById(R.id.show_music_song_label_albumList);
        textSizeOfLabel = label_AlbumList.getTextSize();
        button_click2Show = (Button) rootView.findViewById(R.id.show_music_song_button_click2Show);
    }
    private void SetAllClick()
    {
        View[] views = new View[]
                {  // put views here
                        button_click2Show,
                        label_AlbumList
                };

    setOnClick(Onclick,views);
    }
    private void setOnClick(View.OnClickListener onclick,View[] v)
    {
        for (View aV : v) aV.setOnClickListener(onclick);
    }
    private View.OnClickListener Onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id)
            {
                case R.id.show_music_song_button_click2Show :
              //      FadeInFadeOutTransition.AddFragmentAndTransform((SupportFragmentPlusActivity) getActivity(),ShowMusicSongs.Initialize(getActivity()),getFrameLayout());return;
                case R.id.show_music_song_label_albumList : hideThisFragment();return;
            }
        }
    };



    private void hideThisFragment()
    {
        MainActivity mA = (MainActivity) getActivity();
        mA.pushFragment(SeeThroughFragment.Initialize(mA),true);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onTransitionComplete()
    {
        Completed = true;
        MergerUi();
        SetAllClick();
   ClickToShowAlbumLoader();
    }

    @Override
    public StatusTheme setDefaultStatusTheme() {
       return StatusTheme.BlackIcon;
    }

    @Override
    public void applyFitWindow_Top() {
        View v =rootView.findViewById(R.id.show_music_song_firstLinearLayout);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)v.getLayoutParams();
        params.topMargin= FitWindow_Top;
        v.setLayoutParams(params);

    }
    private boolean Completed =false;

    private void ClickToShowAlbumLoader()
    {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
              //  MediaLoader.StartToRefresh(CR);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                label_AlbumList.setText("Hide This Fragment ("+MediaLoader.simpleSongs.size()+")");
         listView.setAdapter(baseAdapter);
            }
        }.execute();

        // show Album by a ListView
        //    helper.getListViewSize(listView);
        //    Toast.makeText(getActivity(),"Run completely, there are "+albumList.size()+" album !",Toast.LENGTH_SHORT).show();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_show_music_song, container, false);
        return rootView;
    }


}
