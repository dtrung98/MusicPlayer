package com.ldt.musicr.util;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.ldt.musicr.R;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static android.content.Context.VIBRATOR_SERVICE;

public class Tool {
    private static final String TAG="Tool";

    private static Tool tool;
    private Context context;

    public static int ColorOne = getMostCommonColor();
    public static int ColorTwo = getBaseColor();
    public static float AlphaOne = 1;
    public static float AlphaTwo = 1;

    public static void init(Context context) {
        if(tool==null) tool = new Tool();
        tool.context = context;
        Tool.getScreenSize(context);

    }

    public static void vibrate(Context context) {
        if(context==null) return;
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(50);
        }
    }

    public void destroy() {

        clear();
        context = null;
        tool = null;
    }
    public static Tool getInstance() {
        return tool;
    }

    private ArrayList<WallpaperChangedNotifier> notifiers = new ArrayList<>();
    private ArrayList<Boolean> CallFirstTime = new ArrayList<>();
    public void AddWallpaperChangedNotifier(WallpaperChangedNotifier notifier) {
        notifiers.add(notifier);
        CallFirstTime.add(false);
    }
    public void clear() {
        notifiers.clear();
    }
    public void remove(WallpaperChangedNotifier notifier) {
        notifiers.remove(notifier);
    }

    public interface WallpaperChangedNotifier {
        void onWallpaperChanged(Bitmap original, Bitmap blur) ;
    }

    private Bitmap originalWallPaper;
    private Bitmap blurWallPaper;
    private Bitmap getActiveWallPaper()
    {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        Bitmap bmp = ((BitmapDrawable)wallpaperDrawable).getBitmap();
        if(bmp.getWidth()>0) return bmp.copy(bmp.getConfig(),true);
        return Bitmap.createBitmap(150,150, Bitmap.Config.ARGB_8888);
    }

    private Bitmap blurWallBitmap() {
        return BitmapEditor.getBlurredWithGoodPerformance(context,originalWallPaper,1,12,1.6f);
    }
    /*private Bitmap getCropCenterScreenBitmap(Bitmap source_bitmap) {
        Rectangle rect_parent_in_bitmap = new Rectangle();
        float parentWidth = screenSize[0];
        float parentHeight = screenSize[1];
        float ratio_parent =  parentWidth/(parentHeight +0.0f);
        float ratio_source = source_bitmap.getWidth()/(source_bitmap.getHeight() +0.0f);

        if(ratio_parent> ratio_source) {
            // crop height of source
            rect_parent_in_bitmap.Width = source_bitmap.getWidth();
            rect_parent_in_bitmap.Height = (int) (rect_parent_in_bitmap.Width*parentHeight/parentWidth);

            rect_parent_in_bitmap.Left = 0;
            rect_parent_in_bitmap.Top = source_bitmap.getHeight()/2 - rect_parent_in_bitmap.Height/2;
        } else {
            // crop width of source
            // mean that
            rect_parent_in_bitmap.Height = source_bitmap.getHeight();
            rect_parent_in_bitmap.Width = (int) (rect_parent_in_bitmap.Height*parentWidth/parentHeight);

            rect_parent_in_bitmap.Top = 0;
            rect_parent_in_bitmap.Left = source_bitmap.getWidth()/2 - rect_parent_in_bitmap.Width/2;
        }
        Bitmap ret = Bitmap.createBitmap((int)parentWidth, (int) parentHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(source_bitmap,rect_parent_in_bitmap.getRectGraphic(),new Rect(0,0,screenSize[0],screenSize[1]),null);
        return ret;
    }*/


    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }


    private boolean mDarkWallpaper = false;
    private int mAverageColor = Color.WHITE;
    public int getAverageColor() {
        return mAverageColor;
    }
    public boolean isDarkWallpaper() {
        return mDarkWallpaper;
    }
    public static int getContrastVersionForColor(int color) {
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color),
                hsv);
        if (hsv[2] < 0.5) {
            hsv[2] = 0.7f;
        } else {
            hsv[2] = 0.3f;
        }
        hsv[1] = hsv[1] * 0.2f;
        return Color.HSVToColor(hsv);
    }


    private static int GlobalColor = 0xffff4081;
    private static int SurfaceColor =0xff007AFF;
    public static void setSurfaceColor(int globalColor) {
        SurfaceColor = ColorReferTo(globalColor);

    }

    /**
     *  A color get in 7 basic color, nearly the global color
     * @return a color which nearly the global color
     */
    public static boolean WHITE_TEXT_THEME = true;
    public static int getBaseColor() {
        return SurfaceColor;
    }
    public static void setMostCommonColor(int globalColor)
    {
        GlobalColor = globalColor;
    }

    /**
     * The most common color get from the art song.
     * @return integer value of color
     */
    public static int getMostCommonColor()
    {
        return GlobalColor;
    }

    public static void setOneDps(float width) {
        oneDPs =width;
        Log.d(TAG, "oneDps = " + oneDPs);
    }
    public static int getHeavyColor() {
        switch (SurfaceColor) {
            case 0xffFF3B30 : return 0xff770000;
            case 0xffFF9500 : return 0xff923C00;
            case 0xffFFCC00 : return  0xffAF8700;
            case 0xff4CD964 :return 0xff005800;
            case 0xff5AC8FA: return 0xff0058AA;
            case 0xff007AFF: return 0xff00218B;
            case 0xff5855D6: return 0xff162EA6;
            default: //0xffFB2C57
                return  0xffb60024;
        }
    }
    public static int getHeavyColor(int color_in7_basic) {
        switch (color_in7_basic) {
            case 0xffFF3B30 : return 0xff770000;
            case 0xffFF9500 : return 0xff923C00;
            case 0xffFFCC00 : return  0xff802D00;
            case 0xff4CD964 :return 0xff005800;
            case 0xff5AC8FA: return 0xff0058AA;
            case 0xff007AFF: return 0xff00218B;
            case 0xff5855D6: return 0xff162EA6;
            default: //0xffFB2C57
                return  0xffb60024;
        }
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
    public static int ColorReferTo(int cmc) {
        float[] hsv = new float[3];
        Color.colorToHSV(cmc, hsv);
        //     Log.d(hsv[0] + "|" + hsv[1] + "|" + hsv[2], "ColorMe");
        float alpha_7basic = hsv[1];
        float toEight = hsv[0] / 45 + 0.5f;
        if (toEight > 8 | toEight <= 1) return 0xffFF3B30;
        if (toEight <= 2) return 0xffFF9500;
        if (toEight <= 3) return 0xffFFCC00;
        if (toEight <= 4) return 0xff4CD964;
        if (toEight <= 5) return 0xff5AC8FA;
        if (toEight <= 6) return 0xff007AFF;
        if (toEight <= 7) return 0xff5855D6;
        return 0xffFF2D55;
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
    public static int getStatusHeight(Resources myR)
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
    public static float getPixelsFromDPs(Context activity, int dps){
        /*
            public abstract Resources getResources ()

                Return A Resources instance for your application's package.
        */
        Resources r = activity.getResources();

        /*
            TypedValue

                Container for A dynamically typed data value. Primarily
                used with Resources for holding resource values.
        */

        /*
            applyDimension(int unit, float value, DisplayMetrics metrics)

                Converts an unpacked complex data value holding
                A dimension to its final floating pp_point value.
        */

        /*
            Density-independent pixel (dp)

                A virtual pixel unit that you should use when defining UI layout,
                to express layout dimensions or posTop in A density-independent way.

                The density-independent pixel is equivalent to one physical pixel on
                A 160 dpi screen, which is the baseline density assumed by the system
                for A "medium" density screen. At runtime, the system transparently handles
                any scaling of the dp units, as necessary, based on the actual density
                of the screen in use. The conversion of dp units to screen pixels
                is simple: px = dp * (dpi / 160). For example, on A 240 dpi screen,
                1 dp equals 1.5 physical pixels. You should always use dp
                units when defining your application's UI, to ensure proper
                display of your UI on screens with different densities.
        */

        /*
            public static final int COMPLEX_UNIT_DIP

                TYPE_DIMENSION complex unit: Value is Device Independent Pixels.
                Constant Value: 1 (0x00000001)
        */
        return (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
    }
    public static float getOneDps(Context context)
    {
        if(oneDPs !=-1) return oneDPs;
        //      oneDPs = context.getResources().getDimensionPixelOffset(R.dimen.oneDP);
        oneDPs = getPixelsFromDPs(context,1);
        return oneDPs;
    }
    public static float oneDPs =-1;
    public static int getDpsFromPixel(Activity activity,int px) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    static int[] screenSize;
    static float[] screenSizeInDp;
    public static boolean HAD_GOT_SCREEN_SIZE = false;
    public static int[] getScreenSize(Context context)
    {
        if(!HAD_GOT_SCREEN_SIZE) {
            Point p = new Point();
            Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); // this will get the view of screen
            d.getRealSize(p);
            int width = p.x;
            int height = p.y;
            screenSize = new int[] {width,height};
            screenSizeInDp = new float[] {(width+0.0f)/getOneDps(context),(height+0.0f)/getOneDps(context)};
            HAD_GOT_SCREEN_SIZE = true;
        }
        return screenSize;
    }
    public static int[] getScreenSize(boolean sure) {
        return screenSize;
    }

    public static int[] getRefreshScreenSize(Context context)
    {
        HAD_GOT_SCREEN_SIZE = false;
        return getScreenSize(context);
    }

    public static float[] getScreenSizeInDp(Context context)
    {
        if(!HAD_GOT_SCREEN_SIZE) getScreenSize(context);
        return screenSizeInDp;
    }


    public static boolean hasSoftKeys(WindowManager windowManager){
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getNavigationHeight(Activity activity)
    {

        int navigationBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        if(!hasSoftKeys(activity.getWindowManager())) return 0;
        return  navigationBarHeight;
    }
    public static String convertByteArrayToString(byte[] b)
    {
        StringBuilder r = new StringBuilder();
        int len = b.length;
        for(int i=0;i<len;i++)
            if(i!=len-1) r.append(Integer.toHexString(b[i])).append(":");
            else r.append(b[i]);
        return r.toString();
    }
    public static void showToast(Context context,String text, int time)
    {
      final Toast toast;
      //toast =  Toasty.warning(context,text,time);
      toast = Toasty.custom(context,text, R.drawable.emoticon_excited,R.color.library_back_color,time,true,true);
     // toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL,0,0);
        toast.show();
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
    public static String getStringTagForView(View v){
        //   Log.d("Sticky","getStringTagForView");

        Object tagObject = v.getTag();
        return String.valueOf(tagObject);
    }

}