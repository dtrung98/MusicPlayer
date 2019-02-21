package com.ldt.musicr.ui.main;


import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ldt.musicr.util.uitool.BitmapEditor;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;

import com.ldt.musicr.fragments.fragmentholder.BottomUpTransition;
import com.ldt.musicr.fragments.fragmentholder.FadeInFadeOutTransition;
import com.ldt.musicr.fragments.fragmentholder.RightToLeftTransition;
import com.ldt.musicr.fragments.fragmentholder.Prepare4Fragment;
import com.ldt.musicr.fragments.FragmentPlus;

import com.ldt.musicr.ui.widget.RoundClippingFrameLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;

public abstract class SupportFragmentPlusActivity extends AppCompatActivity {
    private static final String TAG = "SFragmentPlusActivity";
    public int StatusHeight=0,NavigationHeight=0;
    public float StatusHeight_DP=0,NavigationHeight_DP =0;
    public static int Standard_FitWindow_Top, Standard_FitWindow_Bottom;
    public FrameLayout rootEveryThing;
    public float ADpInPixel;
    private final int CYCLER_LOAD_WALLPAPER = 5000; // 5 giay test mot lan
    public Bitmap iV_Wallpaper  = Bitmap.createBitmap(150,150, Bitmap.Config.ARGB_8888);

    public Canvas iv_canvas = new Canvas(iV_Wallpaper);
    public Paint iv_paint = new Paint();
    public Boolean WallpaperCreated = false;
    public Bitmap current_wallpaper =null;
    private ValueAnimator valueAnimator;
    public FragmentPlus.StatusTheme nowThemeAcitivity = FragmentPlus.StatusTheme.BlackIcon;
    public FragmentPlus.StatusTheme orderThemeActivity = FragmentPlus.StatusTheme.BlackIcon;
    private final int  dp_unit = 50;
    public float pixel_unit  = 0;
    public FrameLayout container;
    public View navigation_bar;
    public RoundClippingFrameLayout TabSwitcherFrameLayout;
    public View statusColor4Lolipop;

