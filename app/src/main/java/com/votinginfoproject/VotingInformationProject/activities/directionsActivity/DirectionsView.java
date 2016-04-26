package com.votinginfoproject.VotingInformationProject.activities.directionsActivity;

import com.votinginfoproject.VotingInformationProject.models.TabData;

/**
 * Created by max on 4/25/16.
 */
public interface DirectionsView {
    void refreshDataView();

    void setTabs(TabData[] tabs);

    void selectTabAtIndex(int index);

    void navigateToDirectionsListAtIndex(int index);

    void toggleMapDisplaying(boolean displaying);
}
