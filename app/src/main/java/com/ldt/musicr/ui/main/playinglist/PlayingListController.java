package com.ldt.musicr.ui.main.playinglist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.R;
import com.ldt.musicr.fragments.BaseLayerFragment;
import com.ldt.musicr.ui.main.LayerController;
import com.ldt.musicr.ui.widget.rounded.DarkenAndRoundedBackgroundContraintLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayingListController extends BaseLayerFragment {
    private static final String TAG ="PlayingListController";
    @BindView(R.id.root)
    DarkenAndRoundedBackgroundContraintLayout mRoot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.playing_list_controller,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
    }

    @Override
    public String tag() {
        return TAG;
    }

    public void onUpdateLayer(LayerController.Attr attr, float pcOnTopLayer, int active_i) {
        if(active_i==0) {
            if(mRoot!=null) mRoot.setRoundNumber(attr.getPercent(),true);
        } else if(active_i>=1) {
            if(mRoot!=null) {
                mRoot.setDarken(pcOnTopLayer*0.3f,false);
                mRoot.setRoundNumber(1,true);
            }
        }
    }

    @Override
    public void onTranslateChanged(LayerController.Attr attr) {
        if(mRoot!=null) {
            float pc = attr.getRuntimePercent();
            if(pc>1) pc=1;
            else if(pc<0) pc = 0;
            mRoot.setRoundNumber(pc,true);

        }
    }

    @Override
    public void onUpdateLayer(ArrayList<LayerController.Attr> attrs, ArrayList<Integer> actives, int me) {

        if(mRoot==null) return;
        if(me ==1) {
            mRoot.setDarken(0.3f*(attrs.get(actives.get(0)).getRuntimePercent()),false);
            mRoot.setRoundNumber( attrs.get(actives.get(0)).getRuntimePercent(),true);
        } else
        {
            //  other, active_i >1
            // min = 0.3
            // max = 0.45
            float min = 0.3f, max =0.9f;
            float hieu = max - min;
            float heSo_sau = (me-1.0f)/(me-0.75f); // 1/2, 2/3,3/4, 4/5, 5/6 ...
            float heSo_truoc =  (me-2.0f)/(me-0.75f); // 0/1, 1/2, 2/3, ...
            float darken = min + hieu*heSo_truoc + hieu*(heSo_sau - heSo_truoc)*attrs.get(actives.get(0)).getRuntimePercent();
            // Log.d(TAG, "darken = " + darken);
            mRoot.setDarken(darken,false);
            //   TabSwitcherFrameLayout.setDarken(0.3f + 0.6f*pcOnTopLayer,false);
            mRoot.setRoundNumber(1,true);
        }
    }

    @Override
    public int minPosition(Context context, int h) {
        return (int) context.getResources().getDimension(R.dimen.bottom_navigation_height);
    }
}
