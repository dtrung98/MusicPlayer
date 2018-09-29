package com.ldt.musicr.views;

/**
 * Created by trung on 9/17/2017.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.ldt.musicr.InternalTools.Tool;

import java.util.ArrayList;
public class StickyScrollView extends NestedScrollView {
    private static final String TAG = "StickyScrollView";

    /**
     * Tag for views that should stick and have constant drawing. e.g. TextViews, ImageViews etc
     */
    public static final String STICKY_TAG = "sticky";

    /**
     * Flag for views that should stick and have non-constant drawing. e.g. Buttons, ProgressBars etc
     */
    public static final String FLAG_NONCONSTANT = "-nonconstant";

    /**
     * Flag for views that have aren't fully opaque
     */
    public static final String FLAG_HASTRANSPARANCY = "-hastransparancy";

    /**
     * Default height of the shadow peeking out below the stuck view.
     */
    private static final int DEFAULT_SHADOW_HEIGHT = 10; // dp;

    private ArrayList<View> stickyViews;
    public ArrayList<View> getStickyViews() {
        return stickyViews;
    };
    public View getCurrentlyStickingView() {
        return currentlyStickingView;
    }
    public View currentlyStickingView;

    protected float stickyViewTopOffset;
    protected int stickyViewLeftOffset;
    private boolean redirectTouchesToStickyView;
    protected boolean clippingToPadding;
    private boolean clipToPaddingHasBeenSet;

    protected int mShadowHeight;
    protected Drawable mShadowDrawable;
    public com.ldt.musicr.views.stickyActionBarConstraintLayout stickyDrawView;
    public void setStickyDrawView(stickyActionBarConstraintLayout c) {
        stickyDrawView = c;
       c.setStickyScrollView(this);
    }

    private final Runnable invalidateRunnable = new Runnable() {

        @Override
        public void run() {
            if(currentlyStickingView!=null){
                int l = getLeftForViewRelativeOnlyChild(currentlyStickingView);
                int t  = getBottomForViewRelativeOnlyChild(currentlyStickingView);
                int r = getRightForViewRelativeOnlyChild(currentlyStickingView);
                int b = (int) (getScrollY() + (currentlyStickingView.getHeight() + stickyViewTopOffset));
               invalidate(l,t,r,b);
           //     customTitleBar.invalidate();
            }
            postDelayed(this, 16);
        }
    };

    public StickyScrollView(Context context) {
        this(context, null);
    }

    public StickyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }

    public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();



        TypedArray a = context.obtainStyledAttributes(attrs,
                com.emilsjolander.components.StickyScrollViewItems.R.styleable.StickyScrollView, defStyle, 0);

        final float density = context.getResources().getDisplayMetrics().density;
        int defaultShadowHeightInPix = (int) (DEFAULT_SHADOW_HEIGHT * density + 0.5f);

        mShadowHeight = a.getDimensionPixelSize(
                com.emilsjolander.components.StickyScrollViewItems.R.styleable.StickyScrollView_stuckShadowHeight,
                defaultShadowHeightInPix);

        int shadowDrawableRes = a.getResourceId(
                com.emilsjolander.components.StickyScrollViewItems.R.styleable.StickyScrollView_stuckShadowDrawable, -1);

        if (shadowDrawableRes != -1) {
            mShadowDrawable = context.getResources().getDrawable(
                    shadowDrawableRes);
        }

        a.recycle();

    }

    /**
     * Sets the height of the shadow drawable in pixels.
     *
     * @param height
     */
    public void setShadowHeight(int height) {
        mShadowHeight = height;
    }


    public void setup(){
        stickyViews = new ArrayList<View>();
    }

    private int getLeftForViewRelativeOnlyChild(View v){
        int left = v.getLeft();
        while(v.getParent() != getChildAt(0)){
            v = (View) v.getParent();
            left += v.getLeft();
        }
        return left;
    }

    private int getTopForViewRelativeOnlyChild(View v){
        int top = v.getTop();
        while(v.getParent() != getChildAt(0)){
            v = (View) v.getParent();
            top += v.getTop();
        }
        return top;
    }

    private int getRightForViewRelativeOnlyChild(View v){
        int right = v.getRight();
        while(v.getParent() != getChildAt(0)){
            v = (View) v.getParent();
            right += v.getRight();
        }
        return right;
    }

    private int getBottomForViewRelativeOnlyChild(View v){
        int bottom = v.getBottom();
        while(v.getParent() != getChildAt(0)){
            v = (View) v.getParent();
            bottom += v.getBottom();
        }
        return bottom;
    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(!clipToPaddingHasBeenSet){
            clippingToPadding = true;
        }
        notifyHierarchyChanged();
    }

    @Override
    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(clipToPadding);
        clippingToPadding  = clipToPadding;
        clipToPaddingHasBeenSet = true;
    }

    @Override
    public void addView(View child) {
        Log.d("Sticky","addView");

        super.addView(child);
        findStickyViews(child);
    }

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
    public void stickyDrawViewInvalidate() {
      Log.d(TAG,"call invalidate");
        if(stickyDrawView!=null) stickyDrawView.invalidate();
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
    //    Log.d("Sticky","dispatchDraw");
        super.dispatchDraw(canvas);

        stickyDrawViewInvalidate();
       if(true) return;
       /*
        if(currentlyStickingView != null){
            canvas.save();
            canvas.translate(getPaddingLeft() + stickyViewLeftOffset, getScrollY() + stickyViewTopOffset + (clippingToPadding ? getPaddingTop() : 0));

            canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0),
                    getWidth() - stickyViewLeftOffset,
                    currentlyStickingView.getHeight() + mShadowHeight + 1);

            if (mShadowDrawable != null) {
                mShadowDrawable.setBounds(
                        0,
                        currentlyStickingView.getHeight(),
                        currentlyStickingView.getWidth(),
                        currentlyStickingView.getHeight() + mShadowHeight);
                mShadowDrawable.drawVisualWave(canvas);
            }

            canvas.clipRect(0, (clippingToPadding ? -stickyViewTopOffset : 0), getWidth(), currentlyStickingView.getHeight());
            if(getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)){
                showView(currentlyStickingView);
                currentlyStickingView.drawVisualWave(canvas);
                hideView(currentlyStickingView);
            }else{
                currentlyStickingView.drawVisualWave(canvas);
            }
            canvas.restore();
        }
        */
    }

