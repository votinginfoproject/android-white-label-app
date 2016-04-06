package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

/**
 * Created by marcvandehey on 4/5/16.
 */
public interface VoterInformationPresenter {
    void backNavigationBarButtonClicked();

    void ballotButtonClicked();

    void detailsButtonClicked();

    void pollintSitesButtonClicked();

    void mapButtonClicked();

    void overflowButtonClicked();

    void mapFilterButtonClicked(int filterItem);
}
