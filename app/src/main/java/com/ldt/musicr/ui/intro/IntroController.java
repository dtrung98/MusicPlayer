package com.ldt.musicr.ui.intro;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.NavigationControllerFragment;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.NavigationFragment;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.PresentStyle;

/**
 * Controller này tự động loại bỏ giao diện màn hình chính và hiển thị các màn hình (PermissionRequire, NoSongInLibrary) khi cần
 */
public class IntroController {
    private static final String TAG = "IntroController";

    NavigationControllerFragment mNavigationController;

    public NavigationControllerFragment getNavigationController() {
        return mNavigationController;
    }

    public IntroController() {

    }

    public void init(AppCompatActivity activity, Bundle savedInstanceState) {
        initBackStack(activity, savedInstanceState);

    }

    private boolean isNavigationControllerInit() {
        return null != mNavigationController;
    }

    public void presentFragment(NavigationFragment fragment) {
        Log.d(TAG, "presentFragment");
        if (isNavigationControllerInit()) {
            Log.d(TAG, "presentFragment: INIT");
//            Random r = new Random();
//            mNavigationController.setPresentStyle(r.nextInt(39)+1); //exclude NONE present style
            mNavigationController.setPresentStyle(fragment.getPresentTransition());

            //setTheme(fragment.isWhiteTheme());
            mNavigationController.presentFragment(fragment, true);

        }
    }

    private void initBackStack(AppCompatActivity activity, Bundle savedInstanceState) {
        FragmentManager fm = activity.getSupportFragmentManager();
        mNavigationController = NavigationControllerFragment.navigationController(fm, R.id.back_wall_container);
        mNavigationController.setAbleToPopRoot(true);
        mNavigationController.setPresentStyle(PresentStyle.FADE);
        mNavigationController.setDuration(250);
        mNavigationController.setInterpolator(new AccelerateDecelerateInterpolator());
        mNavigationController.presentFragment(new PermissionRequiredFragment());
        // mNavigationController.presentFragment(new MainFragment());
    }
}
