package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.ballotFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.Election;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 4/21/16.
 */
public class ContestListPresenterImpl extends ContestListPresenter {
    private final String ELECTION_KEY = "election_key";
    private final String CONTEST_KEY = "contest_key";
    private Election mElection;
    private ArrayList<Contest> mContests;

    ContestListPresenterImpl(ContestListView view, Election election, ArrayList<Contest> contests) {
        setView(view);

        mElection = election;
        mContests = contests;
    }

    @Override
    public void onCreate(Bundle savedState) {
        if (savedState != null) {
            mElection = savedState.getParcelable(ELECTION_KEY);
            mContests = savedState.getParcelableArrayList(CONTEST_KEY);
        }
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        state.putParcelable(ELECTION_KEY, mElection);
        state.putParcelableArrayList(CONTEST_KEY, mContests);
    }

    @Override
    public void onDestroy() {
        //Not Implemented
    }

    @Override
    public Contest getContest(int index) {
        index -= (hasHeader()) ? 1 : 0;

        if (index < mContests.size()) {
            return mContests.get(index);
        }

        return null;
    }

    @Override
    public int getContestCount() {
        return mContests.size() + ((hasHeader()) ? 1 : 0) + 1;
    }

    @Override
    public void onContestItemClicked(Contest contest) {
        getView().onContestItemClicked(contest);
    }

    @Override
    public void onReportErrorClicked() {
        getView().onReportErrorClicked();
    }

    @Override
    public Election getElection() {
        return mElection;
    }

    @Override
    public String getSectionTitleForIndex(int index) {
        index -= hasHeader() ? 1 : 0;

        return (index == 0) ? getView().getContext().getString(R.string.fragment_ballot_list_header_contests) : null;
    }

    @Override
    public boolean hasHeader() {
        return mElection != null;
    }

    @Override
    public int getViewTypeForIndex(int index) {
        if (index == 0 && hasHeader()) {
            return ELECTION_VIEW_HOLDER;
        } else if (index < getContestCount() - (hasHeader() ? 1 : 0)) {
            return CONTEST_VIEW_HOLDER;
        } else {
            return REPORT_VIEW_HOLDER;
        }
    }
}
