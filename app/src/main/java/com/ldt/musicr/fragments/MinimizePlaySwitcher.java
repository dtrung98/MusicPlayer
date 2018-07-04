package com.ldt.musicr.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.views.MinimizeButton;


public class MinimizePlaySwitcher {
    public interface PlayControlListener {
        int getBarWidth();
        int getNavigationHeight();
        int getBarHeight();
        Bitmap getBitmapRounded(float rounded,Bitmap original);
        float getCurrentRadius();
        // lấy màu viền và màu nền
        int getOutlineColor();
        int getBackColor();
        // láy bức ảnh gốc
        Bitmap getOriginalBitmap();
        // lấy bức ảnh hiện hành
        ImageView getCurrentView();
        // thông báo bắt đầu hiệu ứng minimize
        View getRootLayout();
        void beginSwitchMinimize();
        // thông báo kết thúc hiệu ứng trở về bình thường
        void finishSwitchNormal();

    }
    private static MinimizePlaySwitcher mpf;
     // Gọi hàm này khi muốn minimize page controller
     public static void switchToMinimizeMode(PlayControlListener listener) {
         mpf = new MinimizePlaySwitcher();
        mpf.setPlayController(listener);
        mpf.establish();
    }

    // Gọi hàm này khi muốn switch về chế độ thường
    public static void switchToNormalMode() {
         mpf.mButton.switchOff(mpf);
         mpf = null;

    }
    // class thực hiện việc minimize page controller
    // toàn bộ dưới đây đều private, class chỉ hỗ trợ gọi 2 hàm ở trên.
     public PlayControlListener listener;
    // View gốc chứa giao diện
    public FrameLayout rootLayout;
    public MinimizeButton mButton;
    private boolean inZone= false;
    @SuppressLint("ClickableViewAccessibility")
    private void establish() {
        rootLayout = (FrameLayout) listener.getRootLayout();
       mButton = new MinimizeButton(((Fragment)listener).getActivity());
       mButton.setProperties(listener);
       int  oneDp = Tool.getOneDps(null);
       int _height = listener.getBarHeight()+listener.getNavigationHeight()+oneDp*100;
       FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,_height);
       int[] ss = Tool.getScreenSize(true);
       params.topMargin = ss[1]-_height+ listener.getNavigationHeight() ;
       rootLayout.addView(mButton, params);

       mButton.switchOn();
       mButton.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               // Chạm 2 lần để dừng/phát
               // Nhấn giữ để lựa chọn các tùy chọn
               MinimizeButton m = (MinimizeButton) v;
               float x = event.getX();
               float y = event.getY();
               if(inZone) return false;
               inZone = x >= m.endLeftBack && y >= m.endTopBack;
               if(inZone) {
                   switchToNormalMode();
                   return true;
               }
               return false;
           }
       });
       /*
       mButton.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {


           }
       } );
       */
       listener.beginSwitchMinimize();
    }
     private void setPlayController(PlayControlListener listener) {
        this.listener = listener;
    }
    private MinimizePlaySwitcher() {

    };
}
