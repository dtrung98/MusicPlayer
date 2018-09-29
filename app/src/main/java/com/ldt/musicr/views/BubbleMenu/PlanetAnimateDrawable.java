package com.ldt.musicr.views.BubbleMenu;

import android.animation.TimeAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.ldt.musicr.InternalTools.Tool;
import com.ldt.musicr.MediaData.FormatDefinition.CPointF;

import android.graphics.Typeface;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class PlanetAnimateDrawable extends BaseBubbleIconDrawable {
    private static final String TAG = "PlanetAnimateDrawable";
    public PlanetAnimateDrawable(BubbleMenuUIView bev) {
        super(bev);
    }
/*
Biến thuộc tính riêng phục vụ vẽ hiệu ứng "lực hút hành tinh"
 */

private float lucHutTam;
private float lucConTro;
private float lucMaSat;
float[] kc_lucHut; //  biến khoảng cách từ vật tới vị trí lực hút tâm
    float kc_vat_va_conTro;
    // private CPointF[] TamLucHut; // Vị trí các tâm
    // private CPointF[] ViTriVat; // Vị trí hiện thời của các vật
    // private CPointF ConTro;
    // private int whichZone; // Từ 1 tới n, con trỏ đang ở vùng nào, - 1 nếu không ở vùng nào cả
    private float m;// khối lượng của mỗi vật thể

    private float ax,ay; // gia tốc
    private float[] vx,vy; // vận tốc

    @Override
    public float setTiLe() {
        return 4/3f;
    }

    private boolean calculate(float deltaTime) {
        deltaTime/= 1000f;
        float tongLuc_x, tongLuc_y;
        float lucHut_x, lucMaSat_x, lucConTro_x;
        float lucHut_y, lucMaSat_y, lucConTro_y;
        float goc_vat_va_ConTro_deg = 0;
        float goc_vat_va_ConTro_rad = 0;
        float goc_vat_va_tamLucHut;
        for(int i=0;i<number;i++) {
            //  Tính góc tạo bởi vật và TamLucHut
            goc_vat_va_tamLucHut = (float) ViTriVat[i].getAngle(TamLucHut[i]);
            goc_vat_va_tamLucHut = (float) Math.toRadians(goc_vat_va_tamLucHut);
            goc_vat_va_ConTro_deg = (float) ViTriVat[i].getAngle(ConTro);
            goc_vat_va_ConTro_rad = (float) Math.toRadians(goc_vat_va_ConTro_deg);
            // Trục Ox

            // Lực hút tâm
            if(i!=whichZone&&TamLucHut[i].x != ViTriVat[i].x)
                lucHut_x = (float) (lucHutTam * Math.cos(goc_vat_va_tamLucHut));
            else lucHut_x = 0;

            // Lực ma sát
            if (vx[i] > 0) lucMaSat_x = (float) (-lucMaSat * Math.abs(Math.cos(goc_vat_va_tamLucHut)));
            else if (vx[i] < 0) lucMaSat_x= (float) (lucMaSat * Math.abs(Math.cos(goc_vat_va_tamLucHut)));
            else lucMaSat_x = 0;

            // Lực con trỏ
            if(i==whichZone
                    )//&&ConTro.x!=ViTriVat[i].x)
                lucConTro_x = (float) (lucConTro * Math.cos(goc_vat_va_ConTro_rad));
            else lucConTro_x = 0;

            // Trục Oy
            // Lực hút tâm
            if(i!=whichZone&&TamLucHut[i].y != ViTriVat[i].y)
                lucHut_y = (float) (lucHutTam * Math.sin(goc_vat_va_tamLucHut));
            else lucHut_y = 0;

            // Lực ma sát
            if (vy[i] > 0) lucMaSat_y = (float) (-lucMaSat * Math.abs(Math.sin(goc_vat_va_tamLucHut)));
            else if (vy[i] < 0) lucMaSat_y= (float) (lucMaSat * Math.abs(Math.sin(goc_vat_va_tamLucHut)));
            else lucMaSat_y = 0;

            // Lực con trỏ
            if(i==whichZone
                    )//&&ConTro.y!=ViTriVat[i].y)
                lucConTro_y = (float) (lucConTro * Math.sin(goc_vat_va_ConTro_rad));
            else lucConTro_y = 0;

            // Tính chuyển động
            tongLuc_x = lucHut_x + lucConTro_x + lucMaSat_x;
            tongLuc_y = lucHut_y + lucConTro_y + lucMaSat_y;

            ax = tongLuc_x/m;
            ay = tongLuc_y/m;

            ViTriVat[i].x += vx[i] * deltaTime + 1 / 2 * ax * deltaTime * deltaTime;
            vx[i] += ax * deltaTime;
            ViTriVat[i].y += vy[i] * deltaTime + 1 / 2 * ay * deltaTime * deltaTime;
            vy[i] += ay * deltaTime;
         if(i==whichZone) Log.d(TAG,"vx[i] = "+vx[i]+", vy[i] = "+vy[i]);
        }
        // Vẽ lại nếu có sự dịch chuyển của bất cứ vật nào
        // drawVisualWave again if there're are anything changed
        for (int i = 0; i < number; i++)
            if (vx[i] != 0 || vy[i] != 0)
                return true;
        return false;
    }
   private boolean calculate(float deltaTime, float total) {

        deltaTime /= 1000.f;
        total /= 1000.f;
        float tongLuc_x, tongLuc_y;
        float goc_vat_va_ConTro_deg = 0;
        float goc_vat_va_ConTro_rad = 0;
        for (int i = 0; i < number; i++) {
            // Tính lực tác dụng theo 2 trục tọa độ
            tongLuc_x = 0;
            tongLuc_y = 0;

            //  Tính góc tạo bởi vật và TamLucHut
            float goc_vat_va_tamLucHut = (float) ViTriVat[i].getAngle(TamLucHut[i]);
            goc_vat_va_tamLucHut = (float) Math.toRadians(goc_vat_va_tamLucHut);

            // tính trên trục x

            //  Nếu nằm trong zone thì chịu lực con trỏ
            // nằm ngoài thì chịu lực hút tâm
            if (i != whichZone) {
                if (TamLucHut[i].x != ViTriVat[i].x)
                    tongLuc_x += lucHutTam * Math.cos(goc_vat_va_tamLucHut);
            } else {
                //goc_vat_va_ConTro_deg = ViTriVat[i].fromCPointFToDegree_From0h(ConTro);
                goc_vat_va_ConTro_deg = (float) ViTriVat[i].getAngle(ConTro);
                goc_vat_va_ConTro_rad = (float) Math.toRadians(goc_vat_va_ConTro_deg);

                float kc_vat_va_conTro = (float)
                        Math.sqrt(
                                Math.pow(ConTro.x - ViTriVat[i].x, 2)
                                        + Math.pow(ConTro.y - ViTriVat[i].y, 2));
                float lucConTro_x = (float) (lucConTro * Math.cos(goc_vat_va_ConTro_rad));
             //   Log.d(TAG, "lucConTro_x = " + lucConTro_x + ", goc = " + goc_vat_va_ConTro_rad);
                tongLuc_x += lucConTro_x;//*(banKinh-kc_vat_va_conTro+1)/banKinh;
            }
            if (vx[i] > 0) tongLuc_x += -lucMaSat * Math.abs(Math.cos(goc_vat_va_tamLucHut));
            else if (vx[i] < 0) tongLuc_x += lucMaSat * Math.abs(Math.cos(goc_vat_va_tamLucHut));

            //  Oy
            if (i != whichZone) {
                if (TamLucHut[i].y != ViTriVat[i].y)

                    tongLuc_y += lucHutTam * Math.sin(goc_vat_va_tamLucHut);
            } else {
                kc_vat_va_conTro = (float)
                        Math.sqrt(
                                Math.pow(ConTro.x - ViTriVat[i].x, 2)
                                        +
                                        Math.pow(ConTro.y - ViTriVat[i].y, 2));
                tongLuc_y += lucConTro * Math.sin(goc_vat_va_ConTro_rad);//*(banKinh-kc_vat_va_conTro+1)/banKinh;
            }
            if (vy[i] > 0) tongLuc_y += -lucMaSat * Math.abs(Math.sin(goc_vat_va_tamLucHut));
            else if (vy[i] < 0) tongLuc_y += lucMaSat * Math.abs(Math.sin(goc_vat_va_tamLucHut));
            ax = tongLuc_x / m;
            ay = tongLuc_y / m;

            float new_x = vx[i] * deltaTime + 1 / 2 * ax * deltaTime * deltaTime;
            float new_y = vy[i] * deltaTime + 1 / 2 * ay * deltaTime * deltaTime;
            /*
            Nếu new_x  >= ConTro.x && ConTro.x > old_x
            Hoặc new_x <= ConTro.x && ConTro.x < old_x
            => Vật vừa đi qua ConTro.x
            Nếu vx < giá trị vx tối thiểu
            thì : new_x = ConTro.x && vx = 0
             */
            if(whichZone==i) {
            if((new_x >=ConTro.x&&ConTro.x>ViTriVat[i].x)||(new_x<=ConTro.x&&ConTro.x<ViTriVat[i].x))
             //   Log.d(TAG, "vx[i] = " + vx[i]);
            if(Math.abs(vx[i])<=1000) {
                ViTriVat[i].x = ConTro.x;
                continue;
            }
            }



            ViTriVat[i].x += vx[i] * deltaTime + 1 / 2 * ax * deltaTime * deltaTime;
            vx[i] += ax * deltaTime;
            ViTriVat[i].y += vy[i] * deltaTime + 1 / 2 * ay * deltaTime * deltaTime;
            vy[i] += ay * deltaTime;
            //}
//        if(i==1) Log.d(TAG, "ViTriVat[1] = [" + ViTriVat[1].x+", "+ViTriVat[1].y);
        }
        //     Log.d(TAG,"a[0] = {"+ViTriVat[0].x+", "+ViTriVat[0].y+"}; a[1] = {"+ViTriVat[1].x+", "+ViTriVat[1].y+"; a[2] = {"+ViTriVat[2].x+", "+ ViTriVat[2].y+"}, delta = "+deltaTime);

        for (int i = 0; i < number; i++)
            if (vx[i] != 0 || vy[i] != 0)
                return true;
        return false;
    }
    public void drawStringMenu(Canvas canvas) {
        if(whichZone==-1) return;
        /*
        Tính toán vị trí của các dòng String menu
        được in lên

        +. Ưu tiên in lên phía trên điểm chạm
        +. dòng text được căn lề ngược với phía của điểm chạm (căn lề phải nếu điểm chạm lệch về bên trái, lề trái nếu điểm chạm lệch về bên phải

         */

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
        x -= (ConTro.x- ViTriVat[whichZone].x)/3;
        y -= (ConTro.y- ViTriVat[whichZone].y)/3;
        if(LeftAlign)
            solid_Paint.setTextAlign(Paint.Align.LEFT);else solid_Paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(attr.menu_string[whichZone],x,y,solid_Paint);
    }
    @Override
    public void set() {
        super.set();
        TamLucHut = new CPointF[number];
        ViTriMoi = new CPointF[number];
        ViTriVat = new CPointF[number];
        for (int i = 0; i < number; i++) {
            TamLucHut[i] = new CPointF(attr.item_pos[i]);
            ViTriVat[i] = new CPointF(TamLucHut[i]);
            ViTriMoi[i] = new CPointF(0,0);
    }
        ConTro = new CPointF(attr.getTouch_runtime()[0],attr.getTouch_runtime()[1]);
        whichZone = -1;

        kc_lucHut = new float[number];

        vx = new float[number];
        vy = new float[number];

        m = 1;
        lucConTro = 6000;
        lucHutTam = 4000;
        lucMaSat = 2000;
        GiaTri_Tam = bubbleRadius - attr.oneDp;
        GiaTri_VongTrong = attr.menu_satellite_radius- GiaTri_Tam;
        GiaTri_VongNgoai = GiaTri_VongTrong*3/2;

        banKinh = attr.menu_satellite_radius/2;
      //  textView = new TextView(MCBubblePopupUI.getContext());
        typeface = Typeface.create(Typeface.DEFAULT,Typeface.BOLD);
        init_time_animator();
    }
    TimeAnimator ta;
    private void init_time_animator() {
        ta = new TimeAnimator();

        ta.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator timeAnimator, long l, long l1) {
                if(calculate(l1)) invalidate();
            }
        });
        ta.start();
    }
    /*
        Được gọi mỗi khi Drawable được vẽ lại
         */
    float banKinh;
    float focusWidth;
    private Typeface typeface;
    @Override
    public void draw(Canvas canvas) {
        float pc = MCBubblePopupUI.getBackgroundAlphaPercent();
        if (pc < 0) pc = 0;
        float alpha_after = (pc > 1) ? 255 : pc * 255;

        drawStringMenu(canvas);

        if(kc_vat_va_conTro>banKinh) focusWidth = minBitmapWidth;
        else focusWidth = (banKinh-kc_vat_va_conTro)/banKinh*(tile-1)*minBitmapWidth + minBitmapWidth;
   //     solid_Paint.setAlpha((int) alpha_after);
        for (int i = 0; i < number; i++) {
            if(i!=whichZone)
                canvas.drawBitmap(normalBitmap[i], new Rect(0, 0, (int) realBitmapWidth, (int) realBitmapWidth),
                        new RectF(
                                ViTriVat[i].x - minBitmapWidth / 2,
                                ViTriVat[i].y - minBitmapWidth / 2,
                                ViTriVat[i].x + minBitmapWidth / 2,
                                ViTriVat[i].y + minBitmapWidth / 2), null);
            //   canvas.drawCircle(attr.item_pos[i].x,attr.item_pos[i].y,5*attr.oneDp,solid_Paint);
        }
        if (whichZone!=-1)
            canvas.drawBitmap(focusBitmap[whichZone], new Rect(0, 0, (int) realBitmapWidth, (int) realBitmapWidth),
                    new RectF(
                            ViTriVat[whichZone].x - focusWidth / 2,
                            ViTriVat[whichZone].y - focusWidth / 2,
                            ViTriVat[whichZone].x + focusWidth / 2,
                            ViTriVat[whichZone].y + focusWidth / 2), null);

    }
    private boolean isInTriangle(int zone, float x, float y) {
        float angle = attr.touch.fromCPointFToDegree_From0h(x,y);
        //Log.d(TAG, "angle = " + angle);
        if ( attr.item_angle[zone] - attr.eachAngle/2<=angle && angle < attr.item_angle[zone] + attr.eachAngle/2)
            // angle of each item        angle range of each item
            return true;
        return false;
    }

    private boolean isInCycler(int zone, float x, float y) {
        float distance = (float) attr.touch.getDistance(ConTro);
        if(distance>=GiaTri_Tam&&distance<=GiaTri_Tam+GiaTri_VongTrong+GiaTri_VongNgoai/2)
        return true;
        return false;
    }
    private CPointF[] TamLucHut; // Vị trí các tâm
    private CPointF[] ViTriVat; // Vị trí hiện thời của các vật
    private CPointF[] ViTriMoi;
    private CPointF ConTro;
    private int whichZone; // Từ 1 tới n, con trỏ đang ở vùng nào, - 1 nếu không ở vùng nào cả
    float GiaTri_Tam ;
    float GiaTri_VongTrong ;
    float GiaTri_VongNgoai ;
    private int findZone() {
        for(int i=0;i<number;i++) {
            boolean one = isInCycler(i,ConTro.x,ConTro.y);
            boolean two  = isInTriangle(i,ConTro.x,ConTro.y);
            // Log.d(TAG, "[" +i+"] : inCirle = "+one+", inTriangle = "+two );
            if(one&&two) return i;
        }
        return -1;
    }
    boolean ConTroThayDoi = false;
    @Override
    public boolean updateTouchRuntime(boolean b) {
        // được gọi để tính toán trạng thái, trả về true để xác nhận có sự thay đổi ( cần vẽ lại)
        float x = attr.touch_runtime[0];
        float y = attr.touch_runtime[1];
        if(x!= ConTro.x&&y!=ConTro.y) {
            ConTroThayDoi = true;
            ConTro.set(x,y);
        }
        whichZone = findZone();
        //(whichZone!= whichSelect()) Tool.showToast(this.MCBubblePopupUI.getContext(),"New Zone : "+ whichZone,500);
        whichSelected = whichZone;
        return true;
    }

/*
Tùy chọn : chọn biểu tượng chạm cho drawable
 */
    @Override
    public void setIconView(ImageView iconView) {

    }
/*
Free bộ nhớ
 */
    @Override
    public void destroy() {
        ta.end();
    }
/*

 */



}
