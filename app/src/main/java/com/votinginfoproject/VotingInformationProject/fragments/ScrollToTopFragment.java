package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Fragment;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Created by marcvandehey on 4/8/16.
 */
public abstract class ScrollToTopFragment extends Fragment {
    private WeakReference<OnBackPressedListener> mListener;

    public abstract void scrollToTop();


    @Nullable
    public OnBackPressedListener getOnBackPressedListener() {
        if (mListener != null) {
            return mListener.get();
        }

        return null;
    }

    public void setOnBackPressedListener(@Nullable OnBackPressedListener onBackPressedListener) {
        if (onBackPressedListener != null && mListener != null) {
            mListener.clear();
        } else {
            mListener = new WeakReference<>(onBackPressedListener);
        }
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}
