package com.ldt.musicr.fragments.fragmentholder;

import android.os.Build;
import android.view.View;

import com.ldt.musicr.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by trung on 8/14/2017.
 */

public class Prepare4Fragment {
    private  static boolean InitID = false;
    private static ArrayList<Boolean> usingID =null ;
    private  static ArrayList<Integer> ID = null ;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int _generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
    private static void attachID()
    {
        ID  = new ArrayList<Integer>();
        ID.add(R.id.id1_withFrameLayout);
        ID.add(R.id.id2_withFrameLayout);
        ID.add(R.id.id3_withFrameLayout);
        ID.add(R.id.id4_withFrameLayout);
        ID.add(R.id.id5_withFrameLayout);
        ID.add(R.id.id6_withFrameLayout);
        ID.add(R.id.id7_withFrameLayout);
        ID.add(R.id.id8_withFrameLayout);
        ID.add(R.id.id9_withFrameLayout);
        ID.add(R.id.id10_withFrameLayout);
        ID.add(R.id.id11_withFrameLayout);
        ID.add(R.id.id12_withFrameLayout);
        ID.add(R.id.id13_withFrameLayout);
        ID.add(R.id.id14_withFrameLayout);
        ID.add(R.id.id15_withFrameLayout);
    }
    private static void attachBooleanID()
    {
        int len = ID.size();
        usingID = new ArrayList<>();
        for(int i=0;i<len;i++)
            usingID.add(false);
    }
    private static void INIT_ID()
    {
        if(!InitID) {
            InitID = true;
            attachID();
            attachBooleanID();
        }
    }
    private static int generatedNAddNewID()
    {
        int newID;
        // Generated new ID
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {newID=_generateViewId();}
        else {
            newID = View.generateViewId();
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ID.add(newID);
        usingID.add(true);
        return newID;
    }
    public static int getId()
    {
        INIT_ID();
        int len = ID.size();
        for(int i=0;i<len;i++)
            if(!usingID.get(i)) return useThisID(i);
        return generatedNAddNewID();
    }

    private static int useThisID(int i)
    {
        usingID.set(i,true);
        return ID.get(i);
    }
    public static void unUseThisID(int id)
    {
        int len = ID.size();
        for(int i=0;i<len;i++)
            if(id==ID.get(i)) usingID.set(i,false);
    }
}
