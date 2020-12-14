package com.ldt.musicr.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Show a fragment floating over the screen.
 * <br/>This class is expected to replace the existing Dialog Fragment.
 * <br/>It works as same as DialogFragment, but instead of creating a dialog to wrap the layout,
 * Floating View Fragment attaches/adds its layout to android.R.id.content root view so
 * no need dialog anymore.
 * <br/>Dismiss the owner fragment will dismiss its child FloatingViewFragment automatically.
 * <br/> Back pressed event is handled automatically by default, but you have an option to disable it
 */
public class FloatingViewFragment extends Fragment implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    private static final String SAVED_DIALOG_STATE_TAG = "android:savedDialogState";
    private static final String SAVED_CANCELABLE = "android:cancelable";
    private static final String SAVED_SHOWS_DIALOG = "android:showsDialog";
    private static final String SAVED_BACK_STACK_ID = "android:backStackId";

    private boolean mShowsDialog = true;

    private boolean mViewDestroyed;
    private boolean mDismissed;
    private boolean mShownByMe;
    private boolean mCreatingDialog;
    private boolean mCancelable = true;

    private int mBackStackId = -1;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mDismissRunnable = new Runnable() {
        @SuppressLint("SyntheticAccessor")
        @Override
        public void run() {
            mOnDismissListener.onDismiss(mContentViewContainer);
        }
    };

    private DialogInterface.OnCancelListener mOnCancelListener =
            new DialogInterface.OnCancelListener() {
                @SuppressLint("SyntheticAccessor")
                @Override
                public void onCancel(@Nullable DialogInterface dialog) {
                    if (mContentViewContainer != null) {
                        FloatingViewFragment.this.onCancel(mContentViewContainer);
                    }
                }
            };

    private DialogInterface.OnDismissListener mOnDismissListener =
            new DialogInterface.OnDismissListener() {
                @SuppressLint("SyntheticAccessor")
                @Override
                public void onDismiss(@Nullable DialogInterface dialog) {
                    if (mContentViewContainer != null) {
                        FloatingViewFragment.this.onDismiss(mContentViewContainer);
                    }
                }
            };

    public FloatingViewFragment() {
        super();
    }

    @ContentView
    public FloatingViewFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    public void setShowsDialog(boolean showsDialog) {
        mShowsDialog = showsDialog;
    }

    public boolean getShowsDialog() {
        return mShowsDialog;
    }

    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        mDismissed = false;
        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commit();
    }

    public void dismiss() {
        dismissInternal(false, false);
    }

    public void dismissAllowingStateLoss() {
        dismissInternal(true, false);
    }

    private void dismissInternal(boolean allowStateLoss, boolean fromOnDismiss) {
        if (mDismissed) {
            return;
        }

        mDismissed = true;
        mShownByMe = false;
        if (mContentViewContainer != null) {
            // Instead of waiting for a posted onDismiss(), null out
            // the listener and call onDismiss() manually to ensure
            // that the callback happens before onDestroy()
            mContentViewContainer.setOnDismissListener(null);
            mContentViewContainer.dismiss();
            if (!fromOnDismiss) {
                // onDismiss() is always called on the main thread, so
                // we mimic that behavior here. The difference here is that
                // we don't post the message to ensure that the onDismiss()
                // callback still happens before onDestroy()
                if (Looper.myLooper() == mHandler.getLooper()) {
                    onDismiss(mContentViewContainer);
                } else {
                    mHandler.post(mDismissRunnable);
                }
            }
        }

        mViewDestroyed = true;
        if (mBackStackId >= 0) {
            getParentFragmentManager().popBackStack(mBackStackId,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mBackStackId = -1;
        } else {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.remove(this);
            if (allowStateLoss) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }
        }
    }

    @Nullable
    public ContentViewContainer getContentViewContainer() {
        return mContentViewContainer;
    }

    @NonNull
    public final ContentViewContainer requireDialogContainerView() {
        ContentViewContainer containerView = getContentViewContainer();
        if (containerView == null) {
            throw new IllegalStateException("PersistentDialogFragment " + this + " does not have a container.");
        }
        return containerView;
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    private ContentViewContainer mContentViewContainer;

    @NonNull
    public ContentViewContainer onCreateViewContainer(@Nullable Bundle savedInstanceState) {
        ViewGroup appRootView = getAppRootView();
        return new ContentViewContainer(appRootView);
    }

    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        mDismissed = false;
        mShownByMe = true;
        transaction.add(this, tag);
        mViewDestroyed = false;
        mBackStackId = transaction.commit();
        return mBackStackId;
    }

    public void showNow(@NonNull FragmentManager manager, @Nullable String tag) {
        mDismissed = false;
        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitNow();
    }

    /**
     * Override this method to use other system-created-view as root view, like DecorView
     *
     * @return the new app root view
     */
    public ViewGroup getAppRootView() {
        return requireActivity().getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {

    }

    public void onDismiss(@NonNull DialogInterface dialog) {
        if (!mViewDestroyed) {
            dismissInternal(true, true);
        }
    }

    @MainThread
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mShowsDialog) {
            return;
        }

        View view = getView();
        if (mContentViewContainer != null) {
            if (view != null) {
                if (view.getParent() != null) {
                    throw new IllegalStateException(
                            "FloatingViewFragment can not be attached to a container view");
                }

                mContentViewContainer.setContentView(view);
            }

            mContentViewContainer.setCancelable(mCancelable);
            mContentViewContainer.setOnCancelListener(mOnCancelListener);
            mContentViewContainer.setOnDismissListener(mOnDismissListener);
            if (savedInstanceState != null) {
                Bundle dialogState = savedInstanceState.getBundle(SAVED_DIALOG_STATE_TAG);
                if (dialogState != null) {
                    mContentViewContainer.onRestoreInstanceState(dialogState);
                }
            }

        }
    }

    @MainThread
    public void onStart() {
        super.onStart();
        if (mContentViewContainer != null) {
            mViewDestroyed = false;
            mContentViewContainer.show();
        }
    }

    @MainThread
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!mShownByMe) {
            // If not explicitly shown through our API, take this as an
            // indication that the dialog is no longer dismissed.
            mDismissed = false;
        }
    }

    @MainThread
    @Override
    public void onDetach() {
        super.onDetach();
        if (!mShownByMe && !mDismissed) {
            // The fragment was not shown by a direct call here, it is not
            // dismissed, and now it is being detached...  well, okay, thou
            // art now dismissed.  Have fun.
            mDismissed = true;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCancelable = savedInstanceState.getBoolean(SAVED_CANCELABLE, true);
            mShowsDialog = savedInstanceState.getBoolean(SAVED_SHOWS_DIALOG, mShowsDialog);
            mBackStackId = savedInstanceState.getInt(SAVED_BACK_STACK_ID, -1);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mCancelable) {
                    if (getParentFragmentManager().getPrimaryNavigationFragment() == FloatingViewFragment.this) {
                        requireActivity().finish();
                    } else {
                        dismiss();
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = super.onGetLayoutInflater(savedInstanceState);
        if (!mShowsDialog || mCreatingDialog) {
            return layoutInflater;
        }

        try {
            mCreatingDialog = true;
            mContentViewContainer = onCreateViewContainer(savedInstanceState);
        } finally {
            mCreatingDialog = false;
        }
        return layoutInflater;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mContentViewContainer != null) {
            Bundle containerState = mContentViewContainer.onSaveInstanceState();
            outState.putBundle(SAVED_DIALOG_STATE_TAG, containerState);
        }

        if (!mCancelable) {
            outState.putBoolean(SAVED_CANCELABLE, mCancelable);
        }
        if (!mShowsDialog) {
            outState.putBoolean(SAVED_SHOWS_DIALOG, mShowsDialog);
        }
        if (mBackStackId != -1) {
            outState.putInt(SAVED_BACK_STACK_ID, mBackStackId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mContentViewContainer != null) {
            mViewDestroyed = true;

            // dismiss container
            mContentViewContainer.setOnDismissListener(null);
            mContentViewContainer.dismiss();
            if (!mDismissed) {
                onDismiss(mContentViewContainer);
            }

            mContentViewContainer = null;
        }
    }
}