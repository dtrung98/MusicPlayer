package com.ldt.musicr.ui.widget.navigate;

import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.NavigationControllerFragment;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.NavigationFragment;

import static com.ldt.musicr.ui.widget.fragmentnavigationcontroller.NavigationFragment.PRESENT_STYLE_DEFAULT;


public class BackPressableFragment extends Fragment implements BackPressable {
    NavigationControllerFragment mNavigationController;

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.screen_back_pressable,container,false);
    }

    public Fragment getRootFragment() {
        return mRootFragment;
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBackStack(savedInstanceState);
    }

    @Override
    public boolean onBackPressed() {
        if(mNavigationController.getTopFragment().isReadyToDismiss())
            return isNavigationControllerInit() && mNavigationController.dismissFragment(true);
            return true;
    }

    private boolean isNavigationControllerInit() {
        return null!= mNavigationController;
    }
    public void presentFragment(NavigationFragment fragment) {
        if(isNavigationControllerInit()) {
//            Random r = new Random();
//            mNavigationController.setPresentStyle(r.nextInt(39)+1); //exclude NONE present style
            mNavigationController.setPresentStyle(fragment.getPresentTransition());
            mNavigationController.presentFragment(fragment, true);
        }
    }

    @NonNull
    public static BackPressableFragment newInstance(@NonNull NavigationFragment rootFragmentInstance) {
        BackPressableFragment f = new BackPressableFragment();
        f.mRootFragment = rootFragmentInstance;
        return f;
    }
    private NavigationFragment mRootFragment;
    private void initBackStack(Bundle savedInstanceState) {

        FragmentManager fm = getChildFragmentManager();
        mNavigationController = NavigationControllerFragment.navigationController(fm, R.id.container);
        mNavigationController.setPresentStyle(PRESENT_STYLE_DEFAULT);
        mNavigationController.setDuration(250);
        mNavigationController.setInterpolator(new AccelerateDecelerateInterpolator());
        //if(savedInstanceState==null) {

        mNavigationController.presentFragment(mRootFragment);

        // } else {

//          mNavigationController = (FragmentNavigationController) fm.getFragment(savedInstanceState,"navigation-controller");
//          if(mNavigationController==null) Log.d(TAG, "initBackStack: found Navigation Controller");
//          else Log.d(TAG, "initBackStack: unable to get Navigation Controller");
        // }
    }
    public void dismiss() {
        if(isNavigationControllerInit()) {
            mNavigationController.dismissFragment();
        }
    }

    public void presentFragment(NavigationFragment fragment, boolean animated) {
        if(isNavigationControllerInit()) {
            mNavigationController.presentFragment(fragment,animated);
        }
    }
    public void dismiss(boolean animated) {
        if(isNavigationControllerInit()) {
            mNavigationController.dismissFragment(animated);
        }
    }
    public void popToRootFragment() {
        if(isNavigationControllerInit())
            mNavigationController.popToRootFragment();
    }
}
