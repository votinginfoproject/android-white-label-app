package com.votinginfoproject.VotingInformationProject.activities.voterInformationActivity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 4/6/16.
 */
public class VoterInformationPresenterImpl extends VoterInformationPresenter {
    private static final String TAG = VoterInformationPresenterImpl.class.getSimpleName();
    private static final String VOTER_INFO_KEY = "VOTER_INFO";

    private ArrayList<String> mBallotEntries;
    private ArrayList<String> mPollsEntries;
    private ArrayList<String> mDetailsEntries;


    private VoterInfo mVoterInfo;
    private String mPartyFilter;

    public VoterInformationPresenterImpl(@NonNull VoterInfo voterInfo, String partyFilter) {
        mVoterInfo = voterInfo;
        mPartyFilter = partyFilter;

        mBallotEntries = new ArrayList<>();
        mPollsEntries = new ArrayList<>();
        mDetailsEntries = new ArrayList<>();
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
        mBallotEntries.clear();
        mPollsEntries.clear();
        mDetailsEntries.clear();
    }

    // Voter Information Presenter Protocol

    @Override
    public void backNavigationBarButtonClicked() {

    }

    @Override
    public void ballotButtonClicked() {

    }

    @Override
    public void detailsButtonClicked() {

    }

    @Override
    public void pollingSitesButtonClicked() {
        getView().showPollingSiteFragment();
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
