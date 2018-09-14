package com.ldt.musicr.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.kingfisher.easyviewindicator.AnyViewIndicator;

public class GridRecyclerIndicator extends AnyViewIndicator {
    public GridRecyclerIndicator(Context context) {
        super(context);
    }

    public GridRecyclerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridRecyclerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GridRecyclerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected int getItemCount() {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            int itemCount =  recyclerView.getAdapter().getItemCount();
            if(itemCount%spanCount==0) itemCount =  itemCount/spanCount; else itemCount = 1+ itemCount/spanCount;
            return itemCount;
        }
        return 0;
    }

    @Override
    protected int getCurrentPosition() {
        int pos =  ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        return pos/spanCount +((pos%spanCount==0) ? 0:1);
    }

    RecyclerView recyclerView;
    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    if (getItemCount() <= 0) {
                        return;
                    }
                    onCurrentLocationChange();
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };
public int spanCount=1;
    /**
     * @param recyclerView
     */
    public void setRecyclerView(final RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        if (recyclerView != null) {

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    GridLayoutManager stagger = (GridLayoutManager) recyclerView.getLayoutManager();
                    spanCount = stagger.getSpanCount();
                    mLastPosition = getItemCount() > 0 ? 0 : -1;

                    updateCircleIndicator();
                    recyclerView.removeOnScrollListener(onScrollListener);
                    recyclerView.addOnScrollListener(onScrollListener);
                    onScrollListener.onScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE);
                }
            });
        }
    }

    /**
     * Force update indicators when new item is insert/remove from the Adapter
     */
    public void forceUpdateItemCount() {
        updateCircleIndicator();
    }
}
