package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;


import android.app.Fragment;

/**
 * Created by marcvandehey on 4/6/16.
 */
public interface VoterInformationView {

    void presentParentLevelFragment(Fragment parentLevel);

    void presentChildLevelFragment(Fragment childLevelFragment);

    void navigateBack();

    void updatePollingSitesFilter();

    void scrollCurrentFragmentToTop();
}
