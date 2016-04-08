package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;


import com.votinginfoproject.VotingInformationProject.fragments.ScrollToTopFragment;

/**
 * Created by marcvandehey on 4/6/16.
 */
public interface VoterInformationView {

    void presentParentLevelFragment(ScrollToTopFragment parentLevel);

    void presentChildLevelFragment(ScrollToTopFragment childLevelFragment);

    void navigateBack();

    void updatePollingSitesFilter();

    void scrollCurrentFragmentToTop();
}
