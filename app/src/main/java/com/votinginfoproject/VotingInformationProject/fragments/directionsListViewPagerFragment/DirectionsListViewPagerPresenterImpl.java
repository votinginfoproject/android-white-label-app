package com.votinginfoproject.VotingInformationProject.fragments.directionsListViewPagerFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

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
    public void onCreate(Bundle savedState) {

    }

    @Override
    public void onSaveState(@NonNull Bundle state) {

    }

    @Override
    public void onDestroy() {

    }

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
