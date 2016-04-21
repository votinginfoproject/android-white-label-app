package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.support.annotation.LayoutRes;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 4/11/16.
 */
public abstract class PollingSitesPresenter extends BasePresenter<PollingSitesView> {
    public abstract ArrayList<PollingLocation> getSortedLocations();

    public abstract Election getElection();

    public abstract void menuItemClicked(@LayoutRes int sortType);

    public abstract void itemClickedAtIndex(int index);

    public abstract void onMapNeedsLayout(GoogleMap map);

    public abstract boolean mapMarkerClicked(GoogleMap map, Marker marker);

    public abstract void clearSelectedMapMarkers();

    public abstract void pollingItemSelected();

    @LayoutRes
    public abstract int getCurrentSort();

    public abstract boolean hasPollingLocations();

    public abstract boolean hasEarlyVotingLocations();

    public abstract boolean hasDropBoxLocations();
}
