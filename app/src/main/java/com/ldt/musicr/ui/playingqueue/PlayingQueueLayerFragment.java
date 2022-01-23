package com.ldt.musicr.ui.playingqueue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldt.musicr.R;
import com.ldt.musicr.contract.AbsMediaAdapter;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.service.MusicService;
import com.ldt.musicr.service.MusicServiceEventListener;
import com.ldt.musicr.ui.AppActivity;
import com.ldt.musicr.ui.CardLayerController;
import com.ldt.musicr.ui.floating.LyricFragment;
import com.ldt.musicr.ui.maintab.CardLayerFragment;
import com.ldt.musicr.util.Tool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class PlayingQueueLayerFragment extends CardLayerFragment implements MusicServiceEventListener {
    private static final String TAG = "PlayingQueue";
    @BindView(R.id.rootCardView)
    CardView mRootCardView;

    @BindView(R.id.root)
    View mRoot;

    @BindView(R.id.dim_view)
    View mDimView;
    private float mMaxRadius = 18;

    @BindView(R.id.constraint_root)
    ViewGroup mConstraintRoot;

    @OnClick(R.id.lyric)
    void showLyric() {
        if (getActivity() != null)
            LyricFragment.newInstance().show(getActivity().getSupportFragmentManager(), "LyricBottomSheet");
    }

    @OnClick(R.id.save)
    void saveCurrentPlaylist() {

    }

    @BindView(R.id.playlist_title)
    TextView mPlaylistTitle;
    @BindView(R.id.down)
    ImageView mDownIcon;
    @BindView(R.id.shuffle_button)
    ImageView mShuffleButton;
    @BindView(R.id.repeat_button)
    ImageView mRepeatButton;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.lyric)
    View mLyricView;
    @BindView(R.id.save)
    View mSaveView;

    private final PlayingQueueAdapter mAdapter = new PlayingQueueAdapter();

    @OnTouch({R.id.playlist_title, R.id.down})
    boolean touchDetected(View view, MotionEvent event) {
        return mCardLayerController.dispatchOnTouchEvent(mRoot, event);
    }

    @OnClick({R.id.playlist_title, R.id.down})
    void titlePanelClicked() {
        CardLayerController.CardLayerAttribute a = mCardLayerController.getMyAttr(this);
        if (a != null) {
            if (a.getState() == CardLayerController.CardLayerAttribute.MINIMIZED)
                a.animateToMax();
            else
                a.animateToMin();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter.init(requireContext());
        mAdapter.setName(TAG);
    }

    private void onColorPaletteReady(int color1, int color2, float alpha1, float alpha2) {
        mPlaylistTitle.setTextColor(Tool.lighter(color1, 0.5f));
        mDownIcon.setColorFilter(Tool.lighter(color1, 0.5f));
        updateShuffleState();
        updateRepeatState();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.screen_playing_queue, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mMaxRadius = getResources().getDimension(R.dimen.max_radius_layer);
        mPlaylistTitle.setSelected(true);

        // apply window insets to recyclerview
        ViewCompat.setOnApplyWindowInsetsListener(mRecyclerView, (v, insets) -> {
            mRecyclerView.setPadding(insets.getSystemWindowInsetLeft(), 0, insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return ViewCompat.onApplyWindowInsets(v, insets);
        });

        // attach adapter to recyclerview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRecyclerView.setAdapter(mAdapter);

        if (getActivity() instanceof AppActivity) {
            ((AppActivity) getActivity()).addMusicServiceEventListener(this);
        }
        setUp();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                String ScrollState;
                switch (recyclerView.getScrollState()) {
                    case SCROLL_STATE_FLING:
                        ScrollState = "SCROLL_STATE_FLING";
                        break;
                    case SCROLL_STATE_IDLE:
                        ScrollState = "SCROLL_STATE_IDLE";
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        ScrollState = "SCROLL_STATE_TOUCH_SCROLL";
                        break;
                    default:
                        ScrollState = "";
                }
                Log.d(TAG, "onScrolled: dy = " + dy + ",Scroll State = " + ScrollState);
                if (!recyclerView.canScrollVertically(-1)) Log.d(TAG, "onScrolled: on top");
                else if (!recyclerView.canScrollVertically(1)) Log.d(TAG, "onScrolled: on bottom");

                if (!recyclerView.canScrollVertically(-1) && recyclerView.getScrollState() == SCROLL_STATE_FLING && dy < 0)
                    mCardLayerController.getMyAttr(PlayingQueueLayerFragment.this).shakeOnMax(-dy);
            }
        });
    }

    private boolean mFirstTouchEvent = true;
    private boolean mInStreamEvent = false;
    private float mPreviousY = 0;

    @OnTouch(R.id.recycler_view)
    boolean onTouchRecyclerView(RecyclerView view, MotionEvent event) {
        boolean isRecyclerViewOnTop = !view.canScrollVertically(-1);
        float currentY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchRecyclerView: down");
                //    Toast.makeText(getContext(),"Down",Toast.LENGTH_SHORT).show();
                mFirstTouchEvent = true;
                view.onTouchEvent(event);
                if (isRecyclerViewOnTop) mCardLayerController.dispatchOnTouchEvent(mRoot, event);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchRecyclerView: up");
                mFirstTouchEvent = false;
                mPreviousY = 0;
                if (mInStreamEvent) {
                    mInStreamEvent = false;
                    mCardLayerController.dispatchOnTouchEvent(mRoot, event);
                }
                view.onTouchEvent(event);
                break;
            default:
                Log.d(TAG, "onTouchRecyclerView: " + event.getAction());
                if (mPreviousY < currentY && isRecyclerViewOnTop) mInStreamEvent = true;
                if (mInStreamEvent) mCardLayerController.dispatchOnTouchEvent(mRoot, event);
                else view.onTouchEvent(event);
                mFirstTouchEvent = false;
        }
        mPreviousY = currentY;
        return true;
    }

    @Override
    public void onDestroyView() {
        if (getActivity() instanceof AppActivity)
            ((AppActivity) getActivity()).removeMusicServiceEventListener(this);
        mSet = false;
        mAdapter.destroy();
        super.onDestroyView();
    }

    @Override
    public String getCardLayerTag() {
        return TAG;
    }

    private void setRadius(float value) {
        if (mRootCardView != null) {
            if (value > 1) value = 1;
            else if (value <= 0.1f) value = 0;
            mRootCardView.setRadius(mMaxRadius * value);
        }
    }

    @Override
    public boolean onGestureDetected(int gesture) {
        if (gesture == CardLayerController.SINGLE_TAP_UP) {
            CardLayerController.CardLayerAttribute a = mCardLayerController.getMyAttr(this);
            if (a != null) {
                if (a.getState() == CardLayerController.CardLayerAttribute.MINIMIZED)
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
    public void onLayerHeightChanged(CardLayerController.CardLayerAttribute attr) {
        Log.d(TAG, "onTranslateChanged");

        float pc = attr.getRuntimePercent();
        if (pc > 1) pc = 1;
        else if (pc < 0) pc = 0;
        setRadius(pc);
        if (mConstraintRoot instanceof MotionLayout) {
            MotionLayout motionLayout = ((MotionLayout) mConstraintRoot);
            float currentProgress = motionLayout.getProgress();
            if (isTranslateUp(pc) && currentProgress < pc) motionLayout.setProgress(pc);
            else if (isTranslateDown(pc) && currentProgress > pc) motionLayout.setProgress(pc);
        }
        mPrevProgress = pc;
    }

    private boolean isTranslateUp(float pc) {
        return mPrevProgress <= pc;
    }

    private boolean isTranslateDown(float pc) {
        return mPrevProgress > pc;
    }

    private void updateShuffleState() {
        int mode = MusicPlayerRemote.getShuffleMode();
        if (mode == MusicService.SHUFFLE_MODE_NONE)
            Log.d(TAG, "updateShuffleState: None");
        else if (mode == MusicService.SHUFFLE_MODE_SHUFFLE)
            Log.d(TAG, "updateShuffleState: Normal");
        else Log.d(TAG, "updateShuffleState: Auto");

        if (mode == MusicService.SHUFFLE_MODE_NONE)
            mShuffleButton.setColorFilter(getResources().getColor(R.color.FlatWhite));
        else mShuffleButton.setColorFilter(Tool.getBaseColor());
    }

    @OnClick(R.id.shuffle_button)
    void cycleShuffle() {
        MusicPlayerRemote.toggleShuffleMode();
    }

    @OnClick(R.id.repeat_button)
    void cycleRepeat() {
        MusicPlayerRemote.cycleRepeatMode();
        updateShuffleState();
        updateRepeatState();
    }

    private void updateRepeatState() {
        int mode = MusicPlayerRemote.getRepeatMode();

        switch (mode) {
            case MusicService.REPEAT_MODE_NONE:
                Log.d(TAG, "updateRepeatState: None");
                mRepeatButton.setImageResource(R.drawable.repeat);
                mRepeatButton.setColorFilter(getResources().getColor(R.color.FlatWhite));
                break;
            case MusicService.REPEAT_MODE_THIS:
                Log.d(TAG, "updateRepeatState: Current");
                mRepeatButton.setColorFilter(Tool.getBaseColor());
                mRepeatButton.setImageResource(R.drawable.repeat_one);
                break;
            case MusicService.REPEAT_MODE_ALL:
                mRepeatButton.setImageResource(R.drawable.repeat);
                mRepeatButton.setColorFilter(Tool.getBaseColor());
                Log.d(TAG, "updateRepeatState: All");
        }
    }

    @Override
    public void onLayerUpdate(ArrayList<CardLayerController.CardLayerAttribute> attrs, ArrayList<Integer> actives, int me) {

        if (mRoot == null) return;
        if (me == 1) {
            mDimView.setAlpha(0.3f * (attrs.get(actives.get(0)).getRuntimePercent()));
            setRadius(attrs.get(actives.get(0)).getRuntimePercent());
        } else {
            //  other, active_i >1
            // min = 0.3
            // max = 0.45
            float min = 0.3f, max = 0.9f;
            float hieu = max - min;
            float heSo_sau = (me - 1.0f) / (me - 0.75f); // 1/2, 2/3,3/4, 4/5, 5/6 ...
            float heSo_truoc = (me - 2.0f) / (me - 0.75f); // 0/1, 1/2, 2/3, ...
            float darken = min + hieu * heSo_truoc + hieu * (heSo_sau - heSo_truoc) * attrs.get(actives.get(0)).getRuntimePercent();
            // Log.d(TAG, "darken = " + darken);
            mDimView.setAlpha(darken);
            setRadius(1);
        }
    }

    @Override
    public int getLayerMinHeight(Context context, int h) {
        int systemInsetsBottom = 0;
        CardLayerController controller = getCardLayerController();
        Activity activity = controller != null ? controller.getActivity() : getActivity();
        if (activity instanceof AppActivity) {
            systemInsetsBottom = ((AppActivity) activity).getCurrentSystemInsets()[3];
        }
        Log.d(TAG, "activity availibility = " + (activity != null));
        Log.d(TAG, "systemInsetsBottom = " + systemInsetsBottom);
        return systemInsetsBottom + (int) context.getResources().getDimension(R.dimen.bottom_navigation_height);
    }

    public void popUp() {
        CardLayerController.CardLayerAttribute a = mCardLayerController.getMyAttr(this);
        if (a != null) {
            if (a.getState() == CardLayerController.CardLayerAttribute.MINIMIZED)
                a.animateToMax();
        }
    }

    boolean mSet = false;

    private void setUp() {
        if (mSet) return;
        mSet = true;
        onQueueChanged();
        onRepeatModeChanged();
        onShuffleModeChanged();
        onPlayStateChanged();
    }

    @Override
    public void onServiceConnected() {
        setUp();
    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onQueueChanged() {
        setData(MusicPlayerRemote.getPlayingQueue());
    }


    @Override
    public void onMediaStoreChanged() {
        onQueueChanged();
    }

    @Override
    public void onPaletteChanged() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ((RippleDrawable) mShuffleButton.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
            ((RippleDrawable) mRepeatButton.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
            ((RippleDrawable) mLyricView.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
            ((RippleDrawable) mSaveView.getBackground()).setColor(ColorStateList.valueOf(Tool.getBaseColor()));
        }
        onColorPaletteReady(Tool.ColorOne, Tool.ColorTwo, Tool.AlphaOne, Tool.AlphaTwo);
        mAdapter.notifyOnMediaStateChanged(AbsMediaAdapter.PALETTE_CHANGED);
    }


    @Override
    public void onPlayingMetaChanged() {
        mAdapter.notifyOnMediaStateChanged(AbsMediaAdapter.PLAY_STATE_CHANGED);
    }

    @Override
    public void onPlayStateChanged() {
        mAdapter.notifyOnMediaStateChanged(AbsMediaAdapter.PLAY_STATE_CHANGED);
    }

    @Override
    public void onRepeatModeChanged() {
        updateRepeatState();
    }

    @Override
    public void onShuffleModeChanged() {
        updateShuffleState();
    }


    public void setData(List<Song> songs2) {
        mAdapter.setData(songs2);
    }

}
