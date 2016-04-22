package com.votinginfoproject.VotingInformationProject.fragments.directionsListViewPagerFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.votinginfoproject.VotingInformationProject.constants.TransitModes;

/**
 * Created by max on 4/21/16.
 */
public class DirectionsListViewPagerPresenterImpl extends DirectionsListViewPagerPresenter {

    private String mOrigin = "Austin";
    private String mDestination = "Toronto";


    @Override
    public String[] getTransitModes() {
        return TransitModes.ALL;
    }

    @Override
    public String getOriginCoordinates() {
        return mOrigin;
    }

    @Override
    public String getDestinationCoordinates() {
        return mDestination;
    }
}
