package com.ldt.NewDefinitionMusicApp.InternalTools;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.ldt.NewDefinitionMusicApp.R;

import java.io.File;

/**
 * Created by trung on 7/12/2017.
 */

public final class Tool {
    private static int GlobalColor= 0xFFFF4081;
    public static void setGlobalColor(int globalColor)
    {
        GlobalColor = globalColor;
    }
    public static int getGlobalColor()
    {
        return GlobalColor;
    }


    public static class Avatar {
        public static int getDevideSize(int sizeUWant, Bitmap original) {
            float sizeYouWant= sizeUWant;
              int original_width = original.getWidth();
               int original_height = original.getHeight();
              int sizeOriginal = (original_height < original_width) ? original_width : original_height; // lấy cái lớn hơn
            float devided = (sizeOriginal/sizeYouWant);
       //     System.out.printf(devided+" ");
            int i=1;
            while (true)
            {
                if(devided<i)
                    break;
                else i*=2;
            }
         //   System.out.printf(i+" ");
            if((devided-i/2)<(1/6.0f*i))
                i/=2;
            return i;
        }
    }
    public static int Path_Is_Exist(String dir_path)
    {
        File dir = new File(dir_path);
        if(!dir.exists()) return -1;
        if(dir.isDirectory()) return 1;
        if(dir.isFile()) return  2;
                return 0;
    }
    public static int StatusHeight = -1;
    public static  int getStatusHeight(Resources myR)
    {
        if(StatusHeight!=-1) return StatusHeight;
        int height;
        int idSbHeight = myR.getIdentifier("status_bar_height", "dimen", "android");
        if (idSbHeight > 0) {
            height = myR.getDimensionPixelOffset(idSbHeight);
            //   Toast.makeText(this, "Status Bar Height = "+ height, Toast.LENGTH_SHORT).show();
        } else {
            height = 0;
            //        Toast.makeText(this,"Resources NOT found",Toast.LENGTH_LONG).show();
        }
        StatusHeight =height;
        return StatusHeight;
    }
    public static int getPixelsFromDPs(Context activity, int dps){
        /*
            public abstract Resources getResources ()

                Return a Resources instance for your application's package.
        */
        Resources r = activity.getResources();

        /*
            TypedValue

                Container for a dynamically typed data value. Primarily
                used with Resources for holding resource values.
        */

        /*
            applyDimension(int unit, float value, DisplayMetrics metrics)

                Converts an unpacked complex data value holding
                a dimension to its final floating point value.
        */

        /*
            Density-independent pixel (dp)

                A virtual pixel unit that you should use when defining UI layout,
                to express layout dimensions or position in a density-independent way.

                The density-independent pixel is equivalent to one physical pixel on
                a 160 dpi screen, which is the baseline density assumed by the system
                for a "medium" density screen. At runtime, the system transparently handles
                any scaling of the dp units, as necessary, based on the actual density
                of the screen in use. The conversion of dp units to screen pixels
                is simple: px = dp * (dpi / 160). For example, on a 240 dpi screen,
                1 dp equals 1.5 physical pixels. You should always use dp
                units when defining your application's UI, to ensure proper
                display of your UI on screens with different densities.
        */

        /*
            public static final int COMPLEX_UNIT_DIP

                TYPE_DIMENSION complex unit: Value is Device Independent Pixels.
                Constant Value: 1 (0x00000001)
        */
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }
    public static int getOneDps(Context context)
    {
        if(oneDPs !=-1) return oneDPs;
        oneDPs = context.getResources().getDimensionPixelOffset(R.dimen.oneDP);
        return oneDPs;
    }
    public static int oneDPs =-1;
    public static int getDpsFromPixel(Activity activity,int px) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    static int[] screenSize;
    static float[] screenSizeInDp;
   public static boolean HAD_GET_SCREEN_SIZE = false;
    public static int[] getScreenSize(Context context)
    {
        if(!HAD_GET_SCREEN_SIZE) {
        Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); // this will get the view of screen
        int width = d.getWidth();
        int height = d.getHeight();
            screenSize = new int[] {width,height};
            screenSizeInDp = new float[] {(width+0.0f)/getOneDps(context),(height+0.0f)/getOneDps(context)};
            HAD_GET_SCREEN_SIZE = true;
    }
    return screenSize;
    }
    public static int[] getScreenSize(boolean sure) {
        return screenSize;
    }

    public static int[] getRefreshScreenSize(Context context)
    {
        HAD_GET_SCREEN_SIZE= false;
        return getScreenSize(context);
    }

    public static float[] getScreenSizeInDp(Context context)
    {
        if(!HAD_GET_SCREEN_SIZE) getScreenSize(context);
        return screenSizeInDp;
    }
    public static int getNavigationHeight(Activity activity)
    {
        int navigationBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return  navigationBarHeight;
    }
    public static String convertByteArrayToString(byte[] b)
    {
        String r ="";
        int len = b.length;
        for(int i=0;i<len;i++)
            if(i!=len-1) r+=Integer.toHexString(b[i])+":";
            else r+=b[i];
        return  r;
    }
    public static void showToast(Context context,String text, int time)
    {

        final Toast toast =   Toast.makeText(context,text,Toast.LENGTH_SHORT);
        toast.show();
     //   TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
      //  textView.setBackgroundColor(Color.WHITE);
        //textView.setTextColor(Color.BLACK);
     //   ((View)textView.getParent()).setBackground(R.drawable.corner_layout);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();

            }
        }, time);
    }
    private static boolean drawn = false;

    public static boolean isDrawn() {
        return drawn;
    }

    public static void setDrawn(boolean Drawn) {
        drawn = Drawn;
    }
    private static boolean splashGone = false;

    public static boolean isSplashGone() {
        return splashGone;
    }

    public static void setSplashGone(boolean splashGone) {
        Tool.splashGone = splashGone;
    }

}

