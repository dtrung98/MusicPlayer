package com.ldt.musicr.ui.main.nowplaying;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldt.musicr.R;
import com.ldt.musicr.fragments.BaseLayerFragment;
import com.ldt.musicr.ui.main.LayerController;
import com.ldt.musicr.services.MusicStateListener;
import com.ldt.musicr.ui.widget.rounded.DarkenAndRoundedBackgroundContraintLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NowPlayingController extends BaseLayerFragment implements MusicStateListener {
    private static final String TAG ="NowPlayingController";
    @BindView(R.id.root) CardView mRoot;
    @BindView(R.id.dim_view) View mDimView;
    private float mMaxRadius= 0;

    @BindView(R.id.title) TextView mTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.now_playing_controller,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mMaxRadius = getResources().getDimension(R.dimen.max_radius_layer);
        mTitle.setSelected(true);
    }
    private void setRadius(float value) {
        if(mRoot!=null) {
            if(value>1) value=1;
            else if(value<=0.1f) value = 0;
            mRoot.setRadius(mMaxRadius * value);
        }
    }

    @Override
    public void onUpdateLayer( ArrayList<LayerController.Attr> attrs, ArrayList<Integer> actives, int me) {

        if(mRoot==null) return;
        if(me ==1) {
            mDimView.setAlpha(0.3f*(attrs.get(actives.get(0)).getRuntimePercent()));
          //  mRoot.setRoundNumber( attrs.get(actives.get(0)).getRuntimePercent(),true);
            setRadius( attrs.get(actives.get(0)).getRuntimePercent());
        } else
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
            mDimView.setAlpha(darken);
           // mRoot.setRoundNumber(1,true);
            setRadius(1);
        }
    }

    public void onUpdateLayer(LayerController.Attr attr, float pcOnTopLayer, int active_i) {
        if(active_i==0) {
            if(mRoot!=null) //mRoot.setRoundNumber(attr.getPercent(),true);
                setRadius(attr.getPercent());
        } else if(active_i>=1) {
            if(mRoot!=null) {
                mDimView.setAlpha(pcOnTopLayer*0.3f);
              //  mRoot.setRoundNumber(1,true);
                setRadius(1);
            }
        }
    }

    @Override
    public void onTranslateChanged(LayerController.Attr attr) {
        Log.d(TAG, "onTranslateChanged : pc = "+attr.getRuntimePercent());
    }

    @Override
    public int minPosition(Context context,int h) {
        return (int) (context.getResources().getDimension(R.dimen.bottom_navigation_height)+ context.getResources().getDimension(R.dimen.now_laying_height_in_minimize_mode));
    }

    @Override
    public String tag() {
        return TAG;
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {

    }

    @Override
    public boolean onGestureDetected(int gesture) {
        if(gesture==LayerController.SINGLE_TAP_COMFIRM) {
            LayerController.Attr a = mLayerController.getMyAttr(this);
            if(a!=null) {
                if(a.getState()== LayerController.Attr.MINIMIZED)
                    a.animateToMax();
                else
                    a.animateToMin();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getMaxPositionType() {
        return true;
    }


}
