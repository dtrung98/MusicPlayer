package com.ldt.musicr.ui.base;

import androidx.lifecycle.ViewModel;

public class MPViewModel extends ViewModel {
    public static final String ACTION_SET_PARAMS = "action-set-params";
    public static final String ACTION_RELOAD_DATA = "action-reload-data";
    public static final String ACTION_LOAD_MORE_DATA = "action-load-more-data";

    public static final int MESSAGE_CODE_INVALID_PARAMS = 11;
    public static final int MESSAGE_CODE_INVALID_RESPONSE = 12;
}
