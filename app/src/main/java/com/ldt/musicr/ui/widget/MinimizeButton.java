package com.ldt.musicr.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ldt.musicr.util.uitool.Animation;
import com.ldt.musicr.util.uitool.BitmapEditor;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.fragments.MinimizePlaySwitcher;

/**
 * TODO: document your custom view class.
 */
public class MinimizeButton extends View {
private final String TAG = "MinimizeButton";
    public MinimizeButton(Context context) {
        super(context);

    }

    public MinimizeButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public MinimizeButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    private MinimizePlaySwitcher.PlayControlListener listener;
    public void setProperties(MinimizePlaySwitcher.PlayControlListener listener) {
        this.listener = listener;
       progressColor = listener.getOutlineColor();
        backgroundColor = listener.getBackColor();
        bitmap = listener.getOriginalBitmap();

        ImageView imageView = listener.getCurrentView();
       // storageBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        beginRoundAva = listener.getCurrentRadius();
        navigationHeight = listener.getNavigationHeight();
        barHeight = listener.getBarHeight();

       // width = listener.getBarWidth();
        init();
    }

    private void init() {
        oneDp = Tool.getOneDps(getContext());

        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setColor(backgroundColor);
        backPaint.setStyle(Paint.Style.FILL);
    }
    float pc=0;
    float pc_round_ava = 0;
    // Run animation minimize button
    // Minimize
   // public static int count =0;
    public void switchOn() {

        //if(count!=28) count ++;
     //   else count=0;
        // Log.d(TAG,"Interpolator : "+count);

        ValueAnimator va = ValueAnimator.ofFloat(0,1);
        va.setInterpolator(Animation.getEasingInterpolator(2)); // 2
        va.setDuration(400);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pc = (float)animation.getAnimatedValue();
              // Log.d(TAG, "AnimatedFraction = "+ animation.getAnimatedFraction());
                calculating();
                invalidate();
            }
        });
        va.start();
    }

    // Normalize
    public void switchOff(MinimizePlaySwitcher mps) {
        ValueAnimator va = ValueAnimator.ofFloat(0,1);
        va.setInterpolator(Animation.getEasingInterpolator(20));
        va.setDuration(450);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pc = 1- (float)animation.getAnimatedValue();
                calculating();
                invalidate();
            }
        });

        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
               mps.rootLayout.removeView(mps.mButton);
               mps.listener.finishSwitchNormal();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.start();

    }
   private float oneDp;

    private int backgroundColor; //  màu background của view

    private int progressColor; // màu của thanh progress
    private float progressPercent; // giá trị phần trăm của thanh progress
    // Bức hình avatar gốc, không bo goc
    private Bitmap bitmap;
    // Bức hình avatar hiển thị trên thanh bar trước khi minimize
    private Bitmap shownBitmap;


    private int navigationHeight = 0;
    private int height = 0;
    private int width = 0;
    // chiều cao của thanh lúc  bình thường
    int barHeight = 0;
    private Paint backPaint;
    private Paint progressPaint;
    public float endLeftBack;
    private void calculating() {
        if(width==0) {
            width = getWidth();
            height = getHeight();
            beginTopBack = height- barHeight - navigationHeight;
        }
        // lề phải của button là 2dp = lePhaiButton
        // chiều rộng của nền = chiều rộng avatar
        float lePhai=15*oneDp;
        float endWBack = barHeight;
        endLeftBack = width - barHeight - lePhai;
        float endLeftAva = endLeftBack;
        endTopBack = beginTopBack - lePhai;

        // từ phần trăm 0- 1
        // tính ra :
        // bao nhieu "khoảng cách background đỉnh trái
        //   Công thức :
        // chiều rộng và chiều dài background
        // bao nhiêu độ background
        // bao nhiêu "khoảng cách avatar đỉnh trái"
        // bao nhiêu độ avatar
        curWBack = width - (width- endWBack)*pc;

        curHBack = navigationHeight + barHeight - (navigationHeight + barHeight - endWBack)*pc;
        curRoundBack = endWBack/2*pc;
        curRoundAvar = beginRoundAva +  (bitmap.getWidth()/2 - beginRoundAva)*pc;

        curLeftBack =endLeftBack*pc;
        curLeftAva = curLeftBack;
        curTopBack = beginTopBack - lePhai*pc;
        }
    float beginTopBack ;
   public float endTopBack ;
        float beginRoundAva;
        float curTopBack;
        float curWBack, curHBack;
        float curRoundBack;

        float curRoundAvar;
        float curLeftBack;
        float curLeftAva;

    public void paint(Canvas canvas) {
        if(pc==0) calculating();
        // vẽ background đã được rounded
        if(curRoundBack!=0) canvas.drawPath(BitmapEditor.RoundedRect(curLeftBack,curTopBack,curLeftBack+curWBack,curHBack+curTopBack,curRoundBack,curRoundBack,false),backPaint);
        else canvas.drawRect(0,beginTopBack,width,height,backPaint);
        shownBitmap = listener.getBitmapRounded(curRoundAvar,bitmap);
        canvas.drawBitmap(shownBitmap,new Rect(0,0,shownBitmap.getWidth(),shownBitmap.getHeight()),new Rect((int)curLeftAva,(int) curTopBack,(int)curLeftAva+barHeight,barHeight+ (int)curTopBack),backPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // vẽ background đã được rounded
    paint(canvas);
     }


}
