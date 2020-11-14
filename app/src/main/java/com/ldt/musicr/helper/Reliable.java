package com.ldt.musicr.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Reliable<T> {

    public enum Type {
        /**
         * Tra ket qua thanh cong
         */
        SUCCESS,
        /**
         * Tra ket qua that bai cung message, exception (neu co)
         */
        FAILED
    }

    @NonNull
    private final String mMessage;
    @Nullable
    private final T mData;
    @NonNull
    private final Type mType;

    public final int mMessageCode;
    @Nullable
    public final Throwable mThrowable;

    public String getMessage() {
        return mMessage;
    }

    public T getData() {
        return mData;
    }

    public final boolean hasMessage() {
        return !mMessage.isEmpty();
    }

    @NonNull
    public final Type getType() {
        return mType;
    }

    public boolean hasData() {
        return mData != null;
    }

    /**
     * Create a message, with or without data
     * <br> Success if : empty message and nonnull data
     * <br> Fail if : other
     *
     * @param message
     * @param data
     */
    private Reliable(@NonNull Type type,@Nullable T data, int messageCode, String message,@Nullable Throwable e) {
        /* null message, empty message and null data, all are invalid */

        mType = type;
        mData = data;

        mMessage = message == null ? "" : message;

        mThrowable = e;
        mMessageCode = messageCode;
    }

    /**
     * Create a failed message, with or without data
     *
     * @param data
     */
    public static <T> Reliable<T> success(T data) {
        return new Reliable<>(Type.SUCCESS, data, 0, "", null);
    }

    /**
     * Create a successful message with data, if null data, the message switch to fail
     */
    public static <T> Reliable<T> failed(int messageCode, Throwable exception) {
        return failed(messageCode, "", exception, null);
    }

    public static <T> Reliable<T> failed(int messageCode, String message, Throwable exception, T data) {
        return new Reliable<>(Type.FAILED, data, messageCode, message, exception);
    }

    public static <T> Reliable<T> custom(@NonNull Type type,@Nullable T data, int messageCode, String message,@Nullable Throwable e) {
        return new Reliable<>(type, data, messageCode, message, e);
    }

}
