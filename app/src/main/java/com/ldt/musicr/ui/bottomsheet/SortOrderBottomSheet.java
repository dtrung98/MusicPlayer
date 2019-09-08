package com.ldt.musicr.ui.bottomsheet;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ldt.musicr.R;
import com.ldt.musicr.util.SortOrder;
import com.ldt.musicr.util.Tool;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

public class SortOrderBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "SortOrderBottomSheet";
    public static  final  int ALPHABET_ASC = 0;
    public static final int TIME_ADDED_ASC = 1;
    public static final  int FAVOR_ASC = 2;
    public static final int ALPHABET_DESC = 3;
    public static final int TIME_ADDED_DESC =4;
    public static final int FAVOR_DESC = 5;
    private int mSortOrder = ALPHABET_ASC;
    public static int[] mSortStringRes = {R.string.sort_by_name_asc,R.string.sort_by_time_asc,R.string.sort_by_favor_asc,R.string.sort_by_name_desc,R.string.sort_by_time_desc,R.string.sort_by_favor_desc};
    public static String[] mSortOrderCodes = {
            SortOrder.SongSortOrder.SONG_A_Z,
            SortOrder.SongSortOrder.SONG_DATE,
            SortOrder.SongSortOrder.SONG_ARTIST,
            SortOrder.SongSortOrder.SONG_Z_A,
            SortOrder.SongSortOrder.SONG_DATE_DESC,
            SortOrder.SongSortOrder.SONG_ARTIST_DESC};
    @BindViews({R.id.by_title_text,R.id.last_added_text,R.id.most_played_text})
    View[] mSortTextViews;

    @BindViews({R.id.by_title_tick,R.id.by_time_tick,R.id.most_played_tick})
    ImageView[] mSortTicks;

    @BindViews({R.id.asc_text,R.id.desc_text})
    View[] mOrderTextViews;

    @BindViews({R.id.asc_tick, R.id.desc_tick})
    ImageView[] mOrderTicks;

    @OnClick({R.id.by_title_text,R.id.last_added_text,R.id.most_played_text})
    void onSortTextClick(View view) {
        int order_add = (mSortOrder>2) ? 3 : 0;
        switch (view.getId()) {
            case R.id.by_title_text: updateAndNotifyNewSort(order_add);break;
            case R.id.last_added_text: updateAndNotifyNewSort(order_add + 1);break;
            case R.id.most_played_text: updateAndNotifyNewSort(order_add + 2); break;
        }
    }

    @OnClick({R.id.asc_text,R.id.desc_text})
    void onOrderTextClick(View view) {
        int sort_add = mSortOrder % 3;
        switch (view.getId()) {
            case R.id.asc_text: updateAndNotifyNewSort(sort_add); break;
            case R.id.desc_text: updateAndNotifyNewSort(sort_add + 3); break;
        }
    }

    public interface SortOrderChangedListener {
        int getSavedOrder();
        void onOrderChanged(int newType, String name);
    }
    private SortOrderChangedListener mListener;
    public SortOrderBottomSheet setListener(SortOrderChangedListener listener) {
        mListener = listener;
        return this;
    }

    public void removeListener() {
        mListener = null;
    }

    @Override
    public void dismiss() {
        removeListener();
        super.dismiss();
    }

    public static SortOrderBottomSheet newInstance(SortOrderChangedListener listener) {
        return new SortOrderBottomSheet().setListener(listener);
    }
    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sort_order_bottom_sheet,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            FrameLayout bottomSheet = (FrameLayout)
                    dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setPeekHeight(-Tool.getNavigationHeight(requireActivity()));
            behavior.setHideable(false);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if(newState==STATE_COLLAPSED)
                        SortOrderBottomSheet.this.dismiss();
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
        });

        setTheme();
        // onViewCreated
        if(mListener!=null)  {
         updateSort(mListener.getSavedOrder());
        }

    }

    private void setTheme() {
        for (ImageView tickView : mOrderTicks
             ) {
            tickView.setColorFilter(Tool.getBaseColor());
        }

        for (ImageView tickView : mSortTicks
        ) {
            tickView.setColorFilter(Tool.getBaseColor());
        }
    }

    public void updateAndNotifyNewSort(int newSort) {
        if(updateSort(newSort)&&mListener!=null) {
            mListener.onOrderChanged(newSort,getString(mSortStringRes[newSort]));
        }
    }

    public boolean updateSort(int newSort) {
        if(newSort %3 != mSortOrder % 3) {
            // other sort
            mSortTextViews[mSortOrder%3].setBackgroundResource(R.drawable.ripple_16dp_transparent);
            mSortTextViews[newSort%3].setBackgroundResource(R.drawable.ripple_16dp_translucent);

            mSortTicks[mSortOrder%3].setVisibility(View.GONE);
            mSortTicks[newSort%3].setVisibility(View.VISIBLE);
        }
        boolean isOrderChanged = (newSort>2&&mSortOrder<3)||(newSort<3&&mSortOrder>3);

        if(newSort>2&&mSortOrder<3) {
            mOrderTextViews[0].setBackgroundResource(R.drawable.ripple_16dp_transparent);
            mOrderTextViews[1].setBackgroundResource(R.drawable.ripple_16dp_translucent);

            mOrderTicks[0].setVisibility(View.GONE);
            mOrderTicks[1].setVisibility(View.VISIBLE);
        } else if(newSort <3 && mSortOrder > 2) {
            mOrderTextViews[1].setBackgroundResource(R.drawable.ripple_16dp_transparent);
            mOrderTextViews[0].setBackgroundResource(R.drawable.ripple_16dp_translucent);

            mOrderTicks[1].setVisibility(View.GONE);
            mOrderTicks[0].setVisibility(View.VISIBLE);
        }
        boolean result = mSortOrder != newSort;
        mSortOrder = newSort;
        return result;
    }


}
