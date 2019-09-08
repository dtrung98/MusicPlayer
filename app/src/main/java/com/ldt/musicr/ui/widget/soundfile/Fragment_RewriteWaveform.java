package com.ldt.musicr.ui.widget.soundfile;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.R;

/**
 * Created by trung on 9/14/2017.
 */

public class Fragment_RewriteWaveform extends Fragment {
    private View rootView;
    private View_RewriteWaveform rewriteWaveform;
    private void MergeUI() {
        rewriteWaveform = (View_RewriteWaveform)rootView.findViewById(R.id.waveform);
    }
    public void setMediaPath(String mediaPath)
    {
        // Get Property of file
     // call setMediaPath in WaveForm View
    }
    private void SetAllTouch() {
        View[] views = new View[]
                {  // put views here
                };

        for (View va : views) va.setOnTouchListener(OnTouch);
    }
    private View.OnTouchListener OnTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }

    };
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.waveform_fragment, container, false);
        MergeUI();
        SetAllTouch();
        return rootView;
    }

}

