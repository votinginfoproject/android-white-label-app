package com.votinginfoproject.VotingInformationProject.activities.homeActivity;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;

/**
 * Created by marcvandehey on 3/31/16.
 */
public interface HomePresenter extends BasePresenter {
    void selectedElection(int election);

    void selectedParty(int party);

    void electionTextViewClicked();

    void partyTextViewClicked();

    void goButtonClicked();

    void aboutButtonClicked();

    void searchButtonClicked(String searchAddress);
}
