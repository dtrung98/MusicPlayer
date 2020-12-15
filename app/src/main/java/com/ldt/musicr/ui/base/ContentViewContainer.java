package com.ldt.musicr.ui.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class ContentViewContainer implements DialogInterface {
    private static final int DISMISS = 0x43;
    private static final int CANCEL = 0x44;
    private static final int SHOW = 0x45;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mDismissAction = this::dismissContainer;
    private final Handler mListenersHandler;
    private Message mDismissMessage;
    private Message mCancelMessage;
    private Message mShowMessage;

    private boolean mCreated = false;
    private boolean mCanceled = false;

    public ViewGroup getAppRootView() {
        return mAppRootView;
    }

    /**
     * The root view (usually the android.R.content View)
     */
    private final ViewGroup mAppRootView;

    @Nullable
    public ViewGroup getHostView() {
        return mHostView;
    }

    /**
     * The view group which holds the content layout
     */
    @Nullable
    private ViewGroup mHostView;

    public ContentViewContainer(@NonNull ViewGroup appRootView) {
        mAppRootView = appRootView;
        mListenersHandler = new ListenersHandler(Looper.getMainLooper(), this);
    }

    protected void initialize() {
        mHostView = onCreateHostView(getAppRootView().getContext());
    }

    /**
     * Create the host view
     * You can create a compound view then just return the child view which is used to hold the content view
     *
     * @return the view group which will be used to hold the content view.
     */
    public ViewGroup onCreateHostView(Context context) {
        FrameLayout hostView = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        hostView.setLayoutParams(params);
        return hostView;
    }

    public void removeHostView() {
        mAppRootView.removeView(mHostView);
        android.util.Log.d("ContentViewContainer","removeHostView");
    }

    /**
     * add the content view to container
     *
     * @param view
     */
    public void setContentView(View view) {
        if(mHostView != null) {
            mHostView.addView(view);
        }
    }

    private boolean mShowing = false;

    private static final String DIALOG_SHOWING_TAG = "android:dialogShowing";
    private static final String DIALOG_HIERARCHY_TAG = "android:dialogHierarchy";

    protected boolean mCancelable = true;

    public void setCancelable(boolean flag) {
        mCancelable = flag;
    }

    private boolean mCancelOnTouchOutside = true;

    public void setCanceledOnTouchOutside(boolean cancel) {
        if (cancel && !mCancelable) {
            mCancelable = true;
        }
        setCanceledOnTouchOutsideInternal(cancel);
    }

    /**
     * Represent an event that user touches outside the host view
     */
    protected void touchOutside() {
        if (mCancelOnTouchOutside) {
            cancel();
        }
    }

    protected void setCanceledOnTouchOutsideInternal(boolean cancel) {
        mCancelOnTouchOutside = cancel;
    }

    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        if (listener != null) {
            mCancelMessage = mListenersHandler.obtainMessage(CANCEL, listener);
        } else {
            mCancelMessage = null;
        }
    }

    public void setCancelMessage(@Nullable Message msg) {
        mCancelMessage = msg;
    }

    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        if (listener != null) {
            mDismissMessage = mListenersHandler.obtainMessage(DISMISS, listener);
        } else {
            mDismissMessage = null;
        }
    }

    public void setOnShowListener(@Nullable OnShowListener listener) {
        if (listener != null) {
            mShowMessage = mListenersHandler.obtainMessage(SHOW, listener);
        } else {
            mShowMessage = null;
        }
    }

    /**
     * Saves the state of the dialog into a bundle.
     * <p>
     * The default implementation saves the state of its view hierarchy, so you'll
     * likely want to call through to super if you override this to save additional
     * state.
     *
     * @return A bundle with the state of the dialog.
     */
    public @NonNull
    Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(DIALOG_SHOWING_TAG, mShowing);
     /*   if (mCreated) {
            bundle.putBundle(DIALOG_HIERARCHY_TAG, mWindow.saveHierarchyState());
        }*/
        return bundle;
    }

    /**
     * Restore the state of the dialog from a previously saved bundle.
     * <p>
     * The default implementation restores the state of the dialog's view
     * hierarchy that was saved in the default implementation of {@link #onSaveInstanceState()},
     * so be sure to call through to super when overriding unless you want to
     * do all restoring of state yourself.
     *
     * @param savedInstanceState The state of the dialog previously saved by
     *                           {@link #onSaveInstanceState()}.
     */
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        final Bundle dialogHierarchyState = savedInstanceState.getBundle(DIALOG_HIERARCHY_TAG);
        if (dialogHierarchyState == null) {
            // dialog has never been shown, or onCreated, nothing to restore.
            return;
        }
        //dispatchOnCreate(savedInstanceState);
        //mWindow.restoreHierarchyState(dialogHierarchyState);
        if (savedInstanceState.getBoolean(DIALOG_SHOWING_TAG)) {
            show();
        }
    }

    public void show() {
        if (mShowing) {
            mHostView.setVisibility(View.VISIBLE);
            return;
        }

        mCanceled = false;

        if (!mCreated) {
            dispatchOnCreate(null);
        }

        onStart();
        mAppRootView.addView(mHostView);
        mShowing = true;
        sendShowMessage();
    }

    private void sendShowMessage() {
        if (mShowMessage != null) {
            Message.obtain(mShowMessage).sendToTarget();
        }
    }

    void dispatchOnCreate(Bundle savedInstanceState) {
        if (!mCreated) {
            onCreate(savedInstanceState);
            mCreated = true;
        }
    }

    /**
     * Hide the container but do not dismiss it
     */
    public void hide() {
        if(mHostView != null) {
            mHostView.setVisibility(View.GONE);
        }
    }

    /**
     * Cancel the dialog.  This is essentially the same as calling {@link #dismiss()}, but it will
     * also call your {@link DialogInterface.OnCancelListener} (if registered).
     */
    @Override
    public void cancel() {
        if (!mCanceled && mCancelMessage != null) {
            mCanceled = true;
            // Obtain a new message so this dialog can be re-used
            Message.obtain(mCancelMessage).sendToTarget();
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        if (Looper.myLooper() == mHandler.getLooper()) {
            dismissContainer();
        } else {
            mHandler.post(mDismissAction);
        }
    }

    void dismissContainer() {
        if (!mShowing) {
            android.util.Log.d("ContentViewContainer","dismissContainer but mShowing = true");
            return;
        }
        try {
            removeHostView();
        } finally {
            onStop();
            mShowing = false;
            sendDismissMessage();
        }
    }

    private void sendDismissMessage() {
        if (mDismissMessage != null) {
            // Obtain a new message so this dialog can be re-used
            Message.obtain(mDismissMessage).sendToTarget();
        }
    }

    public void onStop() {

    }

    public void onStart() {

    }

    protected void onCreate(Bundle savedInstanceState) {

    }

    public void onDestroy() {

    }

    private static final class ListenersHandler extends Handler {
        private final WeakReference<DialogInterface> mDialogInterface;

        public ListenersHandler(Looper looper, DialogInterface dialogInterface) {
            super(looper);
            mDialogInterface = new WeakReference<>(dialogInterface);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISMISS:
                    ((OnDismissListener) msg.obj).onDismiss(mDialogInterface.get());
                    break;
                case CANCEL:
                    ((OnCancelListener) msg.obj).onCancel(mDialogInterface.get());
                    break;
                case SHOW:
                    ((OnShowListener) msg.obj).onShow(mDialogInterface.get());
                    break;
            }
        }
    }
}