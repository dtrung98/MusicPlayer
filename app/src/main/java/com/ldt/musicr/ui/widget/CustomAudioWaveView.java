package com.ldt.musicr.ui.widget;

import android.graphics.Color;


import com.ldt.musicr.ui.widget.soundfile.Segment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by trung on 9/5/2017.
 */

public class CustomAudioWaveView extends WaveformFragment {

    /**
     * Provide data to your audio file.
     *
     * @return
     */
    @Override
    protected String getFileName() {
        return audioPath;
    }
    String audioPath;
public static CustomAudioWaveView Initialize(String AudioPath)
   {
     CustomAudioWaveView cawv = new CustomAudioWaveView();
     cawv.audioPath = AudioPath;
     return cawv;
   }

    /**
     * Optional - provide list of segments (start and stop values in seconds) and their corresponding colors
     *
     * @return
     */
    @Override
    protected List<Segment> getSegments() {
        return Arrays.asList(
                new Segment(55.2, 55.8, Color.rgb(238, 23, 104)),
                new Segment(56.2, 56.6, Color.rgb(238, 23, 104)),
                new Segment(58.4, 59.9, Color.rgb(184, 92, 184)));
    }

}