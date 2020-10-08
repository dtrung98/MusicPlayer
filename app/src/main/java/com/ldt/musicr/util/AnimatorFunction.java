package com.ldt.musicr.util;

/**
 * Created by trung on 7/19/2017.
 */

public class AnimatorFunction {
    public static float easeInOutQuad(float current,float start_value,float change_in_value,float duration) {
        current /= duration/2;
        if (current < 1) return change_in_value/2*current*current + start_value;
        current--;
        return -change_in_value/2 * (current*(current-2) - 1) + start_value;
    }

    public static float easeOutQuad(float t,float b,float c,float d) {
        t /= d;
        return -c * t*(t-2) + b;
    }

    public static float easeInOutExpo(float t,float b,float c,float d) {
        t /= d/2;
        if (t < 1) return (float)( c/2 * Math.pow( 2, 10 * (t - 1) ) + b);
        t--;
        return (float)(c/2 * ( -Math.pow( 2, -10 * t) + 2 ) + b);
    }

}
