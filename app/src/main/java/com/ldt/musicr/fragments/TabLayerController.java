package com.ldt.musicr.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ldt.musicr.InternalTools.Animation;
import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.fragments.FragmentHolder.Prepare4Fragment;
import com.ldt.musicr.views.SupportDarkenFrameLayout;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 *  Lớp điều khiển cách hành xử của một giao diện gồm các layer ui chồng lên nhau
 *  <br>Khi một layer trên cùng bật lên thì các layer khác bị lùi ra sau và thu nhỏ dần
 *  <br>+. Layer ở càng sau thì càng nhô lên một khoảng cách so với layer trước
 *  <br>+. Layer dưới cùng thì toàn màn hình (chiếm cả phần trạng thái) khi pc = 1. Mặt khác, nó thậm chí cho phép kéo xuống giảm pc tới 0
 *  <br>+. Các layer còn lại khi pc = 1 sẽ bo góc và cách thanh trạng thái một khoảng cách
 *   <br>+. Hiệu ứng kéo lên và kéo xuống, bo góc do TabLayerController điều khiển, tuy nhiên mỗi layer có thể custom thông số để hiệu ứng xảy ra khác nhau
 */
public class TabLayerController {
    private static final String TAG = "TabLayerController";
    public interface TabLayerCallBack {
        /**
         * Phương thức được gọi khi layer được Controller thay đổi thông số của layer
         * <br>Dùng phương thức này để cập nhật ui cho layer
         * <br>Note : Không cài đặt sự kiện chạm cho rootView
         *<br> Thay vào đó sự kiện chạm sẽ được truyền tới hàm onTouchParentView
         */
       void onUpdateLayer(Attr attr, float pcOnTopLayer, int active_i);
       boolean onTouchParentView(boolean handled) ;
       FrameLayout parentView();
      void  onAddedToContainer(Attr attr);
        /**
         *  Khi layer được thêm vào container thì nó nên được cài đặt ở vị trí nào
         * @return Giá trị pixel của Margin dưới (khoảng cách từ đỉnh layer tới MaxY)
         */
        MarginValue defaultBottomMargin();

        /**
         * Cài đặt khoảng cách giữa đỉnh layer và viền trên
         * khi layer đạt vị trí max
         * @return
         */
        enum MarginValue {
            ZERO,
            STATUS_HEIGHT,
            BELOW_NAVIGATION,
            EQUAL_NAVIGATION,
            VALUE_50_DIP,
            VALUE_100_DIP,
            VALUE_150_DIP,
            VALUE_200_DIP,
            VALUE_250_DIP,
            VALUE_300_DIP,
            VALUE_350_DIP,
            VALUE_400_DIP,
            VALUE_STT_50DIP
        }
       MarginValue maxPosition();

        /**
         *  Cài đặt khoảng cách giữa đỉnh layer và viền dưới
         *  khi layer đạt vị trí min
         * @return Giá trị pixel của Margin dưới
         */
       MarginValue minPosition();

