package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

/**
 * Created by max on 4/15/16.
 */
public class ElectionDetailsPresenterImpl extends ElectionDetailsPresenter {
    private static final String TAG = ElectionDetailsPresenterImpl.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedState) {
        //Not implemented
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        //Not implemented
    }

    @Override
    public void onDestroy() {
        //Not implemented
    }

    @Override
    public ElectionAdministrationBody getLocalAdmin() {
        return UserPreferences.getVoterInfo().getLocalAdmin();
    }

    @Override
    public ElectionAdministrationBody getStateAdmin() {
        return UserPreferences.getVoterInfo().getStateAdmin();
    }

    @Override
    public Election getElection() {
        return UserPreferences.getVoterInfo().election;
    }

    @Override
    public void linkSelected(String urlString) {
        if (urlString != null) {
            getView().navigateToURL(urlString);
        }
    }

    @Override
    public void reportErrorClicked() {
        getView().navigateToErrorView();
    }

    @Override
    public void addressSelected(String addressString) {
        getView().navigateToDirectionsView(addressString);
    }
}
