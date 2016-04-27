package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import android.support.annotation.DrawableRes;

import com.votinginfoproject.VotingInformationProject.models.GoogleDirections.Route;
import com.votinginfoproject.VotingInformationProject.models.TabData;

/**
 * Created by max on 4/25/16.
 */
public interface DirectionsView {
    void refreshDataView();

    void setTabs(TabData[] tabs);

    void selectTabAtIndex(int index);

    void showRouteOnMap(Route route, @DrawableRes int destinationMarkerResource);

    void navigateToDirectionsListAtIndex(int index);

    void navigateToExternalMap(String address);

    void toggleMapDisplaying(boolean displaying);

    void toggleLoading(boolean loading);
}
