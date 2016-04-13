package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 4/11/16.
 */
public class PollingSitesPresenterImpl extends PollingSitesPresenter {
    private
    @LayoutRes
    int currentSort = R.id.all_sites;

    public PollingSitesPresenterImpl(PollingSitesView pollingSitesView) {
        setView(pollingSitesView);
    }

    @Override
    public ArrayList<PollingLocation> getAllLocations() {
        return UserPreferences.getVoterInfo().getAllLocations();
    }

    @Override
    Election getElection() {
        return UserPreferences.getVoterInfo().election;
    }

    @Override
    public void menuItemClicked(@LayoutRes int sortType) {
        switch (sortType) {
            case R.id.all_sites:
                getView().updateList(UserPreferences.getVoterInfo().getAllLocations());
                currentSort = sortType;

                break;
            case R.id.polling_locations:
                getView().updateList(UserPreferences.getVoterInfo().getPollingLocations());
                currentSort = sortType;
                break;
            case R.id.early_vote:
                getView().updateList(UserPreferences.getVoterInfo().getOpenEarlyVoteSites());
                currentSort = sortType;
                break;
            case R.id.drop_boxes:
                getView().updateList(UserPreferences.getVoterInfo().getOpenDropOffLocations());
                currentSort = sortType;
                break;
            case R.id.map_view:
                getView().navigateToMap();
                break;
        }
    }

    @Override
    public void itemClickedAtIndex(int index) {
        ArrayList<PollingLocation> sortedList = null;

        switch (currentSort) {
            case R.id.all_sites:
                sortedList = UserPreferences.getVoterInfo().getAllLocations();
                break;
            case R.id.polling_locations:
                sortedList = UserPreferences.getVoterInfo().getPollingLocations();
                break;
            case R.id.early_vote:
                sortedList = UserPreferences.getVoterInfo().getOpenEarlyVoteSites();
                break;
            case R.id.drop_boxes:
                sortedList = UserPreferences.getVoterInfo().getOpenDropOffLocations();
                break;
        }

        if (sortedList != null) {
            getView().navigateToDirections(sortedList.get(index));
        }
    }

    @Override
    public void onCreate(Bundle savedState) {

    }

    @Override
    public void onSaveState(@NonNull Bundle state) {

    }

    @Override
    public void onDestroy() {

    }
}
