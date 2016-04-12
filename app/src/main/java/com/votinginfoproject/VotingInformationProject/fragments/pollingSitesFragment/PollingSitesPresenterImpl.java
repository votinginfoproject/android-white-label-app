package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 4/11/16.
 */
public class PollingSitesPresenterImpl extends PollingSitesPresenter {
    private ArrayList<PollingLocation> mLocations;
    private Election mElection;

    public PollingSitesPresenterImpl(Election election, ArrayList<PollingLocation> locations) {
        if (locations == null) {
            mLocations = new ArrayList<>();
        } else {
            mLocations = locations;
        }

        mElection = election;
    }

    @Override
    public ArrayList<PollingLocation> getAllLocations() {
        return mLocations;
    }

    @Override
    Election getElection() {
        return mElection;
    }

    @Override
    public void sortTypeChanged(@LayoutRes int sortType) {

    }

    @Override
    public void itemClickedAtIndex(int index) {

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
