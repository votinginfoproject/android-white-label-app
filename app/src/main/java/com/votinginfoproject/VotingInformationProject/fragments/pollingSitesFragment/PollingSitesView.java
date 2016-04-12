package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

/**
 * Created by marcvandehey on 4/11/16.
 */
public interface PollingSitesView {
    void navigateToDirections(PollingLocation pollingLocation);

    void navigateToErrorForm();
}
