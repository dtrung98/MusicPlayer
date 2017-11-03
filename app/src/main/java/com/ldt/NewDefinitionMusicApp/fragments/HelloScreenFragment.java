package com.ldt.NewDefinitionMusicApp.fragments;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldt.NewDefinitionMusicApp.activities.MainActivity;
import com.ldt.NewDefinitionMusicApp.R;
import com.ldt.NewDefinitionMusicApp.activities.SupportFragmentActivity;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HelloScreenFragment#Initialize(Activity)} factory method to
 * create an instance of this fragment.
 */
public class HelloScreenFragment extends FragmentPlus {


    public HelloScreenFragment() {
        // Required empty public constructor
    }

    /**
     *
     */
    //
    public static HelloScreenFragment Initialize(Activity activity) {
        HelloScreenFragment fragment = new HelloScreenFragment();
        fragment.setFrameLayoutNTransitionType(activity, SupportFragmentActivity.TransitionType.LEFT2RIGHT);
        return fragment;
    }
    private ImageView icon;
    private TextView textView;
    private void MergerUi()
    {
        icon= (ImageView) rootView.findViewById(R.id.helloScreen_icon);
       textView =(TextView) rootView.findViewById(R.id.touchMeToContinue);
       MainScreenFragment mainScreenFragment = new MainScreenFragment();
    }
    private void SetAllClick()
    {
        View[] views = new View[]
                {  // put views here
                        textView
                };

        for( View va : views) va.setOnClickListener(Onclick);
    }
    private View.OnClickListener Onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id)
            {
                case R.id.touchMeToContinue :touchMeToContinue();return;
            }
        }
    };
    private void touchMeToContinue()
    {
        MainActivity mainScreenFragment = (MainActivity)getActivity();
   //     FadeInFadeOutTransition.RemoveFragmentAndTransform(this, mainScreenFragment.uilayer);
        mainScreenFragment.pushFragment(ShowMusicSongs.Initialize(getActivity()),true);
    }
    private void Nothing()
    {
        Bitmap bitmap = Bitmap.createBitmap(500,500, Bitmap.Config.ARGB_8888);
        Bitmap res = BitmapFactory.decodeResource(getResources(),R.drawable.tfboys);
        Canvas canvas = new Canvas(bitmap);
        Paint mPaint = new Paint();

        float[] vertices = new float[8];
        vertices[0]=0; vertices[1]=0;
        vertices[2]=100; vertices[3]=0;
        vertices[4]=0;vertices[5]=200;
        vertices[6]=300; vertices[7]=500;

        mPaint.setAntiAlias(true);
        canvas.drawBitmapMesh(res, 1, 1,vertices, 0, null, 0,
                mPaint);
        icon.setImageBitmap(bitmap);
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("onCreate()","LogF");
    }

    @Override
    public ApplyMargin IWantApplyMargin() {
        return ApplyMargin.BOTH;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("onCreateView()","LogF");
       rootView = inflater.inflate(R.layout.fragment_hello_screen, container, false);
        MergerUi();
        SetAllClick();
        return  rootView;
    }



    @Override
    public void onTransitionComplete() {
     //  Nothing();
    }

    @Override
    public StatusTheme setDefaultStatusTheme() {
        return StatusTheme.BlackIcon;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        Log.d("onDetach()","LogF");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Log.d("onDestroyView()","LogF");
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("onDestroy()","LogF");
    }

}
