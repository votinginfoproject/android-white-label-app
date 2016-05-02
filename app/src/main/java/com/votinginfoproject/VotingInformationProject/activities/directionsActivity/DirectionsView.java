package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.support.annotation.DrawableRes;

import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.TabData;

/**
 * Created by max on 4/25/16.
 */
public interface DirectionsView {
    void refreshViewData();

    void setTabs(TabData[] tabs);

    void selectTabAtIndex(int index);

    void showRouteOnMap(Route route, @DrawableRes int destinationMarkerResource);

    void navigateToDirectionsListAtIndex(int index);

    void navigateToExternalMap(String address);

    void toggleMapDisplaying(boolean displaying);

    void toggleLoadingView(boolean loading);

    void toggleConnectionErrorView(boolean error);

    void toggleEnableLocationView(boolean showing);

    void attemptToGetLocation();

    void navigateToAppSettings();
}
