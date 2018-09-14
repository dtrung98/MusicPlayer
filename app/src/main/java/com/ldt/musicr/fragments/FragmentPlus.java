package com.ldt.musicr.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ldt.musicr.activities.MainActivity;
import com.ldt.musicr.activities.SupportFragmentPlusActivity;

/**
 * Created by trung on 8/13/2017.
 *  FragmentPlus is a fragment type which supports visual transitions
 */
public abstract class FragmentPlus extends RuntimeThemeFragment {
    public enum StatusTheme {
        BlackIcon,WhiteIcon
    }
    private MainActivity activity;
    public MainActivity getMainActivity(){
        if(activity==null) activity = (MainActivity)getActivity();
        return activity;
    }
    public StatusTheme statusTheme ;
    public abstract void onTransitionComplete();

    public abstract StatusTheme setDefaultStatusTheme();

    public int FitWindow_Top =0;
    public int FitWindow_Bottom = 0;
    public int init_MarginTop()
    {
        return SupportFragmentPlusActivity.Standard_FitWindow_Top;
    }

    protected int init_MarginBottom()
    {
        return SupportFragmentPlusActivity.Standard_FitWindow_Bottom;
    }
    public enum ApplyMargin {
        ONLY_STATUS,ONLY_NAVIGATION,BOTH, NONE
    }
    public ApplyMargin IWantApplyMargin()
    {
        return ApplyMargin.ONLY_STATUS;
    }
    public void applyFitWindow_Top()
    {
        rootView.setPadding(rootView.getPaddingLeft(),rootView.getPaddingTop() + FitWindow_Top,rootView.getPaddingRight(),rootView.getPaddingBottom());
    }

    public void applyFitWindow_Bottom() {
        rootView.setPadding(rootView.getPaddingLeft(),rootView.getPaddingTop() ,rootView.getPaddingRight(),rootView.getPaddingBottom() + FitWindow_Bottom);
    }
    public ApplyMargin marginMode;
    public final int getFitWindow_Bottom(){
        return FitWindow_Bottom;
    }
    public final int getFitWindow_Top() {return FitWindow_Top;}
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        marginMode = IWantApplyMargin();
        if(marginMode==ApplyMargin.ONLY_STATUS||marginMode==ApplyMargin.BOTH)    applyFitWindow_Top();
          if(marginMode==ApplyMargin.ONLY_NAVIGATION||marginMode==ApplyMargin.BOTH) applyFitWindow_Bottom();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       FitWindow_Top = init_MarginTop();
       FitWindow_Bottom = init_MarginBottom();
    }
    public SupportFragmentPlusActivity.TransitionType getTransitionType() {
        return transitionType;
    }

    private FrameLayout frameLayout;
    private SupportFragmentPlusActivity.TransitionType transitionType;
    public FrameLayout getFrameLayout()
    {
        return frameLayout;
    }
    public void setFrameLayoutNTransitionType(Activity activity,SupportFragmentPlusActivity.TransitionType transition_type)
    {
        frameLayout = new FrameLayout(activity);
        statusTheme = setDefaultStatusTheme();
        transitionType =transition_type;
    }

    public View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
