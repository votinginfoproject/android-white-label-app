package com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.contestInformationFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.Candidate;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.Election;

import java.util.Locale;

/**
 * Created by marcvandehey on 4/21/16.
 */
public class ContestInformationPresenterImpl extends ContestInformationPresenter {
    private static final String TAG = ContestInformationPresenterImpl.class.getSimpleName();

    private Contest mContest;
    private Election mElection;

    private boolean mHasHeader;
    private boolean mIsReferendum;

    private int districtIndex = -1;
    private int typeIndex = -1;
    private int specialIndex = -1;
    private int numberElectedIndex = -1;
    private int numberVotingForIndex = -1;
    private int ballotPlacementIndex = -1;

    private int referendumTextIndex = -1;
    private int referendumProIndex = -1;
    private int referendumConIndex = -1;

    public ContestInformationPresenterImpl(ContestInformationView view, Election election, Contest contest) {
        setView(view);

        mContest = contest;
        mElection = election;

        mHasHeader = mElection != null;

        mIsReferendum = mContest.type.equals("Referendum");
    }

    @Override
    public DataHolder getDataForIndex(int index) {
        int adjustedIndex = index - (hasHeader() ? 1 : 0);
        String title = "";
        String description = "";

        if (index == 0 && hasHeader()) {
            if (mIsReferendum) {
                description = mContest.referendumSubtitle;
                title = mContest.referendumTitle;
            } else {
                title = mContest.office;
                description = mElection.getName();
            }
        } else if (mContest.candidates != null && adjustedIndex < mContest.candidates.size()) {
            Candidate candidate = mContest.candidates.get(adjustedIndex);

            title = candidate.name;
            description = candidate.party;
        } else if (index == districtIndex) {
            title = getView().getContext().getString(R.string.contest_label_office);
            description = mContest.office;
        } else if (index == typeIndex) {
            title = getView().getContext().getString(R.string.contest_label_type);
            description = mContest.type;
        } else if (index == specialIndex) {
            title = getView().getContext().getString(R.string.contest_label_special);

            String special = mContest.special;

            //Uppercase first letter
            try {
                if (special != null && !special.isEmpty()) {
                    special = special.toLowerCase(Locale.US);
                    String notFirstLetter = special.substring(1);
                    special = special.substring(0, 1);
                    special = special.toUpperCase(Locale.US);
                    special += notFirstLetter;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error upper casing first letter of string");
            }

            description = special;
        } else if (index == numberElectedIndex) {
            title = getView().getContext().getString(R.string.contest_label_number_elected);
            description = mContest.numberElected + "";
        } else if (index == numberVotingForIndex) {
            title = getView().getContext().getString(R.string.contest_label_number_voting_for);
            description = mContest.numberVotingFor + "";
        } else if (index == ballotPlacementIndex) {
            title = getView().getContext().getString(R.string.contest_label_ballot_placement);
            description = mContest.ballotPlacement + "";
        }

        return new DataHolder(title, description);
    }

    @Override
    public String getReferendumItemForIndex(int index) {
        String text = "";

        if (index == referendumTextIndex) {
            text = mContest.referendumText;
        } else if (index == referendumProIndex) {
            text = mContest.referendumProStatement;
        } else if (index == referendumConIndex) {
            text = mContest.referendumConStatement;
        }

        return text;
    }

    @Override
    public Candidate getCandidate(int index) {
        int adjustedIndex = index - (hasHeader() ? 1 : 0);

        if (mContest.candidates != null && mContest.candidates.size() > adjustedIndex) {
            return mContest.candidates.get(adjustedIndex);
        }

        return null;
    }

    @Override
    public int getRowCount() {
        int rowCount = 0;

        if (mContest != null) {
            rowCount = getContestRowCount();

            if (mIsReferendum) {
                rowCount = getReferendumRowCount(rowCount);
            }

            //Add an extra row for Report error
            rowCount++;
        }

        return rowCount;
    }

    /**
     * Generates the Row Count in the case that the Contest is a Referendum
     *
     * @return
     */
    private int getReferendumRowCount(int currentRowCount) {
        int rowCount = currentRowCount;

        if (mContest.referendumText != null) {
            referendumTextIndex = rowCount;
            rowCount++;
        } else {
            referendumTextIndex = -1;
        }

        if (mContest.referendumProStatement != null) {
            referendumProIndex = rowCount;
            rowCount++;
        } else {
            referendumProIndex = -1;
        }

        if (mContest.referendumConStatement != null) {
            referendumConIndex = rowCount;
            rowCount++;
        } else {
            referendumConIndex = -1;
        }

        return rowCount;
    }

    /**
     * Generates the Row Count in the case that the Contest is a normal Contest
     *
     * @return
     */
    private int getContestRowCount() {
        int rowCount = mHasHeader ? 1 : 0;

        if (mContest.candidates != null) {
            rowCount += mContest.candidates.size();
        }

        if (mContest.district != null) {
            districtIndex = rowCount;
            rowCount++;
        } else {
            districtIndex = -1;
        }

        if (mContest.type != null) {
            typeIndex = rowCount;
            rowCount++;
        } else {
            typeIndex = -1;
        }

        if (mContest.special != null) {
            specialIndex = rowCount;
            rowCount++;
        } else {
            specialIndex = -1;
        }

        if (mContest.numberElected != null && mContest.numberElected != 0) {
            numberElectedIndex = rowCount;
            rowCount++;
        } else {
            numberElectedIndex = -1;
        }

        if (mContest.numberVotingFor != null && mContest.numberVotingFor != 0) {
            numberVotingForIndex = rowCount;
            rowCount++;
        } else {
            numberVotingForIndex = -1;
        }

        if (mContest.ballotPlacement != null && mContest.ballotPlacement != 0) {
            ballotPlacementIndex = rowCount;
            rowCount++;
        } else {
            ballotPlacementIndex = -1;
        }

        return rowCount;
    }

    @Override
    public Election getElection() {
        return mElection;
    }

    @Override
    public String getSectionTitleForIndex(int index) {
        int adjustedIndex = index - (hasHeader() ? 1 : 0);

        if (index == referendumTextIndex) {
            return getView().getContext().getString(R.string.contest_label_referendum_text);
        } else if (index == referendumProIndex) {
            return getView().getContext().getString(R.string.contest_label_referendum_pro);
        } else if (index == referendumConIndex) {
            return getView().getContext().getString(R.string.contest_label_referendum_con);
        } else if (adjustedIndex == 0 && mContest.candidates != null && !mContest.candidates.isEmpty()) {
            return getView().getContext().getString(R.string.candidate_details_header);
        } else if ((mContest.candidates != null && adjustedIndex == mContest.candidates.size()) || adjustedIndex == 0) {
            return getView().getContext().getString(R.string.contest_header_details);
        }

        return null;
    }

    @Override
    public boolean hasHeader() {
        return mHasHeader;
    }

    @Override
    public int getViewTypeForIndex(int index) {
        int adjustedIndex = index - (hasHeader() ? 1 : 0);

        if (hasHeader() && index == 0) {
            return ELECTION_VIEW_HOLDER;
        } else if (index == referendumTextIndex || index == referendumProIndex || index == referendumConIndex) {
            return REFERENDUM_VIEW_HOLDER;
        } else if (mContest.candidates != null && adjustedIndex < mContest.candidates.size()) {
            return CANDIDATE_VIEW_HOLDER;
        } else if (index < getRowCount() - 1) {
            return CONTEST_DETAIL_VIEW_HOLDER;
        }

        return REPORT_VIEW_HOLDER;
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
