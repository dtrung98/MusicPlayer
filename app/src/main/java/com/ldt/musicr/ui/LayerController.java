package com.ldt.musicr.ui;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.page.BaseLayerFragment;
import com.ldt.musicr.ui.widget.gesture.SwipeDetectorGestureListener;
import com.ldt.musicr.util.Animation;
import com.ldt.musicr.util.Tool;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import butterknife.BindView;


/**
 *  Lớp điều khiển cách hành xử của một giao diện gồm các layer ui chồng lên nhau
 *  <br>Khi một layer trên cùng bật lên thì các layer khác bị lùi ra sau và thu nhỏ dần
 *  <br>+. Layer ở càng sau thì càng nhô lên một khoảng cách so với layer trước
 *  <br>+. Layer dưới cùng thì toàn màn hình (chiếm cả phần trạng thái) khi pc = 1. Mặt khác, nó thậm chí cho phép kéo xuống giảm pc tới 0
 *  <br>+. Các layer còn lại khi pc = 1 sẽ bo góc và cách thanh trạng thái một khoảng cách
 *   <br>+. Hiệu ứng kéo lên và kéo xuống, bo góc do LayerController điều khiển, tuy nhiên mỗi layer có thể custom thông số để hiệu ứng xảy ra khác nhau
 */
public class LayerController {
    private static final String TAG = "LayerController";
    public static int SINGLE_TAP_CONFIRM = 1;
    public static int SINGLE_TAP_UP = 3;
    public static int LONG_PRESSED = 2;

    public void onConfigurationChanged(Configuration newConfig) {
        if(activity!=null) {
//            Log.d(TAG, "onConfigurationChanged " + newConfig.screenHeightDp * activity.getResources().getDimension(R.dimen.oneDP));
//            oneDp = activity.getResources().getDimension(R.dimen.oneDP);
//            ScreenSize[0] = (int) (oneDp*newConfig.screenWidthDp);
//            ScreenSize[1] = (int) (oneDp*newConfig.screenHeightDp);
//            status_height = Tool.getStatusHeight(activity.getResources());
            Log.d(TAG, "onConfigurationChanged: "+ ScreenSize[0]+", "+ScreenSize[1]);
          //  animateLayerChanged();
        }
    }

    public interface BaseLayer {

        /**
         * Phương thức được gọi khi layer được Controller thay đổi thông số của layer
         * <br>Dùng phương thức này để cập nhật ui cho layer
         * <br>Note : Không cài đặt sự kiện chạm cho rootView
         *<br> Thay vào đó sự kiện chạm sẽ được truyền tới hàm onTouchParentView
         */
       void onUpdateLayer(ArrayList<Attr> attrs, ArrayList<Integer> actives,int me);
       void onTranslateChanged(Attr attr);
       boolean onTouchParentView(boolean handled) ;
       View getParent(Activity activity, ViewGroup viewGroup, int maxPosition);
      void  onAddedToContainer(Attr attr);

        /**
         * Cài đặt khoảng cách giữa đỉnh layer và viền trên
         * khi layer đạt vị trí max
         * @return true : full screen, false : below the status bar and below the back_layer_margin_top
         */
       boolean getMaxPositionType();
        boolean onBackPressed();
        /**
         *  Cài đặt khoảng cách giữa đỉnh layer và viền dưới
         *  khi layer đạt vị trí min
         * @return Giá trị pixel của Margin dưới
         */
       int minPosition(Context context, int maxHeight);

        /**
         *  Tag nhằm phân biệt giữa các layer
         * @return String tag
         */
      String tag();
      boolean onGestureDetected(int gesture);
    }
    private AppCompatActivity activity;
    public float margin_inDp = 10f;
    public float mMaxMarginTop;
    public float oneDp;
    public int[] ScreenSize = new int[2];
    public float status_height=0;

    public float bottom_navigation_height;


    // Distance to travel before a drag may begin
    private int mTouchSlop;
    private float mMaxVelocity;
    private float mMinVelocity;

    @BindView(R.id.child_layer_container)
    FrameLayout mChildLayerContainer;

    FrameLayout mLayerContainer;

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    @BindView(R.id.bottom_navigation_parent) View mBottomNavigationParent;

