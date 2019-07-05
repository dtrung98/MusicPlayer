package com.ldt.musicr.ui.bottomnavigationtab;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;
import com.ldt.musicr.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

public class SongOptionBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener{
public interface BottomSheetListener {
    boolean onButtonClick(int id);
}
BottomSheetListener listener;
public void setListener(BottomSheetListener listener) {
    this.listener = listener;
}

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    BaseActivity baseActivity;
    public static SongOptionBottomSheet newInstance() {
        SongOptionBottomSheet fragment = new SongOptionBottomSheet();

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       BottomSheetDialog bsd =  new BottomSheetDialog(requireContext(),getTheme());

       return bsd;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ex_bottom_sheet_layout, container,
                false);

        // get the views and attach the listener
        int[] textViewID = {
                R.id.popup_song_play,
                R.id.popup_song_play,
                R.id.popup_song_play_next,
                R.id.popup_song_play_next,
                R.id.popup_song_addto_queue,
                R.id.popup_song_addto_playlist,
                R.id.popup_song_goto_album,
                R.id.popup_song_goto_artist,
                R.id.popup_song_share,
                R.id.popup_song_remove_playlist,
                R.id.popup_song_delete};
        TextView textView;
        for (int item: textViewID) {
            textView = view.findViewById(item);
            addToBeRipple(R.drawable.ripple_effect,textView);
            textView.setOnClickListener(this);
        }
        initted = true;
        return view;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onButtonClick(view.getId());
            }
        },100);
        }
        this.dismiss();
    }

    private ArrayList<View> rippleViews = new ArrayList<>();
    private boolean first_time = true;
    public void addToBeRipple(int drawable,View... v) {
        if(first_time) {
            first_time = false;
            res = getResources();
        }
        int l = v.length;
        rippleViews.addAll(Arrays.asList(v));
        for(View view :v) {
            view.setBackground( (RippleDrawable) res.getDrawable(drawable));
            view.setClickable(true);
        }
    }
    Resources res;
    public void applyRippleColor(int color) {
        for( final View v : rippleViews)
        {
            ((RippleDrawable)v.getBackground()).setColor(ColorStateList.valueOf(color));
        }
    }


    private boolean initted = false;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
              behavior.setPeekHeight(-Tool.getNavigationHeight(requireActivity()));
              behavior.setHideable(false);
                behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if(newState==STATE_COLLAPSED)
                            SongOptionBottomSheet.this.dismiss();
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });
    }
}
