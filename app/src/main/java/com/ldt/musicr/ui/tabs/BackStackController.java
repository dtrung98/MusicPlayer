package com.ldt.musicr.ui.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.LayerController;
import com.ldt.musicr.ui.MainActivity;
import com.ldt.musicr.ui.tabs.library.LibraryTabFragment;
import com.ldt.musicr.ui.widget.navigate.NavigateFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.VIBRATOR_SERVICE;

public class BackStackController extends BaseLayerFragment implements ViewPager.OnPageChangeListener {
    private static final String TAG ="BackStackController";
    @BindView(R.id.root)
    CardView mRoot;
    private float mNavigationHeight;

    BottomNavigationPagerAdapter mNavigationAdapter;
    private float oneDP=1;

    public BackStackController() {
       // mNavigationStack.add(0);
    }

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.back_stack_controller,container,false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        oneDP = getResources().getDimension(R.dimen.oneDP);
       vibrator  = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
       mNavigationHeight = getActivity().getResources().getDimension(R.dimen.bottom_navigation_height);
       mNavigationAdapter = new BottomNavigationPagerAdapter(getActivity(),getChildFragmentManager());
       mViewPager.setAdapter(mNavigationAdapter);
       mViewPager.setOffscreenPageLimit(3);
       mViewPager.addOnPageChangeListener(this);
       mViewPager.setOnTouchListener((v, event) -> mLayerController.streamOnTouchEvent(mRoot,event));
    }

    public boolean streamOnTouchEvent(MotionEvent event) {
       return mLayerController.streamOnTouchEvent(mRoot,event);
    }


    @Override
    public void onUpdateLayer(ArrayList<LayerController.Attr> attrs, ArrayList<Integer> actives, int me) {

        if(mRoot==null) return;
        if(me ==1) {
                            float pc = attrs.get(actives.get(0)).getRuntimePercent();
                            if(pc<0.01f) pc=0f;else if(pc>1) pc =1;
                             mRoot.setRadius(oneDP*14*pc);
                            Log.d(TAG, "run: pc = "+pc);
                         //   mRoot.setDarken(0.3f*(attrs.get(actives.get(0)).getRuntimePercent()),true);
                          //  mRoot.setRoundNumber( attrs.get(actives.get(0)).getRuntimePercent(),false);
                            mRoot.setAlpha(1);
        } else if(me !=0 )
        {
            //  other, active_i >1
            // min = 0.3
            // max = 0.45
            float min = 0.3f, max =0.65f;
            float hieu = max - min;
            float heSo_sau = (me-1.0f)/(me-0.75f); // 1/2, 2/3,3/4, 4/5, 5/6 ...
            float heSo_truoc =  (me-2.0f)/(me-0.75f); // 0/1, 1/2, 2/3, ...
            float darken = min + hieu*heSo_truoc + hieu*(heSo_sau - heSo_truoc)*attrs.get(actives.get(0)).getRuntimePercent();
            // Log.d(TAG, "darken = " + darken);
          //  mRoot.setDarken(darken,false);
            //   TabSwitcherFrameLayout.setDarken(0.3f + 0.6f*pcOnTopLayer,false);
          //  mRoot.setRoundNumber(1,true);
            mRoot.setRadius(oneDP*14);
            if(me==actives.size()-1) mRoot.setAlpha(1-darken); else mRoot.setAlpha(1);
        }

        doTranslateNavigation(attrs,actives,me);
    }
    public void doTranslateNavigation(ArrayList<LayerController.Attr> attrs, ArrayList<Integer> actives, int me) {
        if(mBottomNavigationParent!=null) {
            if (me == 1) {
                float pc = attrs.get(actives.get(0)).getRuntimePercent();
                if(pc>1) pc=1;
                else if(pc<0) pc = 0;
                mBottomNavigationParent.setTranslationY(pc*mNavigationHeight);
            } else if(me != 0) {
                mBottomNavigationParent.setTranslationY(mNavigationHeight);
            }
        }
    }

    @Override
    public void onTranslateChanged(LayerController.Attr attr) {
        if(mRoot!=null) {
            float pc = (attr.mCurrentTranslate)/attr.getMaxPosition();
            Log.d(TAG, "onTranslateChanged : pc = "+pc);
            if(pc>1) pc=1;
            else if(pc<0) pc = 0;
            float scale = 0.2f + pc*0.8f;

            float radius = 1-pc;
            if(radius<0.1f) radius = 0; else if(radius>1) radius = 1;
            mRoot.setRadius(oneDP*14*radius);
            mRoot.setAlpha(scale);

        }
    }

    @Override
    public boolean getMaxPositionType() {
        return true;
    }

    @Override
    public int minPosition(Context context, int maxHeight) {
        return maxHeight;
    }

    @Override
    public String tag() {
        return TAG;
    }

    private BottomNavigationView mBottomNavigationView;
    private View mBottomNavigationParent;

    public BottomNavigationView getBottomNavigationView() {
        return mBottomNavigationView;
    }

    public BackStackController attachBottomNavigationView(MainActivity activity) {

        try {
            mBottomNavigationParent = activity.findViewById(R.id.bottom_navigation_parent);
            mBottomNavigationView = mBottomNavigationParent.findViewById(R.id.bottom_navigation_view);
            mBottomNavigationView.setOnNavigationItemSelectedListener(mItemSelectedListener);


        } catch (Exception ignore) {}

        return this;
    }
    Vibrator vibrator;
    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(50);
        }
    }
    public void goToSongTab() {
        mViewPager.setCurrentItem(1);
        Fragment fragment =  mNavigationAdapter.getItem(1);
        if(fragment instanceof NavigateFragment) {
            ((NavigateFragment)fragment).popToRootFragment();
           Fragment lib = ((NavigateFragment)fragment).getRootFragment();
           if(lib instanceof LibraryTabFragment)
            ((LibraryTabFragment)lib).goToSongTab();
        } else Log.d(TAG, "goToSongTab: not librarytabfragment");
    }
    public void removeBottomNavigationView() {
        mBottomNavigationView = null;
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_feature:
                    if (mViewPager.getCurrentItem() != 0) {
                        mViewPager.setCurrentItem(0);
                        bringToTopThisTab(0);
                    }
                    return true;
                case R.id.navigation_library:
                    if (mViewPager.getCurrentItem() != 1) {
                        mViewPager.setCurrentItem(1);
                        bringToTopThisTab(1);
            }
                    return true;
                case R.id.navigation_setting:
                    if(mViewPager.getCurrentItem()!=2) {
                        mViewPager.setCurrentItem(2);
                        bringToTopThisTab(2);
                    }
                    return true;
            }
            return false;
        }
    };
    ArrayList<Integer> mNavigationStack = new ArrayList<>();
    private void bringToTopThisTab(Integer tabPosition) {
        vibrate();
        Log.d(TAG, "bringToTopThisTab: after : "+mNavigationStack);
        boolean b = mNavigationStack.remove(tabPosition);
        if(b)
        Log.d(TAG, "bringToTopThisTab: removed "+tabPosition);

        mNavigationStack.add(tabPosition);
        Log.d(TAG, "bringToTopThisTab: after : "+mNavigationStack);
    }

    @Override
    public boolean onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if(!mNavigationStack.isEmpty()) {
            if(!mNavigationAdapter.onBackPressed(mNavigationStack.get(mNavigationStack.size()-1))) {
                mNavigationStack.remove(mNavigationStack.size() - 1);
                if(mNavigationStack.isEmpty()) return onBackPressed();
                mViewPager.setCurrentItem(mNavigationStack.get(mNavigationStack.size()-1),true);
            }
        } else if(mViewPager.getCurrentItem()!=0) mViewPager.setCurrentItem(0,true);
        else if(mViewPager.getCurrentItem()==0&&!mNavigationAdapter.onBackPressed(0))
        return false;
        return true;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }
    MenuItem prevMenuItem;

    @Override
    public void onPageSelected(int i) {
        bringToTopThisTab(i);
        if(mBottomNavigationView!=null) {
            if (prevMenuItem != null)
                prevMenuItem.setChecked(false);
            else
                mBottomNavigationView.getMenu().getItem(0).setChecked(false);

            mBottomNavigationView.getMenu().getItem(i).setChecked(true);
            prevMenuItem = mBottomNavigationView.getMenu().getItem(i);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
