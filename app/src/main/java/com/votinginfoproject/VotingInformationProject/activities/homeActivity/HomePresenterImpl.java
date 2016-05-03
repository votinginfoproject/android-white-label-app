package com.votinginfoproject.VotingInformationProject.activities.homeActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.votinginfoproject.VotingInformationProject.BuildConfig;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.CivicApiError;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.VoterInfoResponse;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.CivicInfoInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.GeocodeInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.requests.CivicInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.GeocodeVoterInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.StopLightCivicInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.singletons.VoterInformation;

import java.util.ArrayList;


/**
 * Created by marcvandehey on 3/31/16.
 */
public class HomePresenterImpl extends HomePresenter implements CivicInfoInteractor.CivicInfoCallback, GeocodeInteractor.GeocodeCallback {

    private static final String TAG = HomePresenterImpl.class.getSimpleName();
    private static final String VOTER_INFO_KEY = "VOTER_INFO";
    private static final String ALL_PARTIES_KEY = "ALL_PARTIES";
    private CivicInfoInteractor mCivicInteractor;
    private boolean mTestRun = false;
    private VoterInfoResponse mVoterInfoResponse;
    private ArrayList<Election> mElections;
    private ArrayList<String> mParties;

    private Context mContext;

    private int mSelectedElection;
    private int mSelectedParty;

    private String allPartiesString;

    public HomePresenterImpl(@NonNull Context context) {
        this.mVoterInfoResponse = null;
        mContext = context;

        mSelectedElection = 0;
        mSelectedParty = 0;

        allPartiesString = context.getString(R.string.fragment_home_all_parties);
    }

    @Override
    public void onCreate(Bundle savedState) {
        if (savedState != null) {
            String voterInfoString = savedState.getString(VOTER_INFO_KEY);


            Log.v(TAG, "Saved String: " + voterInfoString);

            if (voterInfoString != null && voterInfoString.length() > 0) {
                mVoterInfoResponse = new Gson().fromJson(voterInfoString, VoterInfoResponse.class);
            }

            allPartiesString = savedState.getString(ALL_PARTIES_KEY);
        }
    }

    @Override
    public void onAttachView(HomeView homeView) {
        super.onAttachView(homeView);

        //Recreate view with cached response
        if (mVoterInfoResponse != null) {
            civicInfoResponse(mVoterInfoResponse);
        }
    }

    @Override
    public void onDetachView() {
        super.onDetachView();

        cancelSearch();
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        if (mVoterInfoResponse != null) {
            String voterInfoString = new Gson().toJson(mVoterInfoResponse);
            state.putString(VOTER_INFO_KEY, voterInfoString);
            state.putString(ALL_PARTIES_KEY, allPartiesString);
        }
    }

    @Override
    public void onDestroy() {
        setView(null);
        mContext = null;

        cancelSearch();
    }

    /**
     * For use when testing only.  Sets flag to indicate that we're testing the app, so it will
     * use the special AboutVIPActivity election ID for the query.
     */
    public void doTestRun() {
        mTestRun = true;
    }

    // Home Presenter Protocol

    @Override
    public void selectedElection(Context context, String address, int election) {
        if (mElections.size() > election) {
            mSelectedElection = election;
            getView().setElectionText(mElections.get(mSelectedElection).getName());

            searchElection(address, mElections.get(election).getId());
        }
    }

    @Override
    public void selectedParty(int party) {
        if (mParties.size() > party) {
            mSelectedParty = party;
            getView().setPartyText(mParties.get(mSelectedParty));

            VoterInformation.setSelectedParty(mParties.get(mSelectedParty));
        }
    }

    @Override
    public void electionTextViewClicked() {
        ArrayList<String> list = new ArrayList<>();

        for (Election election : mElections) {
            list.add(election.getName());
        }

        getView().displayElectionPickerWithItems(list, mSelectedElection);
    }

    @Override
    public void partyTextViewClicked() {
        getView().displayPartyPickerWithItems(mParties, mSelectedParty);
    }

    @Override
    public void goButtonClicked() {
        Log.d(TAG, "Go Button Clicked");

        if (mVoterInfoResponse != null && mVoterInfoResponse.isSuccessful()) {
            //Filter Voting info and send to activity
            String filter = "";

            //If All Parties is not Selected
            if (mSelectedParty > 0) {
                filter = mParties.get(mSelectedParty);
            }

            getView().navigateToVoterInformationActivity(mVoterInfoResponse, filter);
        }
    }

    @Override
    public void aboutButtonClicked() {
        Log.d(TAG, "About Button Clicked");

        if (mCivicInteractor == null) {
            getView().navigateToAboutActivity();
        }
    }

