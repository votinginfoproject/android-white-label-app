package com.votinginfoproject.VotingInformationProject.fragments.directionsListViewPagerFragment;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;

/**
 * Created by max on 4/21/16.
 */
public abstract class DirectionsListViewPagerPresenter extends BasePresenter<DirectionsListViewPagerView> {
    public abstract String[] getTransitModes();

    public abstract String getOriginCoordinates();

    public abstract String getDestinationCoordinates();
}