    private void bindView(View view) {
        mChildLayerContainer = view.findViewById(R.id.child_layer_container);
        mBottomNavigationParent = view.findViewById(R.id.bottom_navigation_parent);
        mBottomNavigationView = mBottomNavigationParent.findViewById(R.id.bottom_navigation_view);

    }

    @SuppressLint("ClickableViewAccessibility")
    public LayerController(AppCompatActivity activity) {
        this.activity =  activity;
        oneDp = Tool.getOneDps(activity);
        mMaxMarginTop =margin_inDp*oneDp;
        ScreenSize[0] = ((MainActivity)activity).mRootEverything.getWidth();
        ScreenSize[1] = ((MainActivity)activity).mRootEverything.getHeight();

        listeners_size =0;
        mBaseLayers = new ArrayList<>();
        mBaseAttrs = new ArrayList<>();
        this.status_height = (status_height==0)? 24*oneDp :status_height;

        this.bottom_navigation_height = activity.getResources().getDimension(R.dimen.bottom_navigation_height);

        mTouchListener = (view, motionEvent) -> {
            //Log.d(TAG,"onTouchEvent");
            for(int i = 0; i< mBaseLayers.size(); i++ )
                if(mBaseAttrs.get(i).parent== view)
                    return onTouchEvent(i,view,motionEvent);
            return true;
        };

        final ViewConfiguration vc = ViewConfiguration.get(activity);
        mTouchSlop = vc.getScaledTouchSlop();
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        mMinVelocity = vc.getScaledMinimumFlingVelocity();
        mGestureDetector = new GestureDetector(activity,mGestureListener);

    }
    private long mStart = System.currentTimeMillis();
    private void assign(Object mark) {
        long current = System.currentTimeMillis();
        Log.d(TAG, "logTime: Time "+ mark+" = "+(current - mStart));
        mStart = current;
    }

    public void init(FrameLayout layerContainer, BaseLayerFragment... fragments) {
        assign(0);
        mLayerContainer = layerContainer;

        //ButterKnife.bind(this,layerContainer);
        bindView(layerContainer);
        assign("bind");
        mBaseLayers.clear();

        for (int i = 0; i < fragments.length; i++) {
            addTabLayerFragment(fragments[i],0);
            assign("add base layer "+ i);
        }

        mLayerContainer.setVisibility(View.VISIBLE);
        float value = activity.getResources().getDimension(R.dimen.bottom_navigation_height);

       /* mBottomNavigationParent.setTranslationY(value);
        mBottomNavigationParent.animate().translationYBy(-value);
        */
        for (int i = 0; i < mBaseAttrs.size(); i++) {
            mBaseAttrs.get(i).initImmediately();
        }

        assign(3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ObjectAnimator.ofArgb(mLayerContainer,"backgroundColor",0,0x11000000).setDuration(350).start();
        } else {
            ObjectAnimator.ofObject(mLayerContainer, "backgroundColor", new ArgbEvaluator(), 0,0x11000000).setDuration(350).start();
        }
        assign(4);
    }



    /**
     * Phương thức trả về giá trị phần trăm scale khi scale view đó để đạt được hiệu quả
     * tương ứng như khi đặt margin phải-trái là marginInPx
     * @param marginInPx
     * @return
     */
    private float convertPixelToScaleX(float marginInPx) {
        return 1 - marginInPx*2/ ScreenSize[0];
    }

