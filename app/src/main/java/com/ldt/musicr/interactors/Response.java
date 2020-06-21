package com.ldt.musicr.interactors;

public class Response<T> extends BaseResponse {
    protected final String mMessage;
    protected final T mData;
    public String getMessage() {
        return mMessage;
    }

    public T getData() {
        return mData;
    }

    public final boolean hasMessage() {
        return !mMessage.isEmpty();
    }

    public boolean hasData() {
        return mData != null;
    }

    /**
     * Create a message, with or without data
     * <br> Success if : empty message and nonnull data
     * <br> Fail if : other
     * @param message
     * @param data
     */
    public Response(String message, T data) {
        /* null message, empty message and null data, all are invalid */
        String tempMessage = (message == null) ? "" : message;

        if(tempMessage.isEmpty() && data == null)
            tempMessage = BaseResponse.MESSAGE_INVALID_RESPONSE;

        mMessage = tempMessage;
        mData = data;
    }

    /**
     *  Create a successful message with data, if null data, the message switch to fail
     */
    public Response(T data) {
        mMessage = (data == null) ? BaseResponse.MESSAGE_INVALID_RESPONSE : "";
        mData = data;
    }

    /**
     * Create a failed message, with or without data
     * @param message
     * @param data
     */
    public static <T> Response<T> create(String message, T data) {
        return new Response<>(message, data);
    }

    /**
     *  Create a successful message with data, if null data, the message switch to fail
     */
    public static <T> Response<T> create(T data) {
        return new Response<>(data);
    }

}
