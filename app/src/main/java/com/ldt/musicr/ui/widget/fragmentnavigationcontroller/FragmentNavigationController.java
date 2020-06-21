package com.ldt.musicr.ui.widget.fragmentnavigationcontroller;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.Stack;

/**
 * Created by burt on 2016. 5. 24..
 */
public class FragmentNavigationController extends SupportFragment {

    private FragmentManager fragmentManager = null;
    private Stack<SupportFragment> fragmentStack = new Stack<>();
    private @IdRes int containerViewId;
    private Object sync = new Object();
    private PresentStyle presentStyle = PresentStyle.get(PresentStyle.NONE);
    private TimeInterpolator interpolator = new LinearInterpolator();
    private long duration = 500;

    public static FragmentNavigationController navigationController(@NonNull FragmentManager fragmentManager, @IdRes int containerViewId) {
        return new FragmentNavigationController(fragmentManager, containerViewId);
    }

    private FragmentNavigationController(@NonNull FragmentManager fragmentManager, @IdRes int containerViewId) {
        setRetainInstance(true);
        this.containerViewId = containerViewId;
        this.fragmentManager = fragmentManager;

        synchronized (sync) {
            // 자기 자신을 넣는다.
            fragmentManager
                    .beginTransaction()
                    .replace(containerViewId, this, "navigation-controller")
                    .commit();
        }
    }
    public SupportFragment getTopFragment() {
        if(fragmentStack.size()!=0)return  fragmentStack.lastElement();
        return null;
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    public int getFragmentCount() {
        return fragmentStack.size();
    }


    /**
     * set the present style
     * @param style
     */
    public void setPresentStyle(int style) {
        presentStyle = PresentStyle.get(style);
    }

    /**
     * for setting of user defined PrensetStyle
     * @param style
     */
    public void setPresentStyle(PresentStyle style) {
        presentStyle = style;
    }

    PresentStyle getPresentStyle() {
        return presentStyle;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    public void setDuration(long presentDuration) {
        duration = presentDuration;
    }

    TimeInterpolator getInterpolator() {
        return interpolator;
    }

    long getDuration() {
        return duration;
    }

    public void pushFragment(SupportFragment fragment) {
        PresentStyle oldPresetStyle = presentStyle;
        setDuration(300);
        setInterpolator(new AccelerateDecelerateInterpolator());
        setPresentStyle(PresentStyle.SLIDE_LEFT);
        presentFragment(fragment);
        presentStyle = oldPresetStyle;
        setPresentStyle(presentStyle);
    }

    public void popFragment() {
        dismissFragment();
    }

    public void presentFragment(SupportFragment fragment) {
        presentFragment(fragment, true);
    }

    public void presentFragment(SupportFragment fragment, boolean withAnimation) {

        if(fragmentManager == null) return;

        synchronized (sync) {

            if (fragmentStack.size() == 0) {
                fragment.setNavigationController(this);
                fragment.setAnimatable(false);
                fragment.setPresentStyle(presentStyle);
                fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(containerViewId, fragment, "root")
                        .commit();

            } else {

                fragment.setNavigationController(this);
                fragment.setAnimatable(withAnimation);
                fragment.setPresentStyle(presentStyle);
                // hide last fragment and add new fragment
                SupportFragment hideFragment = fragmentStack.peek();
                hideFragment.onHideFragment();
                fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .hide(hideFragment)
                        .add(containerViewId, fragment, fragment.getClass().getName())
                        .commit();
            }
            fragmentStack.add(fragment);
        }
    }
    private boolean mIsAbleToPopRoot = false;
    public void setAbleToPopRoot(boolean able) {
        mIsAbleToPopRoot = able;
    }

    public boolean dismissFragment() {
        return dismissFragment(true);
    }

    public boolean dismissFragment(boolean withAnimation) {
        if(mIsAbleToPopRoot) return dismissFragmentIncludingRoot(withAnimation);
        else return dismissFragmentWithoutRoot(withAnimation);
    }

    public boolean dismissFragmentIncludingRoot(boolean withAnimation) {

        if(fragmentManager == null) return false;

        // fragmentStack only has root fragment
        if(fragmentStack.size() == 1) {

            // remove root
            SupportFragment fragmentToRemove= fragmentStack.pop();
            fragmentToRemove.setNavigationController(this);
            fragmentToRemove.setAnimatable(withAnimation);
            fragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .show(this)
                    .remove(fragmentToRemove)
                    .commit();
            return true;
        }
        else if(fragmentStack.size()==0) return false;

        synchronized (sync) {

            SupportFragment fragmentToRemove = fragmentStack.pop();
            fragmentToRemove.setNavigationController(this);
            fragmentToRemove.setAnimatable(withAnimation);

            SupportFragment fragmentToShow = fragmentStack.peek();
            fragmentToShow.setNavigationController(this);
            fragmentToShow.setAnimatable(withAnimation);
            fragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .show(fragmentToShow)
                    .remove(fragmentToRemove)
                    .commit();

        }
        return true;
    }
    public boolean dismissFragmentWithoutRoot(boolean withAnimation) {

        if(fragmentManager == null) return false;

        // fragmentStack only has root fragment
        if(fragmentStack.size() == 1) {

            // show the root fragment
            SupportFragment fragmentToShow = fragmentStack.peek();
            fragmentToShow.setNavigationController(this);
            fragmentToShow.setAnimatable(withAnimation);
            fragmentManager
                    .beginTransaction()
                    .show(fragmentToShow)
                    .commit();
            return false;
        }

        synchronized (sync) {

            SupportFragment fragmentToRemove = fragmentStack.pop();
            fragmentToRemove.setNavigationController(this);
            fragmentToRemove.setAnimatable(withAnimation);

            SupportFragment fragmentToShow = fragmentStack.peek();
            fragmentToShow.setNavigationController(this);
            fragmentToShow.setAnimatable(withAnimation);
            fragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .show(fragmentToShow)
                    .remove(fragmentToRemove)
                    .commit();

        }
        return true;
    }

    public void popToRootFragment() {

        while (fragmentStack.size() >= 2) {
            dismissFragment();
        }
    }
    public void popAllFragments() {
        if(!mIsAbleToPopRoot) {
            popToRootFragment();
        } else {
            while (fragmentStack.size()>=1)
                dismissFragment();
        }
    }

}
