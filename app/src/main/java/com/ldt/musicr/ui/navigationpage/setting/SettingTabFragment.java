package com.ldt.musicr.ui.navigationpage.setting;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ldt.musicr.App;
import com.ldt.musicr.R;
import com.ldt.musicr.helper.LocaleHelper;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.ui.MainActivity;
import com.ldt.musicr.ui.navigationpage.BaseMusicServiceSupportFragment;
import com.ldt.musicr.ui.widget.rangeseekbar.OnRangeChangedListener;
import com.ldt.musicr.ui.widget.rangeseekbar.RangeSeekBar;
import com.ldt.musicr.util.Tool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingTabFragment extends BaseMusicServiceSupportFragment implements OnRangeChangedListener {
    private static final String EN = "en";
    private static final String VI = "vi";

    @BindView(R.id.status_bar)
    View mStatusBar;

    @BindView(R.id.switch_to_vi)
    TextView mSwitchToVi;

    @BindView(R.id.switch_to_en)
    TextView mSwitchToEn;

    @BindView(R.id.seek_bar)
    RangeSeekBar mSeekBar;

    @BindView(R.id.hide_switch)
    SwitchCompat mUseArtistImgAsBg;

    @BindView(R.id.create_now) View mCreateNowView;

    @OnCheckedChanged(R.id.hide_switch)
    void onChangedUseArtistImgAsBg(boolean value) {
        App.getInstance().getPreferencesUtility().setIsUsingArtistImageAsBackground(value);
        if(getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).getBackStackController().onUsingArtistImagePreferenceChanged();
        }
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.setting_tab_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mSeekBar.setOnRangeChangedListener(this);
        refreshData();
      onPaletteChanged();

    }

    private boolean mIsEnglish = true;
    private float mCurrentInAppVolume = -1;

    private void refreshInAppVolume() {
        mCurrentInAppVolume = MusicPlayerRemote.getInAppVolume();
        if(mCurrentInAppVolume>=0) {
            mSeekBar.setValue(100*mCurrentInAppVolume);
        } else {

        }
    }

    void refreshData() {
        refreshInAppVolume();
        mUseArtistImgAsBg.setChecked(App.getInstance().getPreferencesUtility().isUsingArtistImageAsBackground());

        Context context = getContext();
        if(context!=null) {
            String lang = LocaleHelper.getLanguage(context);
            mIsEnglish = lang.equals(EN);

            if(mIsEnglish) {
                mSwitchToEn.setBackgroundResource(R.drawable.ripple_16dp_solid_left);
                mSwitchToVi.setBackgroundResource(R.drawable.ripple_16dp_border_right);
                mSwitchToEn.setTextColor(getResources().getColor(R.color.FlatOrange));
                mSwitchToVi.setTextColor(getResources().getColor(R.color.FlatWhite));
            } else {
                mSwitchToEn.setBackgroundResource(R.drawable.ripple_16dp_border_left);
                mSwitchToVi.setBackgroundResource(R.drawable.ripple_16dp_solid_right);
                mSwitchToEn.setTextColor(getResources().getColor(R.color.FlatWhite));
                mSwitchToVi.setTextColor(getResources().getColor(R.color.FlatOrange));

            }
        }
    }

    @OnClick(R.id.switch_to_en)
    void switchToEN() {
        if(mIsEnglish) return;
        Activity activity = getActivity();
        if(activity!=null) {
            LocaleHelper.setLocale(activity,"en");
           activity.recreate();
        }
    }

    @Override
    public void onSetStatusBarMargin(int value) {
        mStatusBar.getLayoutParams().height = value;
    }

    @OnClick(R.id.switch_to_vi)
    void switchToVI() {
        if(mIsEnglish) {
            Activity activity = getActivity();
            if (activity != null) {
                LocaleHelper.setLocale(activity, "vi");
                activity.recreate();
            }
        }

    }

    @Override
    public void onPaletteChanged() {
        super.onPaletteChanged();

        int color = Tool.getBaseColor();
        int alpha_color = Color.argb(0x22,Color.red(color),Color.green(color),Color.blue(color));
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                0xFF888888,
                color,
        };

        int[] trackColors = new int[] {
                0x22000000,
                alpha_color,
        };

        //  checkBox.setSupportButtonTintList(new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(mUseArtistImgAsBg.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(mUseArtistImgAsBg.getTrackDrawable()), new ColorStateList(states, trackColors));


        mSeekBar.setProgressColor(color);
        mSeekBar.requestLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         /*   ColorStateList colorStateList = new ColorStateList(
                    new int[][] {new int[] {android.R.attr.state_pressed},new int[] {android.R.attr.state_focused}},
                    new int[] {color, alpha_color}
            );*/

                ((RippleDrawable) mSwitchToEn.getBackground()).setColor(ColorStateList.valueOf(color));
            ((RippleDrawable) mSwitchToVi.getBackground()).setColor(ColorStateList.valueOf(color));
            ((RippleDrawable) mCreateNowView.getBackground()).setColor(ColorStateList.valueOf(color));
            ((RippleDrawable) mMoreSettingView.getBackground()).setColor(ColorStateList.valueOf(color));

        }

        if(mIsEnglish) {
            mSwitchToEn.setTextColor(color);
        }
        else mSwitchToVi.setTextColor(color);

    }
    public void setCurrentInAppVolume(float volume) {
        if(volume<0||volume>1) return;
        mCurrentInAppVolume = volume;
        MusicPlayerRemote.setInAppVolume(mCurrentInAppVolume);
    }

    @Override
    public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
        if(isFromUser) {
             setCurrentInAppVolume(leftValue/100);
        }
    }

    @Override
    public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }

    @Override
    public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }
    @BindView(R.id.more_setting) View mMoreSettingView;
    @OnClick(R.id.more_setting)
    void goToMoreSetting() {

    }
}
