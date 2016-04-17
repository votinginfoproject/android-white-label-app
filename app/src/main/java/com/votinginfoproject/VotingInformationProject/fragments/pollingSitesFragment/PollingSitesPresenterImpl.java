package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 4/11/16.
 */
public class PollingSitesPresenterImpl extends PollingSitesPresenter {
    private static final String TAG = PollingSitesPresenterImpl.class.getSimpleName();
    private
    @LayoutRes
    int currentSort = R.id.sort_all;

    public PollingSitesPresenterImpl(PollingSitesView pollingSitesView) {
        setView(pollingSitesView);
    }

    public PollingSitesPresenterImpl(PollingSitesView pollingSitesView, @LayoutRes int selectedSort) {
        setView(pollingSitesView);
        currentSort = selectedSort;
    }

    @Override
    public ArrayList<PollingLocation> getSortedLocations() {
        switch (currentSort) {
            case R.id.sort_all:
                return UserPreferences.getAllPollingLocations();
            case R.id.sort_polling_locations:
                return UserPreferences.getPollingLocations();
            case R.id.sort_early_vote:
                return UserPreferences.getEarlyVotingLocations();
            case R.id.sort_drop_boxes:
                return UserPreferences.getDropBoxLocations();
        }

        return new ArrayList<>();
    }

    @Override
    public Election getElection() {
        return UserPreferences.getVoterInfo().election;
    }

    @Override
    public void menuItemClicked(@LayoutRes int sortType) {
        switch (sortType) {
            case R.id.map_view:
                getView().navigateToMap(currentSort);
                break;
            case R.id.list_view:
                getView().navigateToList(currentSort);
            default:
                currentSort = sortType;
                getView().updateList(getSortedLocations());
        }
    }

    @Override
    public void itemClickedAtIndex(int index) {
        ArrayList<PollingLocation> sortedList = getSortedLocations();

        if (sortedList.size() > index) {
            getView().navigateToDirections(sortedList.get(index));
        } else {
            Log.e(TAG, "Cannot retrieve data for item selected: " + index);
        }
    }

    @Override
    public int getCurrentSort() {
        return currentSort;
    }

    @Override
    public boolean hasPollingLocations() {
        return !UserPreferences.getVoterInfo().getPollingLocations().isEmpty();
    }

    @Override
    public boolean hasEarlyVotingLocations() {
        return !UserPreferences.getVoterInfo().getOpenEarlyVoteSites().isEmpty();
    }

    @Override
    public boolean hasDropBoxLocations() {
        return !UserPreferences.getVoterInfo().getOpenDropOffLocations().isEmpty();
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
