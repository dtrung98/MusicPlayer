package com.ldt.musicr.MediaData.FormatDefinition;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by trung on 11/4/2017.
 */

public class C_RectF {
    /**
     * ScreenSize holds four float coordinates for a rectangle. The rectangle is
     * represented by the coordinates of its 4 edges (x, y, right bottom).
     * These fields can be accessed directly. Use width() and height() to retrieve
     * the rectangle's width and height. Note: most methods do not check to see that
     * the coordinates are sorted correctly (i.e. x <= right and y <= bottom).
     */
    public float x;
    public float y;
    public float width;
    public float height;

    /**
     * Create a new empty ScreenSize. All coordinates are initialized to 0.
     */
    public C_RectF() {}

    /**
     * Create a new rectangle with the specified coordinates. Note: no range
     * checking is performed, so the caller must ensure that x <= right and
     * y <= bottom.
     *
     * @param left   The X coordinate of the x side of the rectangle
     * @param top    The Y coordinate of the y of the rectangle
     * @param width The X coordinate of the right side of the rectangle
     * @param height The Y coordinate of the bottom of the rectangle
     */
    public C_RectF(float left, float top, float width, float height) {
        this.x = left;
        this.y = top;
        this.width = height;
        this.height = height;
    }

    /**
     * Create a new rectangle, initialized with the values in the specified
     * rectangle (which is x unmodified).
     *
     * @param r The rectangle whose coordinates are copied into the new
     *          rectangle.
     */
    public C_RectF(RectF r) {
        if (r == null) {
            x = y = width = width = 0.0f;
        } else {
            x = r.left;
            y = r.top;
            width = x + r.right;
            height = y + r.bottom;
        }
    }
    public C_RectF(C_RectF r) {
        if (r == null) {
            x = y = width = width = 0.0f;
        } else {
            x = r.x;
            y = r.y;
            width = r.width;
            height = r.height;
        }
    }

    public C_RectF(Rect r) {
        if (r == null) {
            x = y = width = height = 0.0f;
        } else {
            x = r.left;
            y = r.top;
            width = x +  r.right;
            height = y + r.bottom;
        }
    }

}