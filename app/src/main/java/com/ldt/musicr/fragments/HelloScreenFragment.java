package com.ldt.musicr.fragments;


import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldt.musicr.ui.main.MainActivity;
import com.ldt.musicr.R;
import com.ldt.musicr.ui.main.SupportFragmentPlusActivity;

import static com.ldt.musicr.services.MusicService.NEXT_ACTION;
import static com.ldt.musicr.services.MusicService.PREVIOUS_ACTION;
import static com.ldt.musicr.services.MusicService.TOGGLEPAUSE_ACTION;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HelloScreenFragment#Initialize(Activity)} factory method to
 * create an instance of this fragment.
 */
public class HelloScreenFragment extends FragmentPlus implements View.OnTouchListener {


    public HelloScreenFragment() {
        // Required empty public constructor
    }

    /**
     *
     */

    public static HelloScreenFragment Initialize(Activity activity) {
        HelloScreenFragment fragment = new HelloScreenFragment();
        fragment.setFrameLayoutNTransitionType(activity, SupportFragmentPlusActivity.TransitionType.RIGHT_LEFT);
        return fragment;
    }
    private ImageView icon;
    private TextView textView;
    private TextView touch_me_to_show_notification;
    private void MergerUi()
    {
        icon= rootView.findViewById(R.id.helloScreen_icon);
       textView = rootView.findViewById(R.id.touchMeToContinue);
       touch_me_to_show_notification = rootView.findViewById(R.id.touchToShowNotification);
      // rootView.setOnTouchListener(this);
    }
    private void SetAllClick()
    {
        View[] views = new View[]
                {  // put views here
                        textView,
                        touch_me_to_show_notification
                };

        for( View va : views) va.setOnClickListener(Onclick);
    }
    private View.OnClickListener Onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id)
            {
                case R.id.touchMeToContinue :return;
                case R.id.touchToShowNotification:touchToShowNotification();return;
            }
        }
    };
    private NotificationManagerCompat mNotificationManager;
    private Bitmap art;
    private Notification mNotification;
    private int id=0;
    private void touchToShowNotification() {
        if(mNotificationManager==null) {
            mNotificationManager = NotificationManagerCompat.from(getActivity());
            art = ((BitmapDrawable)getResources().getDrawable(R.drawable.default_image2)).getBitmap();

        }
        Notification.Builder builder = new Notification.Builder(getActivity())
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(art)
             //   .setContentIntent(clickIntent)
                .setContentTitle("Send from Hello Screen Fragment")
                .setContentText("This notification is send from the fragment named Hello Screen Fragment.")
            //    .setWhen(1);
                .addAction(R.drawable.ic_skip_previous_white_36dp,
                        "",
                        retrievePlaybackAction(PREVIOUS_ACTION))
                .addAction(R.drawable.ic_play_white_36dp, "",
                        retrievePlaybackAction(TOGGLEPAUSE_ACTION))
                .addAction(R.drawable.ic_skip_next_white_36dp,
                        "",
                        retrievePlaybackAction(NEXT_ACTION));
        ;
        mNotificationManager.notify (++id,builder.build());
    }
    private final PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(getMainActivity(), MainActivity.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);

        return PendingIntent.getService(getActivity(), 0, intent, 0);
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
        return  rootView;//new FrameLayout(getContext());
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
       // Tool.showToast(getContext(),"Container got event",500);

        return false;
    }
}