    protected void checkNSwitch()
    {
        if(orderThemeActivity!=nowThemeAcitivity) switchTheme();
    }
    private void switchTheme()
    {
        nowThemeAcitivity = orderThemeActivity;
        if(nowThemeAcitivity == FragmentPlus.StatusTheme.BlackIcon)
           turn2BlackIcon();
        else
            turn2LightIcon();
    }
    private void turn2LightIcon()
    {
     // Log.d("Theme","Light Icon");
            rootEveryThing.setSystemUiVisibility(0);
    }
    private void turn2BlackIcon()
    {
     //   Log.d("Theme","Black Icon");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rootEveryThing.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    public final void OrderToChangeStatusTheme(FragmentPlus iAm)
    {
        // Pager sẽ chỉ đổi được Theme khi nó là Top Pager
        //Todo : Kiểm tra coi thử nó có phải là Top Pager không.
        if(nowThemeAcitivity == iAm.statusTheme) return;
        if(iAm == getTopStackFragment()) {
            orderThemeActivity = iAm.statusTheme;
            switchTheme();
        }
    }
    public FragmentPlus.StatusTheme beforeBlock;
    public void blockToWhiteTheme() {
        beforeBlock =nowThemeAcitivity;
        if(nowThemeAcitivity ==FragmentPlus.StatusTheme.WhiteIcon) return;
        orderThemeActivity = FragmentPlus.StatusTheme.WhiteIcon;
        switchTheme();
}
    public void unblockWhiteTheme() {
    if(beforeBlock == FragmentPlus.StatusTheme.WhiteIcon) return;
    orderThemeActivity = beforeBlock;
    switchTheme();
}
//    public final void OrderToChangeStatusTheme(PlaylistControllerFragment playlistControllerFragment)
//    {
//        // Tương tự với PlaylistControllerFragment
//        orderThemeActivity = playlistControllerFragment.statusTheme;
//        switchTheme();
//    }
    private void updateWhenSwitchFragment()
    {
        FragmentPlus fragmentPlus = getTopStackFragment();
        if(fragmentPlus==null) return;
       orderThemeActivity = fragmentPlus.statusTheme;
       switchTheme();
    }



    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {

        super.onResume();
    }


    private Bitmap applyBackWallEffect4Wallpaper(Bitmap old)
    {
        Bitmap blur  = BitmapEditor.getBlurredWithGoodPerformance(this,old,1,12,1.4f);
        //     Canvas canvas = new Canvas(blur);
        //    Paint paint= new Paint();
        //   int w=blur.getWidth(),h = blur.getHeight();
        //   paint.setARGB(100,0,0,0);
        //   canvas.drawRect(0,0,w,h,paint);

        return blur;
    }
    public void setStatusNNavigationHeight(int statusHeight,int navigationHeight,float aDpInPixel)
    {
        ADpInPixel = aDpInPixel;
        StatusHeight =  statusHeight;
        Standard_FitWindow_Top = StatusHeight;
        Standard_FitWindow_Bottom = navigationHeight;
        NavigationHeight = navigationHeight;
        StatusHeight_DP = statusHeight/aDpInPixel;
        NavigationHeight_DP = statusHeight/aDpInPixel;
    }


    public enum MenuType {
        CONTAINER, BACK_WALL
    }

    public enum TransitionType {
        RIGHT_LEFT,BOTTOM_UP,FADE_IN_OUT,SPECIAL_TYPE
    }

    public Map<MenuType, FragmentPackage> mFragmentMap;
    protected MenuType mCurrentMenuTab = MenuType.CONTAINER;
    public class FragmentPackage {
        public Stack<FragmentPlus> mStackMap;
        public Stack<TransitionType> mStackTransition;
        public FragmentPackage()
        {
            mStackMap = new Stack<>();
            mStackTransition= new Stack<>();
        }
    }
    public void setHeightOfNavigation()
    {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) navigation_bar.getLayoutParams();
        params.height = NavigationHeight;
    }
    public void setNecessaryValuesBeforeRunning()
    {
        pixel_unit = Tool.getPixelsFromDPs(this,dp_unit);
        int ssh = Tool.getStatusHeight(getResources());
        int nhh= Tool.getNavigationHeight(this);
        setStatusNNavigationHeight(ssh,nhh,Tool.getPixelsFromDPs(this,1));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNecessaryValuesBeforeRunning();
        initStackFragments();
    }


protected final void callSuperMergeUI()
{
    rootEveryThing = findViewById(R.id.rootEveryThing);
   // TabSwitcherFrameLayout =findViewById(R.id.back_stack_container);


    turnOnTranslucent(false);

}
protected void turnOnTranslucent(boolean on)
{

    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) statusColor4Lolipop.getLayoutParams();
    params.height = StatusHeight;
    statusColor4Lolipop.setLayoutParams(params);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M||on) {
        statusColor4Lolipop.setVisibility(View.VISIBLE);
    }
}
    private void initStackFragments() {
        mFragmentMap = new HashMap<MenuType, FragmentPackage>();
        for (MenuType menuType : MenuType.values()) {
            mFragmentMap.put(menuType, new FragmentPackage());
        }

    }
    private static boolean inTransition = false;

    public static boolean isInTransition() {
        return inTransition;
    }

    public static void setInTransition(boolean InTransition) {
        inTransition = InTransition;
    }

    public void setCurrentMenuOrTab(MenuType menuType) {
        mCurrentMenuTab = menuType;
    }
    public void pushFragment(FragmentPlus fragment, boolean shouldAdd) {
        if(inTransition) return;

        // this command add current to stack (add, meaning like attracting, it does not change anything).

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        int numberStack = mFragmentMap.get(mCurrentMenuTab).mStackMap.size();
        if (shouldAdd) {
            mFragmentMap.get(mCurrentMenuTab).mStackMap.push(fragment);
        }


        if(numberStack>=1)
        {
            FragmentPlus oldFragment = mFragmentMap.get(mCurrentMenuTab).mStackMap.get(numberStack-1);
            TransitionAdding(this,fragment,oldFragment);
        }
        else {
            // if this is the first one

            final FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            fragment.getFrameLayout().setId(Prepare4Fragment.getId());

            TabSwitcherFrameLayout.addView(fragment.getFrameLayout(),params1);
            ft.add(fragment.getFrameLayout().getId(), fragment, fragment.getClass().getSimpleName());
        }
        // Apply the changes and reload FragmentManager.
        ft.commitAllowingStateLoss();
        updateWhenSwitchFragment();
    }
    public void pushFragmentWithoutChangeCurrentMenuType(FragmentPlus fragment, MenuType menuType) {
        // this command add current to stack (add, meaning like attracting, it does not change anything).
        if(inTransition) return;

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        int numberStack = mFragmentMap.get(menuType).mStackMap.size();

        mFragmentMap.get(menuType).mStackMap.push(fragment);


        if(numberStack>=1)
        {
            FragmentPlus oldFragment = mFragmentMap.get(menuType).mStackMap.get(numberStack-1);
            TransitionAdding(this,fragment,oldFragment);
        }
        else {
            // if this is the first one
            FrameLayout rl_root = getContainer(menuType);
            final FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            fragment.getFrameLayout().setId(Prepare4Fragment.getId());

            rl_root.addView(fragment.getFrameLayout(),params1);
            ft.add(fragment.getFrameLayout().getId(), fragment, fragment.getClass().getSimpleName());
        }
        // Apply the changes and reload FragmentManager.
        ft.commitAllowingStateLoss();
        updateWhenSwitchFragment();
    }
    public FrameLayout getContainer(MenuType menuType)
    {
        switch (menuType)
        {

            case BACK_WALL: return (FrameLayout) findViewById(R.id.back_wall_container);
            case CONTAINER : default:
                                        return null;//(FrameLayout)findViewById(R.id.back_stack_container);
        }
    }
    public void InitializeBackWall()
    {
      //  pushFragmentWithoutChangeCurrentMenuType(BackWall.Companion.Initialize(this),MenuType.BACK_WALL);
    }
    private void TransitionAdding(SupportFragmentPlusActivity activity, FragmentPlus NewFragment, FragmentPlus BackFragment)
    {
        inTransition = true;
        TransitionType tT = NewFragment.getTransitionType();
        switch (tT)
        {
            case FADE_IN_OUT: FadeInFadeOutTransition.AddFragmentAndTransform(activity,NewFragment,BackFragment);break;
            case BOTTOM_UP: BottomUpTransition.AddFragmentAndTransform(activity,NewFragment,BackFragment);break;
            case RIGHT_LEFT: RightToLeftTransition.AddFragmentAndTransform(activity,NewFragment,BackFragment);break;
        }
    }

    private void TransitionRemoving(FragmentPlus NewFragment,FragmentPlus BackFragment)
    {
        inTransition = true;
        TransitionType tT = NewFragment.getTransitionType();
        switch (tT)
        {
            case FADE_IN_OUT: FadeInFadeOutTransition.RemoveFragmentAndTransform(NewFragment,BackFragment);break;
            case BOTTOM_UP: BottomUpTransition.RemoveFragmentAndTransform(NewFragment,BackFragment);break;
            case RIGHT_LEFT: RightToLeftTransition.RemoveFragmentAndTransform(NewFragment,BackFragment);break;
        }
    }
    public FragmentPlus getTopStackFragment() {
        try {
            return mFragmentMap.get(mCurrentMenuTab).mStackMap.lastElement();
        }
        catch (NoSuchElementException e)
        {
            return null;
        }
    }
    public boolean canGetTopFragment()
    {
        return !mFragmentMap.get(mCurrentMenuTab).mStackMap.empty();
    }
    private void popFragment() {
        if(inTransition) return;


        final Stack<FragmentPlus> stackFragment = mFragmentMap.get(mCurrentMenuTab).mStackMap;


        if (stackFragment.size() > 1) {
            FragmentPlus newfragment = (FragmentPlus) stackFragment.elementAt(stackFragment.size() - 2);
            FragmentPlus poped = stackFragment.peek();
            stackFragment.pop();
            //     Tool.showToast(this,"Pop this, "+(stackFragmentSize-1)+" x.",500);
            if (newfragment != null) {
                TransitionRemoving(poped,newfragment);
            }
        }
        updateWhenSwitchFragment();
    }
    public void popFragment(Bundle argBundle) {
        if(inTransition) return;
        final Stack<FragmentPlus> stackFragment = mFragmentMap.get(mCurrentMenuTab).mStackMap;
        if (stackFragment.size() > 1) {

            Fragment fragment = stackFragment.elementAt(stackFragment.size() - 2);
            stackFragment.pop();

            if (fragment != null) {
                if (argBundle != null) {
                    fragment.setArguments(argBundle);
                }
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();

              //  ft.add(R.id.back_stack_container, fragment, fragment.getClass().getSimpleName());
                ft.commitAllowingStateLoss();
            }
        }
        updateWhenSwitchFragment();
    }
    abstract boolean Scroll2Top();

    /**
     *  Quay trở lại một level trong stack fragment ( UILAYER)
     *  Nếu không xử lý, trả về false
     */
    public boolean onBackUILayer() {
        if (mFragmentMap.get(mCurrentMenuTab).mStackMap.size() <= 1) {
            return false;
        } else {
            popFragment();
            return true;
        }

    }
    public abstract void  SlideDownNShowWallBack();
 /*
         Phương thức này thực hiện Chuyển động Kéo Màn Chính xuống và hiện ra Wall Back
         Sau chuyển động, hàm thực hiện gửi Màn Chính vào Stack và Chạy nội dung Wall Back
         Hàm này được miêu tả trong MainAcitivity.
      */

}
