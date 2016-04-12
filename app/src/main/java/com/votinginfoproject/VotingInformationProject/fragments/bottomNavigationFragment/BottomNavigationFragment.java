package com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    public abstract void scrollToTop();

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

                mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        item.setChecked(true);
                        return false;
                    }
                });

                if (getMenu() != 0) {
                    mToolbar.inflateMenu(getMenu());

                    highlightMenuItem(0);
                }
            }
        }
    }

    public void highlightMenuItem(int item) {
        Menu menu = mToolbar.getMenu();

        MenuItem menuItem = menu.getItem(0);
        menuItem.setCheckable(true);
        menuItem.setChecked(true);

        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setActionView(android.R.layout.simple_list_item_1);
        menuItem.getActionView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_blue));
        MenuItem view = menu.findItem(R.id.all_sites);
        view.setChecked(true);

//        if (menuItem != null) {
//            menuItem.getActionView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_blue));
//        } else {
//            Log.v(TAG, "was null :(");
//        }

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