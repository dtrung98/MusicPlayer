package com.ldt.musicr.ui.playingqueue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.motion.MotionLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.service.MusicPlayer;
import com.ldt.musicr.service.MusicService;
import com.ldt.musicr.service.MusicStateListener;
import com.ldt.musicr.ui.MainActivity;
import com.ldt.musicr.ui.tabs.BaseLayerFragment;
import com.ldt.musicr.ui.LayerController;
import com.ldt.musicr.util.Tool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class PlayingQueueController extends BaseLayerFragment implements MusicStateListener {
    private static final String TAG ="PlayingQueueController";
    @BindView(R.id.rootCardView)
    CardView mRootCardView;

    @BindView(R.id.root)
    View mRoot;

    @BindView(R.id.dim_view) View mDimView;
    private float mMaxRadius = 18;

    @BindView(R.id.constraint_root)
    ViewGroup mConstraintRoot;


    @BindView(R.id.playlist_title)
    TextView mPlaylistTitle;
    @BindView(R.id.down)
    ImageView mDownIcon;
    @BindView(R.id.shuffle_button) ImageView mShuffleButton;
    @BindView(R.id.repeat_button) ImageView mRepeatButton;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PlayingQueueAdapter mAdapter;



    @OnTouch({R.id.playlist_title,R.id.down})
    boolean touchDetected(View view, MotionEvent event) {
        return mLayerController.streamOnTouchEvent(mRoot,event);
    }
    @OnClick({R.id.playlist_title,R.id.down})
    void titlePanelClicked() {
        LayerController.Attr a = mLayerController.getMyAttr(this);
        if(a!=null) {
            if(a.getState()== LayerController.Attr.MINIMIZED)
                a.animateToMax();
            else
                a.animateToMin();
        }
    }

    public void onColorPaletteReady(int color1, int color2, float alpha1, float alpha2) {
        mPlaylistTitle.setTextColor(Tool.lighter(color1,0.5f));
        mDownIcon.setColorFilter(Tool.lighter(color1,0.5f));
        updateShuffleState();
        updateRepeatState();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.playing_queue_controller,container,false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mMaxRadius = getResources().getDimension(R.dimen.max_radius_layer);
        mPlaylistTitle.setSelected(true);
        mAdapter = new PlayingQueueAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);

        if(getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).addMusicStateListener(this);
        onMetaChanged();
    }
    @OnTouch(R.id.recycler_view)
    boolean onTouchRecyclerView(RecyclerView view, MotionEvent event) {
        boolean recycer_handle = view.onTouchEvent(event);

        Log.d(TAG, "onTouchRecyclerView: "+recycer_handle);
        if(!recycer_handle)
            return mLayerController.streamOnTouchEvent(mRoot,event);
        return true;

    }

    @Override
    public void onDestroyView() {
        if(getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).removeMusicStateListener(this);
        super.onDestroyView();
    }

    @Override
    public String tag() {
        return TAG;
    }
    private void setRadius(float value) {
        if(mRoot!=null) {
            if(value>1) value=1;
            else if(value<=0.1f) value = 0;
            mRootCardView.setRadius(mMaxRadius * value);
        }
    }

    @Override
    public boolean onGestureDetected(int gesture) {
        if(gesture==LayerController.SINGLE_TAP_UP) {
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
    float mPrevProgress = 0;

    @Override
    public void onTranslateChanged(LayerController.Attr attr) {
            Log.d(TAG, "onTranslateChanged");

            float pc = attr.getRuntimePercent();
            if(pc>1) pc=1;
            else if(pc<0) pc = 0;
            setRadius(pc);
        /*    if(mConstraintRoot instanceof MotionLayout) {
                MotionLayout motionLayout = ((MotionLayout)mConstraintRoot);
                float currentProgress = motionLayout.getProgress();
                if(isTranslateUp(pc)&&currentProgress<pc) motionLayout.setProgress(pc);
                else if(isTranslateDown(pc)&&currentProgress>pc) motionLayout.setProgress(pc);
            }
        mPrevProgress = pc;*/
    }
    private boolean isTranslateUp(float pc) {
        return mPrevProgress <= pc;
    }
    private boolean isTranslateDown(float pc) {
        return mPrevProgress>pc;
    }
    private void updateShuffleState() {
      int  mode =  MusicPlayer.getShuffleMode();
      if(mode== MusicService.SHUFFLE_NONE)
          Log.d(TAG, "updateShuffleState: None");
      else if(mode ==MusicService.SHUFFLE_NORMAL)
          Log.d(TAG, "updateShuffleState: Normal");
      else Log.d(TAG, "updateShuffleState: Auto");

      if(mode==MusicService.SHUFFLE_NONE)
          mShuffleButton.setColorFilter(getResources().getColor(R.color.FlatWhite));
      else mShuffleButton.setColorFilter(Tool.getBaseColor());
    }

    @OnClick(R.id.shuffle_button)
    void cycleShuffle() {
        MusicPlayer.cycleShuffle();
        updateShuffleState();
        updateRepeatState();
    }

    @OnClick(R.id.repeat_button)
    void cycleRepeat() {
        MusicPlayer.cycleRepeat();
        updateShuffleState();
        updateRepeatState();
    }
    private void updateRepeatState() {
        int mode = MusicPlayer.getRepeatMode();

        switch (mode) {
            case MusicService.REPEAT_NONE:
                Log.d(TAG, "updateRepeatState: None");
                mRepeatButton.setImageResource(R.drawable.repeat);
                mRepeatButton.setColorFilter(getResources().getColor(R.color.FlatWhite));
                break;
            case MusicService.REPEAT_CURRENT:
                Log.d(TAG, "updateRepeatState: Current");
                mRepeatButton.setColorFilter(Tool.getBaseColor());
                mRepeatButton.setImageResource(R.drawable.repeat_one);
                break;
            case MusicService.REPEAT_ALL:
                mRepeatButton.setImageResource(R.drawable.repeat);
                mRepeatButton.setColorFilter(Tool.getBaseColor());
                Log.d(TAG, "updateRepeatState: All");
        }
    }

    @Override
    public void onUpdateLayer(ArrayList<LayerController.Attr> attrs, ArrayList<Integer> actives, int me) {

        if(mRoot==null) return;
        if(me ==1) {
            mDimView.setAlpha(0.3f*(attrs.get(actives.get(0)).getRuntimePercent()));
            setRadius( attrs.get(actives.get(0)).getRuntimePercent());
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
            mDimView.setAlpha(darken);
            setRadius(1);
        }
    }

    @Override
    public int minPosition(Context context, int h) {
        return (int) context.getResources().getDimension(R.dimen.bottom_navigation_height);
    }

    public void popUp() {
        LayerController.Attr a = mLayerController.getMyAttr(this);
        if(a!=null) {
            if(a.getState()== LayerController.Attr.MINIMIZED)
                a.animateToMax();
        }
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {
        Log.d(TAG, "onPlaylistChanged");
    }

    @Override
    public void onMetaChanged() {

        updateShuffleState();
        updateRepeatState();

    }

    public void setData(List<Song> songs2) {
        if(songs2==null||songs2.isEmpty())
        mAdapter.setData(songs2);
        else {
            int current = MusicPlayer.getQueuePosition();
            if(current<songs2.size()) {
                List<Song> songWillPlay = songs2.subList(current,songs2.size()-1);
                mAdapter.setData(songWillPlay);
            }
        }
        int[] history = MusicPlayer.getQueueHistoryList();
        String historyStr = "";
        for (int h: history){
            historyStr +=" | "+h;
        }
        Log.d(TAG, "History :"+historyStr);
    }

}
