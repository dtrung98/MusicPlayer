package com.ldt.musicr.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.util.Animation;
import com.ldt.musicr.util.BitmapEditor;
import com.ldt.musicr.util.GradientResourses;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;


/**
 * Created by trung on 10/2/2017.
 */

public class SplashScreenView extends View {
    private static final String TAG = "SplashScreenView";
    private final int alphaPaint = 60;

    float eachDp=0;
    int upN=0;
    int upN2 =0;

    Paint backgroundCloudEffectPaint;
    Paint makeBackgroundSeeThroughPaint;
    Paint loadingPaint;
    Shader shader;
    Bitmap bmp;
    Canvas canvas_bmp;
    Bitmap icon;

    Bitmap icon_loading;
    Canvas canvas_icon_loading;
    Rect dstCrop4IconLoading;
    Rect dstRect;
    Rect srcBlur;
    int[] ScreenSize;
    final int time_delay1=1500;
    final int time_delay2= 2000;
    public SplashScreenView(Context context) {

        super(context);

    }

    public SplashScreenView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public SplashScreenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private LinearGradient getmLoadingShader()
    {
        GradientResourses.GradientColor g= GradientResourses.getRandomGradientColor();
        return new LinearGradient(0,widthOfIcon,widthOfIcon,0,g.colors,g.pos,
                Shader.TileMode.MIRROR);
    }
    private LinearGradient getmBackgroundShader()
    {
        GradientResourses.GradientColor g= GradientResourses.getRandomGradientColor();
        return new LinearGradient(0,0,ScreenSize[0],ScreenSize[1],g.colors,g.pos,
                Shader.TileMode.MIRROR);
    }

    float widthOfIcon=0;

