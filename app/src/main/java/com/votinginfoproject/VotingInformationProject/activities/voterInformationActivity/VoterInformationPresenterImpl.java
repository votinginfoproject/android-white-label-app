package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.votinginfoproject.VotingInformationProject.fragments.ballotFragment.ContestListFragment;
import com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment.ElectionDetailsListFragment;

import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.PollingSitesListFragment;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.VIPMapFragment;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

/**
 * Created by marcvandehey on 4/6/16.
 */
public class VoterInformationPresenterImpl extends VoterInformationPresenter {
    private final int POLLS_TAB = 0x0;
    private final int BALLOT_TAB = 0x1;
    private final int DETAILS_TAB = 0x2;

    private int mCurrentTab = -1;

    public VoterInformationPresenterImpl() {

    }

    @Override
    public void onCreate(Bundle savedState) {
        //Not implemented
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        //Not implemented
    }

    @Override
    public void onDestroy() {
        //Not implemented
    }

    @Override
    public void onAttachView(VoterInformationView voterInformationView) {
        super.onAttachView(voterInformationView);

        if (mCurrentTab == -1) {
            mCurrentTab = POLLS_TAB;

            getView().presentParentLevelFragment(PollingSitesListFragment.newInstance());
        }
    }

    // Voter Information Presenter Protocol

    @Override
    public void backNavigationBarButtonClicked() {
        getView().navigateBack();
    }

    @Override
    public void ballotButtonClicked() {
        if (mCurrentTab != BALLOT_TAB) {
            mCurrentTab = BALLOT_TAB;

            getView().presentParentLevelFragment(ContestListFragment.newInstance(VoterInformation.getElection(), VoterInformation.getContests()));
        } else {
            //If currently selected, reset the scroll position
            getView().scrollCurrentFragmentToTop();
        }
    }

    @Override
    public void detailsButtonClicked() {
        if (mCurrentTab != DETAILS_TAB) {
            mCurrentTab = DETAILS_TAB;

            getView().presentParentLevelFragment(ElectionDetailsListFragment.newInstance());
        } else {
            //If currently selected, reset the scroll position
            getView().scrollCurrentFragmentToTop();
        }
    }

    @Override
    public void pollingSitesButtonClicked() {
        if (mCurrentTab != POLLS_TAB) {
            mCurrentTab = POLLS_TAB;

            getView().presentParentLevelFragment(PollingSitesListFragment.newInstance());
        } else {
            //If currently selected, reset the scroll position
            getView().scrollCurrentFragmentToTop();
        }
    }

    @Override
    void mapButtonClicked(@LayoutRes int currentSort) {
        getView().presentChildLevelFragment(VIPMapFragment.newInstance((Activity) getView(), "home", currentSort));
    }

    @Override
    void listButtonClicked(@LayoutRes int currentSort) {
        //Kill all back navigation since we are going to the default view
        getView().presentParentLevelFragment(PollingSitesListFragment.newInstance(currentSort));
    }
}
