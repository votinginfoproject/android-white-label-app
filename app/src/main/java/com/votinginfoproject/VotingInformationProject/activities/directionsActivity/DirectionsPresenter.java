package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.support.v13.app.FragmentStatePagerAdapter;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;

/**
 * Created by max on 4/25/16.
 */
public abstract class DirectionsPresenter extends BasePresenter<DirectionsView> {
    public abstract boolean isLoading();

    public abstract String[] getTransitModes();

    public abstract Route getRouteForTransitMode(String transitMode);

    public abstract String getOriginCoordinates();

    public abstract String getDestinationCoordinates();

    public abstract void tabSelectedAtIndex(int index);

    public abstract void swipedToDirectionsListAtIndex(int index);

    public abstract void mapButtonPressed();
}
