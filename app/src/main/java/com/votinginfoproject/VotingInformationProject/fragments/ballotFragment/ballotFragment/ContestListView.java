package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.ballotFragment;

import android.content.Context;

import com.votinginfoproject.VotingInformationProject.models.Contest;

/**
 * Created by marcvandehey on 4/21/16.
 */
public interface ContestListView {
    void onContestItemClicked(Contest contest);

    void onReportErrorClicked();

    Context getContext();
}