        /**
         *  Tag nhằm phân biệt giữa các layer
         * @return String tag
         */
      String tag();
    }
    private AppCompatActivity activity;
    public float margin_inDp = 10f;
    public float margin_inPx;
    public float oneDp;
    public int[] ScreenSize;
    public float status_height=0;
    public float navigation_height =0;
    FrameLayout container;
    public TabLayerController(AppCompatActivity activity, float status_height, float navigationHeight, FrameLayout container) {
        this.activity = activity;
        oneDp = Tool.getOneDps(activity);
        margin_inPx =margin_inDp*oneDp;
        ScreenSize = Tool.getScreenSize(activity);
        this.container = container;
        listeners_size =0;
        listeners = new ArrayList<>();
        attrs = new ArrayList<>();
        this.status_height = (status_height==0)? 24*oneDp :status_height;
        this.navigation_height = navigationHeight;
        ScreenSize[1]+=navigationHeight;
        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG,"onTouchEvent");
                for(int i=0;i<listeners.size();i++ )
                    if(attrs.get(i).parent.getId()== view.getId()) return OnTouchEvent(i,view,motionEvent);
                return false;
            }
        };
    }

    /**
     * Phương thức trả về giá trị phần trăm scale khi scale view đó để đạt được hiệu quả
     * tương ứng như khi đặt margin phải-trái là marginInPx
     * @param marginInPx
     * @return
     */
    private float getScaleValueWithMargin(float marginInPx) {
        return 1 - marginInPx*2/ ScreenSize[0];
    }

    /**
     *  Cập nhật lại margin lúc pc = 1 của mỗi layer
     *  Được gọi bất cứ khi nào một pc của một layer bất kỳ được thay đổi (sự kiện chạm)
     */
    private void updateMarginLayers() {

        // Đi từ 0 - n
        // Chỉ xét những layer có pc !=0, gọi là layer hiện hoạt
        // Những layer có pc = 0 sẽ bị bỏ qua và không tính vào bộ layer, gọi là layer ẩn
        // Layer có pc !=1 nghĩa là đang có sự kiện xảy ra

        // Đếm số lượng layer hiện hoạt
        // và tìm ra on-top-layer
        // on-top-layer là layer đầu tiên được tìm thấy có pc !=0 ( thường là khác 1)
        // các layer còn lại mặc định có pc = 1
        // pc của on-top-layer ảnh hưởng lên các layer khác phía sau nó
        int onTopLayer =0;
        float pcOnTopLayer =  1;
        int active_number = 0;
        for(int i=0;i<listeners_size;i++)
            if(attrs.get(i).Pc!=0) {
            if(active_number==0) {
                onTopLayer = i;
                pcOnTopLayer = attrs.get(i).Pc;
            }
            active_number++;
            }

         // Sau đây chỉ thực hiện tính toán với các layer hiện hoạt

        // Giá trị scale mới của mỗi layer theo thứ tự
        // <br>Các layer ẩn không tính

        /*
         *  ScaleXY là giá trị tương ứng khi scale view để đạt hiệu quả
         *  tương tự khi cài đạt viền trái  để view nằm cách viền trái phải một khoảng cách mong muốn
         */
        float[] scaleXY = new float[active_number];
        /*
         *  TranslateY là giá trị cần phải translate view theo trục y (sau khi view đã scale)
         *  để đỉnh của view cách màn hình một khoảng cách mong muốn
         */
        float[] translateY = new float[active_number];

        // tính toán scale và translateY
        // Mỗi layer cách đều nhau một khoảng cách, và khoảng cách giữa layer cuối và đầu là một giá trị định trước không đổi

        // i : vị trí của layer trong layer list
        // pos : thứ tự của layer trong danh sách hiện hoạt
        int pos = -1;
        for(int i=0;i<listeners_size;i++) {
            // bỏ qua các layer ẩn
            if(attrs.get(i).Pc==0) {
                continue;
            }
            pos++;

            //            margin =margin(onTopPc = 0) + onTopPC*(margin(onTopPC = 1) - margin(onTopPC = 0))
           //     float margin = (i-1)*margin_inPx*(1- pcOnTopLayer) + pcOnTopLayer*i*margin_inPx;
            scaleXY[pos] = getScaleValueWithMargin(
                    (pos-1)*margin_inPx*(1- pcOnTopLayer)
                            + pcOnTopLayer*pos*margin_inPx);

            // khoảng cách từ đỉnh top với đỉnh màn hình, sau khi scale
            float scale_marginY = ScreenSize[1]*(1- scaleXY[pos])/2.0f;

              float need_marginY;
            if(attrs.get(i).maxTopMargin==0) { // fullscreen layer
                need_marginY = (pos==1) // just behind onToplayer
                        ?  pcOnTopLayer*(status_height + 2*oneDp)
                        : status_height + 2*oneDp + margin_inPx -   (margin_inPx*(pos-1)/(active_number-2) + pcOnTopLayer*(margin_inPx*(pos)/(active_number-1) - margin_inPx*(pos-1)/(active_number-2)));
            }
            else { // normal
                // bằng margin của cái thấp, cộng với hiệu số cái thấp cái cao nhân cho pc
                need_marginY = -(margin_inPx*(pos-1)/(active_number-2) + pcOnTopLayer*(margin_inPx*(pos)/(active_number-1) - margin_inPx*(pos-1)/(active_number-2)));
            }
            translateY[pos] = need_marginY - scale_marginY;
        }
        Attr attr;
        pos =-1;
        for(int i=0;i<listeners_size;i++) {
            attr = attrs.get(i);
            if(attr.Pc==0) continue;
            pos++;
            attr.ScaleXY = scaleXY[pos];
            attr.TranslateY = translateY[pos];
            // Scale và translate những layer phía sau
           if(pos!=0) {
               attr.parent.setScaleX(attr.ScaleXY);
               attr.parent.setScaleY(attr.ScaleXY);
               attr.parent.setTranslationY(attr.TranslateY);
             //  Log.d(TAG,"attr.ScaleXY = "+attr.ScaleXY+", attr.TranslateY = "+attr.TranslateY);
           }
           else {
               attr.parent.setScaleX(1);
               attr.parent.setScaleX(1);
               attr.parent.setTranslationY(0);
           }
           listeners.get(i).onUpdateLayer(attrs.get(i), pcOnTopLayer, pos);
        }
    }
    private void setPositionAndSizeLayer(  Attr attr, int pos, float valuePc) {
        // lưu pc
        attr.setPc(valuePc);
        // gọi hàm cài đặt vị trí của onTopLayer
        setPosAndSizeOnTopLayer(attr, pos);
        // cập nhật lại các layer phía sau
        updateMarginLayers();
    }
    private void setPosAndSizeOnTopLayer( Attr attr, int pos) {
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams) attr.parent .getLayoutParams();

        float maxMove = attr.layerSize[1] - navigation_height - attr.minBottomMargin; // đoạn đường layer có thể di chuyển : từ min -> max
        float Move = maxMove*attr.Pc; // Move từng này
        params.topMargin = (int) (ScreenSize[1] - navigation_height - attr.minBottomMargin - Move);

        // Nếu layer đi quá vị trí cao nhất, nó phải bị thay đổi độ cao (kéo dãn ra)
        if(params.topMargin<attr.maxTopMargin||isOverDone(pos)) {
            // chiều cao =
            params.height = (int) (attr.layerSize[1]+ (attr.maxTopMargin- params.topMargin));
            if(!isDown(pos)) setOverDone(pos,true);
        }
        else {
            params.height = (int) attr.layerSize[1];
        }
        attr.parent.setLayoutParams(params);
    }
    private class block_animate {
        int pos;
        boolean block_upDown;
        boolean overDone;
        boolean down;
        public block_animate(int pos) {
            this.pos = pos;
            block_upDown = true;
            overDone = false;
            down = true;
        }
    }
    /*
    Mỗi một hiệu ứng tranform cần sử dụng một vài biến để hoạt động đúng
    Mảng này thực hiện lưu trữ những biến đó ở trong block_animate có thứ tự theo thứ tự
    đăng ký của các hiệu ứng, xác định bằng biến nguyên vị trí layer của hiệu ứng đó.
     */
    private ArrayList<block_animate> blockAnimates = new ArrayList<>();
    private boolean isBlockUpDown(int pos) {
        for(int i=0;i<blockAnimates.size();i++)
            if(pos == blockAnimates.get(i).pos) return blockAnimates.get(i).block_upDown;
        return false;
    }

    private void removeBlockAnimates(int pos) {
        for(int i=0;i<blockAnimates.size();i++)
            if(pos == blockAnimates.get(i).pos) blockAnimates.remove(i);
    }
    private boolean isOverDone(int pos) {
        for(int i=0;i<blockAnimates.size();i++)
            if(pos == blockAnimates.get(i).pos) return blockAnimates.get(i).overDone;
        return false;
    }
    private void addBlockUpDown(int pos) {
        blockAnimates.add(new block_animate(pos));
    }
    private void setOverDone( int pos, boolean b) {
        for(int i=0;i<blockAnimates.size();i++)
            if(pos == blockAnimates.get(i).pos)  blockAnimates.get(i).overDone  = b;
    }
    private boolean isDown(int pos) {
        for(int i=0;i<blockAnimates.size();i++)
            if(pos == blockAnimates.get(i).pos)  return blockAnimates.get(i).down;
        return true;
    }
    private void setDown(int pos, boolean b) {
        for(int i=0;i<blockAnimates.size();i++)
            if(pos == blockAnimates.get(i).pos)  blockAnimates.get(i).down = b;
    }
    private void AnimateLayer(int pos, TabLayerCallBack l, Attr attr, float from, final float to)
    {
        if(isBlockUpDown(pos)) {
            return;
        }
         addBlockUpDown(pos);
        ValueAnimator va = ValueAnimator.ofFloat( from,to);
        int mDuration; //in millis
        float pec= Math.abs(from-to);
        if(from>to) {
            mDuration =200+(int)(attr.downDuration*pec);
            va.setInterpolator(Animation.getInterpolator(attr.downInterpolator));
            //    count = (count==28)? 0 : count+1; va.setInterpolator(Animation.getEasingInterpolator(15)); Log.d(TAG,"count = "+count);
            setDown(pos,true);
        }
        else {
            setDown(pos,false);
            mDuration = 200+(int)(attr.upDuration*pec);
            //  va.setInterpolator(new Choose_Playlist.MyBounceInterpolator(0.1, 15));
            va.setInterpolator(Animation.getInterpolator(attr.upInterpolator));
        }
        // layer đã bị kéo xuống quá vị trí min
        // đây là hiệu ứng kéo trả layer lại về vị trí cũ
        if(from<0&&to==0)
        {
                mDuration =600;
            va.setInterpolator(Animation.getInterpolator(4));
        }
        va.setDuration(mDuration);
        va.addUpdateListener(animation -> {

            float  number= ((float)(animation.getAnimatedValue()));
            setPositionAndSizeLayer(attr,pos,number);
        });
        va.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
              removeBlockAnimates(pos);
            }
        });
        va.start();
    }
    private ArrayList<TabLayerCallBack> listeners ;
    private ArrayList<Attr> attrs;
    private View.OnTouchListener onTouchListener;

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
    private boolean OnTouchEvent(int i, View view, MotionEvent event) {
        TabLayerCallBack listener = listeners.get(i);
        FrameLayout parent = attrs.get(i).parent;
        Attr attr = attrs.get(i);
        if(currentLayerEvent!=i) {
        // reset variables
            onDown = true;
            _xDelta = 0;
            _yDelta = 0;
            topMargin = 0;
            currentLayerEvent = i;
        }

        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                timeDown = System.currentTimeMillis();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) parent.getLayoutParams();
                topMargin = params.topMargin;
             //   _xDelta = X - params.leftMargin; // chênh lệch vị trí giữa event và vật
                _yDelta = Y - topMargin;
                break;

            case MotionEvent.ACTION_UP:
                long elapsedMsec = System.currentTimeMillis() - timeDown;
                if (elapsedMsec <= 300) {
                    // A Quick Touch - A Click, what should we do ?
                    if(attr.Pc<=0.2f) AnimateLayer(i,listener,attr,attr.Pc,1);
                    else if(attr.Pc>=0.8f) AnimateLayer(i,listener,attr,attr.Pc,0);
                }
                if( attr.Pc==1)
                    AnimateLayer(i,listener,attr,1,0);
                else if(attr.Pc ==0)
                    AnimateLayer(i,listener,attr,0,1);
                else if(onDown) // nếu kéo tay xuống
                {
                    if(attr.Pc <=0.8f) AnimateLayer(i,listener,attr,attr.Pc,0);
                    else     AnimateLayer(i,listener,attr,attr.Pc,1);
                }
                else // nếu kéo tay lên
                {
                    if(attr.Pc >=0.2f) AnimateLayer(i,listener,attr,attr.Pc,1);
                    else AnimateLayer(i,listener,attr,attr.Pc,0);
                }
                currentLayerEvent = -1;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
            case MotionEvent.ACTION_MOVE:
                //     layoutParams.leftMargin = X - _xDelta;
                float new_topMargin = Y - _yDelta;

                //  nếu topMargin ở quá vị trí maxTopmargin
                // thì chiều cao bị kéo dãn ra và có khoảng chênh lệch
                // over_height = new_height - old_height
                float over_height = 0;
                float new_pc;
                float maxMove = attr.layerSize[1] - navigation_height - attr.minBottomMargin; // đoạn đường layer có thể di chuyển : từ min -> max

                // lúc này over_height lớn hơn 0
                if(new_topMargin<attr.maxTopMargin) {
                    float move = attr.maxTopMargin - new_topMargin;
                    float HeSo = move/(move+100);
                    over_height = attr.maxTopMargin*HeSo;
                    new_pc = 1 + over_height/maxMove;
                } else {
                   // params.topMargin = (int) (ScreenSize[1] - navigation_height - attr.minBottomMargin - Move);
                    float Move = ScreenSize[1] -navigation_height - attr.minBottomMargin - new_topMargin;
                    new_pc = Move/maxMove;
                }
                onDown = (attr.Pc> new_pc);

                if(attr.Pc !=new_pc) {
                    setPositionAndSizeLayer(attr,currentLayerEvent,new_pc);
                    //       Log.d("Param Move : " + layoutParams.topMargin+"|"+layoutParams.bottomMargin+"|"+layoutParams.width+"|"+layoutParams.height,"Params");
                }
                break;
        }
        //   relat(R.id.root).invalidate();

        return true;
    }

    /**
     * Xử lý sự kiện nhấn nút back
     */
    public void onBackPressed() {

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
    boolean streamOnTouchEvent(String tagLayer, View view, MotionEvent motionEvent) {
        for(int i=0;i<listeners_size;i++) {
            if(attrs.get(i).Tag.equals(tagLayer)) return OnTouchEvent(i,view,motionEvent);
        }
        throw new NoSuchElementException("No Layer has that tag :\""+tagLayer+"\"");
    }

    public class Attr {
        private float[] layerSize;

        public Attr() {
            ScaleXY = 1;
            TranslateY = 0;
            upInterpolator = downInterpolator = 4;
            upDuration = 400;
            downDuration = 500;
            initDuration = 1000;

        }
        public float ScaleXY;
        public float TranslateY;
        public String Tag;
        public float Pc = 0;
        public float maxTopMargin;
        public float minBottomMargin;
        public float defaultBottomMargin;
        public int upInterpolator;
        public int downInterpolator;
        public int upDuration;
        public int downDuration;
        public int initDuration;

        public FrameLayout getParent() {
            return parent;
        }

        public Attr setParent() {
            this.parent = new SupportDarkenFrameLayout(activity);
            this.parent.setId(Prepare4Fragment.getId());
            this.parent.setOnTouchListener(onTouchListener);
            return this;
        }

        public FrameLayout parent;


        public float getScaleXY() {
            return ScaleXY;
        }

        public float getTranslateY() {
            return TranslateY;
        }

        public Attr setTranslateY(float translateY) {
            TranslateY = translateY;
            return this;
        }

        public String getTag() {
            return Tag;
        }

        public Attr setTag(String tag) {
            Tag = tag;
            return this;
        }

        public float getPc() {
            return Pc;
        }

        public Attr setPc(float pc) {
            Pc = pc;
            return this;
        }
        public Attr setDefaultBottomMargin(TabLayerCallBack.MarginValue value)
        {
            defaultBottomMargin = getValue(value);
            return this;
        }

        public float getMinBottomMargin() {
            return minBottomMargin;
        }
        public float getValue(TabLayerCallBack.MarginValue value) {
            switch (value) {
                case ZERO: return 0;
                case STATUS_HEIGHT: return status_height + 2*oneDp;
                case BELOW_NAVIGATION: return  - navigation_height;
                case EQUAL_NAVIGATION: return 0;
                case VALUE_50_DIP: return 50*oneDp;
                case VALUE_100_DIP:return 100*oneDp;
                case VALUE_150_DIP: return 150*oneDp;
                case VALUE_200_DIP:return 200*oneDp;
                case VALUE_250_DIP:return 250*oneDp;
                case VALUE_300_DIP:return 300*oneDp;
                case VALUE_350_DIP:return 350*oneDp;
                case VALUE_400_DIP:return 400*oneDp;
                case VALUE_STT_50DIP:return status_height+50*oneDp;
            }
            throw new NoSuchElementException("No Value");
        }

        public Attr setMinBottomMargin(TabLayerCallBack.MarginValue value) {
            this.minBottomMargin = getValue(value);
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
        public Attr set(TabLayerCallBack l) {
            if(l.parentView()==null)
                setParent();
            else {
                parent = l.parentView();
                parent.setOnTouchListener(onTouchListener);
            }
            this.setTag(l.tag())
                    .setDefaultBottomMargin(l.defaultBottomMargin())
                    .setMinBottomMargin(l.minPosition())
                    .setMaxTopMargin(l.maxPosition())
                    .setLayerSize();
            return this;
        }

        private void setLayerSize() {
            layerSize = new float[]{
                    ScreenSize[0], ScreenSize[1] - maxTopMargin};
            };

        public Attr setMaxTopMargin(TabLayerCallBack.MarginValue m) {
            if(m==TabLayerCallBack.MarginValue.ZERO) maxTopMargin = 0;
            else maxTopMargin = status_height + 2*oneDp + margin_inPx;
            return this;
        }
    }

    /**
     * Cài đặt vị trí ban đầu và kích cỡ cho layer
     * Thực hiện hiệu ứng đưa layer từ dưới cùng lên tới vị trí minPosition ( pc = 0)
     * hàm initLayer được thực hiện một lần, lúc nó được chèn vào controller
     * @param i
     */
    private void initLayer( int i,boolean added) {
        TabLayerCallBack listener = listeners.get(i);
        Attr attr = attrs.get(i);
        attr.set(listener);

       if(added)
       {
        attr.setPc(1);
       }
        else {
           FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
           params.height = (int) attr.layerSize[1];
           params.topMargin = (int) (ScreenSize[1] - navigation_height - attr.minBottomMargin);
           //  params.bottomMargin = (int) (params.topMargin + LayerSize[1]);

           container.addView(attr.parent, params);
           activity.getSupportFragmentManager().beginTransaction().add(attr.parent.getId(),(Fragment)listener).commit();
         //  activity.getSupportFragmentManager().beginTransaction().add(attr.parent.getId(), (Fragment)listener, ((Fragment)listener).getClass().getSimpleName());
           activity.getSupportFragmentManager().beginTransaction().commitAllowingStateLoss();
           listener.onAddedToContainer(attr);
       }

    }
    public void openLayer(TabLayerCallBack l) {

    }

    /**
     * Thực hiện hiệu ứng loại bỏ layer ra khỏi controller
     * @param i
     */
    private void removeLayer( int i) {

    }
    public void addTabLayerFragment(BaseTabLayerFragment tabLayer, int pos) {
        int p = (pos>=listeners_size) ? listeners_size : pos;
        if(listeners.size()>pos) {
            listeners.add(pos,tabLayer);
            attrs.add(pos,new Attr());
        }
        else {
            listeners.add(tabLayer);
            attrs.add(new Attr());
        }
        listeners_size++;
        tabLayer.setTabLayerController(this);
        initLayer(p,false);
    }
    public void addBaseListener(TabLayerCallBack l, int pos) {
        int p = (pos>=listeners_size) ? listeners_size : pos;
        if(listeners.size()>pos) {
            listeners.add(pos,l);
            attrs.add(pos,new Attr());
        }
        else {
            listeners.add(l);
            attrs.add(new Attr());
        }
        listeners_size++;
        initLayer(p,true);
    }
    public void removeListener(String tag) {
        for(int i=0;i<listeners_size;i++)
            if(tag.equals(attrs.get(i).Tag)) {
           removeLayer(i);
            return;
            }
    }
    public Attr getMyAttr(TabLayerCallBack l) {
        for(int i=0;i<attrs.size();i++)
            if(l.tag().equals(attrs.get(i).Tag)) return attrs.get(i);
        return null;
    }

    /**
     * This method is only used for the on-top-layer
     * @param l which layer order to update
     * @param pc new float-value-pc
     */
    public void setPc(TabLayerCallBack l, float pc) {
        for(int i=0;i<attrs.size();i++)
            if(l.tag().equals(attrs.get(i).Tag)) attrs.get(i).Pc= pc;
    }

}
