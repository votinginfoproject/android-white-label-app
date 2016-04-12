package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;


import com.votinginfoproject.VotingInformationProject.fragments.bottomNavigationFragment.BottomNavigationFragment;

/**
 * Created by marcvandehey on 4/6/16.
 */
public interface VoterInformationView {

    void presentParentLevelFragment(BottomNavigationFragment parentLevel);

    void presentChildLevelFragment(BottomNavigationFragment childLevelFragment);

    void navigateBack();

    void updatePollingSitesFilter();

    void scrollCurrentFragmentToTop();
}
