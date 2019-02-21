package com.ldt.musicr.ui.widget.bubblepopupmenu;

import android.animation.ValueAnimator;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.ldt.musicr.util.uitool.Animation;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.mediadata.oldmodel.CPointF;


/**
 * Created by trung on 11/2/2017.
 *  Lớp đối tượng "vật thể" là các icon lựa chọn
 */

public class BounceAnimateDrawable extends BaseBubbleIconDrawable {
    private final static String TAG = "BounceAnimateDrawable";

    public BounceAnimateDrawable(BubbleMenuUIView bev) {
        super(bev);
    }
   // private float tile = 5/4f;

    // Đoạn đường đi của bức bitmap
    // khi được phóng to dần lên
    // Đi được xa x* dp khi phóng to hết cỡ
    private float go = 15; //x * oneDp

    // Vị trí và mức độ phóng to hiện tại và sắp tới của các vật
    private CPointF ViTriHienTai[];
    private float ZoomHienTai[];
    private CPointF ViTriMoi[];
    private CPointF ViTriCu[];
    private float ZoomMoi[]; // Từ 1 -> tile == 5/4
    private float ZoomCu[];
    private CPointF ViTriMax[];
    private CPointF ViTriMin[];


    @Override
    public float setTiLe() {
        //  Tỉ lệ giữa 2 bức bitmap nhỏ nhất và lớn nhất có thể được vẽ lên màn hình
        // Chính là độ rộng "thật" của bức bitmap đó.
        return 5/4f;
    }
    public void drawStringMenu(Canvas canvas) {
        if(whichSelected==-1) return;
        /*
        Tính toán vị trí của các dòng String menu
        được in lên

        +. Ưu tiên in lên phía trên điểm chạm
        +. dòng text được căn lề ngược với phía của điểm chạm (căn lề phải nếu điểm chạm lệch về bên trái, lề trái nếu điểm chạm lệch về bên phải

         */
        float kc = (float) ViTriHienTai[whichSelected].getDistance(ConTro);
        if(kc>=banKinh)ZoomHienTai[whichSelected] =1;
        else ZoomHienTai[whichSelected] = 1 + (tile -1)*(1- kc/banKinh);
        float focusWidth = minBitmapWidth*ZoomHienTai[whichSelected];
        boolean LeftAlign = (attr.touch.x>Tool.getScreenSize(true)[0]/2);
        float textHeight = (float) (35*attr.oneDp*(focusWidth/minBitmapWidth));
        // Khoảng cách giữa điểm text và điểm chạm
        float Distance = GiaTri_Tam + GiaTri_VongTrong+GiaTri_VongNgoai/2 + 10*attr.oneDp;
        boolean OnTop = true;
        float margin = 25*attr.oneDp;
        solid_Paint.setTextSize(textHeight);
        solid_Paint.setColor(0xf0555555);
        //solid_Paint.setFakeBoldText(true);
        solid_Paint.setTypeface(Typeface.DEFAULT_BOLD);
        float x = (LeftAlign) ? margin : Tool.getScreenSize(true)[0] - margin;
        float y = attr.touchY - Distance -textHeight;
        x -= (ConTro.x- ViTriHienTai[whichSelected].x)/3;
        y -= (ConTro.y- ViTriHienTai[whichSelected].y)/3;
        if(LeftAlign)
            solid_Paint.setTextAlign(Paint.Align.LEFT);else solid_Paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(attr.menu_string[whichSelected],x,y,solid_Paint);
    }

