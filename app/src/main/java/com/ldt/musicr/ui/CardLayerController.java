package com.ldt.musicr.ui;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.maintab.CardLayerFragment;
import com.ldt.musicr.ui.widget.gesture.SwipeDetectorGestureListener;
import com.ldt.musicr.util.InterpolatorUtil;
import com.ldt.musicr.util.Tool;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Lớp điều khiển cách hành xử của một giao diện gồm các layer ui chồng lên nhau
 * <br>Khi một layer trên cùng bật lên thì các layer khác bị lùi ra sau và thu nhỏ dần
 * <br>+. Layer ở càng sau thì càng nhô lên một khoảng cách so với layer trước
 * <br>+. Layer dưới cùng thì toàn màn hình (chiếm cả phần trạng thái) khi pc = 1. Mặt khác, nó thậm chí cho phép kéo xuống giảm pc tới 0
 * <br>+. Các layer còn lại khi pc = 1 sẽ bo góc và cách thanh trạng thái một khoảng cách
 * <br>+. Hiệu ứng kéo lên và kéo xuống, bo góc do LayerController điều khiển, tuy nhiên mỗi layer có thể custom thông số để hiệu ứng xảy ra khác nhau
 */
public class CardLayerController {
    private static final String TAG = "LayerController";
    public static int SINGLE_TAP_CONFIRM = 1;
    public static int SINGLE_TAP_UP = 3;
    public static int LONG_PRESSED = 2;

    private static final TimeInterpolator sCardAutoInterpolator = InterpolatorUtil.getInterpolator(7);
    private static final int sCardAutoDuration = 625;

    public void onConfigurationChanged(Configuration newConfig) {
        if (activity != null) {
//            Log.d(TAG, "onConfigurationChanged " + newConfig.screenHeightDp * activity.getResources().getDimension(R.dimen.oneDP));
//            oneDp = activity.getResources().getDimension(R.dimen.oneDP);
//            ScreenSize[0] = (int) (oneDp*newConfig.screenWidthDp);
//            ScreenSize[1] = (int) (oneDp*newConfig.screenHeightDp);
//            status_height = Tool.getStatusHeight(activity.getResources());
            Log.d(TAG, "onConfigurationChanged: " + screenSize[0] + ", " + screenSize[1]);
            //  animateLayerChanged();
        }
    }

    public interface CardLayer {

        /**
         * Phương thức được gọi khi có sự thay đổi thuộc tính của layer (position, state ..)
         * <br>Dùng phương thức này để cập nhật ui cho layer
         * <br>Note: Không cài đặt sự kiện chạm cho rootView
         * <br> Thay vào đó sự kiện chạm sẽ được truyền tới hàm onTouchParentView
         */
        default void onLayerUpdate(ArrayList<CardLayerAttribute> attrs, ArrayList<Integer> actives, int me) {
        }

        default void onLayerHeightChanged(CardLayerAttribute attr) {
        }

        boolean onTouchParentView(boolean handled);

        View getLayerRootView(Activity activity, ViewGroup viewGroup, int maxPosition);

        default void onAddedToLayerController(CardLayerAttribute attr) {
        }

        /**
         * Cài đặt khoảng cách giữa đỉnh layer và viền trên
         * khi layer đạt vị trí max
         *
         * @return true : full screen, false : below the status bar and below the back_layer_margin_top
         */
        boolean isFullscreenLayer();

        default boolean onBackPressed() {
            return false;
        }

        /**
         * The minimum value of a card layer
         *
         * @return Giá trị pixel của Margin dưới
         */
        int getLayerMinHeight(Context context, int maxHeight);

        /**
         * Tag nhằm phân biệt giữa các layer
         *
         * @return String tag
         */
        String getCardLayerTag();

        default boolean onGestureDetected(int gesture) {
            return false;
        }
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    private final AppCompatActivity activity;
    public float maxMarginDp = 12f;
    public float maxMarginTop;
    public float oneDp;
    public int[] screenSize = new int[2];
    public float statusBarHeight = 0;

    public float bottom_navigation_height;


    // Distance to travel before a drag may begin
    private int mTouchSlop;
    private float mMaxVelocity;
    private float mMinVelocity;

    FrameLayout mChildLayerContainer;

    FrameLayout mLayerContainer;

    BottomNavigationView mBottomNavigationView;

    View mBottomNavigationParent;

    private void bindView(View view) {
        mChildLayerContainer = view.findViewById(R.id.child_layer_container);
        mBottomNavigationParent = view.findViewById(R.id.bottom_navigation_parent);
        mBottomNavigationView = mBottomNavigationParent.findViewById(R.id.bottom_navigation_view);

    }

    @SuppressLint("ClickableViewAccessibility")
    public CardLayerController(AppCompatActivity activity) {
        this.activity = activity;
        oneDp = Tool.getOneDps(activity);
        maxMarginTop = maxMarginDp * oneDp;
        screenSize[0] = ((AppActivity) activity).mAppRootView.getWidth();
        screenSize[1] = ((AppActivity) activity).mAppRootView.getHeight();

        mCardLayerCount = 0;
        mCardLayers = new ArrayList<>();
        mCardLayerAttrs = new ArrayList<>();
        this.statusBarHeight = (statusBarHeight == 0) ? 24 * oneDp : statusBarHeight;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final WindowInsets platformWindowInsets = activity.getWindow().getDecorView().getRootView().getRootWindowInsets();
            if(platformWindowInsets != null) {
                this.statusBarHeight = platformWindowInsets.getStableInsetTop();
            }
        }

