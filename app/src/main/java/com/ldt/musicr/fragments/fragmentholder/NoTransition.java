package com.ldt.musicr.fragments.fragmentholder;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ldt.musicr.util.Tool;
import com.ldt.musicr.ui.main.SupportFragmentPlusActivity;
import com.ldt.musicr.fragments.FragmentPlus;

/**
 * Created by trung on 8/19/2017.
 */

public class NoTransition {
    public static void AddFragmentAndTransform(final SupportFragmentPlusActivity activity, final FragmentPlus ThisFragment, final ViewGroup behindView) {
        final int[] screenSize = Tool.getScreenSize(activity);
        final FrameLayout ViewFragment = ThisFragment.getFrameLayout();
        final RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) behindView.getLayoutParams();
        ViewFragment.setId(Prepare4Fragment.getId());
        ViewFragment.setAlpha(0);
        ((RelativeLayout)behindView.getParent()).addView(ViewFragment,params1);
        activity.getSupportFragmentManager().beginTransaction().add(ViewFragment.getId(),ThisFragment).commit();
    }
    public static void RemoveFragmentAndTransform( final FragmentPlus ThisFragment, final ViewGroup behindView) {
        final SupportFragmentPlusActivity activity = (SupportFragmentPlusActivity) ThisFragment.getActivity();
        final int[] screenSize = Tool.getScreenSize(activity);
        final  FrameLayout ViewFragment = ThisFragment.getFrameLayout();
        final RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)ViewFragment.getLayoutParams();
        final RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) behindView.getLayoutParams();
        activity.getSupportFragmentManager().beginTransaction().remove(ThisFragment).commit();
        Prepare4Fragment.unUseThisID(ViewFragment.getId());
        ((ViewGroup)ViewFragment.getParent()).removeView(ViewFragment);
    }
}