    @Override
    public void searchButtonClicked(@NonNull String searchAddress) {
        Log.d(TAG, "Search Button Clicked");

        searchElection(searchAddress, "");
    }

    private void searchElection(@NonNull String searchAddress, @NonNull String electionId) {
        if (mCivicInteractor == null) {
            getView().hideElectionPicker();
            getView().hidePartyPicker();
            getView().hideGoButton();

            mVoterInfoResponse = null;
            mSelectedElection = 0;

            getView().showMessage(R.string.activity_home_status_loading);

            if (mTestRun) {
                electionId = "2000"; // AboutVIPActivity election ID (for use only in testing)
            }

            //TODO Determine if we need to search available elections before this point

            mCivicInteractor = new CivicInfoInteractor();

            CivicInfoRequest request;

            //Check if we are building with the Debug settings, if so attempt to use StopLight
            if (BuildConfig.DEBUG && mContext.getResources().getBoolean(R.bool.use_stoplight)) {
                if (searchAddress.isEmpty()) {
                    searchAddress = mContext.getString(R.string.test_address);
                }

                request = new StopLightCivicInfoRequest(mContext, electionId, searchAddress);

                //Set address to AboutVIPActivity string for directions api

                getView().overrideSearchAddress(searchAddress);
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

    // Interactor Callback

    /**
     * This chain preloads all the needed data before we load into the app, Currently chained loading calls
     * Get Civic Info > Geocode Address Entered > Geocode Polling Locations > Geocode State Admin Addresses > Geocode Local Admin Addresses
     *
     * @param response
     */

    @Override
    public void civicInfoResponse(VoterInfoResponse response) {
        if (response != null) {
            if (response.isSuccessful()) {
                mVoterInfoResponse = response;

                VoterInformation.updateWithVoterInfoResponse(mVoterInfoResponse);

                //If this succeeds, it is assumed Play services is available for the rest of the app
                GoogleApiAvailability api = GoogleApiAvailability.getInstance();
                int code = api.isGooglePlayServicesAvailable(mContext);
                if (code == ConnectionResult.SUCCESS) {
                    //Start loading up location overhead data

                    mVoterInfoResponse.setUpLocations();

                    GeocodeInteractor interactor = new GeocodeInteractor();
                    //TODO use key here when it is hooked up correctly
                    GeocodeVoterInfoRequest request = new GeocodeVoterInfoRequest(""/*mContext.getString(R.string.google_api_browser_key)*/, mVoterInfoResponse);

                    interactor.enqueueRequest(request, this);
                } else {
                    Log.e(TAG, "Play Services Unavailable");
                    updateViewWithVoterInfo();

                    getView().showMessage(R.string.locations_map_error_play_services_unavailable);
                }
            } else {
                getView().hideGoButton();

                CivicApiError error = response.getError();

                CivicApiError.Error error1 = error.errors.get(0);

                Log.e(TAG, "Civic API returned error: " + error.code + ": " +
                        error.message + " : " + error1.domain + " : " + error1.reason);

                if (CivicApiError.errorMessages.get(error1.reason) != null) {
                    int reason = CivicApiError.errorMessages.get(error1.reason);
                    getView().showMessage(reason);
                } else {
                    Log.e(TAG, "Unknown API error reason: " + error1.reason);
                    getView().showMessage(R.string.fragment_home_error_unknown);
                }
            }
        } else {
            Log.d(TAG, "API returned null response");
            getView().showMessage(R.string.fragment_home_error_unknown);
        }

        cancelSearch();
    }

    private void updateViewWithVoterInfo() {
        getView().hideStatusView();
        getView().showGoButton();

        if (mVoterInfoResponse.otherElections != null && mVoterInfoResponse.otherElections.size() > 0) {
            //Setup all elections data and show chooser

            mElections = new ArrayList<>(mVoterInfoResponse.otherElections);

            //Add the default election to the front of the list.
            mElections.add(0, mVoterInfoResponse.election);

            VoterInformation.setElection(mVoterInfoResponse.election);

            getView().showElectionPicker();
            mSelectedElection = 0;
            getView().setElectionText(mVoterInfoResponse.election.getName());
        }

        mParties = mVoterInfoResponse.getUniqueParties();
        mParties.add(0, allPartiesString);

        VoterInformation.setSelectedParty(allPartiesString);
        if (mParties.size() > 1) {
            getView().setPartyText(mParties.get(0));
            mSelectedParty = 0;
            getView().showPartyPicker();
        }
    }

    @Override
    public void onGeocodeResults(VoterInfoResponse voterInfoResponse) {
        mVoterInfoResponse = voterInfoResponse;
        updateViewWithVoterInfo();
    }
}
