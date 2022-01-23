package com.ldt.musicr.ui.maintab;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ldt.musicr.ui.CardLayerController;

import java.util.ArrayList;

public abstract class CardLayerFragment extends Fragment implements CardLayerController.CardLayer {
    private static final String TAG = "CardLayerFragment";

    public CardLayerController getCardLayerController() {
        return mCardLayerController;
    }

    public CardLayerController mCardLayerController;

    public void setCardLayerController(CardLayerController cardLayerController) {
        this.mCardLayerController = cardLayerController;
    }

    @Override
    public void onAddedToLayerController(CardLayerController.CardLayerAttribute attr) {

    }

    private int mMaxPosition = 0;
    private View mLayerRootView;

    public View getLayerRootView(Activity activity, ViewGroup container, int maxPosition) {
        if (mLayerRootView == null) {
            mMaxPosition = maxPosition;
            View view = onCreateView(LayoutInflater.from(activity), container);
            Log.d(TAG, "getParent: id = " + view.getId());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) mMaxPosition);
            params.gravity = Gravity.BOTTOM;
            view.setLayoutParams(params);
            mLayerRootView = view;
        }
        return mLayerRootView;
    }

    @Override
    public void onLayerUpdate(ArrayList<CardLayerController.CardLayerAttribute> attrs, ArrayList<Integer> actives, int me) {

    }

    /**
     * Call on CardLayer position changed
     * @param attr the CardLayerAttribute associates with this Cardlayer
     */
    @Override
    public void onLayerHeightChanged(CardLayerController.CardLayerAttribute attr) {

    }

    @Override
    public boolean onGestureDetected(int gesture) {
        return false;
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mMaxPosition == 0) {
            if (isFullscreenLayer()) mMaxPosition = mCardLayerController.screenSize[1];
            else
                mMaxPosition = (int) (mCardLayerController.screenSize[1] - mCardLayerController.statusBarHeight - 2 * mCardLayerController.oneDp - mCardLayerController.maxMarginTop);
        }
        return getLayerRootView(getActivity(), container, mMaxPosition);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    @Override
    public boolean onTouchParentView(boolean handled) {
        return false;
    }

    @Override
    public boolean isFullscreenLayer() {
        return false;
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }
}
