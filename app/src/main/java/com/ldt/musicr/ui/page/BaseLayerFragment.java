package com.ldt.musicr.ui.page;

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

import com.ldt.musicr.ui.LayerController;

import java.util.ArrayList;

public abstract class BaseLayerFragment extends Fragment implements LayerController.BaseLayer {
    private static final String TAG ="BaseLayerFragment";

    public LayerController getLayerController() {
        return mLayerController;
    }

    public LayerController mLayerController;
    public  void setLayerController(LayerController layerController) {
        this.mLayerController = layerController;
    }

    @Override
    public void onAddedToContainer(LayerController.Attr attr) {

    }

    private int mMaxPosition=0;
    private View v;
    public View getParent(Activity activity, ViewGroup container, int maxPosition) {
        if(v==null) {
            mMaxPosition = maxPosition;
            View view = onCreateView(LayoutInflater.from(activity), container);
            Log.d(TAG, "getParent: id = "+view.getId());
            FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) mMaxPosition);
            params.gravity = Gravity.BOTTOM;
            view.setLayoutParams(params);
            v = view;
        }
        return v;
    }

    @Override
    public void onUpdateLayer(ArrayList<LayerController.Attr> attrs, ArrayList<Integer> actives, int me) {

    }

    @Override
    public void onTranslateChanged(LayerController.Attr attr) {

    }

    @Override
    public boolean onGestureDetected(int gesture) {
        return false;
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mMaxPosition==0) {
            if (getMaxPositionType()) mMaxPosition = mLayerController.ScreenSize[1];
            else mMaxPosition = (int) (mLayerController.ScreenSize[1] -mLayerController.status_height - 2 * mLayerController.oneDp - mLayerController.mMaxMarginTop);
        }
        return getParent(getActivity(),container,mMaxPosition);
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
    public boolean getMaxPositionType() {
        return false;
    }


    @Override
    public boolean onBackPressed() {return false;}
}
