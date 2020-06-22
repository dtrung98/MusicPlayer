package com.ldt.musicr.util;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.ldt.musicr.R;

import java.util.ArrayList;
import java.util.List;

public class Animation extends Activity {

    private static final float[] FACTORS = {0.1f, 0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.5f, 3.0f};
    private static final int TEXT_SIZE = 12;
    private static final int ANIMATION_DURATION = 1000;
    private static final int START_DELAY = 3000;
    private static final int FINISH_DELAY = 3000;
    private static final int ANIMATION_DELAY = 2000;

    private LinearLayout root;
    private TextView title;
    private List<Integer> colorList;
    private Point displaySize;
    private int maxTextWidth;
    private int margin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        displaySize = getDisplaySize(this);
        margin = (int) (displaySize.x * 0.1);
        colorList = getColorList();
        root = (LinearLayout) findViewById(R.id.rootZ);
        title = (TextView) findViewById(R.id.titleZ);
        addViews(getInterpolatorList(), null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(START_DELAY);

                    List<Class> interpolatorClassList = new ArrayList<>();
                    interpolatorClassList.add(null);
                    interpolatorClassList.add(AccelerateInterpolator.class);
                    interpolatorClassList.add(DecelerateInterpolator.class);
                    interpolatorClassList.add(OvershootInterpolator.class);
                    interpolatorClassList.add(AnticipateInterpolator.class);
                    interpolatorClassList.add(AnticipateOvershootInterpolator.class);

                    for (Class aClass : interpolatorClassList) {
                        List<Interpolator> interpolatorList = getInterpolatorList(aClass);
                        addViewsOnUI(interpolatorList, aClass);
                        Thread.sleep(ANIMATION_DELAY);
                        startAnimationOnUI(interpolatorList);
                        Thread.sleep(ANIMATION_DELAY + ANIMATION_DURATION);
                        clearAnimationOnUI();
                        Thread.sleep(ANIMATION_DELAY);
                        startAnimationOnUI(interpolatorList);
                        Thread.sleep(ANIMATION_DELAY + ANIMATION_DURATION);
                    }

                    Thread.sleep(FINISH_DELAY);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreenCall();
    }

