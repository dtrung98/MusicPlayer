package com.ldt.musicr.ui.intro;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.FragmentNavigationController;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;

import static com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment.PRESENT_STYLE_DEFAULT;

public class IntroController {
    private static final String TAG ="IntroController";

    FragmentNavigationController mNavigationController;

    public FragmentNavigationController getNavigationController() {
        return mNavigationController;
    }

    public IntroController() {

    }

    public void init(AppCompatActivity activity, Bundle savedInstanceState) {
        initBackStack(activity, savedInstanceState);

    }
    private boolean isNavigationControllerInit() {
        return null!= mNavigationController;
    }
    public void presentFragment(SupportFragment fragment) {
        Log.d(TAG, "presentFragment");
        if(isNavigationControllerInit()) {
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
        mNavigationController = FragmentNavigationController.navigationController(fm, R.id.back_wall_container);
        mNavigationController.setAbleToPopRoot(true);
        mNavigationController.setPresentStyle(PRESENT_STYLE_DEFAULT);
        mNavigationController.setDuration(250);
        mNavigationController.setInterpolator(new AccelerateDecelerateInterpolator());
        mNavigationController.presentFragment(new IntroStepOneFragment());
        // mNavigationController.presentFragment(new MainFragment());
    }
}