        this.bottom_navigation_height = activity.getResources().getDimension(R.dimen.bottom_navigation_height);

        mTouchListener = (view, motionEvent) -> {
            //Log.d(TAG,"onTouchEvent");
            for (int i = 0; i < mCardLayers.size(); i++)
                if (mCardLayerAttrs.get(i).parent == view)
                    return onTouchEvent(i, view, motionEvent);
            return true;
        };

        final ViewConfiguration vc = ViewConfiguration.get(activity);
        mTouchSlop = vc.getScaledTouchSlop();
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        mMinVelocity = vc.getScaledMinimumFlingVelocity();
        mGestureDetector = new GestureDetector(activity, mGestureListener);

    }

    private long mStart = System.currentTimeMillis();

    private void assign(Object mark) {
        long current = System.currentTimeMillis();
        Log.d(TAG, "logTime: Time " + mark + " = " + (current - mStart));
        mStart = current;
    }

    public void init(FrameLayout layerContainer, CardLayerFragment... fragments) {
        assign(0);
        mLayerContainer = layerContainer;

        //ButterKnife.bind(this,layerContainer);
        bindView(layerContainer);
        assign("bind");
        mCardLayers.clear();

        for (int i = 0; i < fragments.length; i++) {
            addCardLayerFragment(fragments[i], 0);
            assign("add base layer " + i);
        }

        mLayerContainer.setVisibility(View.VISIBLE);
        float value = activity.getResources().getDimension(R.dimen.bottom_navigation_height);

       /* mBottomNavigationParent.setTranslationY(value);
        mBottomNavigationParent.animate().translationYBy(-value);
        */
        for (int i = 0; i < mCardLayerAttrs.size(); i++) {
            mCardLayerAttrs.get(i).expandImmediately();
        }

        assign(3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ObjectAnimator.ofArgb(mLayerContainer, "backgroundColor", 0, 0x11000000).setDuration(350).start();
        } else {
            ObjectAnimator.ofObject(mLayerContainer, "backgroundColor", new ArgbEvaluator(), 0, 0x11000000).setDuration(350).start();
        }
        assign(4);

        activity.getOnBackPressedDispatcher().addCallback(activity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                boolean backPressed = onBackPressed();
                if (!backPressed) {
                    activity.finish();
                }
            }
        });
    }


    /**
     * Phương thức trả về giá trị phần trăm scale khi scale view đó để đạt được hiệu quả
     * tương ứng như khi đặt margin phải-trái là marginInPx
     *
     * @param marginInPx
     * @return
     */
    private float convertPixelToScaleX(float marginInPx) {
        return 1 - marginInPx * 2 / screenSize[0];
    }

    /**
     * Cập nhật lại margin lúc pc = 1 của mỗi layer
     * Được gọi bất cứ khi nào một pc của một layer bất kỳ được thay đổi (sự kiện chạm)
     */
    int mFocusedCardLayerPosition = -1;
    int active_number = 0;

    private void findCurrentFocusLayer() {
        mFocusedCardLayerPosition = -1;

        active_number = 0;
        for (int i = 0; i < mCardLayerCount; i++)
            if (mCardLayerAttrs.get(i).getRuntimePercent() != 0 || mCardLayerAttrs.get(i).mCurrentTranslate == mCardLayerAttrs.get(i).getMaxPosition()) {
                if (active_number == 0) {
                    mFocusedCardLayerPosition = i;
                }
                active_number++;
            }
    }

    private void animateLayerChanged() {

        // Các layer sẽ được update
        // là các layer không bị minimize
        ArrayList<Integer> actives = new ArrayList<>();
        for (int i = 0; i < mCardLayerAttrs.size(); i++) {
            // Reset
            mCardLayerAttrs.get(i).mScaleXY = 1;
            mCardLayerAttrs.get(i).mScaleDeltaTranslate = 0;
            // Only Active Layer
            if (mCardLayerAttrs.get(i).getRuntimePercent() != 0 || mCardLayerAttrs.get(i).mCurrentTranslate == mCardLayerAttrs.get(i).getMaxPosition()) {
                //Log.d(TAG, "animateLayerChanged: runtime : "+mBaseAttrs.get(i).getRuntimePercent());
                actives.add(i);
            } else {
                mCardLayerAttrs.get(i).parent.setScaleX(mCardLayerAttrs.get(i).mScaleXY);
                mCardLayerAttrs.get(i).parent.setScaleY(mCardLayerAttrs.get(i).mScaleXY);
                mCardLayerAttrs.get(i).updateTranslateY();
            }
        }
        // Size
        int activeSize = actives.size();
        //Log.d(TAG, "animateLayerChanged: size = "+activeSize);
//        if(activeSize==1) {
//            mBaseAttrs.get(actives.get(0)).parent.setScaleX(mBaseAttrs.get(actives.get(0)).mScaleXY);
//            mBaseAttrs.get(actives.get(0)).parent.setScaleY(mBaseAttrs.get(actives.get(0)).mScaleXY);
//            mBaseAttrs.get(actives.get(0)).updateTranslateY();
//        }

        // Sau đây chỉ thực hiện tính toán với các layer hiện hoạt

        // Giá trị scale mới của mỗi layer theo thứ tự
        // <br>Các layer ẩn không tính

        /*
         *  mScaleXY là giá trị tương ứng khi scale view để đạt hiệu quả
         *  tương tự khi cài đặt viền trái để view nằm cách viền trái phải một khoảng cách mong muốn
         */
        float[] scaleXY = new float[activeSize];

        /*
         *  mScaleDeltaTranslate là giá trị cần phải translate view theo trục y (sau khi view đã scale)
         *  để đỉnh của view cách màn hình một khoảng cách mong muốn
         */
        float[] deltaTranslateY = new float[activeSize];

        // Save the percent of the top focus layer (pos 0 )
        float pcOfFocusLayer_End = 1;

        if (activeSize != 0) {
            CardLayerAttribute a = mCardLayerAttrs.get(actives.get(0));

            pcOfFocusLayer_End = a.getPercent();
        }

        for (int item = 1; item < activeSize; item++) {

            // layer trên cùng mặc nhiên scale = 1 nên không cần phải tính
            // nên bỏ qua item 0
            // bắt đầu vòng lặp từ item 1
            int position = actives.get(item);

            scaleXY[item] = convertPixelToScaleX((item - 1) * maxMarginTop * (1 - pcOfFocusLayer_End)
                    + pcOfFocusLayer_End * item * maxMarginTop);

            // khi scale một giá trị là scaleXY[item] thì layer sẽ nhỏ đi
            // và khi đó đó nó làm tăng viên trên thêm một giá trị trong pixel là:
            float scale_marginY = screenSize[1] * (1 - scaleXY[item]) / 2.0f;

            float need_marginY = 0;
            //item này cần cộng thêm giá trị (khoảng cách max - vị trí "chuẩn")
            if (item == 1) {
                // Layer này khác với các layer khác, nó phải đi từ vị trí getMaxPositionType() -> margin của tương ứng của nó
                need_marginY = pcOfFocusLayer_End * (mCardLayerAttrs.get(position).getMaxPosition() - (screenSize[1] - statusBarHeight - 2 * oneDp - maxMarginTop));
            } else
                need_marginY = mCardLayerAttrs.get(position).getMaxPosition() - (screenSize[1] - statusBarHeight - 2 * oneDp - maxMarginTop);


            if (activeSize == 2) {
                need_marginY -= maxMarginTop * pcOfFocusLayer_End;
            } else { // activeSize >=3
                need_marginY -= maxMarginTop * (item - 1f) / (activeSize - 2f) + pcOfFocusLayer_End * (maxMarginTop * item / (activeSize - 1) - maxMarginTop * (item - 1) / (activeSize - 2));
            }
            deltaTranslateY[item] = need_marginY - scale_marginY;
            //Log.d(TAG, "updateLayerChanged: item "+item +", delatTranslateY = "+deltaTranslateY[item]);
        }

        // Update UI
        CardLayerAttribute attr;
        for (int item = 1; item < activeSize; item++) {
            attr = mCardLayerAttrs.get(actives.get(item));

            attr.mScaleXY = scaleXY[item];
            attr.mScaleDeltaTranslate = deltaTranslateY[item];
            //Log.d(TAG, "updateLayerChanged: deltaLayer["+item+"] = "+deltaTranslateY[item]);
            // Scale và translate những layer phía sau

            final TimeInterpolator interpolator = sCardAutoInterpolator;
            final int duration = sCardAutoDuration;

            attr.parent.animate().scaleX(attr.mScaleXY).setDuration(duration).setInterpolator(interpolator);
            //Log.d(TAG, "animateLayerChanged: item "+actives.get(item)+" : scaleX from "+attr.parent.getScaleX()+" to "+attr.mScaleXY);
            attr.parent.animate().scaleY(attr.mScaleXY).setDuration(duration).setInterpolator(interpolator);

            //          translationY(getRealTranslateY()).setDuration((long) (350 + 150f/ScreenSize[1]*minPosition)).setInterpolator(Animation.sInterpolator)

            final int item_copy = item;
            attr.parent.animate().translationY(attr.getRealTranslateY()).setDuration(duration).setInterpolator(interpolator).setUpdateListener(animation -> {
                mCardLayers.get(actives.get(item_copy)).onLayerUpdate(mCardLayerAttrs, actives, item_copy);
            });
        }

    }

    private void updateLayerChanged() {


        // Đi từ 0 - n
        // Chỉ xét những layer có pc !=0, gọi là layer hiện hoạt
        // Những layer có pc = 0 sẽ bị bỏ qua và không tính vào bộ layer, gọi là layer ẩn
        // Layer có pc !=1 nghĩa là đang có sự kiện xảy ra

        // Đếm số lượng layer hiện hoạt
        // và tìm ra on-top-layer
        // on-top-layer là layer đầu tiên được tìm thấy có pc !=0 ( thường là khác 1)
        // các layer còn lại mặc định có pc = 1
        // pc của on-top-layer ảnh hưởng lên các layer khác phía sau nó
        findCurrentFocusLayer();
        if (mFocusedCardLayerPosition < 0) return;

        int touchLayer = mGestureListener.item;
        // Các layer sẽ được update
        // là các layer không bị minimize
        ArrayList<Integer> actives = new ArrayList<>();
        for (int i = 0; i < mCardLayerAttrs.size(); i++) {
            // Reset
            mCardLayerAttrs.get(i).mScaleXY = 1;
            mCardLayerAttrs.get(i).mScaleDeltaTranslate = 0;
            // Only Active Layer
            if (mCardLayerAttrs.get(i).getState() != CardLayerAttribute.MINIMIZED || mCardLayerAttrs.get(i).mCurrentTranslate == mCardLayerAttrs.get(i).getMaxPosition())
                actives.add(i);
            else {
                mCardLayerAttrs.get(i).parent.setScaleX(mCardLayerAttrs.get(i).mScaleXY);
                mCardLayerAttrs.get(i).parent.setScaleY(mCardLayerAttrs.get(i).mScaleXY);
                mCardLayerAttrs.get(i).updateTranslateY();
            }
        }
        // Size
        int activeSize = actives.size();

        if (activeSize == 1) {
            mCardLayerAttrs.get(actives.get(0)).parent.setScaleX(mCardLayerAttrs.get(actives.get(0)).mScaleXY);
            mCardLayerAttrs.get(actives.get(0)).parent.setScaleY(mCardLayerAttrs.get(actives.get(0)).mScaleXY);
            mCardLayerAttrs.get(actives.get(0)).updateTranslateY();
        }

        // Sau đây chỉ thực hiện tính toán với các layer hiện hoạt

        // Giá trị scale mới của mỗi layer theo thứ tự
        // <br>Các layer ẩn không tính

        /*
         *  mScaleXY là giá trị tương ứng khi scale view để đạt hiệu quả
         *  tương tự khi cài đặt viền trái để view nằm cách viền trái phải một khoảng cách mong muốn
         */
        float[] scaleXY = new float[activeSize];

        /*
         *  mScaleDeltaTranslate là giá trị cần phải translate view theo trục y (sau khi view đã scale)
         *  để đỉnh của view cách màn hình một khoảng cách mong muốn
         */
        float[] deltaTranslateY = new float[activeSize];

        // Save the percent of the top focus layer (pos 0 )
        float pcOfTopFocusLayer = 1;
        if (activeSize != 0)
            pcOfTopFocusLayer = mCardLayerAttrs.get(actives.get(0)).getRuntimePercent();

        for (int item = 1; item < activeSize; item++) {

            // layer trên cùng mặc nhiên scale = 1 nên không cần phải tính
            // nên bỏ qua item 0
            // bắt đầu vòng lặp từ item 1
            int position = actives.get(item);

            scaleXY[item] = convertPixelToScaleX((item - 1) * maxMarginTop * (1 - pcOfTopFocusLayer)
                    + pcOfTopFocusLayer * item * maxMarginTop);

            // khi scale một giá trị là scaleXY[item] thì layer sẽ nhỏ đi
            // và khi đó đó nó làm tăng viên trên thêm một giá trị trong pixel là:
            float scale_marginY = screenSize[1] * (1 - scaleXY[item]) / 2.0f;

            float need_marginY = 0;
            //item này cần cộng thêm giá trị (khoảng cách max - vị trí "chuẩn")
            if (item == 1) {
                // Layer này khác với các layer khác, nó phải đi từ vị trí getMaxPositionType() -> margin của tương ứng của nó
                need_marginY = pcOfTopFocusLayer * (mCardLayerAttrs.get(position).getMaxPosition() - (screenSize[1] - statusBarHeight - 2 * oneDp - maxMarginTop));
            } else
                need_marginY = mCardLayerAttrs.get(position).getMaxPosition() - (screenSize[1] - statusBarHeight - 2 * oneDp - maxMarginTop);


            if (activeSize == 2) {
                need_marginY -= maxMarginTop * pcOfTopFocusLayer;
            } else { // activeSize >=3
                need_marginY -= maxMarginTop * (item - 1f) / (activeSize - 2f) + pcOfTopFocusLayer * (maxMarginTop * item / (activeSize - 1) - maxMarginTop * (item - 1) / (activeSize - 2));
            }
            deltaTranslateY[item] = need_marginY - scale_marginY;
            //Log.d(TAG, "updateLayerChanged: item "+item +", delatTranslateY = "+deltaTranslateY[item]);
        }

        // Update UI
        CardLayerAttribute attr;
        for (int item = 1; item < activeSize; item++) {
            attr = mCardLayerAttrs.get(actives.get(item));

            attr.mScaleXY = scaleXY[item];
            attr.mScaleDeltaTranslate = deltaTranslateY[item];
            //Log.d(TAG, "updateLayerChanged: deltaLayer["+item+"] = "+deltaTranslateY[item]);
            // Scale và translate những layer phía sau

            attr.parent.setScaleX(attr.mScaleXY);
            attr.parent.setScaleY(attr.mScaleXY);

            attr.updateTranslateY();
            final int item_copy = item;
            mCardLayers.get(actives.get(item)).onLayerUpdate(mCardLayerAttrs, actives, item_copy);
        }

    }

    private final ArrayList<CardLayerFragment> mCardLayers;
    private final ArrayList<CardLayerAttribute> mCardLayerAttrs;
    private final View.OnTouchListener mTouchListener;

    enum MOVE_DIRECTION {
        NONE,
        MOVE_UP,
        MOVE_DOWN
    }

    private final GestureDetector mGestureDetector;
    public SwipeGestureListener mGestureListener = new SwipeGestureListener();

    static class SwipeGestureListener extends SwipeDetectorGestureListener {
        public boolean down = false;
        private boolean flingMasked = false;
        public float assignPosY0;
        public float assignPosX0;
        public boolean handled = true;
        private MOVE_DIRECTION direction;


        private float prevY;

        private boolean isMoveUp() {
            return direction == MOVE_DIRECTION.MOVE_UP;
        }

        private boolean isMoveDown() {
            return direction == MOVE_DIRECTION.MOVE_DOWN;
        }

        @Override
        public boolean onUp(MotionEvent e) {
            down = false;
            if (flingMasked) {
                flingMasked = false;
                //Log.d(TAG, "onUp: fling mask, cancelled handle");
                return false;
            }
            //TODO: when user touch up, what should we do ?
            if (onMoveUp()) {
                if (attr.isBigger1_4())
                    attr.animateToMax();
                else attr.animateToMin();
            } else if (onMoveDown()) {
                if (attr.isSmaller3_4())
                    attr.animateToMin();
                else attr.animateToMax();
            } else {
                if (attr.isSmaller_1_2()) attr.animateToMin();
                else attr.animateToMax();
            }


            return false;
        }

        private boolean onMoveUp() {
            return direction == MOVE_DIRECTION.MOVE_UP;
        }

        private boolean onMoveDown() {
            return direction == MOVE_DIRECTION.MOVE_DOWN;
        }

        @Override
        public boolean onMove(MotionEvent e) {

            if (!down) {
                down = true;
                handled = true;
                prevY = assignPosY0 = e.getRawY();
                assignPosX0 = e.getRawX();
                direction = MOVE_DIRECTION.NONE;
                return handled;
            } else {
                if (!handled) return false;
                float y = e.getRawY();
                if (direction == MOVE_DIRECTION.NONE) {
                    float diffX = Math.abs(e.getRawX() - assignPosX0);
                    float diffY = Math.abs(e.getRawY() - assignPosY0);
                    if (diffX / diffY >= 2) {
                        handled = false;
                        return false;
                    }
                }
                direction = (y > prevY) ? MOVE_DIRECTION.MOVE_DOWN : (y == prevY) ? MOVE_DIRECTION.NONE : MOVE_DIRECTION.MOVE_UP;

                if (isLayerAvailable()) {
                    attr.moveTo(attr.mCurrentTranslate - y + prevY);
                }
                //TODO: When user move and we know the direction, what should we do ?

                prevY = y;
                return handled;
            }
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (direction != MOVE_DIRECTION.NONE) return;
            if (isLayerAvailable()) layer.onGestureDetected(LONG_PRESSED);
        }

        @Override
        public boolean onSwipeTop(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onSwipeTop layer " + item);
            if (isLayerAvailable()) {
                attr.animateToMax();

            }
            return handled;
        }

        @Override
        public boolean onSwipeBottom(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onSwipeBottom layer " + item);
            if (isLayerAvailable()) attr.animateToMin();
            return handled;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            down = true;
            handled = true;
            prevY = assignPosY0 = e.getRawY();
            assignPosX0 = e.getRawX();
            direction = MOVE_DIRECTION.NONE;
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isLayerAvailable()) return layer.onGestureDetected(SINGLE_TAP_UP);
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //     Toast.makeText(activity,"single tap confirmed",Toast.LENGTH_SHORT).show();
            if (isLayerAvailable()) return layer.onGestureDetected(SINGLE_TAP_CONFIRM);
            return super.onSingleTapConfirmed(e);
        }
    }


    private boolean onLayerTouchEvent(int i, View view, MotionEvent event) {

        //Log.d(TAG, "event = "+logAction(event));
        view.performClick();
        mGestureListener.setMotionLayer(i, mCardLayers.get(i), mCardLayerAttrs.get(i));
        mGestureListener.setAdaptiveView(view);

        boolean b = mGestureDetector.onTouchEvent(event);
        boolean c = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                c = mGestureListener.onMove(event);
                break;
            case MotionEvent.ACTION_UP:
                c = mGestureListener.onUp(event);
                break;

        }
        Log.d(TAG, "onLayerTouchEvent: b = " + b + ", c = " + c);
        return b || c;
    }

    private boolean onTouchEvent(int i, View view, MotionEvent event) {
        return onLayerTouchEvent(i, view, event);

    }

    /**
     * Xử lý sự kiện nhấn nút back
     */
    public boolean onBackPressed() {
        /*
         * Nếu có bất cứ focusLayer nào ( focusLayer >=0)
         * Tiến hành gửi lệnh back tới layer đó, nếu không thì nghĩa là nó đang trong bộ đếm delta time
         * Nếu nó không xử lý lệnh back, thì tiến hành "pop down" nó đi
         * Nếu nó là layer cuối cùng và bị status =="pop down", CardController không xử lý sự kiện và trả về false
         */

        /*
         * Find the current focused CardLayer.
         *    +. Found, send back pressed event to it. If it doesn't consume the event, let's minimize the layer.
         *    +. Not found, this means there are zero layer in the card layer controller. We return false to the activity
         */

        findCurrentFocusLayer();
        if (mFocusedCardLayerPosition != -1) {
            final boolean focusedCardConsumesEvent = mCardLayers.get(mFocusedCardLayerPosition).onBackPressed();

            if (focusedCardConsumesEvent) {
                return true;
            }


            if (mFocusedCardLayerPosition < mCardLayerCount - 1) {
                /* we try to minimize the focused card layer */
                mCardLayerAttrs.get(mFocusedCardLayerPosition).animateToMin();
                return true;
            }

        }
        return false;

    }

    private int mCardLayerCount = 0;

    public boolean dispatchOnTouchEvent(View view, MotionEvent motionEvent) {
        for (int i = 0; i < mCardLayerCount; i++) {
            if (mCardLayerAttrs.get(i).parent == view) {
                return onTouchEvent(i, view, motionEvent);
            }
        }
        throw new NoSuchElementException("No layer has that view");
    }

    public int getMyPosition(CardLayerAttribute attr) {
        return mCardLayerAttrs.indexOf(attr);
    }

    public class CardLayerAttribute {

        public CardLayerAttribute() {
            mScaleXY = 1;
            mScaleDeltaTranslate = 0;
            upInterpolator = downInterpolator = 4;
            upDuration = 400;
            downDuration = 500;
            initDuration = 1000;
        }

        public float mScaleXY;
        public float mScaleDeltaTranslate = 0;
        public float mCurrentTranslate = 0;
        public static final int MINIMIZED = -1;
        public static final int MAXIMIZED = 1;
        public static final int CAPTURED = 0;

        public int getState() {
            if (minHeight == mCurrentTranslate) return MINIMIZED;
            if (getMaxPosition() == mCurrentTranslate) return MAXIMIZED;
            return CAPTURED;
        }

        public float getPercent() {
            return (mCurrentTranslate - minHeight + 0f) / (getMaxPosition() - minHeight + 0f);
        }

        public float getRuntimePercent() {
            return ((getMaxPosition() - parent.getTranslationY() + mScaleDeltaTranslate) - minHeight + 0f) / (getMaxPosition() - minHeight + 0f);
        }

        public float getRuntimeSelfTranslate() {
            return (getMaxPosition() - parent.getTranslationY() + mScaleDeltaTranslate);
        }

        public boolean isBigger1_4() {
            return (mCurrentTranslate - minHeight) * 4 > (getMaxPosition() - minHeight);
        }

        public boolean isSmaller3_4() {
            return (mCurrentTranslate - minHeight) * 4 < 3 * (getMaxPosition() - minHeight);
        }

        public boolean isSmaller_1_2() {
            return (mCurrentTranslate - minHeight) * 2 < (getMaxPosition() - minHeight);
        }

        public float getRealTranslateY() {
            return getMaxPosition() - mCurrentTranslate + mScaleDeltaTranslate;
        }

        public void animateAndGone() {

        }

        public void animateOnInit() {
            parent.setTranslationY(getMaxPosition());
            parent.animate().translationYBy(-getMaxPosition() + getRealTranslateY()).setDuration((long) (350 + 150f / screenSize[1] * minHeight)).setInterpolator(InterpolatorUtil.sInterpolator);
            //  parent.animate().translationYBy(-getMaxPositionType()+getRealTranslateY()).setDuration(computeSettleDuration(0,(int) Math.abs(-getMaxPositionType() + getRealTranslateY()),0,(int)getMaxPositionType())).setInterpolator(Animation.sInterpolator);
            mCurrentTranslate = minHeight;
        }

        public void expandImmediately() {
            parent.setTranslationY(getRealTranslateY());
        }

        public CardLayerAttribute setCurrentTranslate(float current) {
            mCurrentTranslate = current;
            if (mCurrentTranslate > getMaxPosition()) mCurrentTranslate = getMaxPosition();
            return this;
        }

        public void moveTo(float translateY) {
            if (translateY == mCurrentTranslate) return;
            setCurrentTranslate(translateY);
            updateTranslateY();
            if (mGestureListener.isLayerAvailable())
                mCardLayers.get(mGestureListener.item).onLayerHeightChanged(this);
            updateLayerChanged();
        }

        public void shakeOnMax(float _value) {
            float value = 10 * oneDp + _value;
            if (value > 30 * oneDp) value = 30 * oneDp;
            moveTo(mCurrentTranslate - value);
            animateTo(mCurrentTranslate - value);
            mBottomNavigationParent.postDelayed(this::animateToMax, 300);
        }


        public void animateTo(float selfTranslateY) {
            if (selfTranslateY == mCurrentTranslate) return;
            mCurrentTranslate = selfTranslateY;
            final int item = mGestureListener.item;
            if (parent != null) {
                animateLayerChanged();

                parent.animate().translationY(getRealTranslateY()).setDuration((long) (350 + 150f / screenSize[1] * minHeight)).setInterpolator(InterpolatorUtil.sInterpolator)
                        .setUpdateListener(animation -> {
                            if (item != -1)
                                mCardLayers.get(item).onLayerHeightChanged(CardLayerAttribute.this);
                        });
            }
        }

        public void animateTo(float selfTranslateY, float velocityY) {
            if (selfTranslateY == mCurrentTranslate) return;
            mCurrentTranslate = selfTranslateY;
            if (parent != null) {
                parent.animate().translationY(getRealTranslateY()).setDuration((long) (350 + 150f / screenSize[1] * minHeight)).setInterpolator(InterpolatorUtil.sInterpolator);
            }
        }

        private int computeSettleDuration(int dx, int dy, int xvel, int yvel) {
            xvel = clampMag(xvel, (int) mMinVelocity, (int) mMaxVelocity);
            yvel = clampMag(yvel, (int) mMinVelocity, (int) mMaxVelocity);
            final int absDx = Math.abs(dx);
            final int absDy = Math.abs(dy);
            final int absXVel = Math.abs(xvel);
            final int absYVel = Math.abs(yvel);
            final int addedVel = absXVel + absYVel;
            final int addedDistance = absDx + absDy;

            final float xweight = xvel != 0 ? (float) absXVel / addedVel :
                    (float) absDx / addedDistance;
            final float yweight = yvel != 0 ? (float) absYVel / addedVel :
                    (float) absDy / addedDistance;

            int xduration = computeAxisDuration(dx, xvel, 0);
            int yduration = computeAxisDuration(dy, yvel, (int) Math.abs(getMaxPosition() - minHeight));

            return (int) (xduration * xweight + yduration * yweight);
        }

        private int computeAxisDuration(int delta, int velocity, int motionRange) {
            if (delta == 0) {
                return 0;
            }

            final int width = screenSize[0];
            final int halfWidth = width / 2;
            final float distanceRatio = Math.min(1f, (float) Math.abs(delta) / width);
            final float distance = halfWidth + halfWidth *
                    distanceInfluenceForSnapDuration(distanceRatio);

            int duration;
            velocity = Math.abs(velocity);
            if (velocity > 0) {
                duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
            } else {
                final float range = (float) Math.abs(delta) / motionRange;
                duration = (int) ((range + 1) * BASE_SETTLE_DURATION);
            }
            return Math.min(duration, MAX_SETTLE_DURATION);
        }

        private float distanceInfluenceForSnapDuration(float f) {
            f -= 0.5f; // center the values about 0.
            f *= 0.3f * Math.PI / 2.0f;
            return (float) Math.sin(f);
        }

        private static final int BASE_SETTLE_DURATION = 256; // ms
        private static final int MAX_SETTLE_DURATION = 600; // ms

        /**
         * Clamp the magnitude of value for absMin and absMax.
         * If the value is below the minimum, it will be clamped to zero.
         * If the value is above the maximum, it will be clamped to the maximum.
         *
         * @param value  Value to clamp
         * @param absMin Absolute value of the minimum significant value to return
         * @param absMax Absolute value of the maximum value to return
         * @return The clamped value with the same sign as <code>value</code>
         */
        private int clampMag(int value, int absMin, int absMax) {
            final int absValue = Math.abs(value);
            if (absValue < absMin) return 0;
            if (absValue > absMax) return value > 0 ? absMax : -absMax;
            return value;
        }

        public void animateToMax() {
            mGestureListener.item = getMyPosition(this);
            animateTo(getMaxPosition());
        }

        public void animateToMin() {
            mGestureListener.item = getMyPosition(this);
            animateTo(minHeight);
        }

        public void updateTranslateY() {
            if (parent != null) parent.setTranslationY(getRealTranslateY());
//            if(parent instanceof DarkenAndRoundedBackgroundContraintLayout) {
//                RoundColorable roundable = (RoundColorable)parent;
//                if(minPosition!=getMaxPositionType())
//                    roundable.setRoundNumber(getPercent(),true);
//                else {
//                    float pc = mCurrentTranslate/getMaxPositionType();
//                    //Log.d(TAG, "updateTranslateY: "+mCurrentTranslate+" of "+getMaxPositionType()+" : "+(1-pc));
//
//                    roundable.setRoundNumber(1-pc, true);
//                }
//            }
        }

        public String Tag;
        public float minHeight;
        public int upInterpolator;
        public int downInterpolator;
        public int upDuration;
        public int downDuration;
        public int initDuration;

        public View getParent() {
            return parent;
        }

        public View parent;


        public float getScaleXY() {
            return mScaleXY;
        }

        public float getScaleDeltaTranslate() {
            return mScaleDeltaTranslate;
        }

        public CardLayerAttribute setScaleDeltaTranslate(float scaleDeltaTranslate) {
            mScaleDeltaTranslate = scaleDeltaTranslate;
            return this;
        }

        public String getTag() {
            return Tag;
        }

        public CardLayerAttribute setTag(String tag) {
            Tag = tag;
            return this;
        }

        public float getMinHeight() {
            return minHeight;
        }


        public CardLayerAttribute setMinHeight(float value) {
            this.minHeight = value;
            return this;
        }

        public int getUpInterpolator() {
            return upInterpolator;
        }

        public CardLayerAttribute setUpInterpolator(int upInterpolator) {
            this.upInterpolator = upInterpolator;
            return this;
        }

        public int getDownInterpolator() {
            return downInterpolator;
        }

        public CardLayerAttribute setDownInterpolator(int downInterpolator) {
            this.downInterpolator = downInterpolator;
            return this;
        }

        public int getUpDuration() {
            return upDuration;
        }

        public CardLayerAttribute setUpDuration(int upDuration) {
            this.upDuration = upDuration;
            return this;
        }

        public int getDownDuration() {
            return downDuration;
        }

        public CardLayerAttribute setDownDuration(int downDuration) {
            this.downDuration = downDuration;
            return this;
        }

        public int getInitDuration() {
            return initDuration;

        }

        public CardLayerAttribute setInitDuration(int initDuration) {
            this.initDuration = initDuration;
            return this;
        }

        public CardLayerAttribute set(CardLayer l) {
            this.setTag(l.getCardLayerTag())
                    .setMinHeight(l.getLayerMinHeight(activity, screenSize[1]))
                    .setMaxPosition(l.isFullscreenLayer())
                    .setCurrentTranslate(this.getMinHeight());

            return this;
        }

        public CardLayerAttribute attachView(View view) {
            if (parent != null) parent.setOnTouchListener(null);
            parent = view;
            parent.setOnTouchListener(mTouchListener);
            return this;
        }

        private boolean mM = true;

        public int getMaxPosition() {
            if (mM) return screenSize[1];
            else return (int) (screenSize[1] - statusBarHeight - 2 * oneDp - maxMarginTop);
        }

        public CardLayerAttribute setMaxPosition(boolean m) {
            mM = m;
            return this;
        }
    }

    /**
     * Cài đặt vị trí ban đầu và kích cỡ cho layer
     * Thực hiện hiệu ứng đưa layer từ dưới cùng lên tới vị trí minPosition ( pc = 0)
     * hàm initLayer được thực hiện một lần, lúc nó được chèn vào controller
     *
     * @param i
     */
    private void initLayer(int i) {
        CardLayerFragment layer = mCardLayers.get(i);
        CardLayerAttribute attr = mCardLayerAttrs.get(i);
        attr.set(layer);
        attr.attachView(layer.getLayerRootView(activity, mChildLayerContainer, (int) attr.getMaxPosition()));

        activity.getSupportFragmentManager().beginTransaction().add(mChildLayerContainer.getId(), layer).commitNow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            attr.parent.setElevation(0);
        }
        layer.onAddedToLayerController(attr);
    }


    /**
     * Thực hiện hiệu ứng loại bỏ layer ra khỏi controller
     *
     * @param i
     */
    private void removeCardLayerInternal(int i) {

    }

    /**
     * add fragment to the last of stack
     *
     * @param cardLayerFragment
     */
    public CardLayerAttribute addCardLayerFragment(CardLayerFragment cardLayerFragment) {
        return addCardLayerFragment(cardLayerFragment, mCardLayerCount);
    }

    public CardLayerAttribute addCardLayerFragment(CardLayerFragment cardLayerFragment, int index) {
        int p = Math.min(index, mCardLayerCount);
        CardLayerAttribute attribute = new CardLayerAttribute();
        if (mCardLayers.size() > index) {
            mCardLayers.add(index, cardLayerFragment);
            mCardLayerAttrs.add(index, attribute);
        } else {
            mCardLayers.add(cardLayerFragment);
            mCardLayerAttrs.add(attribute);
        }

        mCardLayerCount++;

        cardLayerFragment.setCardLayerController(this);
        initLayer(p);
        findCurrentFocusLayer();
        return attribute;
    }

    public void removeCardLayer(String tag) {
        for (int i = 0; i < mCardLayerCount; i++)
            if (tag.equals(mCardLayerAttrs.get(i).Tag)) {
                removeCardLayerInternal(i);
                return;
            }
    }

    public CardLayerAttribute getMyAttr(CardLayer l) {
        int pos = mCardLayers.indexOf(l);
        if (pos != -1) return mCardLayerAttrs.get(pos);
        return null;
    }

    public int getMyPosition(@NonNull CardLayer l) {
        return mCardLayers.indexOf(l);
    }

}
