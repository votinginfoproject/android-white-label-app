package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 4/11/16.
 */
public class PollingSitesPresenterImpl extends PollingSitesPresenter {
    private ArrayList<PollingLocation> mLocations;

    public PollingSitesPresenterImpl(ArrayList<PollingLocation> locations) {
        if (locations == null) {
            mLocations = new ArrayList<>();
        } else {
            mLocations = locations;
        }
    }

    @Override
    public ArrayList<PollingLocation> getAllLocations() {
        return mLocations;
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
