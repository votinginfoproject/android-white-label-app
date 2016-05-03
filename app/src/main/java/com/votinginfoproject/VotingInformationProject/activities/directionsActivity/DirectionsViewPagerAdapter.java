package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment.DirectionsListFragment;

/**
 * Created by max on 4/25/16.
 */
public class DirectionsViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = DirectionsViewPagerAdapter.class.getSimpleName();

    private final DirectionsPresenter mPresenter;

    public DirectionsViewPagerAdapter(FragmentManager fm, DirectionsPresenter presenter) {
        super(fm);

        mPresenter = presenter;
    }

    @Override
    public Fragment getItem(int position) {
        String transitMode = mPresenter.getTransitModes()[position];
        return DirectionsListFragment.newInstance(mPresenter.getRouteForTransitMode(transitMode));
    }

    @Override
    public int getCount() {
        if (mPresenter.isLoading()) {
            return 0;
        }

        return mPresenter.getTransitModes().length;
    }
}
