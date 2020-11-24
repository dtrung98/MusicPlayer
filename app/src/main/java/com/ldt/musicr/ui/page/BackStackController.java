package com.ldt.musicr.ui.page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldt.musicr.App;
import com.ldt.musicr.R;
import com.ldt.musicr.glide.ArtistGlideRequest;
import com.ldt.musicr.glide.GlideApp;
import com.ldt.musicr.loader.medialoader.ArtistLoader;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.service.MusicServiceEventListener;
import com.ldt.musicr.ui.AppActivity;
import com.ldt.musicr.ui.MusicServiceActivity;
import com.ldt.musicr.ui.CardLayerController;
import com.ldt.musicr.ui.page.librarypage.LibraryTabFragment;
import com.ldt.musicr.ui.widget.navigate.BackPressableFragment;
import com.ldt.musicr.util.Tool;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BackStackController extends CardLayerFragment implements ViewPager.OnPageChangeListener, MusicServiceEventListener {
    private static final String TAG = "BackStackController";
    @BindView(R.id.root)
    CardView mRoot;
    @BindView(R.id.dim_view)
    View mDimView;
    @BindView(R.id.back_image)
    ImageView mBackImageView;

    private boolean mIsUsingAIAsBg = true;

    private float mNavigationHeight;

    BottomNavigationPagerAdapter mNavigationAdapter;
    private float oneDP = 1;

    public BackStackController() {
        // mNavigationStack.add(0);
    }

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.screen_card_layer_back_stack, container, false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (getActivity() instanceof MusicServiceActivity)
            ((MusicServiceActivity) getActivity()).addMusicServiceEventListener(this);

        oneDP = getResources().getDimension(R.dimen.oneDP);
        mNavigationHeight = getActivity().getResources().getDimension(R.dimen.bottom_navigation_height);
        //if(true) return;
        mNavigationAdapter = new BottomNavigationPagerAdapter(getActivity(), getChildFragmentManager());
        mViewPager.setAdapter(mNavigationAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOnTouchListener((v, event) -> mCardLayerController.dispatchOnTouchEvent(mRoot, event));

        onUsingArtistImagePreferenceChanged();

    }

    public boolean streamOnTouchEvent(MotionEvent event) {
        return mCardLayerController.dispatchOnTouchEvent(mRoot, event);
    }


    @Override
    public void onLayerUpdate(ArrayList<CardLayerController.CardLayerAttribute> attrs, ArrayList<Integer> actives, int me) {

        if (mRoot == null) return;
        if (me == 1) {
            float pc = attrs.get(actives.get(0)).getRuntimePercent();
            if (pc < 0.01f) pc = 0f;
            else if (pc > 1) pc = 1;
            mRoot.setRadius(oneDP * 14 * pc);
            Log.d(TAG, "run: pc = " + pc);
            mDimView.setAlpha(0.4f * (attrs.get(actives.get(0)).getRuntimePercent()));
            //  mRoot.setRoundNumber( attrs.get(actives.get(0)).getRuntimePercent(),false);
            mRoot.setAlpha(1);
        } else if (me != 0) {
            //  other, active_i >1
            // min = 0.3
            // max = 0.45
            float min = 0.3f, max = 0.65f;
            float hieu = max - min;
            float heSo_sau = (me - 1.0f) / (me - 0.75f); // 1/2, 2/3,3/4, 4/5, 5/6 ...
            float heSo_truoc = (me - 2.0f) / (me - 0.75f); // 0/1, 1/2, 2/3, ...
            float darken = min + hieu * heSo_truoc + hieu * (heSo_sau - heSo_truoc) * attrs.get(actives.get(0)).getRuntimePercent();
            // Log.d(TAG, "darken = " + darken);
            //  mRoot.setDarken(darken,false);
            if (darken < 0) darken = 0;
            if (darken > 1) darken = 1;
            mDimView.setAlpha(darken);
            //   TabSwitcherFrameLayout.setDarken(0.3f + 0.6f*pcOnTopLayer,false);
            //  mRoot.setRoundNumber(1,true);
            mRoot.setRadius(oneDP * 14);
            if (me == actives.size() - 1) mRoot.setAlpha(1 - darken);
            else mRoot.setAlpha(1);
        }

        doTranslateNavigation(attrs, actives, me);
    }

    public void doTranslateNavigation(ArrayList<CardLayerController.CardLayerAttribute> attrs, ArrayList<Integer> actives, int me) {
        if (mBottomNavigationParent != null) {
            int bnpHeight = mBottomNavigationParent.getHeight();
            if (me == 1) {
                float pc = attrs.get(actives.get(0)).getRuntimePercent();
                if (pc > 1) pc = 1;
                else if (pc < 0) pc = 0;
                mBottomNavigationParent.setTranslationY(pc * bnpHeight);
            } else if (me != 0) {
                mBottomNavigationParent.setTranslationY(bnpHeight);
            }
        }
    }

    @Override
    public void onLayerHeightChanged(CardLayerController.CardLayerAttribute attr) {
        if (mRoot != null) {
            float pc = (attr.mCurrentTranslate) / attr.getMaxPosition();
            Log.d(TAG, "onTranslateChanged : pc = " + pc);
            if (pc > 1) pc = 1;
            else if (pc < 0) pc = 0;
            float scale = 0.2f + pc * 0.8f;

            float radius = 1 - pc;
            if (radius < 0.1f) radius = 0;
            else if (radius > 1) radius = 1;
            mRoot.setRadius(oneDP * 14 * radius);
            mRoot.setAlpha(scale);

        }
    }

    @Override
    public boolean isFullscreenLayer() {
        return true;
    }

    @Override
    public int getLayerMinHeight(Context context, int maxHeight) {
        return maxHeight;
    }

    @Override
    public String getCardLayerTag() {
        return TAG;
    }

    private BottomNavigationView mBottomNavigationView;
    private View mBottomNavigationParent;

    public BottomNavigationView getBottomNavigationView() {
        return mBottomNavigationView;
    }

    public void attachBottomNavigationView(AppActivity activity) {

        try {
            mBottomNavigationParent = activity.findViewById(R.id.bottom_navigation_parent);
            mBottomNavigationView = mBottomNavigationParent.findViewById(R.id.bottom_navigation_view);
            mBottomNavigationView.setOnNavigationItemSelectedListener(mItemSelectedListener);


        } catch (Exception ignore) {
        }
    }

    public LibraryTabFragment navigateToLibraryTab(boolean go) {
        Fragment fragment = navigateToTab(1, go);
        if (fragment instanceof LibraryTabFragment) {
            return (LibraryTabFragment) fragment;
        }
        return null;
    }

    public Fragment navigateToTab(int item, boolean go) {
        if (go)
            mViewPager.setCurrentItem(item);
        Fragment fragment = mNavigationAdapter.getItem(1);
        if (fragment instanceof BackPressableFragment) {
            ((BackPressableFragment) fragment).popToRootFragment();
            return ((BackPressableFragment) fragment).getRootFragment();
        }
        return null;
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
                    if (mViewPager.getCurrentItem() != 2) {
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
        Tool.vibrate(getContext());
        Log.d(TAG, "bringToTopThisTab: after : " + mNavigationStack);
        boolean b = mNavigationStack.remove(tabPosition);
        if (b)
            Log.d(TAG, "bringToTopThisTab: removed " + tabPosition);

        mNavigationStack.add(tabPosition);
        Log.d(TAG, "bringToTopThisTab: after : " + mNavigationStack);
    }

    @Override
    public boolean onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if (!mNavigationStack.isEmpty()) {
            if (!mNavigationAdapter.onBackPressed(mNavigationStack.get(mNavigationStack.size() - 1))) {
                mNavigationStack.remove(mNavigationStack.size() - 1);
                if (mNavigationStack.isEmpty()) return onBackPressed();
                mViewPager.setCurrentItem(mNavigationStack.get(mNavigationStack.size() - 1), true);
            }
        } else if (mViewPager.getCurrentItem() != 0) mViewPager.setCurrentItem(0, true);
        else return mViewPager.getCurrentItem() != 0 || mNavigationAdapter.onBackPressed(0);
        return true;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    MenuItem prevMenuItem;

    @Override
    public void onPageSelected(int i) {
        bringToTopThisTab(i);
        if (mBottomNavigationView != null) {
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

    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onQueueChanged() {

    }

    @Override
    public void onPlayingMetaChanged() {
        if (getContext() != null) {
            Artist artist = ArtistLoader.getArtist(getContext(), MusicPlayerRemote.getCurrentSong().artistId);
            if (mBackImageView.getVisibility() == View.VISIBLE)
                ArtistGlideRequest.Builder.from(GlideApp.with(getContext()), artist)
                        .tryToLoadOriginal(true)
                        .generateBuilder(getContext())
                        .build()
                        /*    .error(
                                    ArtistGlideRequest
                                            .Builder
                                            .from(GlideApp.with(getContext()),mArtist)
                                            .tryToLoadOriginal(false)
                                            .generateBuilder(getContext())
                                            .build())*/
                        .thumbnail(
                                ArtistGlideRequest
                                        .Builder
                                        .from(GlideApp.with(getContext()), artist)
                                        .tryToLoadOriginal(false)
                                        .generateBuilder(getContext())
                                        .build())
                        .into(mBackImageView);
        }
    }

    @Override
    public void onPlayStateChanged() {

    }

    @Override
    public void onRepeatModeChanged() {

    }

    @Override
    public void onShuffleModeChanged() {

    }

    @Override
    public void onMediaStoreChanged() {

    }

    @Override
    public void onPaletteChanged() {

    }

    @Override
    public void onDestroyView() {
        if (getActivity() instanceof MusicServiceActivity)
            ((MusicServiceActivity) getActivity()).removeMusicServiceEventListener(this);
        super.onDestroyView();
    }

    public void onUsingArtistImagePreferenceChanged() {
        mIsUsingAIAsBg = App.getInstance().getPreferencesUtility().isUsingArtistImageAsBackground();
        if (mIsUsingAIAsBg) {
            mBackImageView.setVisibility(View.VISIBLE);
            onPlayingMetaChanged();
        } else {
            mBackImageView.setVisibility(View.GONE);
        }
    }
}