    @Override
    void draw(Canvas canvas) {
        drawStringMenu(canvas);
        for(int i = 0;i<number;i++)
        {
            // zoom = (1-khoangCach/BanKinh)*tiLe;;=
            float kc = (float) ViTriHienTai[i].getDistance(ConTro);
            if(kc>=banKinh)ZoomHienTai[i] =1;
            else ZoomHienTai[i] = (float) (1 + (tile -1)*(1- Math.pow(kc/banKinh,3)));
            float w = minBitmapWidth*ZoomHienTai[i];
            if(i==whichSelected)
                canvas.drawBitmap(focusBitmap[i], new Rect(0, 0, (int) realBitmapWidth, (int) realBitmapWidth),
                        new RectF(
                                ViTriHienTai[i].x -w / 2,
                                ViTriHienTai[i].y - w / 2,
                                ViTriHienTai[i].x + w / 2,
                                ViTriHienTai[i].y + w / 2), null);
            else
            canvas.drawBitmap(normalBitmap[i], new Rect(0, 0, (int) realBitmapWidth, (int) realBitmapWidth),
                    new RectF(
                            ViTriHienTai[i].x -w / 2,
                            ViTriHienTai[i].y - w / 2,
                            ViTriHienTai[i].x + w / 2,
                            ViTriHienTai[i].y + w / 2), null);
        }
    }
    CPointF getViTriMax(int which) {
        int i = which;
        CPointF c = new CPointF(0,0);
        float angle = (float) attr.touch.getAngle(ViTriMin[i]);
        angle = (float) Math.toRadians(angle);
        c.x = (float) (ViTriMin[i].x + Math.cos(angle)*go);
        c.y = (float) (ViTriMin[i].y + Math.sin(angle)*go);
        return c;
    }
    @Override
    public void set() {
        super.set();
        go = go*attr.oneDp;
        ConTro.x = attr.touchX;
        ConTro.y = attr.touchY;
        ViTriHienTai = new CPointF[number];
        ZoomHienTai = new float[number];
        ViTriMoi = new CPointF[number];
        ViTriCu = new CPointF[number];
        ZoomMoi = new float[number];
        ZoomCu = new float[number];
        va = new ValueAnimator[number];
        inAnimation = new boolean[number];
        ViTriMax = new CPointF[number];
        ViTriMin = new CPointF[number];
        for(int i=0;i<number;i++) {

            ViTriHienTai[i] = new CPointF(attr.item_pos[i]);
            ViTriCu[i] = new CPointF(ViTriHienTai[i]);
            ViTriMoi[i] = new CPointF(ViTriHienTai[i]);
            ZoomHienTai[i] = 1;
            ZoomMoi[i] = 1;
            va[i] = ValueAnimator.ofFloat(0,1);
            inAnimation[i] = false;
            ViTriMin[i] = new CPointF(ViTriHienTai[i]);
            ViTriMax[i] = new CPointF(getViTriMax(i));
        }

        GiaTri_Tam = bubbleRadius - attr.oneDp;
        GiaTri_VongTrong = attr.menu_satellite_radius- GiaTri_Tam+go;
        GiaTri_VongNgoai = GiaTri_VongTrong*3/2;
        setSpring();

    }

    private boolean calculate(float delta,float total) {

        return true;
    }

    @Override
    void setIconView(ImageView iconView) {

    }

    @Override
    void destroy() {

    }
    // kiểm tra xem điểm (x,y) có nằm trong "vùng tam giác" zone hay không
    private boolean isInTriangle(int zone, float x, float y) {
        float angle = attr.touch.fromCPointFToDegree_From0h(x,y);
        //Log.d(TAG, "angle = " + angle);
        if ( attr.item_angle[zone] - attr.eachAngle/2<=angle && angle < attr.item_angle[zone] + attr.eachAngle/2)
            // angle of each item        angle range of each item
            return true;
        return false;
    }