    private void fullScreenCall() {
        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void clearAnimationOnUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearAnimation();
            }
        });
    }

    private void startAnimationOnUI(final List<Interpolator> interpolatorList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startAnimation(interpolatorList);
            }
        });
    }

    private void addViewsOnUI(final List<Interpolator> interpolatorList, final Class aClass) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addViews(interpolatorList, aClass);
            }
        });
    }

    private void clearAnimation() {
        for (int i = 0; i < root.getChildCount(); i++) {
            View view = root.getChildAt(i);
            view.clearAnimation();
        }
    }

    private void startAnimation(List<Interpolator> interpolatorList) {
        for (int i = 0; i < interpolatorList.size(); i++) {
            Interpolator interpolator = interpolatorList.get(i);
            View view = root.getChildAt(i);
            //   TranslateAnimation Tanimation = new TranslateAnimation(0, displaySize.x - maxTextWidth - 2 * margin, 0, 0);
            ScaleAnimation animation = new ScaleAnimation(2, 1, 2, 1);
            //    RotateAnimation animation = new RotateAnimation(0,60);
            animation.setFillAfter(true);
            //     Tanimation.setFillAfter(true);
            animation.setDuration(ANIMATION_DURATION);
            //   Tanimation.setDuration(ANIMATION_DURATION);
            if (interpolator != null) {
                animation.setInterpolator(interpolator);
                //    Tanimation.setInterpolator(pp_interpolator);
            }

            //    view.startAnimation(Tanimation);
            view.startAnimation(animation);
            //   view.setRotation(60);
        }
    }

    private void addViews(List<Interpolator> interpolatorList, Class aClass) {
        if (aClass == null) {
            title.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
            title.setText(aClass.getSimpleName());
        }
        root.removeAllViews();
        maxTextWidth = 0;
        List<View> views = new ArrayList<>();
        for (int i = 0; i < interpolatorList.size(); i++) {
            TextView view = new TextView(this);
            view.setTextColor(Color.WHITE);
            view.setTextSize(TEXT_SIZE);
            view.setTypeface(Typeface.DEFAULT_BOLD);
            view.setGravity(Gravity.CENTER);
            String interpolatorText;
            if (aClass == null) {
                interpolatorText = interpolatorList.get(i).getClass().getSimpleName().replace("Interpolator", "");
            } else if (aClass.getSimpleName().contains("celerate")) {
                interpolatorText = "factor = " + FACTORS[i];
            } else {
                interpolatorText = "tension = " + FACTORS[i];
            }
            view.setText(interpolatorText);
            view.setBackgroundColor(colorList.get(i));
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f);
            param.setMargins(margin, 0, margin, 0);
            int newTextWidth = getTextWidth(view);
            if (newTextWidth > maxTextWidth) {
                maxTextWidth = newTextWidth;
            }
            root.addView(view, param);
            views.add(view);
        }
        maxTextWidth = (int) (maxTextWidth * 1.1);
        for (View view : views) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = maxTextWidth;
            view.requestLayout();
        }
    }

    private int getTextWidth(TextView textView) {
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(textView.getText().toString(), 0, textView.getText().length(), bounds);
        return bounds.width();
    }

    private static Point getDisplaySize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(displaySize);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                display.getSize(displaySize);
            }
        }
        return displaySize;
    }

    private static List<Integer> getColorList() {
        List<Integer> colorList = new ArrayList<>();
        colorList.add(Color.parseColor("#FF3B30"));
        colorList.add(Color.parseColor("#FF9501"));
        colorList.add(Color.parseColor("#FFCC00"));
        colorList.add(Color.parseColor("#4CDA64"));
        colorList.add(Color.parseColor("#5AC8FB"));
        colorList.add(Color.parseColor("#007AFF"));
        colorList.add(Color.parseColor("#5855D6"));
        colorList.add(Color.parseColor("#FF2C55"));
        colorList.add(Color.parseColor("#8E24AA"));
        colorList.add(Color.parseColor("#607d8b"));
        colorList.add(Color.parseColor("#827717"));
        //   Collections.shuffle(colorList);
        return colorList;
    }

    public static List<Interpolator> getInterpolatorList() {
        List<Interpolator> interpolatorList = new ArrayList<>();
        interpolatorList.add(new LinearInterpolator());
        interpolatorList.add(new AccelerateInterpolator());
        interpolatorList.add(new DecelerateInterpolator());
        interpolatorList.add(new AccelerateDecelerateInterpolator());
        interpolatorList.add(new OvershootInterpolator());
        interpolatorList.add(new AnticipateInterpolator());
        interpolatorList.add(new AnticipateOvershootInterpolator());
        interpolatorList.add(new BounceInterpolator());
        interpolatorList.add(new FastOutLinearInInterpolator());
        interpolatorList.add(new FastOutSlowInInterpolator());
        interpolatorList.add(new LinearOutSlowInInterpolator());
        return interpolatorList;
    }

    @NonNull
    public static Interpolator getInterpolator(int id) {
        switch (id) {
            case 0:
                return new LinearInterpolator();
            case 1:
                return new AccelerateInterpolator();
            case 2:
                return new DecelerateInterpolator();
            case 3:
                return new AccelerateDecelerateInterpolator();
            case 4:
                return new OvershootInterpolator();
            case 5:
                return new AnticipateInterpolator();
            case 6:
                return new AnticipateOvershootInterpolator();
            case 7:
                return new BounceInterpolator();
            case 8:
                return new FastOutLinearInInterpolator();
            case 9:
                return new LinearInterpolator();
            case 10:
                return new LinearOutSlowInInterpolator();
            default:
                return new FastOutSlowInInterpolator();
        }
    }

    public static TimeInterpolator getEasingInterpolator(int id) {
        switch (id) {
            case 0:
                return new EasingInterpolator(Ease.LINEAR);
            case 1:
                return new EasingInterpolator(Ease.QUAD_IN);
            case 2:
                return new EasingInterpolator(Ease.QUAD_OUT);
            case 3:
                return new EasingInterpolator(Ease.QUAD_IN_OUT);
            case 4:
                return new EasingInterpolator(Ease.CUBIC_IN);
            case 5:
                return new EasingInterpolator(Ease.CUBIC_OUT);
            case 6:
                return new EasingInterpolator(Ease.CUBIC_IN_OUT);
            case 7:
                return new EasingInterpolator(Ease.QUART_IN);
            case 8:
                return new EasingInterpolator(Ease.QUART_OUT);
            case 9:
                return new EasingInterpolator(Ease.QUART_IN_OUT);
            case 10:
                return new EasingInterpolator(Ease.QUINT_IN);
            case 11:
                return new EasingInterpolator(Ease.QUINT_OUT);
            case 12:
                return new EasingInterpolator(Ease.QUINT_IN_OUT);
            case 13:
                return new EasingInterpolator(Ease.SINE_IN);
            case 14:
                return new EasingInterpolator(Ease.SINE_OUT);
            case 15:
                return new EasingInterpolator(Ease.SINE_IN_OUT);
            case 16:
                return new EasingInterpolator(Ease.BACK_IN);
            case 17:
                return new EasingInterpolator(Ease.BACK_OUT);
            case 18:
                return new EasingInterpolator(Ease.BACK_IN_OUT);
            case 19:
                return new EasingInterpolator(Ease.CIRC_IN);
            case 20:
                return new EasingInterpolator(Ease.CIRC_OUT);
            case 21:
                return new EasingInterpolator(Ease.CIRC_IN_OUT);
            case 22:
                return new EasingInterpolator(Ease.BOUNCE_IN);
            case 23:
                return new EasingInterpolator(Ease.BOUNCE_OUT);
            case 24:
                return new EasingInterpolator(Ease.BOUNCE_IN_OUT);
            case 25:
                return new EasingInterpolator(Ease.ELASTIC_IN);
            case 26:
                return new EasingInterpolator(Ease.ELASTIC_OUT);
            case 27:
                return new EasingInterpolator(Ease.ELASTIC_IN_OUT);
            default:
                return new EasingInterpolator(Ease.LINEAR);
        }
    }

    /**
     * Interpolator defining the animation curve for mScroller
     */
    public static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    private static List<Interpolator> getInterpolatorList(Class aClass) {
        if (aClass == null) {
            return getInterpolatorList();
        }
        List<Interpolator> interpolatorList = new ArrayList<>();
        for (float factor : FACTORS) {
            try {
                //noinspection unchecked
                interpolatorList.add((Interpolator) aClass.getConstructor(float.class).newInstance(factor));
            } catch (Exception ignored) {
            }
        }
        return interpolatorList;
    }
}
