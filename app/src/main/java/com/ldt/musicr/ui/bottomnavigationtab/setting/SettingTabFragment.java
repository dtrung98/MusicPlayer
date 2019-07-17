package com.ldt.musicr.ui.bottomnavigationtab.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.R;
import com.ldt.musicr.helper.LocaleHelper;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.SupportFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class SettingTabFragment extends SupportFragment {
    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.setting_tab_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
    }

    @OnClick(R.id.button)
    void doSomething() {
        Activity activity = getActivity();
        if(activity!=null) {
            Toasty.normal(activity,"Clicked").show();
            String lang = LocaleHelper.getLanguage(activity);
            LocaleHelper.setLocale(activity, lang.equals("vi") ?"en" : "vi");
           activity.recreate();
        }
    }
}
