package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.contestInformationFragment;

import com.votinginfoproject.VotingInformationProject.activities.BasePresenter;
import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.Election;

/**
 * Created by marcvandehey on 4/21/16.
 */
public abstract class ContestInformationPresenter extends BasePresenter<ContestInformationView> {
    public static final int ELECTION_VIEW_HOLDER = 0x0;
    public static final int CANDIDATE_VIEW_HOLDER = 0x1;
    public static final int CONTEST_DETAIL_VIEW_HOLDER = 0x2;
    public static final int REFERENDUM_VIEW_HOLDER = 0x3;
    public static final int REPORT_VIEW_HOLDER = 0x4;

    public abstract DataHolder getDataForIndex(int index);

    public abstract String getReferendumItemForIndex(int index);

    public abstract Candidate getCandidate(int index);

    public abstract int getRowCount();

    public abstract Election getElection();

    public abstract String getSectionTitleForIndex(int index);

    public abstract boolean hasHeader();

    public abstract int getViewTypeForIndex(int index);

    public class DataHolder {
        public String title;
        public String description;

        public DataHolder(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }
}