    /**
     *  Cập nhật lại margin lúc pc = 1 của mỗi layer
     *  Được gọi bất cứ khi nào một pc của một layer bất kỳ được thay đổi (sự kiện chạm)
     */
    int focusLayer = -1;
    int active_number = 0;
    private void findFocusLayer() {
        focusLayer =-1;

        active_number = 0;
        for(int i=0;i<listeners_size;i++)
            if(mBaseAttrs.get(i).getRuntimePercent()!=0||mBaseAttrs.get(i).mCurrentTranslate==mBaseAttrs.get(i).getMaxPosition()) {
                if(active_number==0) {
                    focusLayer = i;
                }
                active_number++;
            }
    }
    private void animateLayerChanged() {

        // Các layer sẽ được update
        // là các layer không bị minimize
        ArrayList<Integer> actives = new ArrayList<>();
        for (int i = 0; i < mBaseAttrs.size(); i++) {
            // Reset
            mBaseAttrs.get(i).mScaleXY =1;
            mBaseAttrs.get(i).mScaleDeltaTranslate = 0;
            // Only Active Layer
            if(mBaseAttrs.get(i).getRuntimePercent()!=0||mBaseAttrs.get(i).mCurrentTranslate==mBaseAttrs.get(i).getMaxPosition()) {
                //Log.d(TAG, "animateLayerChanged: runtime : "+mBaseAttrs.get(i).getRuntimePercent());
                actives.add(i);
            }
            else {
                mBaseAttrs.get(i).parent.setScaleX(mBaseAttrs.get(i).mScaleXY);
                mBaseAttrs.get(i).parent.setScaleY(mBaseAttrs.get(i).mScaleXY);
                mBaseAttrs.get(i).updateTranslateY();
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

        if(activeSize!=0) {
            Attr a = mBaseAttrs.get(actives.get(0));

            pcOfFocusLayer_End = a.getPercent();
        }

        for(int item = 1; item < activeSize; item++) {

            // layer trên cùng mặc nhiên scale = 1 nên không cần phải tính
            // nên bỏ qua item 0
            // bắt đầu vòng lặp từ item 1
            int position = actives.get(item);

            scaleXY[item] = convertPixelToScaleX((item-1)* mMaxMarginTop *(1- pcOfFocusLayer_End)
                    + pcOfFocusLayer_End*item* mMaxMarginTop);

            // khi scale một giá trị là scaleXY[item] thì layer sẽ nhỏ đi
            // và khi đó đó nó làm tăng viên trên thêm một giá trị trong pixel là:
            float scale_marginY = ScreenSize[1]*(1- scaleXY[item])/2.0f;

            float need_marginY = 0;
            //item này cần cộng thêm giá trị (khoảng cách max - vị trí "chuẩn")
            if(item==1) {
                // Layer này khác với các layer khác, nó phải đi từ vị trí getMaxPositionType() -> margin của tương ứng của nó
                need_marginY = pcOfFocusLayer_End*(mBaseAttrs.get(position).getMaxPosition()-(ScreenSize[1] - status_height - 2*oneDp - mMaxMarginTop));
            } else need_marginY = mBaseAttrs.get(position).getMaxPosition()-(ScreenSize[1] - status_height - 2*oneDp - mMaxMarginTop);


            if(activeSize==2) {
                need_marginY -= mMaxMarginTop*pcOfFocusLayer_End;
            } else  { // activeSize >=3
                need_marginY -= mMaxMarginTop *(item - 1f) /(activeSize - 2f) + pcOfFocusLayer_End*(mMaxMarginTop *item/(activeSize - 1) - mMaxMarginTop*(item - 1)/(activeSize -2));
            }
            deltaTranslateY[item] = need_marginY - scale_marginY;
            //Log.d(TAG, "updateLayerChanged: item "+item +", delatTranslateY = "+deltaTranslateY[item]);
        }

        // Update UI
        Attr attr;
        for(int item = 1;item<activeSize;item++) {
            attr = mBaseAttrs.get(actives.get(item));

            attr.mScaleXY = scaleXY[item];
            attr.mScaleDeltaTranslate = deltaTranslateY[item];
            //Log.d(TAG, "updateLayerChanged: deltaLayer["+item+"] = "+deltaTranslateY[item]);
            // Scale và translate những layer phía sau

            TimeInterpolator interpolator = Animation.getInterpolator(7);
            int duration = 650;

            attr.parent.animate().scaleX(attr.mScaleXY).setDuration(duration).setInterpolator(interpolator);
            //Log.d(TAG, "animateLayerChanged: item "+actives.get(item)+" : scaleX from "+attr.parent.getScaleX()+" to "+attr.mScaleXY);
            attr.parent.animate().scaleY(attr.mScaleXY).setDuration(duration).setInterpolator(interpolator);

          //          translationY(getRealTranslateY()).setDuration((long) (350 + 150f/ScreenSize[1]*minPosition)).setInterpolator(Animation.sInterpolator)

            final int item_copy =  item;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                attr.parent.animate().translationY(attr.getRealTranslateY()).setDuration(duration).setInterpolator(interpolator).setUpdateListener(animation -> {
                   mBaseLayers.get(actives.get(item_copy)).onUpdateLayer(mBaseAttrs,actives,item_copy);
                });
            }
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
        findFocusLayer();
        if(focusLayer<0) return;

        int touchLayer = mGestureListener.item;
        // Các layer sẽ được update
        // là các layer không bị minimize
        ArrayList<Integer> actives = new ArrayList<>();
        for (int i = 0; i < mBaseAttrs.size(); i++) {
            // Reset
            mBaseAttrs.get(i).mScaleXY =1;
            mBaseAttrs.get(i).mScaleDeltaTranslate = 0;
            // Only Active Layer
            if(mBaseAttrs.get(i).getState()!= Attr.MINIMIZED||mBaseAttrs.get(i).mCurrentTranslate==mBaseAttrs.get(i).getMaxPosition())
                actives.add(i);
            else {
                mBaseAttrs.get(i).parent.setScaleX(mBaseAttrs.get(i).mScaleXY);
                mBaseAttrs.get(i).parent.setScaleY(mBaseAttrs.get(i).mScaleXY);
                mBaseAttrs.get(i).updateTranslateY();
            }
        }
        // Size
        int activeSize = actives.size();

        if(activeSize==1) {
            mBaseAttrs.get(actives.get(0)).parent.setScaleX(mBaseAttrs.get(actives.get(0)).mScaleXY);
            mBaseAttrs.get(actives.get(0)).parent.setScaleY(mBaseAttrs.get(actives.get(0)).mScaleXY);
            mBaseAttrs.get(actives.get(0)).updateTranslateY();
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
        if(activeSize!=0) pcOfTopFocusLayer = mBaseAttrs.get(actives.get(0)).getRuntimePercent();

        for(int item = 1; item < activeSize; item++) {

            // layer trên cùng mặc nhiên scale = 1 nên không cần phải tính
            // nên bỏ qua item 0
            // bắt đầu vòng lặp từ item 1
            int position = actives.get(item);

            scaleXY[item] = convertPixelToScaleX((item-1)* mMaxMarginTop *(1- pcOfTopFocusLayer)
                    + pcOfTopFocusLayer*item* mMaxMarginTop);

            // khi scale một giá trị là scaleXY[item] thì layer sẽ nhỏ đi
            // và khi đó đó nó làm tăng viên trên thêm một giá trị trong pixel là:
            float scale_marginY = ScreenSize[1]*(1- scaleXY[item])/2.0f;

            float need_marginY = 0;
            //item này cần cộng thêm giá trị (khoảng cách max - vị trí "chuẩn")
            if(item==1) {
                // Layer này khác với các layer khác, nó phải đi từ vị trí getMaxPositionType() -> margin của tương ứng của nó
                need_marginY = pcOfTopFocusLayer*(mBaseAttrs.get(position).getMaxPosition()-(ScreenSize[1] - status_height - 2*oneDp - mMaxMarginTop));
            } else need_marginY = mBaseAttrs.get(position).getMaxPosition()-(ScreenSize[1] - status_height - 2*oneDp - mMaxMarginTop);


            if(activeSize==2) {
                need_marginY -= mMaxMarginTop*pcOfTopFocusLayer;
            } else  { // activeSize >=3
                need_marginY -= mMaxMarginTop *(item - 1f) /(activeSize - 2f) + pcOfTopFocusLayer*(mMaxMarginTop *item/(activeSize - 1) - mMaxMarginTop*(item - 1)/(activeSize -2));
            }
            deltaTranslateY[item] = need_marginY - scale_marginY;
            //Log.d(TAG, "updateLayerChanged: item "+item +", delatTranslateY = "+deltaTranslateY[item]);
        }

        // Update UI
        Attr attr;
        for(int item = 1;item<activeSize;item++) {
            attr = mBaseAttrs.get(actives.get(item));

            attr.mScaleXY = scaleXY[item];
            attr.mScaleDeltaTranslate = deltaTranslateY[item];
            //Log.d(TAG, "updateLayerChanged: deltaLayer["+item+"] = "+deltaTranslateY[item]);
            // Scale và translate những layer phía sau

                attr.parent.setScaleX(attr.mScaleXY);
                attr.parent.setScaleY(attr.mScaleXY);

                attr.updateTranslateY();
                final int item_copy = item;
                mBaseLayers.get(actives.get(item)).onUpdateLayer(mBaseAttrs,actives,item_copy);
        }

   }

    private ArrayList<BaseLayerFragment> mBaseLayers;
    private ArrayList<Attr> mBaseAttrs;
    private View.OnTouchListener mTouchListener;
    enum MOVE_DIRECTION {
        NONE,
        MOVE_UP,
        MOVE_DOWN
    }
    private GestureDetector mGestureDetector;
    public SwipeGestureListener mGestureListener = new SwipeGestureListener();
    class SwipeGestureListener extends SwipeDetectorGestureListener {
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
        if(flingMasked) {
            flingMasked = false;
            //Log.d(TAG, "onUp: fling mask, cancelled handle");
            return false;
        }
        //TODO: when user touch up, what should we do ?
        if(onMoveUp()) {
            if(attr.isBigger1_4())
            attr.animateToMax(); else attr.animateToMin();
        }

        else if(onMoveDown()) {
          if(attr.isSmaller3_4())
            attr.animateToMin();
          else attr.animateToMax();
        } else {
            if(attr.isSmaller_1_2()) attr.animateToMin(); else attr.animateToMax();
        }


       return false;
    }
        private boolean onMoveUp() {
            return direction==MOVE_DIRECTION.MOVE_UP;
        }
        private boolean onMoveDown() {
            return direction == MOVE_DIRECTION.MOVE_DOWN;
        }

    @Override
    public boolean onMove(MotionEvent e) {

        if(!down) {
            down = true;
            handled = true;
            prevY = assignPosY0 = e.getRawY();
            assignPosX0 = e.getRawX();
            direction = MOVE_DIRECTION.NONE;
            return handled;
        } else {
            if(!handled) return false;
                float y = e.getRawY();
            if(direction==MOVE_DIRECTION.NONE) {
                float diffX = Math.abs(e.getRawX() - assignPosX0);
                float diffY = Math.abs(e.getRawY() - assignPosY0);
                if(diffX/diffY>=2) { handled = false; return false;}
            }
            direction = (y> prevY) ? MOVE_DIRECTION.MOVE_DOWN : (y== prevY) ? MOVE_DIRECTION.NONE : MOVE_DIRECTION.MOVE_UP;

            if(isLayerAvailable()) {
                attr.moveTo(attr.mCurrentTranslate - y + prevY);
            }
            //TODO: When user move and we know the direction, what should we do ?

            prevY = y;
            return handled;
        }
    }

        @Override
        public void onLongPress(MotionEvent e) {
        if(direction!=MOVE_DIRECTION.NONE) return;
            if(isLayerAvailable()) layer.onGestureDetected(LONG_PRESSED);
        }

        @Override
        public boolean onSwipeTop(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onSwipeTop layer " + item);
            if(isLayerAvailable()) {
                attr.animateToMax();

            }
            return handled;
        }

        @Override
        public boolean onSwipeBottom(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onSwipeBottom layer "+item);
            if(isLayerAvailable()) attr.animateToMin();
            return handled;
        }

        @Override
        public boolean onDown(MotionEvent e) {
        down = true;
        handled =true;
        prevY = assignPosY0 = e.getRawY();
        assignPosX0 = e.getRawX();
        direction = MOVE_DIRECTION.NONE;
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(isLayerAvailable()) return layer.onGestureDetected(SINGLE_TAP_UP);
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
       //     Toast.makeText(activity,"single tap confirmed",Toast.LENGTH_SHORT).show();
            if(isLayerAvailable()) return layer.onGestureDetected(SINGLE_TAP_CONFIRM);
            return super.onSingleTapConfirmed(e);
        }
    }


    private boolean onLayerTouchEvent(int i, View view, MotionEvent event) {

        //Log.d(TAG, "event = "+logAction(event));
        view.performClick();
        mGestureListener.setMotionLayer(i,mBaseLayers.get(i),mBaseAttrs.get(i));
        mGestureListener.setAdaptiveView(view);

       boolean b = mGestureDetector.onTouchEvent(event);
        boolean c = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: c = mGestureListener.onMove(event); break;
            case MotionEvent.ACTION_UP  : c =mGestureListener.onUp(event); break;

        }
        Log.d(TAG, "onLayerTouchEvent: b = "+b+", c = "+c);
        return b||c;
    }

    private String logAction(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:return "Down";
            case MotionEvent.ACTION_MOVE:return "Move";
            case MotionEvent.ACTION_UP:return "UP";
        }
        return "Unsupported";
    }
    /**
     *  Tất cả sự kiện chạm của tất cả các view được xử lý trong hàm này
     *  Xử lý sự kiện của một view hiện thời đang xảy ra sự kiện chạm :
     *  <br>Capture gestures as slide up, slide down, click ..
     * @param view View đã gửi sự kiện tới
     * @param event Sự kiện chạm
     * @return true nếu sự kiện được xử lý, false nếu sự kiện bị bỏ qua
     */
    int currentLayerEvent = -1;
    private int topMargin;
    private int _xDelta;
    private int _yDelta;
    private boolean onDown = true;
    private long timeDown = 0;
    private boolean onTouchEvent(int i, View view, MotionEvent event) {
       return onLayerTouchEvent(i,view,event);

    }

