package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.contestListFragment;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.Election;

/**
 * Created by marcvandehey on 4/21/16.
 */
public abstract class ContestListPresenter extends BasePresenter<ContestListView> {
    public static final int ELECTION_VIEW_HOLDER = 0x0;
    public static final int CONTEST_VIEW_HOLDER = 0x1;
    public static final int REPORT_VIEW_HOLDER = 0x2;

    public abstract Contest getContest(int index);

    public abstract int getContestCount();

    public abstract void onContestItemClicked(Contest contest);

    public abstract void onReportErrorClicked();

    public abstract Election getElection();

    public abstract String getSectionTitleForIndex(int index);

    public abstract boolean hasHeader();

    public abstract int getViewTypeForIndex(int index);
}
