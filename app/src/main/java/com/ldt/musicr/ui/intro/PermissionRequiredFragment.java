package com.ldt.musicr.ui.intro;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.R;
import com.ldt.musicr.common.MediaManager;
import com.ldt.musicr.ui.AppActivity;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.NavigationFragment;

public class PermissionRequiredFragment extends NavigationFragment implements AppActivity.PermissionListener {
    SwipeRefreshLayout mSwipeRefresh;
    View mAllowButton;

    void allowAccess() {
        getMainActivity().requestPermission();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        getMainActivity().setPermissionListener(this);
        return inflater.inflate(R.layout.screen_grant_permission,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefresh = view.findViewById(R.id.swipe_refresh);
        mAllowButton = view.findViewById(R.id.allow_button);
        mAllowButton.setOnClickListener((v)-> allowAccess());
        mSwipeRefresh.setColorSchemeResources(R.color.flatBlue);
        mSwipeRefresh.setOnRefreshListener(this::refreshData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getMainActivity().removePermissionListener();

    }

    private void refreshData() {
        if(getMainActivity().checkSelfPermission()) onPermissionGranted();
        else mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onPermissionGranted() {
        mSwipeRefresh.setRefreshing(false);
        MediaManager.clearMedia();
        MediaManager.loadMediaIfNeeded();
        getMainActivity().showMainUI();
    }

    @Override
    public void onPermissionDenied() {
        mSwipeRefresh.setRefreshing(false);
    }
}
