package com.ldt.NewDefinitionMusicApp.MediaData.FormatDefinition;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by trung on 11/4/2017.
 */

public class C_PointF extends PointF {
   public C_PointF getPointAround(float distance, float degree) {
       degree = -degree + 90;
        C_PointF p = new C_PointF();
        p.x = (float)(x + distance*Math.cos(Math.toRadians(degree)));
        p.y = (float)( y - distance*Math.sin(Math.toRadians(degree)));
        Log.d("C_PointF","touch : (x = "+ x+", y = "+y+" ), distance = " + distance);
       Log.d("C_PointF" ,"degree: " +degree+", p.x = "+ p.x+", p.y = "+ p.y);

        return  p;
    }

    public float calculateDegree(C_PointF point) {
       return (float) getAngle(point);
    }
    /**
     * Fetches angle relative to screen centre point
     * where 3 O'Clock is 0 and 12 O'Clock is 270 degrees
     *
     * @param screenPoint
     * @return angle in degress from 0-360.
     */
    private double getAngle(C_PointF screenPoint) {
        double dx = screenPoint.x - x;
        // Minus to correct for coord re-mapping
        double dy = -(screenPoint.y + y);

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return Math.toDegrees(inRads);
    }
    private C_PointF() {

    }
    public C_PointF(float x, float y)   {
        this.x = x;
        this.y = y;
    }

    public C_PointF(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

}
