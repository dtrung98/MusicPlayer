package com.ldt.NewDefinitionMusicApp.views.EffectView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.util.Log;

import com.ldt.NewDefinitionMusicApp.InternalTools.Animation;

/**
 * Created by trung on 11/2/2017.
 */

public class BackgroundBehavior {
    /**
     *
     * @param canvas canvas which to draw on
     *               draw a rectangle which makes a shadow on the bottom of
     */
    EffectView effectView;
    EffectView.Property property;
    BackgroundBehavior(EffectView effectView) {
        this.effectView = effectView;
        property = effectView.property;
    }
    private void invalidate() {
        effectView.invalidate();
    }
    public void draw_gradient_shadow(Canvas canvas) {
        canvas.save();
        canvas.translate(0, property.height - property.length_dp_100);
        //       canvas.drawRect(0,0,property.width,property.length_dp_100,gradient_shadow_Paint);
        canvas.restore();
    }
    private boolean inAnimation = false;
    private void canvas_drawColor(Canvas canvas, int alpha) {
        canvas.drawColor(alpha<<24|0x00ffffff);
    }
    private int background_alpha=0;
    private float background_pc = 0;
    boolean prepare2End = false;
    private void set_background_alpha(float bA)
    {

        background_alpha = (int) bA;
    }
    void drawBackgroundAlpha(Canvas canvas) {
        int BACKGROUND_ALPHA_MAX = 0xee;
        if(inAnimation) {}
        else if(background_alpha== BACKGROUND_ALPHA_MAX &&!prepare2End) {
            canvas_drawColor(canvas, BACKGROUND_ALPHA_MAX);
        }
        else if(!(background_alpha==0&&prepare2End)) {
            ValueAnimator va ;
            if (!prepare2End) { // bat dau
                va= ValueAnimator.ofFloat(0, 1);

            }
            else { // ket thuc
                va = ValueAnimator.ofFloat(1,0);
            }
            va.setInterpolator(Animation.getInterpolator(4));
            va.setDuration(450);
            va.addUpdateListener(animation -> {
                background_pc = (float)animation.getAnimatedValue();
                set_background_alpha(background_pc*BACKGROUND_ALPHA_MAX);
                invalidate();
            });
            va.addListener(new Animator.AnimatorListener() {


                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationStart(Animator animation) {
                    inAnimation = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ((ValueAnimator)animation).removeAllUpdateListeners();
                    animation.removeAllListeners();
                    inAnimation = false;
                    if(prepare2End) {
                        effectView.destroy();
                    }
                }
            });
            va.start();
        }

        if(inAnimation) {
            set_background_alpha((background_alpha> BACKGROUND_ALPHA_MAX) ? BACKGROUND_ALPHA_MAX : background_alpha);
            set_background_alpha((background_alpha<0) ? 0 : background_alpha);

            canvas_drawColor(canvas,background_alpha);
        }
    }

    void destroy() {
        effectView = null;
        property = null;
    }

    public void sync() {

    }

    public float getBackgroundPc() {
        return background_pc;
    }
}
