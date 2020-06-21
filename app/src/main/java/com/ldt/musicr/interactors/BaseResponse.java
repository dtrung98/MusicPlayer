package com.ldt.musicr.interactors;

public class BaseResponse {
        public static final String ACTION_LOAD_DATA = "load-all-data";
        public static final String ACTION_LOADING = "refreshing";
        public static final String ACTION_SET_PARAMETER = "set-parameter";
        public static final String ACTION_LOAD_MORE = "load-more";
        public static final String ACTION_RETURN_RESULT = "return-result";

        public static final String MESSAGE_NO_ACTIVE_ACCOUNT = "No active account. ";

        public static final String MESSAGE_INVALID_RESPONSE = "Invalid response. ";
        public static final String MESSAGE_INVALID_PARAMETER = "Invalid parameter. ";
        public static final String MESSAGE_EMPTY_RESULT = "Empty result";
        public static final String MESSAGE_NEED_AUTHENTICATION = "Authentication required. ";

        public static AppExecutors getAppExecutors() {
            return AppExecutors.getInstance();
        }
}
