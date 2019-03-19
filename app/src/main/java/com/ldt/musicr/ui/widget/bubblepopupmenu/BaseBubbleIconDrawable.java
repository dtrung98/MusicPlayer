package com.ldt.musicr.ui.widget.bubblepopupmenu;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.widget.ImageView;

import com.ldt.musicr.util.BitmapEditor;
import com.ldt.musicr.util.Tool;

import com.ldt.musicr.mediadata.oldmodel.C_RectF;

/*
 định nghĩa cách hành xử của các icon động
 */
public abstract class BaseBubbleIconDrawable {
    private static final String TAG = "BaseBubbleIconDrawable";

    public BaseBubbleIconDrawable(BubbleMenuUIView bev) {
        this.MCBubblePopupUI = bev;
        attr = MCBubblePopupUI.mcAttributes;
        from_rect = new C_RectF(0, 0, 0, 0);
        gradient_shadow_shader = new RadialGradient(0, 0, attr.length_dp_50 / 4, 0x10ffffff, 0x50ffffff, Shader.TileMode.MIRROR);
        gradient_shadow_Paint = new Paint();
        gradient_shadow_Paint.setAntiAlias(true);
        gradient_shadow_Paint.setShader(gradient_shadow_shader);

        solid_Paint = new Paint();
        solid_Paint.setAntiAlias(true);
        solid_Paint.setColor(0xaaaaaaaa);

    }

    /*
    truyền vào canvas để vẽ icon lên
     */
    abstract void draw(Canvas canvas);

    /*
     kêu gọi view cha vẽ lại
     */
    abstract void setIconView(ImageView iconView); // ???

    abstract void destroy(); // hủy icon, dùng khi free bộ nhớ


    protected int whichSelect() {
        return whichSelected;
    }

    public void set_source(float x, float y, float w, float h) {
        from_rect.x = x;
        from_rect.y = y;
        from_rect.width =w;
        from_rect.height = h;
    }

    // đổ bóng cho icon
    protected Shader gradient_shadow_shader;
    protected Paint gradient_shadow_Paint;
    // bút vẽ màu
    protected Paint solid_Paint;
    // ???
    ImageView iconView;
    //
    public float wish_width;
    // hình chữ nhật ???
    protected C_RectF from_rect;
    protected boolean haveIcon = false;
    protected BubbleMenuUIView.MCAttributes attr;
    protected BubbleMenuUIView MCBubblePopupUI;

    public final void invalidate() {
        MCBubblePopupUI.invalidate();
    }

    int whichSelected = -1;
    // bitmap lúc không chọn và lúc chọn
    protected Bitmap[] normalBitmap, focusBitmap;
    // bitmap background
    protected Bitmap backIcon, focusBackIcon;
    protected float wIcon, hIcon;

    protected int focusColor = 0xffD4610A;
    float tile = 1.5f;
    float realBitmapWidth;
    float minBitmapWidth;
    float bubbleRadius;
    public float setTiLe() {
        return 1.5f;
    }
    private Bitmap getIconBackground(boolean normal) {
        // Bức hình bitmap có kích thước = kích thướng to nhất của icon ( lúc được chọn và nó dc phóng to nhất)
        minBitmapWidth = attr.menu_item_width;
        realBitmapWidth = attr.menu_item_width * tile;

        // tạo bitmap
        Bitmap bitmap = Bitmap.createBitmap((int) realBitmapWidth, (int) realBitmapWidth, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
         //  c.drawColor(0xff5CD315);
        if (normal)
            attr.solidPaint.setColor(Color.WHITE);
        else attr.solidPaint.setColor(focusColor);
        attr.solidPaint.setStyle(Paint.Style.FILL);

        // c.translate(BLUR_RADIUS, BLUR_RADIUS);
        //     c.drawRoundRect(sShadowRectF, sShadowRectF.width() / 40,
//                sShadowRectF.height() / 40, mShadowPaint);
        float rbw_1_2 = realBitmapWidth / 2;
        bubbleRadius = rbw_1_2 - 10*attr.oneDp;
        // -20 = a * 2 => a = 10
        c.drawCircle(rbw_1_2 - 1, rbw_1_2 + 1.5f*attr.oneDp, bubbleRadius, attr.mShadowPaint);
        c.drawCircle(rbw_1_2 - 1, rbw_1_2, bubbleRadius, attr.solidPaint);

        return bitmap;
    }
public void set() {
        tile = setTiLe();
        createIcon();

}
    int number;
    private void createIcon() {
        // tạo 2 background cho icon
        focusColor = Tool.getBaseColor();
        number = attr.menu_number;
        backIcon = getIconBackground(true);
        focusBackIcon = getIconBackground(false);
        normalBitmap = new Bitmap[number];
        focusBitmap = new Bitmap[number];
        float content_tile = (float) (1/ (Math.sqrt(2.5)));
        float padding = (realBitmapWidth * content_tile/2 );

        // vẽ các biểu tượng lên background
        for (int i = 0; i < number; i++) {
            normalBitmap[i] = backIcon.copy(Bitmap.Config.ARGB_8888, true);
            Canvas c = new Canvas(normalBitmap[i]);
           // Bitmap b = BitmapEditor.changeBitmapColor(attr.menu_item_bitmap[i], focusColor);

            c.drawBitmap(attr.menu_item_bitmap[i], new Rect(0, 0, attr.menu_item_bitmap[i].getWidth(), attr.menu_item_bitmap[i].getHeight()),
                    new RectF(padding, padding, (realBitmapWidth - padding), (realBitmapWidth -padding))
                    , solid_Paint);
           // b.recycle();


        }
        solid_Paint.setAlpha(245);
        // vẽ các biểu tượng lên background
        for (int i = 0; i < number; i++) {
            focusBitmap[i] = focusBackIcon.copy(Bitmap.Config.ARGB_8888, true);
            Canvas c = new Canvas(focusBitmap[i]);
            Bitmap b = BitmapEditor.changeBitmapColor(attr.menu_item_bitmap[i],Color.WHITE);
            c.drawBitmap(b, new Rect(0, 0, attr.menu_item_bitmap[i].getWidth(), attr.menu_item_bitmap[i].getHeight()),
                    new RectF( padding, padding,  (realBitmapWidth - padding),  (realBitmapWidth - padding)), solid_Paint);
            b.recycle();
        }
    }
    /*
    Return true if you want to invalidate again
     */
   public abstract boolean updateTouchRuntime(boolean b);

}
