package com.votinginfoproject.VotingInformationProject.fragments.directionsListViewPagerFragment;

/**
 * Created by max on 4/21/16.
 */
public abstract class DirectionsListViewPagerPresenter {
    public abstract String[] getTransitModes();

    public abstract String getOriginCoordinates();

    public abstract String getDestinationCoordinates();
}
