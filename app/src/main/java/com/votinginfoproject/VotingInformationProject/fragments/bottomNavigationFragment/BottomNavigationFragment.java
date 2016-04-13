package com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.votinginfoproject.VotingInformationProject.R;

import java.lang.ref.WeakReference;


/**
 * Created by marcvandehey on 4/8/16.
 */
public abstract class BottomNavigationFragment extends Fragment {
    private final static String TAG = BottomNavigationFragment.class.getSimpleName();
    public WeakReference<OnBackPressedListener> mListener;
    private Toolbar mToolbar;

    @StringRes
    public abstract int getTitle();

    @MenuRes
    public int getMenu() {
        return 0;
    }

    //Handle everything needed to reset the task
    public abstract void resetView();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view != null) {
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);

            if (mToolbar == null) {
                Log.e(TAG, "No toolbar found in class: " + getClass().getSimpleName());
            } else {
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                mToolbar.setTitle(getTitle());
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null && mListener.get() != null) {
                            mListener.get().onBackPressed();
                        }
                    }
                });

                if (getMenu() != 0) {
                    mToolbar.inflateMenu(getMenu());
                }
            }
        }
    }

    /**
     * Call onViewCreated after toolbar is inflated
     *
     * @param listener
     */
    public void setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener listener) {
        mToolbar.setOnMenuItemClickListener(listener);
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        if (mListener != null) {
            mListener.clear();
        }

        if (onBackPressedListener != null) {
            mListener = new WeakReference<>(onBackPressedListener);
        }
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }
}