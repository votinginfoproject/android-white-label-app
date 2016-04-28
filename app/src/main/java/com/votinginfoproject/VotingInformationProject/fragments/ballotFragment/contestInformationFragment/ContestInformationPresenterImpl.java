package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.contestInformationFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.Election;

/**
 * Created by marcvandehey on 4/21/16.
 */
public class ContestInformationPresenterImpl extends ContestInformationPresenter {
    private Contest mContest;
    private Election mElection;

    private boolean mHasHeader;

    ContestInformationPresenterImpl(ContestInformationView view, Election election, Contest contest) {
        setView(view);

        mContest = contest;
        mElection = election;

        mHasHeader = mElection != null;
    }

    @Override
    public void itemSelected(int index) {

    }

    @Override
    public Candidate getCandidate(int index) {
        return null;
    }

    @Override
    public int getRowCount() {
        int rowCount = mHasHeader ? 1 : 0;

        if (mContest != null) {
            if (mContest.candidates != null) {
                rowCount += mContest.candidates.size();
            }

            rowCount += mContest.district != null ? 1 : 0;
            rowCount += mContest.type != null ? 1 : 0;
            rowCount += mContest.special != null ? 1 : 0;
        }

        return rowCount;
    }

    @Override
    public Election getElection() {
        return mElection;
    }

    @Override
    public String getSectionTitleForIndex(int index) {
        return null;
    }

    @Override
    public boolean hasHeader() {
        return mHasHeader;
    }

    @Override
    public int getViewTypeForIndex(int index) {
        return 0;
    }

    @Override
    public void onCreate(Bundle savedState) {

    }

    @Override
    public void onSaveState(@NonNull Bundle state) {

    }

    @Override
    public void onDestroy() {

    }
}
