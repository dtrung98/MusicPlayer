package com.ldt.musicr.views.EffectView;

import android.graphics.Canvas;
import android.widget.ImageView;
/*
Interface định nghĩa cách hành xử của các icon động
 */
public interface AnimateDrawable {
    void draw(Canvas canvas); // truyền vào canvas để vẽ icon lên
    void invalidate(); // kêu gọi view cha vẽ lại
    void setIconView(ImageView iconView); // ???
    void destroy(); // hủy icon, dùng khi free bộ nhớ
    boolean sync(boolean b);
    int whichSelect();
    void set_source(float x, float y, float w, float h);
}
