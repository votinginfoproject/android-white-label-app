package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

/**
 * Created by marcvandehey on 4/6/16.
 */
public interface VoterInformationView {
    void showPollingSiteFragment();

    void showBallotFragment();

    void showDetailsFragment();

    void showMapDetailFragment();

    void updateFilter();
}