    CountDownTimer countDownTimer;
    boolean inCountDownTime = false;
    /**
     * Xử lý sự kiện nhấn nút back
     */
    public boolean onBackPressed() {
/**
 * Nếu có bất cứ focusLayer nào ( focusLayer >=0)
 * Tiến hành gửi lệnh back tới layer đó, nếu không  thì nghĩa là nó đang trong bộ đếm delta time
 * Nếu nó không xử lý lệnh back, thì tiến hành "pop down" nó đi
 * Nếu nó là Layer cuối và bị "pop down", tiến hành bộ đếm thời gian
 * Nếu nhấn back trong delta time, tiến hành đóng ứng dụng
 * Nếu không có lệnh back trong delta time, tiến hành "pop up" focusLayer
 */

        // slide down on current layer
        //TODO: SLIDE DOWN CURRENT LAYER
        //Log.d(TAG, "focusLayer = " + focusLayer);
        findFocusLayer();
        if(focusLayer !=-1) {
            if(!mBaseLayers.get(focusLayer).onBackPressed()) {
                mBaseAttrs.get(focusLayer).animateToMin();
                if(focusLayer ==listeners_size-1) {
                    // Start Countdown time
                    checkInCountDownTime();
                }
            } else {
                // Do nothing
            }
        } else {
            checkInCountDownTime();
        }


/*
        if(focusLayer!=-1) // focusLayer is available
        {
            if(mBaseAttrs.get(focusLayer).getPc()>0) // The focusLayer is maximum
                if(!mBaseLayers.get(focusLayer).onBackPressed()) // if it cannot be back pressed
                    AnimateLayer(focusLayer,mBaseLayers.get(listeners_size-1),mBaseAttrs.get(focusLayer),1,0);  // minimize it

        } else activity.SlideDownNShowWallBack();
        */
        return true;
    }
    private void checkInCountDownTime() {
        if(inCountDownTime) {
            countDownTimer.cancel();
            countDownTimer=null;
            activity.finish();
            return;
        }
       inCountDownTime = true;
        Tool.showToast(activity,activity.getString(R.string.alert_ext),350);

        if(countDownTimer==null) countDownTimer = new CountDownTimer(1500,1500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                countDownTimer = null;
                inCountDownTime = false;
            }
        };
        countDownTimer.start();
    }

    /**
     * Giả lập rằng có sự kiện chạm của rootView của Layer có tag là tagLayer
     * <br>Truyền trực tiếp sự kiện chạm tới hàm này
     *
     * @param tagLayer
     * @param view
     * @param motionEvent
     * @return
     */

    private int listeners_size = 0;
    public boolean streamOnTouchEvent(View view, MotionEvent motionEvent) {
        for(int i=0;i<listeners_size;i++) {
            if(mBaseAttrs.get(i).parent == view) {
                return onTouchEvent(i,view,motionEvent);
            }
        }
        throw new NoSuchElementException("No layer has that view");
    }

    public int getMyPosition(Attr attr) {
       return mBaseAttrs.indexOf(attr);
    }

    public class Attr {

        public Attr() {
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
         if(minPosition == mCurrentTranslate) return MINIMIZED;
         if(getMaxPosition() == mCurrentTranslate) return MAXIMIZED;
         return CAPTURED;
        }
        public float getPercent() {
            return (mCurrentTranslate - minPosition +0f)/(getMaxPosition() - minPosition+0f);
        }
        public float getRuntimePercent() {
            return ((getMaxPosition() - parent.getTranslationY() + mScaleDeltaTranslate)- minPosition +0f)/(getMaxPosition() - minPosition+0f);
        }
        public float getRuntimeSelfTranslate() {
            return (getMaxPosition() - parent.getTranslationY() + mScaleDeltaTranslate);
        }
        public boolean isBigger1_4() {
            return (mCurrentTranslate-minPosition)*4>(getMaxPosition()-minPosition);
        }
        public boolean isSmaller3_4() {
            return (mCurrentTranslate-minPosition)*4<3*(getMaxPosition()-minPosition);
        }
        public boolean isSmaller_1_2() {
            return (mCurrentTranslate-minPosition)*2<(getMaxPosition()-minPosition);
        }

        public float getRealTranslateY() {
            return getMaxPosition() - mCurrentTranslate + mScaleDeltaTranslate;
        }

        public void animateAndGone() {

        }

        public void animateOnInit() {
            parent.setTranslationY(getMaxPosition());
            parent.animate().translationYBy(-getMaxPosition()+getRealTranslateY()).setDuration((long) (350 + 150f/ScreenSize[1]*minPosition)).setInterpolator(Animation.sInterpolator);
          //  parent.animate().translationYBy(-getMaxPositionType()+getRealTranslateY()).setDuration(computeSettleDuration(0,(int) Math.abs(-getMaxPositionType() + getRealTranslateY()),0,(int)getMaxPositionType())).setInterpolator(Animation.sInterpolator);
            mCurrentTranslate = minPosition;
        }

        public void initImmediately() {
            parent.setTranslationY(getRealTranslateY());
        }

        public Attr setCurrentTranslate(float current) {
            mCurrentTranslate = current;
            if(mCurrentTranslate>getMaxPosition()) mCurrentTranslate = getMaxPosition();
            return this;
        }

        public void moveTo(float translateY) {
            if(translateY==mCurrentTranslate) return;
            setCurrentTranslate(translateY);
            updateTranslateY();
            if(mGestureListener.isLayerAvailable())
               mBaseLayers.get(mGestureListener.item).onTranslateChanged(this);
            updateLayerChanged();
        }

        public void shakeOnMax(float _value) {
            float value = 10*oneDp + _value;
            if(value>30*oneDp) value = 30*oneDp;
            moveTo(mCurrentTranslate - value);
            animateTo(mCurrentTranslate -value);
            mBottomNavigationParent.postDelayed(this::animateToMax,300);
        }


        public void animateTo(float selfTranslateY) {
            if(selfTranslateY== mCurrentTranslate) return;
            mCurrentTranslate = selfTranslateY;
            final int item = mGestureListener.item;
            if(parent!=null) {
                animateLayerChanged();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    parent.animate().translationY(getRealTranslateY()).setDuration((long) (350 + 150f/ScreenSize[1]*minPosition)).setInterpolator(Animation.sInterpolator)
                    .setUpdateListener(animation -> {
                        if(item!=-1) mBaseLayers.get(item).onTranslateChanged(Attr.this);
                    });
                } else {
                     ObjectAnimator oa = ObjectAnimator.ofFloat(parent, View.TRANSLATION_Y, getRealTranslateY()).setDuration((long) (350 + 150f/ScreenSize[1]*minPosition));
                     oa.addUpdateListener(animation -> {
                         if(item!=-1) mBaseLayers.get(item).onTranslateChanged(Attr.this);
                     });
                     oa.setInterpolator(Animation.sInterpolator);
                     oa.start();
                }
            }
        }
        public void animateTo(float selfTranslateY, float velocityY) {
            if(selfTranslateY== mCurrentTranslate) return;
            mCurrentTranslate = selfTranslateY;
            if(parent!=null) {
                parent.animate().translationY(getRealTranslateY()).setDuration((long) (350 + 150f/ScreenSize[1]*minPosition)).setInterpolator(Animation.sInterpolator);
            }
        }

        private int computeSettleDuration( int dx, int dy, int xvel, int yvel) {
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
            int yduration = computeAxisDuration(dy, yvel, (int) Math.abs(getMaxPosition()-minPosition));

            return (int) (xduration * xweight + yduration * yweight);
        }
        private int computeAxisDuration(int delta, int velocity, int motionRange) {
            if (delta == 0) {
                return 0;
            }

            final int width = ScreenSize[0];
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
         * @param value Value to clamp
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
            animateTo(minPosition);
        }

        public void updateTranslateY() {
            if(parent!=null) parent.setTranslationY(getRealTranslateY());
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
        public float minPosition;
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

        public Attr setScaleDeltaTranslate(float scaleDeltaTranslate) {
            mScaleDeltaTranslate = scaleDeltaTranslate;
            return this;
        }

        public String getTag() {
            return Tag;
        }

        public Attr setTag(String tag) {
            Tag = tag;
            return this;
        }

        public float getMinPosition() {
            return minPosition;
        }


        public Attr setMinPosition(float value) {
            this.minPosition = value;
            return this;
        }

        public int getUpInterpolator() {
            return upInterpolator;
        }

        public Attr setUpInterpolator(int upInterpolator) {
            this.upInterpolator = upInterpolator;
            return this;
        }

        public int getDownInterpolator() {
            return downInterpolator;
        }

        public Attr setDownInterpolator(int downInterpolator) {
            this.downInterpolator = downInterpolator;
            return this;
        }

        public int getUpDuration() {
            return upDuration;
        }

        public Attr setUpDuration(int upDuration) {
            this.upDuration = upDuration;
            return this;
        }

        public int getDownDuration() {
            return downDuration;
        }

        public Attr setDownDuration(int downDuration) {
            this.downDuration = downDuration;
            return this;
        }

        public int getInitDuration() {
            return initDuration;

        }

        public Attr setInitDuration(int initDuration) {
            this.initDuration = initDuration;
            return this;
        }
        public Attr set(BaseLayer l) {
            this.setTag(l.tag())
                    .setMinPosition(l.minPosition(activity,ScreenSize[1]))
                    .setMaxPosition(l.getMaxPositionType())
                     .setCurrentTranslate(this.getMinPosition());

            return this;
        }
        public Attr attachView(View view) {
            if(parent!=null) parent.setOnTouchListener(null);
            parent = view;
            parent.setOnTouchListener(mTouchListener);
            return this;
        }
        private boolean mM = true;
        public int getMaxPosition() {
            if(mM) return ScreenSize[1];
            else return (int) (ScreenSize[1] - status_height - 2*oneDp - mMaxMarginTop);
        }

        public Attr setMaxPosition(boolean m) {
            mM = m;
            return this;
        }
    }

    /**
     * Cài đặt vị trí ban đầu và kích cỡ cho layer
     * Thực hiện hiệu ứng đưa layer từ dưới cùng lên tới vị trí minPosition ( pc = 0)
     * hàm initLayer được thực hiện một lần, lúc nó được chèn vào controller
     * @param i
     */
    private void initLayer( int i) {
        BaseLayerFragment layer = mBaseLayers.get(i);
        Attr attr = mBaseAttrs.get(i);
        attr.set(layer);
        attr.attachView(layer.getParent(activity,mChildLayerContainer, (int) attr.getMaxPosition()));

        activity.getSupportFragmentManager().beginTransaction().add(mChildLayerContainer.getId(),layer).commit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            attr.parent.setElevation(0);
        }
        layer.onAddedToContainer(attr);
       }


    /**
     * Thực hiện hiệu ứng loại bỏ layer ra khỏi controller
     * @param i
     */
    private void removeLayer( int i) {

    }
    public void addTabLayerFragment(BaseLayerFragment tabLayer, int pos) {
        int p = (pos>=listeners_size) ? listeners_size : pos;
        if(mBaseLayers.size()>pos) {
            mBaseLayers.add(pos,tabLayer);
            mBaseAttrs.add(pos,new Attr());
        }
        else {
            mBaseLayers.add(tabLayer);
            mBaseAttrs.add(new Attr());
        }

        listeners_size++;

        tabLayer.setLayerController(this);
        initLayer(p);
        findFocusLayer();

    }

    public void removeListener(String tag) {
        for(int i=0;i<listeners_size;i++)
            if(tag.equals(mBaseAttrs.get(i).Tag)) {
           removeLayer(i);
            return;
            }
    }
    public Attr getMyAttr(BaseLayer l) {
     int pos = mBaseLayers.indexOf(l);
     if(pos!=-1) return mBaseAttrs.get(pos);
     return null;
    }

    public int getMyPosition(@NonNull  BaseLayer l) {
        return mBaseLayers.indexOf(l);
    }

}