    // Kiểm tra xem điểm (x,y) có nằm trong "vùng tròn zone" bán kính banKinh hay không
    float banKinh =0;
    private boolean isInCycler(int zone, float x, float y) {
        if(banKinh ==0) {
          banKinh = attr.menu_satellite_radius/2;
        }
        float kc_vat_conTro = (float)
                Math.sqrt(
                        Math.pow(x - attr.item_pos[zone].x, 2)
                                +
                                Math.pow(y - attr.item_pos[zone].y, 2)
                );
        if(kc_vat_conTro<= banKinh) return  true;
        return false;
    }
    // Tính toán duration phù hợp với khoảng cách di chuyển
    // Tối đa là maxDuration = 350ms
    // Khoảng thời gian dài nhất của một hiệu ứng "transform"
    private float maxDuration = 350;
    private int getDuration(int which) {
        int i = which;

        return (int) (maxDuration*Math.abs(ViTriHienTai[i].getDistance(ViTriMoi[i]))/(GiaTri_VongNgoai));
    }
    private ValueAnimator[] va;
    private boolean[] inAnimation;
    private SpringAnimation[] sA;
    public class SpringAnimation {
        Spring springX,springY;
        private boolean setX = false, setY = false;
        private View v;
        private SpringAnimation ViewParent(View v) {
            this.v = v;
            return this;
        }
        private void setNewX(float x) {
            p.x = x;
            if(!invalidate())
                setX = true;
        }
        private void setNewY(float y) {
            p.y = y;
            if(!invalidate())
                setY = true;
        }
        private boolean invalidate() {
            if(setX&&setY) {
                v.invalidate();
                return true;
            }
            else return false;
        }
        public SpringAnimation(SpringSystem ss) {
            springX = ss.createSpring();
            springY = ss.createSpring();
            springX.setOvershootClampingEnabled(true);
            springY.setOvershootClampingEnabled(true);
            SpringConfig sc = springX.getSpringConfig();
            sc.tension = 280;
         //   sc.friction = 4;
            springX.setSpringConfig(sc);
            springY.setSpringConfig(sc);
            Log.d(TAG,"Tension"+sc.tension+",Friction  = "+sc.friction);
            springX.addListener(new SpringListener() {
                @Override
                public void onSpringUpdate(Spring spring) {
                    setNewX((float) spring.getCurrentValue());
                }

                @Override
                public void onSpringAtRest(Spring spring) {

                }

                @Override
                public void onSpringActivate(Spring spring) {

                }

                @Override
                public void onSpringEndStateChange(Spring spring) {

                }
            });
            springY.addListener(new SpringListener() {
                @Override
                public void onSpringUpdate(Spring spring) {
                 setNewY((float) spring.getCurrentValue());
                }

                @Override
                public void onSpringAtRest(Spring spring) {

                }

                @Override
                public void onSpringActivate(Spring spring) {

                }

                @Override
                public void onSpringEndStateChange(Spring spring) {

                }
            });
        }
        public SpringAnimation setCurrentValue(float x, float y) {
            springX.setCurrentValue(x);
            springY.setCurrentValue(y);
            return this;
        }
        public SpringAnimation AnimTo(float x, float y) {
            springX.setEndValue(x);
            springY.setEndValue(y);
            return this;
        }
        CPointF p;
        public SpringAnimation setPosObject(CPointF p) {
            this.p = p;
            setCurrentValue(p.x,p.y);
            return this;
        }
    }
    private void setSpring() {
        SpringSystem ss = SpringSystem.create();
        sA= new SpringAnimation[number];
        for(int i=0;i<number;i++) {
            sA[i]= new SpringAnimation(ss);
            sA[i].setPosObject(ViTriHienTai[i])
                    .ViewParent(MCBubblePopupUI);
        }
    }
    private void doReboundAnimation(int which) {
        int i=which;
        sA[i].AnimTo(ViTriMoi[i].x,ViTriMoi[i].y);
    }
    private void doAnimation(int which) {
        int i = which;

      //  Đã cài đặt vị trí mới
        // Đã cài đặt zoom mới
     //   va[i] = ValueAnimator.ofFloat(0,1);
        /*
        if(Math.abs(ViTriMoi[i].getDistance(ViTriHienTai[i]))<attr.oneDp) {
            ViTriHienTai[i].x = ViTriMoi[i].x;
            ViTriHienTai[i].y = ViTriMoi[i].y;
            ZoomHienTai[i] = ZoomMoi[i];
            inAnimation[i] = false;
            return;
        }
        */

        va[i] = ValueAnimator.ofFloat(0,1);
      va[i].setDuration(getDuration(i));
     va[i].setInterpolator(Animation.getEasingInterpolator(4));
    ViTriCu[i].set(ViTriHienTai[i].x,ViTriHienTai[i].y);
    ZoomCu[i] = ZoomHienTai[i];
        Log.d(TAG,"new animation "+i+", from "+ ViTriCu[i] +" to "+ ViTriMoi[i]+", duration = "+ va[i].getDuration());
/*
      va[i].addBaseListener(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animator) {
       Log.d(TAG,"Animation "+i+" with duration = "+animator.getDuration()+" start");
              inAnimation[i] = true;
          }

          @Override
          public void onAnimationEnd(Animator animator) {
              inAnimation[i] = false;
              Log.d(TAG,"Animation "+i+" end");
          }

          @Override
          public void onAnimationCancel(Animator animator) {
              inAnimation[i] =false;
              Log.d(TAG,"Animation "+i+" cancelAndUnBind");

          }

          @Override
          public void onAnimationRepeat(Animator animator) {

          }
      });
*/
        va[i].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float pc = (float)valueAnimator.getAnimatedValue();
              //  Log.d(TAG, "pc = " + pc+", go_x = "+go_x);
                ViTriHienTai[i].x =ViTriCu[i].x + (ViTriMoi[i].x - ViTriCu[i].x) *pc;
                ViTriHienTai[i].y =ViTriCu[i].y + (ViTriMoi[i].y - ViTriCu[i].y) *pc;

                ZoomHienTai[i] = ZoomCu[i] + (ZoomMoi[i] - ZoomCu[i])*pc;
                invalidate();
                if(pc==1) {
                    inAnimation[i] = false;
                    Log.d(TAG,"Animaton "+i+" end");
                }
            }
        });
        inAnimation[i] = true;
        va[i].start();
    }
    CPointF ConTro= new CPointF(0,0);

    float GiaTri_Tam ;
    float GiaTri_VongTrong ;
    float GiaTri_VongNgoai ;
    private void calculateAndDoAnimation() {

        if(attr.touch_runtime[0]==ConTro.x&&attr.touch_runtime[1]==ConTro.y) return;
        ConTro.x = attr.touch_runtime[0];
        ConTro.y = attr.touch_runtime[1];
        whichSelected = -1;
        for(int i=0;i<number;i++) {
            boolean needToAnimation = false;
            if(isInTriangle(i,ConTro.x,ConTro.y)) // nằm trong vùng tam giác
            {
                if(isInCycler(i,ConTro.x,ConTro.y)) whichSelected = i;
                // khoảng cách từ tâm chạm tới con trỏ
                // 3 trường hợp
                // +. Ở vòng trong bán kính chạm : không làm gì cả
                // +. ở vòng trong vị trí max vật tới bán kính chạm
                // +/ ở vòng ngoài vị trí max tới điểm cực
                float distance = (float) attr.touch.getDistance(ConTro);
                //     float GiaTri_Tam = minBitmapWidth - attr.oneDp;
                //   float GiaTri_VongTrong = attr.menu_satellite_radius- GiaTri_Tam;
                //   float GiaTri_VongNgoai = GiaTri_VongTrong*3/2;
                if (distance > GiaTri_Tam && distance <= GiaTri_VongTrong + GiaTri_Tam) // Vòng trong
                {
                    float new_pc = (distance - GiaTri_Tam) / GiaTri_VongTrong;
                    ViTriMoi[i].x = ViTriMin[i].x + (ViTriMax[i].x - ViTriMin[i].x) * new_pc;
                    ViTriMoi[i].y = ViTriMin[i].y + (ViTriMax[i].y - ViTriMin[i].y) * new_pc;
                    ZoomMoi[i] = 1 + (tile - 1) * new_pc;
                    needToAnimation = true;
                }
                // Vòng ngoài
                else if (distance > GiaTri_VongTrong + GiaTri_Tam && distance <= GiaTri_VongNgoai + GiaTri_VongTrong + GiaTri_Tam) {
                    float new_pc =1 - (distance - GiaTri_Tam - GiaTri_VongTrong ) / GiaTri_VongNgoai;
                    ViTriMoi[i].x = ViTriMin[i].x + (ViTriMax[i].x - ViTriMin[i].x) * new_pc;
                    ViTriMoi[i].y = ViTriMin[i].y + (ViTriMax[i].y - ViTriMin[i].y) * new_pc;
                    ZoomMoi[i] = 1 + (tile - 1) * new_pc;
                    needToAnimation = true;

                }
                else if(ViTriHienTai[i].x!=ViTriMin[i].x||ViTriHienTai[i].y!=ViTriMin[i].y) {
                    ViTriMoi[i].x = ViTriMin[i].x;
                    ViTriMoi[i].y = ViTriMin[i].y;
                    ZoomMoi[i]=1;
                    needToAnimation = true;
                }
            }
            else if(ViTriHienTai[i].x!=ViTriMin[i].x||ViTriHienTai[i].y!=ViTriMin[i].y) {
                //  Thực hiện đưa vật về vị trí cũ
                ViTriMoi[i].x = ViTriMin[i].x;
                ViTriMoi[i].y = ViTriMin[i].y;
                ZoomMoi[i]=1;
                needToAnimation = true;
            }

            /*
            if(va[i].isRunning()&&needToAnimation) {
                // Tắt animation cũ
                // tạo một animation mới
                Log.d(TAG,"Animation "+i+" cancelAndUnBind");
              //  va[i].cancelAndUnBind();
           // }
          //  doAnimation(i);
              */
            if(needToAnimation)
                doReboundAnimation(i);
        }
    }
    @Override
    public boolean updateTouchRuntime(boolean b) {
        //  Hàm này được gọi khi có sự kiện chạm
        // Từ con trỏ mới, hãy tính toán vị trí/ zoom mới cho các vật
        // nếu có thay đổi thì thực hiện hiệu ứng "transform"
        calculateAndDoAnimation();
        return true;
    }
}
