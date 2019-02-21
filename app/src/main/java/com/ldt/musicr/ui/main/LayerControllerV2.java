package com.ldt.musicr.ui.main;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.main.navigate.BackStackController;
import com.ldt.musicr.ui.main.nowplaying.NowPlayingController;
import com.ldt.musicr.ui.main.playinglist.PlayingListController;
import com.ldt.musicr.util.Tool;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LayerControllerV2 {
    private static final String TAG ="LayerControllerV2";
    private FrameLayout mLayerContainer;
    private float mStatusHeight;
    private float mScreenWidth;
    private float mScreenHeight;
    private float mLayerMarginTop;
    private float mHightestMargin;

    @BindView(R.id.child_layer_container) FrameLayout mChildLayerContainer;
    private enum MODE {
        INIT

    }
    private AppCompatActivity mActivity;
    public LayerControllerV2(AppCompatActivity activity) {
        mActivity = activity;
        mStatusHeight = Tool.getStatusHeight(activity.getResources());

        mScreenWidth = Tool.getRefreshScreenSize(activity)[0];
        mStatusHeight = Tool.getScreenSize(true)[1];
        mLayerMarginTop = activity.getResources().getDimension(R.dimen.layer_margin_top);
        mHightestMargin = activity.getResources().getDimension(R.dimen.highest_back_margin);
    }

    public void init(FrameLayout layerContainer) {

        mLayerContainer = layerContainer;
        ButterKnife.bind(this, layerContainer);

       animateOnStart();
    }

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    @BindView(R.id.bottom_navigation_parent) View mBottomNavigationParent;

    BackStackController mBackStackController;
    NowPlayingController mNowPlayingLayer;
    PlayingListController mPlayingListLayer;
    private ArrayList<BaseLayerFragmentV2> mBaseLayers = new ArrayList<>();




    public void animateOnStart() {
        mLayerContainer.setVisibility(View.VISIBLE);
        float value = mActivity.getResources().getDimension(R.dimen.bottom_navigation_height);
        mBottomNavigationParent.setTranslationY(value);
        mBottomNavigationParent.animate().translationYBy(-value);
       // mBottomNavigationParent.setTranslationY(mActivity.getResources().getDimension(R.dimen.bottom_navigation_height)).animate().translationYBy(-mActivity.getResources().getDimension(R.dimen.bottom_navigation_height)).;
    }

    public void postMyPercent(Attr attr, float percent) {

    }

}
