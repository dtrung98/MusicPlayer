package com.ldt.musicr.ui.widget.navigate;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.FragmentNavigationController;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;

import static com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment.PRESENT_STYLE_DEFAULT;


public class NavigateFragment extends Fragment implements BackPressable {
    FragmentNavigationController mNavigationController;

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.back_pressable_fragment,container,false);
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
            if(!(isNavigationControllerInit() && mNavigationController.dismissFragment(true)))
        return false;
            return true;
    }

    private boolean isNavigationControllerInit() {
        return null!= mNavigationController;
    }
    public void presentFragment(SupportFragment fragment) {
        if(isNavigationControllerInit()) {
//            Random r = new Random();
//            mNavigationController.setPresentStyle(r.nextInt(39)+1); //exclude NONE present style
            mNavigationController.setPresentStyle(fragment.getPresentTransition());
            mNavigationController.presentFragment(fragment, true);
        }
    }

    @NonNull
    public static NavigateFragment newInstance(@NonNull SupportFragment rootFragmentInstance) {
        NavigateFragment f = new NavigateFragment();
        f.mRootFragment = rootFragmentInstance;
        return f;
    }
    private SupportFragment mRootFragment;
    private void initBackStack(Bundle savedInstanceState) {

        FragmentManager fm = getChildFragmentManager();
        mNavigationController = FragmentNavigationController.navigationController(fm, R.id.container);
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

    public void presentFragment(SupportFragment fragment, boolean animated) {
        if(isNavigationControllerInit()) {
            mNavigationController.presentFragment(fragment,animated);
        }
    }
    public void dismiss(boolean animated) {
        if(isNavigationControllerInit()) {
            mNavigationController.dismissFragment(animated);
        }
    }
}
