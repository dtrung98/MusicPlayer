package com.ldt.musicr.ui.widget.viewgroup;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.ldt.musicr.R;

public class CardLayerContainerView extends FrameLayout {
    public CardView getCardView() {
        return mCardView;
    }

    public View getDimView() {
        return mDimView;
    }

    public ViewGroup getSubContainerView() {
        return mSubContainerView;
    }

    private CardView mCardView;
    private View mDimView;
    private View mBottomEdgeView;
    private ViewGroup mSubContainerView;

    public CardLayerContainerView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CardLayerContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public CardLayerContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CardLayerContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, null);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        View.inflate(context, R.layout.card_layer_container, this);
        mCardView = this.findViewById(R.id.cardView);
        mSubContainerView = this.findViewById(R.id.containerView);
        mSubContainerView.setId(View.generateViewId());
        mDimView = this.findViewById(R.id.dimView);
        mBottomEdgeView = this.findViewById(R.id.bottomEdgeView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(0);
        }
    }

    public void setCardBackgroundColor(int color) {
        mCardView.setCardBackgroundColor(color);
        mBottomEdgeView.setBackgroundColor(color);
    }

    public void setRadius(float radius) {
        mCardView.setRadius(radius);
    }

    public void setDimAmount(float dimAmount) {
        final float amount = dimAmount < 0 ? 0 : dimAmount > 1 ? 1 : dimAmount;
        mDimView.setAlpha(amount);

        /* disable interactions when dim is enabled*/
        if (dimAmount != 0) {
            mDimView.setClickable(true);
            mDimView.setFocusable(true);
        } else {
            mDimView.setClickable(false);
            mDimView.setClickable(false);
        }
    }
}
