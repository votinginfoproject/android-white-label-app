package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;

/**
 * Created by marcvandehey on 4/5/16.
 */
public abstract class VoterInformationPresenter extends BasePresenter<VoterInformationView> {
    abstract void backNavigationBarButtonClicked();

    abstract void ballotButtonClicked();

    abstract void detailsButtonClicked();

    abstract void pollingSitesButtonClicked();

    abstract void mapButtonClicked();

    abstract void overflowButtonClicked();

    abstract void mapFilterButtonClicked(int filterItem);
}
