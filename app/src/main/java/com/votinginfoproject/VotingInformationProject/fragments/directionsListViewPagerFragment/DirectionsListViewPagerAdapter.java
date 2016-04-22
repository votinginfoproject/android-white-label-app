package com.votinginfoproject.VotingInformationProject.fragments.directionsListViewPagerFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment.DirectionsListFragment;

/**
 * Created by max on 4/22/16.
 */
public class DirectionsListViewPagerAdapter extends FragmentStatePagerAdapter {

    private final DirectionsListViewPagerPresenter mPresenter;

    public DirectionsListViewPagerAdapter(FragmentManager fm, DirectionsListViewPagerPresenter presenter) {
        super(fm);
        mPresenter = presenter;
    }

    @Override
    public Fragment getItem(int position) {
        return DirectionsListFragment.newInstance(
                mPresenter.getTransitModes()[position],
                mPresenter.getOriginCoordinates(),
                mPresenter.getDestinationCoordinates()
        );
    }

    @Override
    public int getCount() {
        return mPresenter.getTransitModes().length;
    }
}
