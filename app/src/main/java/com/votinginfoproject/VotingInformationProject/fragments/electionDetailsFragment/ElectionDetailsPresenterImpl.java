package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

/**
 * Created by max on 4/15/16.
 */
public class ElectionDetailsPresenterImpl extends ElectionDetailsPresenter {
    private static final String TAG = ElectionDetailsPresenterImpl.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedState) {

    }

    @Override
    public void onSaveState(@NonNull Bundle state) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public VoterInfo getVoterInfo() {
        return UserPreferences.getVoterInfo();
    }

    @Override
    public void linkSelected(String urlString) {
        if (urlString != null) {
            getView().navigateToURL(urlString);
        }
    }

    @Override
    public void reportErrorClicked() {
        //TODO add things here
        Log.v(TAG, "Report Error Clicked()");
    }

    @Override
    public void addressSelected(String addressString) {
        //TODO add other things here
        Log.v(TAG, "Address selected: " + addressString);
    }
}
