package com.votinginfoproject.VotingInformationProject.fragments.directionsListViewPagerFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.votinginfoproject.VotingInformationProject.fragments.directionsListFragment.DirectionsListFragment;

/**
 * Created by max on 4/22/16.
 */
public class DirectionsListViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final String[] TRANSIT_MODES = {"driving","walking","biking"};

    public DirectionsListViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return DirectionsListFragment.newInstance(TRANSIT_MODES[position]);
    }

    @Override
    public int getCount() {
        return TRANSIT_MODES.length;
    }
}