/*  parents handler event
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
     //   Log.d("Sticky","dispatchTouchEvent");

        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            redirectTouchesToStickyView = true;
        }

        if(redirectTouchesToStickyView){
            redirectTouchesToStickyView = currentlyStickingView != null;
            if(redirectTouchesToStickyView){
                redirectTouchesToStickyView =
                        ev.getY()<=(currentlyStickingView.getHeight()+stickyViewTopOffset) &&
                                ev.getX() >= getLeftForViewRelativeOnlyChild(currentlyStickingView) &&
                                ev.getX() <= getRightForViewRelativeOnlyChild(currentlyStickingView);
            }
        }else if(currentlyStickingView == null){
            redirectTouchesToStickyView = false;
        }
        if(redirectTouchesToStickyView){
            ev.offsetLocation(0, -1*((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
        }
        return super.dispatchTouchEvent(ev);
    }
    */

    private boolean hasNotDoneActionDown = true;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
   //     Log.d("Sticky","onTouchEventThatView");

        if(redirectTouchesToStickyView){
            ev.offsetLocation(0, ((getScrollY() + stickyViewTopOffset) - getTopForViewRelativeOnlyChild(currentlyStickingView)));
        }

        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            hasNotDoneActionDown = false;
        }

        if(hasNotDoneActionDown){
            MotionEvent down = MotionEvent.obtain(ev);
            down.setAction(MotionEvent.ACTION_DOWN);
            super.onTouchEvent(down);
            hasNotDoneActionDown = false;
        }
        if(ev.getAction()==MotionEvent.ACTION_UP || ev.getAction()==MotionEvent.ACTION_CANCEL){
            hasNotDoneActionDown = true;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {

        super.onScrollChanged(l, t, oldl, oldt);
        doTheStickyThing();
        stickyDrawView.connectOnScrollChanged(l,t,oldl,oldt);
    }

    private void doTheStickyThing() {
       Log.d("Sticky","doTheStickyThing");

        View viewThatShouldStick = null; // View mà cần Stick ?
        View approachingView = null; // View đang tiến đến ?

        for(View v : stickyViews){ // dạo một vòng quanh các view có sticky, đã được tìm và lưu trong mảng trước đó.

            // xem xét vị trí tương đối của nó đối với khung hình scrollview, sau đây lấy vị trí Top
            int viewTop = getTopForViewRelativeOnlyChild(v) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop());
            Log.d("View : ","ViewTop = " + viewTop+",  Height = "+v.getHeight());
            if(viewTop+v.getHeight()<=0) {  // nếu view đó 'thấp' hơn 0
                if(viewThatShouldStick==null || viewTop>(getTopForViewRelativeOnlyChild(viewThatShouldStick) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()))){
                    viewThatShouldStick = v;
                }
            } else {
                if(approachingView == null || viewTop<(getTopForViewRelativeOnlyChild(approachingView) - getScrollY() + (clippingToPadding ? 0 : getPaddingTop()))){
                    approachingView = v;
                }
            }
        }
        if(viewThatShouldStick!=null){
            stickyViewTopOffset = approachingView == null ? 0 : Math.min(0,getTopForViewRelativeOnlyChild(approachingView) - getScrollY()  + (clippingToPadding ? 0 : getPaddingTop())
                    -viewThatShouldStick.getHeight()
            );
            if(viewThatShouldStick != currentlyStickingView){
                if(currentlyStickingView!=null){
                    stopStickingCurrentlyStickingView();
                }

                // only compute the x offset when we start sticking.
                stickyViewLeftOffset = getLeftForViewRelativeOnlyChild(viewThatShouldStick);
                startStickingView(viewThatShouldStick);
            }
        }else if(currentlyStickingView!=null){
            stopStickingCurrentlyStickingView();
        }
    }

    private void startStickingView(View viewThatShouldStick) {
        Log.d("Sticky","startStickingView");

        currentlyStickingView = viewThatShouldStick;
        if(Tool.getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)){
            hideView(currentlyStickingView);
        }
        if(((String)currentlyStickingView.getTag()).contains(FLAG_NONCONSTANT)){
            post(invalidateRunnable);
        }
    }

    private void stopStickingCurrentlyStickingView() {
        Log.d("Sticky","stopStickingCurrentlyStickView");

        if(Tool.getStringTagForView(currentlyStickingView).contains(FLAG_HASTRANSPARANCY)){
            showView(currentlyStickingView);
        }
        currentlyStickingView = null;
        removeCallbacks(invalidateRunnable);
    }

    /**
     * Notify that the sticky attribute has been added or removed from one or more views in the View hierarchy
     */
    public void notifyStickyAttributeChanged(){
        notifyHierarchyChanged();
    }

    private void notifyHierarchyChanged(){
        Log.d("Sticky","notifyHierarchyChanged");

        if(currentlyStickingView!=null){
            stopStickingCurrentlyStickingView();
        }
        stickyViews.clear();
        findStickyViews(getChildAt(0));
       doTheStickyThing();
        invalidate();
      //  customTitleBar.invalidate();
    }

    private void findStickyViews(View v) {
        Log.d("Sticky","findStickyViews");

        if(v instanceof ViewGroup){
            ViewGroup vg = (ViewGroup)v;
            for(int i = 0 ; i<vg.getChildCount() ; i++){
                String tag = Tool.getStringTagForView(vg.getChildAt(i));
                if(tag!=null && tag.contains(STICKY_TAG)){
                    stickyViews.add(vg.getChildAt(i));
                }else if(vg.getChildAt(i) instanceof ViewGroup){
                    findStickyViews(vg.getChildAt(i));
                }
            }
        }else{
            String tag = (String) v.getTag();
            if(tag!=null && tag.contains(STICKY_TAG)){
                stickyViews.add(v);
            }
        }
    }


    protected void hideView(View v) {
     v.setVisibility(INVISIBLE);

    }

    protected void showView(View v) {
                v.setVisibility(VISIBLE);
    }

}
