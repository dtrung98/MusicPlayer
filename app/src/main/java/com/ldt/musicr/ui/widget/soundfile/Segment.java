package com.ldt.musicr.ui.widget.soundfile;

/**
 * Created by trung on 9/9/2017.
 */

public class Segment {
    private final Double start;
    private final Double stop;
    private final int color;

    public Segment(final Double start, final Double stop, final int color) {
        this.start = start;
        this.stop = stop;
        this.color = color;
    }

    public final Double getStart() {
        return start;
    }

    public final Double getStop() {
        return stop;
    }

    public final int getColor() {
        return color;
    }
}