package com.ldt.musicr.ui.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldt.musicr.helper.menu.MenuHelper;
import com.ldt.musicr.model.Media;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

public class OptionBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener{
    public final static String OPTION_RES_ARRAY = "option_res_array";
    private int[] mOptionStringID;

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

   /* public static OptionBottomSheet newInstance(int[] optionIDs) {

        Bundle args = new Bundle();
        args.putIntArray();
        OptionBottomSheet fragment = new OptionBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }*/

   private Object mObject;

    public static OptionBottomSheet newInstance(int[] optionIDs, Media media) {
        OptionBottomSheet fragment = new OptionBottomSheet();
        fragment.mOptionStringID = optionIDs;
        fragment.mObject = media;
        return fragment;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(),getTheme());
    }

    private ArrayList<View> mOptionViews = new ArrayList<>();

    public View initLayout(Context context, final int[] options) {
        if(context ==null) throw new NullPointerException();
        LinearLayout root = new LinearLayout(context);
        root.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        float dp_8 = context.getResources().getDimension(R.dimen._8dp);
        float dp = context.getResources().getDimension(R.dimen.oneDP);
        root.setPadding((int)(dp*3),(int)(dp_8),(int)(dp*3),(int)(dp_8));
        root.setOrientation(LinearLayout.VERTICAL);

        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams((int)dp*35,(int)dp*5);
        imageParams.bottomMargin = (int)(5*dp);
        imageParams.gravity = Gravity.CENTER_HORIZONTAL;
        imageView.setImageResource(R.drawable.slide_up_down_icon_drawable);
        root.addView(imageView,imageParams);
        mOptionViews = new ArrayList<>(options.length);

        LinearLayout.LayoutParams textViewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(48*dp));
        textViewParam.setMarginStart((int)(16*dp));
        textViewParam.setMarginEnd((int)(16*dp));

        for (int i = 0, optionsLength = options.length; i < optionsLength; i++) {
            if(options[i]==R.string.divider) {
                View divider = new View(context);
                divider.setBackgroundColor(0x66333333);
                LinearLayout.LayoutParams divParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)dp);
                divParam.bottomMargin = (int)dp_8;
                divParam.topMargin = (int)(7*dp);
                mOptionViews.add(divider);
                root.addView(divider,divParam);
                continue;
            }
            TextView view  = new TextView(context);
            view.setPadding((int)(12*dp),0,(int)(12*dp),0);

           /* if(options[i]==R.string.go_to_artist &&mObject instanceof Song && !((Song)mObject).artistName.isEmpty()) {
                view.setText(getString(R.string.see_more_about)+" "+ ((Song)mObject).artistName);
            } else*/
            view.setText(options[i]);

            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            view.setTypeface(Typeface.DEFAULT_BOLD);

            if(options[i]==R.string.delete_from_playlist)
                view.setTextColor(context.getResources().getColor(R.color.flatOrange));
            else if(options[i]==R.string.delete_from_device)
                view.setTextColor(context.getResources().getColor(R.color.flatRed));
            else
            view.setTextColor(context.getResources().getColor(R.color.md_bottom_sheet_text_color));

            view.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            view.setOnClickListener(this);
            addToBeRipple(R.drawable.ripple_effect,view);
            mOptionViews.add(view);
            root.addView(view,textViewParam);
        }

        return root;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

     /*   View view = inflater.inflate(R.layout.menu_bottom_sheet_layout, container,
                false);

        // get the views and attach the listener
        int[] textViewID = {
                R.id.popup_song_play,
                R.id.popup_song_play,
                R.id.popup_song_play_next,
                R.id.popup_song_play_next,
                R.id.popup_song_add_to_queue,
                R.id.popup_song_add_to_playlist,
                R.id.popup_song_go_to_album,
                R.id.popup_song_go_to_artist,
                R.id.popup_song_share,
                R.id.popup_song_remove_playlist,
                R.id.popup_song_delete};
        TextView textView;
        for (int item: textViewID) {
            textView = view.findViewById(item);
            addToBeRipple(R.drawable.ripple_effect,textView);
            textView.setOnClickListener(this);
        }
        return view;*/
        return initLayout(getContext(),mOptionStringID);
    }

    @Override
    public void onClick(View view) {
        if(view instanceof TextView) {
            int item = mOptionViews.indexOf(view);
            if (item != -1 && getActivity() != null) {
                 MenuHelper.handleMenuClick((AppCompatActivity) getActivity(), mObject, mOptionStringID[item]);
                this.dismiss();
            }
        }
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setBackground( (RippleDrawable) res.getDrawable(drawable));
            } else {
                //TODO: setBackground below Android L
            }
            view.setClickable(true);
        }
    }

    Resources res;
    public void applyRippleColor(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (final View v : rippleViews) {
                ((RippleDrawable) v.getBackground()).setColor(ColorStateList.valueOf(color));
            }
        }
        else {
            //TODO: setBackground below Android L
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
              behavior.setPeekHeight(-Tool.getNavigationHeight(requireActivity()));
              behavior.setHideable(false);
                behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if(newState==STATE_COLLAPSED)
                            OptionBottomSheet.this.dismiss();
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
            }
        });
    }
}
