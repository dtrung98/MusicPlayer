package com.ldt.musicr.interactors;

public class Event<T> extends Response<T> {
    public boolean isSucceed() {
        return mResult;
    }

    private String mAction;

    public boolean isSuccessful() {
        return mResult;
    }

    private final boolean mResult;
    public Event<T> withAction(String action) {
        mAction = action;
        return this;
    }

    public String getAction() {
        return mAction;
    }

    protected Event(boolean result, String message, T data) {
        super(message, data);
        mResult = result;
    }

    protected Event(boolean result, String message, T data, String action) {
        super(message, data);
        mResult = result;
        mAction = action;
    }

    public static <T> Event<T> create(boolean result, T data, String message, String action) {
        return new Event<T>(result, message, data, action);
    }

    public static <T> Event<T> create(T data, String action) {
        return new Event<T>(true, "", data).withAction(action);
    }

    public static <T> Event<T> create(T data) {
        return new Event<T>(true, "", data);
    }

    public static <T> Event<T> create(String message, T data) {
        return new Event<T>(false, message, data);
    }

    public static <T> Event<T> create(String message) {
        return new Event<T>(false, message, null);
    }

}
