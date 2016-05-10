package com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

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
    public void onAttachView(ElectionDetailsView view) {
        super.onAttachView(view);

        if (getLocalAdmin() == null && getStateAdmin() == null) {
            getView().showNoContentView();
        }
    }

    @Override
    public ElectionAdministrationBody getLocalAdmin() {
        return VoterInformation.getLocalAdministrationBody();
    }

    @Override
    public ElectionAdministrationBody getStateAdmin() {
        return VoterInformation.getStateAdministrationBody();
    }

    @Override
    public Election getElection() {
        return VoterInformation.getElection();
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
