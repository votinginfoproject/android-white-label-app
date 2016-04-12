package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.votinginfoproject.VotingInformationProject.fragments.TestFragment;
import com.votinginfoproject.VotingInformationProject.fragments.TestFragment2;
import com.votinginfoproject.VotingInformationProject.fragments.pollingSitesFragment.PollingSitesFragment;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;

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

            getView().presentParentLevelFragment(TestFragment2.newInstance("details", "nope"));
        } else {
            //If currently selected, reset the scroll position
            getView().scrollCurrentFragmentToTop();
            getView().presentChildLevelFragment(TestFragment2.newInstance("details child", "nope"));

        }
    }

    @Override
    public void pollingSitesButtonClicked() {
        if (mCurrentTab != POLLS_TAB) {
            mCurrentTab = POLLS_TAB;

            getView().presentParentLevelFragment(PollingSitesFragment.newInstance(new ArrayList<>(mVoterInfo.getPollingLocations())));
        } else {
            //If currently selected, reset the scroll position
            getView().scrollCurrentFragmentToTop();
        }
    }

    @Override
    public void mapButtonClicked() {

    }

    @Override
    public void overflowButtonClicked() {

    }

    @Override
    public void mapFilterButtonClicked(int filterItem) {

    }
}
