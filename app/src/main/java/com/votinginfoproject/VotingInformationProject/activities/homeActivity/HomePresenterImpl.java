package com.votinginfoproject.VotingInformationProject.activities.homeActivity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.votinginfoproject.VotingInformationProject.BuildConfig;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.CivicApiError;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.CivicInfoInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.requests.CivicInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.StopLightCivicInfoRequest;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 3/31/16.
 */
public class HomePresenterImpl implements HomePresenter, CivicInfoInteractor.CivicInfoCallback {

    private static final String TAG = HomePresenterImpl.class.getSimpleName();
    private HomeView mHomeView;
    private Context mContext;
    private CivicInfoInteractor mCivicInteractor;
    private boolean mTestRun = false;

    private VoterInfo mVoterInfo;
    private ArrayList<Election> mElections;
    private ArrayList<String> mParties;

    private int mSelectedElection;
    private int mSelectedParty;

    private Uri mContactUri;

    public HomePresenterImpl(Context context, HomeView homeView) {
        this.mContext = context;
        this.mHomeView = homeView;

        this.mVoterInfo = null;
        this.mContactUri = null;

        mSelectedElection = 0;
        mSelectedParty = 0;
    }

    /**
     * For use when testing only.  Sets flag to indicate that we're testing the app, so it will
     * use the special test election ID for the query.
     */
    public void doTestRun() {
        mTestRun = true;
    }

    @Override
    public void selectedElection(int election) {
        if (mElections.size() > election) {
            mSelectedElection = election;
            mHomeView.setElectionText(mElections.get(mSelectedElection).getName());
        }
    }

    @Override
    public void selectedParty(int party) {
        if (mParties.size() > party) {
            mSelectedParty = party;
            mHomeView.setPartyText(mParties.get(mSelectedParty));
        }
    }

    @Override
    public void electionTextViewClicked() {
        ArrayList<String> list = new ArrayList<>();

        for (Election election : mElections) {
            list.add(election.getName());
        }

        mHomeView.displayElectionPickerWithItems(list, mSelectedElection);
    }

    @Override
    public void partyTextViewClicked() {
        mHomeView.displayPartyPickerWithItems(mParties, mSelectedParty);
    }

    @Override
    public void goButtonClicked() {
        Log.d(TAG, "Go Button Clicked");

        if (mVoterInfo != null && mVoterInfo.isSuccessful()) {
            mHomeView.navigateToVIPResultsActivity(mVoterInfo);
        }
    }

    @Override
    public void aboutButtonClicked() {
        Log.d(TAG, "About Button Clicked");

        if (mCivicInteractor == null) {
            mHomeView.navigateToAboutActivity();
        }
    }

    @Override
    public void searchButtonClicked(String searchAddress) {
        Log.d(TAG, "Search Button Clicked");

        if (mCivicInteractor == null) {
            mHomeView.hideElectionPicker();
            mHomeView.hidePartyPicker();
            mHomeView.hideGoButton();

            mVoterInfo = null;
            mSelectedElection = 0;

            mHomeView.showMessage(mContext.getString(R.string.activity_home_status_loading));

            String electionId = "";

            if (mTestRun) {
                electionId = "2000"; // test election ID (for use only in testing)
            }

            //TODO Determine if we need to search available elections before this point

            mCivicInteractor = new CivicInfoInteractor();

            CivicInfoRequest request;

            //Check if we are building with the Debug settings, if so attempt to use StopLight
            if (BuildConfig.DEBUG && mContext.getResources().getBoolean(R.bool.use_stoplight)) {
                searchAddress = mContext.getString(R.string.test_address);

                request = new StopLightCivicInfoRequest(mContext, electionId, searchAddress);

                //Set address to test string for directions api

                mHomeView.overrideSearchAddress(searchAddress);
            } else {
                request = new CivicInfoRequest(mContext, electionId, searchAddress);
            }

            mCivicInteractor.enqueueRequest(request, this);
        }
    }

    private void cancelSearch() {
        if (mCivicInteractor != null) {
            mCivicInteractor.cancel(true);
            mCivicInteractor.onDestroy();

            mCivicInteractor = null;
        }
    }

    @Override
    public void onDestroy() {
        mHomeView = null;
        mContext = null;
        cancelSearch();
    }

    // Interactor Callback

    @Override
    public void civicInfoResponse(VoterInfo response) {
        if (response != null) {
            if (response.isSuccessful()) {
                mVoterInfo = response;

                mHomeView.showGoButton();
                mHomeView.hideStatusView();

                if (mVoterInfo.otherElections != null && mVoterInfo.otherElections.size() > 0) {
                    //Setup all elections data and show chooser

                    mElections = new ArrayList<>(mVoterInfo.otherElections);

                    //Add the default election to the front of the list.
                    mElections.add(0, mVoterInfo.election);

                    mHomeView.showElectionPicker();
                    mSelectedElection = 0;
                    mHomeView.setElectionText(mVoterInfo.election.getName());
                }

                mParties = mVoterInfo.getUniqueParties();
                mParties.add(0, mContext.getString(R.string.fragment_home_all_parties));

                if (mParties.size() > 0) {
                    mHomeView.setPartyText(mParties.get(0));
                    mSelectedParty = 0;
                    mHomeView.showPartyPicker();
                }
            } else {
                mHomeView.hideGoButton();

                CivicApiError error = response.getError();

                CivicApiError.Error error1 = error.errors.get(0);

                Log.e(TAG, "Civic API returned error: " + error.code + ": " +
                        error.message + " : " + error1.domain + " : " + error1.reason);

                if (CivicApiError.errorMessages.get(error1.reason) != null) {
                    int reason = CivicApiError.errorMessages.get(error1.reason);
                    mHomeView.showMessage(mContext.getString(reason));
                } else {
                    Log.e(TAG, "Unknown API error reason: " + error1.reason);
                    mHomeView.showMessage(mContext.getString(R.string.fragment_home_error_unknown));
                }
            }
        } else {
            Log.d(TAG, "API returned null response");
            mHomeView.showMessage(mContext.getString(R.string.fragment_home_error_unknown));
        }

        cancelSearch();
    }
}
