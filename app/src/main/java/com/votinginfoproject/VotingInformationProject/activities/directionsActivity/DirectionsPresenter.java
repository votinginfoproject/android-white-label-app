package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;

/**
 * Created by max on 4/25/16.
 */
public abstract class DirectionsPresenter extends BasePresenter<DirectionsView> {
    public abstract boolean isLoading();

    public abstract String[] getTransitModes();

    public abstract Route getRouteForTransitMode(String transitMode);

    public abstract void tabSelectedAtIndex(int index);

    public abstract void swipedToDirectionsListAtIndex(int index);

    public abstract void mapButtonPressed();

    public abstract void onMapReady();

    public abstract void externalMapButtonPressed();

    public abstract void retryButtonPressed();
}
