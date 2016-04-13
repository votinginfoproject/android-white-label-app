package com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment;

import android.support.annotation.LayoutRes;

import com.votinginfoproject.VotingInformationProject.models.PollingLocation;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 4/11/16.
 */
public interface PollingSitesView {
    void navigateToDirections(PollingLocation pollingLocation);

    void navigateToErrorForm();

    void navigateToMap(@LayoutRes int currentSort);

    void navigateToList(@LayoutRes int currentSort);

    void updateList(ArrayList<PollingLocation> locations);
}