    private void init()
    {
        eachDp = Tool.getOneDps(getContext());
        ScreenSize = Tool.getScreenSize(getContext());
        widthOfIcon = 75*eachDp;
  //     shader = new LinearGradient(0, 0, 0,800*eachDp /*canvas height*/, color1,  color2, Shader.TileMode.MIRROR /*or REPEAT*/);
        shader =getmBackgroundShader();
      //  shader = new LinearGradient(0,0,0,Tool.getScreenSize(getContext())[1],new int[]{0xff231557,0xff44107A,0xffFF1361,0xffFFF800},new float[]{0f,0.29f,0.67f,1f},Shader.TileMode.MIRROR );
       // background-image: linear-gradient(-225deg, #231557 0%, #44107A 29%, #FF1361 67%, #FFF800 100%);
       //        background-image: linear-gradient(to right, #eea2a2 0%, #bbc1bf 19%, #57c6e1 42%, #b49fda 79%, #7ac5d8 100%);
       backgroundCloudEffectPaint = new Paint();
       backgroundCloudEffectPaint.setAntiAlias(true);
        backgroundCloudEffectPaint.setShader(shader);
        backgroundCloudEffectPaint.setStyle(Paint.Style.FILL);
        backgroundCloudEffectPaint.setStrokeWidth(eachDp);
        backgroundCloudEffectPaint.setAlpha(alphaPaint);

        loadingPaint = new Paint();
        loadingPaint.setAntiAlias(true);
        loadingPaint.setShader(getmLoadingShader());
        loadingPaint.setStyle(Paint.Style.FILL);
       Bitmap iconTemp = ((BitmapDrawable)getResources().getDrawable(R.drawable.splash_icon_mini)).getBitmap();
       icon =  BitmapEditor.getResizedBitmap(iconTemp,(int) (75*eachDp),(int) (75*eachDp));
       iconTemp.recycle();

        icon_loading = Bitmap.createBitmap((int)(75*eachDp),(int)(75*eachDp), Bitmap.Config.ARGB_8888);
        canvas_icon_loading = new Canvas(icon_loading);
        dstCrop4IconLoading = new Rect(0,0,(int)(75*eachDp),(int)(75*eachDp));
        srcBlur = new Rect(0, 0, 150, 150);
        makeBackgroundSeeThroughPaint = new Paint();
        makeBackgroundSeeThroughPaint.setAntiAlias(true);
        makeBackgroundSeeThroughPaint.setColor(Color.BLACK);
        BitmapShader bitmapShader = new BitmapShader(icon, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        makeBackgroundSeeThroughPaint.setXfermode(new PorterDuffXfermode(BitmapEditor.getPorterMode(11)));
        makeBackgroundSeeThroughPaint.setStyle(Paint.Style.FILL);
        makeBackgroundSeeThroughPaint.setShader(bitmapShader);
        bmp = Bitmap.createBitmap(ScreenSize[0], ScreenSize[1], Bitmap.Config.ARGB_8888);
        dstRect = new Rect(0, 0, ScreenSize[0], ScreenSize[1]);
        canvas_bmp = new Canvas(bmp);
        path1 = new Path();
        path2 = new Path();
        value1 = ValueAnimator.ofInt((int)(-100*eachDp),(int)(100*eachDp));
        value1.setInterpolator(Animation.getInterpolator(3));
        value1.setDuration(time_delay1);
        value1.setRepeatMode(ValueAnimator.REVERSE);
        value1.setRepeatCount(ValueAnimator.INFINITE);
        value1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                upN = (int)animation.getAnimatedValue();
                invalidate();
            }
        });
       value1.start();

        value2 = ValueAnimator.ofInt((int)(-150*eachDp),(int)(150*eachDp));
        value2.setInterpolator(Animation.getInterpolator(4));
        value2.setDuration(time_delay2);
        value2.setRepeatMode(ValueAnimator.REVERSE);
        value2.setRepeatCount(ValueAnimator.INFINITE);
        value2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                upN2 = (int)animation.getAnimatedValue();

            }
        });
        value2.start();

        value_makeIconMoveLikeGradient = ValueAnimator.ofInt(0,(int)(4.0f*widthOfIcon));
        value_makeIconMoveLikeGradient.setInterpolator(Animation.getInterpolator(9));
        value_makeIconMoveLikeGradient.setDuration(time_delay2);
        value_makeIconMoveLikeGradient.setRepeatMode(ValueAnimator.RESTART);
        value_makeIconMoveLikeGradient.setRepeatCount(ValueAnimator.INFINITE);
        value_makeIconMoveLikeGradient.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveGradient = (int)animation.getAnimatedValue();

            }
        });
        value_makeIconMoveLikeGradient.start();

    }
    ValueAnimator value1,value2, value_makeIconMoveLikeGradient, value_makeSplashGone, value_makeIconGone;
    private final boolean iWant2BlurBackground = true;
    private float moveGradient = 0;
    private boolean onEnding = false;
    private void beforeEnd()
    {
        value_makeSplashGone = ValueAnimator.ofFloat(0,1);
        value_makeSplashGone.setDuration(300);
        value_makeSplashGone.setStartDelay(20);
        value_makeSplashGone.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float number = (float)animation.getAnimatedValue();
                SplashScreenView.this.setAlpha(1-number);
            }
        });
        value_makeSplashGone.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Tool.setSplashGone(true);
                ((ViewGroup)getParent()).removeView(SplashScreenView.this);
                value1.removeAllUpdateListeners();
                value1.cancel();
                value2.removeAllUpdateListeners();
                value2.cancel();  value1.removeAllUpdateListeners();
                value2.cancel();
                value_makeIconMoveLikeGradient.removeAllUpdateListeners();
                value_makeIconMoveLikeGradient.cancel();
                value1= null;
                value2 = null;
                value_makeIconMoveLikeGradient = null;

                value_makeSplashGone.cancel();
                value_makeSplashGone.removeAllUpdateListeners();
                value_makeSplashGone = null;
                value_makeIconGone.cancel();
                value_makeIconGone.removeAllUpdateListeners();
                value_makeIconGone = null;
                backgroundCloudEffectPaint.setShader(null);
                backgroundCloudEffectPaint.reset();
                backgroundCloudEffectPaint = null;
                loadingPaint.setShader(null);
                loadingPaint.reset();
                loadingPaint = null;
                bmp.recycle();
                icon.recycle();
                icon_loading.recycle();

                canvas_bmp = null;
                canvas_icon_loading = null;
                shader = null;
                dstRect = null;
                srcBlur = null;
                dstCrop4IconLoading = null;
                System.gc();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        value_makeIconGone = ValueAnimator.ofFloat(1,0);
        value_makeIconGone.setDuration(350);
        value_makeIconGone.setStartDelay(2000);
        value_makeIconGone.setInterpolator(Animation.getInterpolator(6));
        value_makeIconGone.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale_icon = (float)value_makeIconGone.getAnimatedValue();
            }
        });
        value_makeIconGone.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                value_makeSplashGone.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    float scale_icon = 1;
    private void onEnd()
    {
        if(onEnding) return;
        onEnding =true;
        beforeEnd();
        value_makeIconGone.start();
    }
    private boolean firstDraw = true;
    private void doFirstDraw() {
        if(!firstDraw) return;
        firstDraw = false;
      init();
    }
    protected void onDraw(Canvas canvas) {
     doFirstDraw();
        if(Tool.isDrawn()&&!onEnding) {
            onEnd();
        }
        canvas.drawColor(Color.BLACK);
        if(scale_icon>0) {
            canvas.save();
            canvas.translate(
                    ScreenSize[0] / 2 - 3 * widthOfIcon + moveGradient,
                    ScreenSize[1] / 2 - widthOfIcon / 2);
            float icon_scale = (scale_icon>1 )? scale_icon : 1 ;
            canvas.scale(
                    icon_scale,
                    icon_scale,
                    dstCrop4IconLoading.centerX(),
                    dstCrop4IconLoading.centerY());
            canvas.drawRect(
                    -2 * widthOfIcon,
                    0,
                    4 * widthOfIcon,
                    widthOfIcon,
                    loadingPaint);
            canvas.restore();
        }
        run_cloud_background();

        /*
         * drawVisualWave blur background or original background
         */
        if (iWant2BlurBackground) {
            Bitmap bitmap1 = BitmapEditor.getBlurredWithGoodPerformance(getContext(), bmp, 1, 25, 5);
            bmp.eraseColor(0);
            canvas_bmp.drawBitmap(bitmap1, srcBlur, dstRect, null);
            bitmap1.recycle();
        }

        make_background_with_through_icon();
       canvas.drawBitmap(bmp,0,0,null);
        /*
         * drawVisualWave icon loading
         */
    //    canvas.drawBitmap(icon_loading,dstRect.touch_event_X()-dstCrop4IconLoading.touch_event_X(),dstRect.centerY()- dstCrop4IconLoading.centerY(),null);

    }
    private void run_cloud_background()
    {
        bmp.eraseColor(0xfff0f0f0);
      //  bmp.eraseColor(0);
        drawMyPath1();
        drawMyPath2();
        canvas_bmp.drawPath(path1, backgroundCloudEffectPaint);
        canvas_bmp.drawPath(path2, backgroundCloudEffectPaint);

        // example drawVisualWave
     // canvas_bmp.drawRect(0,0,ScreenSize[0],ScreenSize[1],backgroundCloudEffectPaint);

    }
    Path path1,path2;
    private void make_background_with_through_icon()
    {
        if(scale_icon<=0) return;
        canvas_bmp.save();
        canvas_bmp.translate(
                dstRect.centerX() - dstCrop4IconLoading.centerX(),
                dstRect.centerY()- dstCrop4IconLoading.centerY());
        canvas_bmp.scale(scale_icon,scale_icon,dstCrop4IconLoading.centerX(),dstCrop4IconLoading.centerY());
        canvas_bmp.drawRect(
                0,
                0,
                dstCrop4IconLoading.right,
                dstCrop4IconLoading.bottom,
                makeBackgroundSeeThroughPaint);
        canvas_bmp.restore();
    }
    private void drawMyPath1()
    {
       path1.reset();
        path1.moveTo(100+upN/2,0-upN2);
        path1.quadTo(120,120,500+upN2,100-upN);
      //  data.moveTo(500,100);
        path1.quadTo(150-upN,150,0,500);
       // data.moveTo(0,500);
        path1.quadTo(100,150,100+upN,0);

    }
    private void drawMyPath2()
    {
      path2.reset();
        int w= ScreenSize[0],h = ScreenSize[1];
        path2.moveTo(w-100-upN2 ,h-0+upN);
        path2.quadTo(w-120,h-120 ,w-500+0.5f*upN2,upN+h-100);
        //  data.moveTo(500,100);
        path2.quadTo(w-150+upN,h-150,w-0-upN,h-500+upN2);
        // data.moveTo(0,500);
        path2.quadTo(w-100,h-150,w-100-upN,h-0);
    }

}
