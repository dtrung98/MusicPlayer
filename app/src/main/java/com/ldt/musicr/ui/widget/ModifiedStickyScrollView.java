package com.ldt.musicr.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.ldt.musicr.util.Tool;

import java.util.ArrayList;

/**
 * Created by trung on 0023 23 Dec  2017.
 */

public class ModifiedStickyScrollView extends ScrollView {
    /**
     * Tag for views that should stick and have constant drawing. e.g. TextViews, ImageViews etc
     */
    public static final String STICKY_TAG = "sticky";
    public ModifiedStickyScrollView(Context context) {
        super(context);
    }

    public ModifiedStickyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModifiedStickyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public static class StickyStruct {
        public View view;
        public int posTop,posBot;
        public StickyStruct(View v) {
            view = v;

         //   posTop = pos;
          //  posBot = bot;
        }
    }



    private ArrayList<StickyStruct> stickyList;
    private void add2StickyList(View v) {
        stickyList.add(new StickyStruct(v));
    }
    private void init() {
        stickyList = new ArrayList<>();
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
     //   doTheStickyThing(); //  tìm vị trí các sticky, sticky hiện hành ...
      //  customTitleBar.connectOnScrollChanged(l,t,oldl,oldt); //  kết nối với title bar ?
    }
    @Override
    public void addView(View child) {
        Log.d("Sticky","addView");

        super.addView(child);
        findStickyViews(child);
    }
        //////////////////////////////////////////////////////////////////////////////////////////
      //  Mỗi khi có bất cứ View nào được thêm vào, đều xem xem nó có sticky hay ko

    @Override
    public void addView(View child, int index) {
        Log.d("Sticky","addView on index "+index);

        super.addView(child, index);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        Log.d("Sticky","addView on index "+index+", params");
        super.addView(child, index, params);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, int width, int height) {
        Log.d("Sticky","addView with width ="+width+", height = "+height);

        super.addView(child, width, height);
        findStickyViews(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        Log.d("Sticky","addView with params");
        super.addView(child, params);
        findStickyViews(child);
    }

      ///  Hết phần kiểm tra thêm view.
    /////////////////////////////////////////////////////////////////////////////////////////


    private void findStickyViews(View v) {
        Log.d("Sticky","findStickyViews");

        if(v instanceof ViewGroup){
            ViewGroup vg = (ViewGroup)v;
            for(int i = 0 ; i<vg.getChildCount() ; i++){
                String tag = Tool.getStringTagForView(vg.getChildAt(i));
                if(tag!=null && tag.contains(STICKY_TAG)){
                    add2StickyList(vg.getChildAt(i));
                }else if(vg.getChildAt(i) instanceof ViewGroup){
                    findStickyViews(vg.getChildAt(i));
                }
            }
        } else {
            String tag = (String) v.getTag();
            if(tag!=null && tag.contains(STICKY_TAG)){
                add2StickyList(v);
            }
        }
    }

}
