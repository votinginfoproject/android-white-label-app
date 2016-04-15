package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.votinginfoproject.VotingInformationProject.fragments.TestFragment;
import com.votinginfoproject.VotingInformationProject.fragments.TestFragment2;
import com.votinginfoproject.VotingInformationProject.fragments.electionDetailsFragment.ElectionDetailsListFragment;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.PollingSitesListFragment;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.VIPMapFragment;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

/**
 * Created by marcvandehey on 4/6/16.
 */
public class VoterInformationPresenterImpl extends VoterInformationPresenter {
    private static final String TAG = VoterInformationPresenterImpl.class.getSimpleName();
    private static final String VOTER_INFO_KEY = "VOTER_INFO";

    private final int POLLS_TAB = 0x0;
    private final int BALLOT_TAB = 0x1;
    private final int DETAILS_TAB = 0x2;

    private int mCurrentTab = 0x0;

    private VoterInfo mVoterInfo;
    private String mPartyFilter;

    public VoterInformationPresenterImpl(@NonNull VoterInfo voterInfo, String partyFilter) {
        mVoterInfo = voterInfo;

        UserPreferences.setVoterInfo(voterInfo);

        mPartyFilter = partyFilter;
    }

    @Override
    public void onCreate(Bundle savedState) {
        if (savedState != null) {
            String voterInfoString = savedState.getString(VOTER_INFO_KEY);

            Log.v(TAG, "Saved String: " + voterInfoString);

            if (voterInfoString != null && voterInfoString.length() > 0) {
                mVoterInfo = new Gson().fromJson(voterInfoString, VoterInfo.class);
            } else {
                //Kill View and navigate back to home.
            }
        }
    }

    @Override
    public void onAttachView(VoterInformationView voterInformationView) {
        super.onAttachView(voterInformationView);
        //Tell view to style fragments with voterInfo
    }

    @Override
    public void onDetachView() {
        super.onDetachView();

    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        if (mVoterInfo != null) {
            String voterInfoString = new Gson().toJson(mVoterInfo);
            state.putString(VOTER_INFO_KEY, voterInfoString);
        }
    }

    @Override
    public void onDestroy() {

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

            getView().presentParentLevelFragment(TestFragment.newInstance("ballot", "nope"));
        } else {
            //If currently selected, reset the scroll position
            getView().scrollCurrentFragmentToTop();
            getView().presentChildLevelFragment(TestFragment.newInstance("ballot child", "nope"));
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

    @Override
    public void overflowButtonClicked() {

    }

    @Override
    public void mapFilterButtonClicked(int filterItem) {

    }
}
