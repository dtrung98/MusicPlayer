package com.ldt.musicr.views.EffectView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;

import com.ldt.musicr.InternalTools.Animation;

/**
 * Created by trung on 11/2/2017.
 */

public class BackgroundBehavior {
    /**
     *
     * @param canvas canvas which to draw on
     *               draw a rectangle which makes a shadow on the bottom of
     */
    MCBubblePopupUI MCBubblePopupUI;
    com.ldt.musicr.views.EffectView.MCBubblePopupUI.MCAttributes mcAttributes;
    BackgroundBehavior(MCBubblePopupUI MCBubblePopupUI) {
        this.MCBubblePopupUI = MCBubblePopupUI;
        mcAttributes = MCBubblePopupUI.mcAttributes;
    }
    private void invalidate() {
        MCBubblePopupUI.invalidate();
    }
    public void draw_gradient_shadow(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mcAttributes.height - mcAttributes.length_dp_100);
        //       canvas.drawRect(0,0,mcAttributes.width,mcAttributes.length_dp_100,gradient_shadow_Paint);
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
                        MCBubblePopupUI.destroy();
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
        MCBubblePopupUI = null;
        mcAttributes = null;
    }

    boolean sync() {
        return false;
    }
    float getBackgroundPc() {
        return background_pc;
    }
}
