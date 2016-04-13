package com.votinginfoproject.VotingInformationProject.activities.homeActivity;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.votinginfoproject.VotingInformationProject.BuildConfig;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.asynctasks.GeocodeQuery;
import com.votinginfoproject.VotingInformationProject.models.CivicApiAddress;
import com.votinginfoproject.VotingInformationProject.models.CivicApiError;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.ElectionAdministrationBody;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.CivicInfoInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.requests.CivicInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.StopLightCivicInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.singletons.UserPreferences;

import java.util.ArrayList;

/**
 * Created by marcvandehey on 3/31/16.
 */
public class HomePresenterImpl extends HomePresenter implements CivicInfoInteractor.CivicInfoCallback {

    private static final String TAG = HomePresenterImpl.class.getSimpleName();
    private static final String VOTER_INFO_KEY = "VOTER_INFO";
    private static final String ALL_PARTIES_KEY = "ALL_PARTIES";
    private CivicInfoInteractor mCivicInteractor;
    private boolean mTestRun = false;
    private VoterInfo mVoterInfo;
    private ArrayList<Election> mElections;
    private ArrayList<String> mParties;

    private Context mContext;

    private int mSelectedElection;
    private int mSelectedParty;

    private String allPartiesString;

    public HomePresenterImpl(@NonNull Context context) {
        this.mVoterInfo = null;
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
                mVoterInfo = new Gson().fromJson(voterInfoString, VoterInfo.class);
            }

            allPartiesString = savedState.getString(ALL_PARTIES_KEY);
        }
    }

    @Override
    public void onAttachView(HomeView homeView) {
        super.onAttachView(homeView);

        //Recreate view with cached response
        if (mVoterInfo != null) {
            civicInfoResponse(mVoterInfo);
        }
    }

    @Override
    public void onDetachView() {
        super.onDetachView();

        cancelSearch();
    }

    @Override
    public void onSaveState(@NonNull Bundle state) {
        if (mVoterInfo != null) {
            String voterInfoString = new Gson().toJson(mVoterInfo);
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

        if (mVoterInfo != null && mVoterInfo.isSuccessful()) {
            //Filter Voting info and send to activity


            String filter = "";

            //If All Parties is not Selected
            if (mSelectedParty > 0) {
                filter = mParties.get(mSelectedParty);
            }

            getView().navigateToVoterInformationActivity(mVoterInfo, filter);
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

            mVoterInfo = null;
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
                searchAddress = mContext.getString(R.string.test_address);

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
    public void civicInfoResponse(VoterInfo response) {
        if (response != null) {
            if (response.isSuccessful()) {
                mVoterInfo = response;

                //If this succeeds, it is assumed Play services is available for the rest of the app
                GoogleApiAvailability api = GoogleApiAvailability.getInstance();
                int code = api.isGooglePlayServicesAvailable(mContext);
                if (code == ConnectionResult.SUCCESS) {
                    //Start loading up location overhead data

                    mVoterInfo.setUpLocations();

                    geocodeHomeAddress(mVoterInfo.normalizedInput.toGeocodeString());
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

    private void geocodeHomeAddress(String address) {
        Log.v(TAG, "Attempting to geocode home address");
        new GeocodeQuery(mContext, new GeocodeQuery.GeocodeCallBackListener() {
            @Override
            public void callback(String key, double latitude, double longitude, double distance) {
                if (key.equals("error")) {
                    Log.e(TAG, "Failed to geocode home address!");
                } else {
                    Location homeLocation = new Location("home");
                    homeLocation.setLatitude(latitude);
                    homeLocation.setLongitude(longitude);
                    UserPreferences.setHomeLocation(homeLocation);
                }

                //Chain polling locations geocoding, continue even if home location failed
                geocodePollingLocations();
            }
        }, "home", address, null, UserPreferences.useMetric(), null).execute();
    }

    private void geocodePollingLocations() {
        if (UserPreferences.getHomeLocation() == null) {
            Log.e(TAG, "No home address available in Geocode Polling Locations canceling");

            updateViewWithVoterInfo();

            return;
        }

        // start background geocode tasks for polling locations
        final ArrayList<PollingLocation> allLocations = mVoterInfo.getAllLocations();
        for (final PollingLocation location : allLocations) {
            // key by address, if location has no ID
            String id = (location.id != null) ? location.id : location.address.toGeocodeString();

            Log.v(TAG, "Attempting to geocode Polling address with id: " + id);

            //TODO not sure if this was ever used.
            new GeocodeQuery(mContext, new GeocodeQuery.GeocodeCallBackListener() {
                @Override
                public void callback(String key, double latitude, double longitude, double distance) {
                    if (key.equals("error")) {
                        Log.e(TAG, "Failed to geocode Polling Locations");
                    } else {
                        // find object and set values on it
                        PollingLocation foundLoc = mVoterInfo.getLocationForId(key);
                        if (foundLoc != null) {
                            foundLoc.address.latitude = latitude;
                            foundLoc.address.longitude = longitude;
                            foundLoc.address.distance = distance;
                        } else {
                            Log.e(TAG, "Could not find location " + key + " to set geocoding result!");
                        }
                    }
                }
            }, id, location.address.toGeocodeString(), UserPreferences.getHomeLocation(), UserPreferences.useMetric(), null).execute();
        }

        //Proceeding while Polling locations are loading
        geoCodeStateAdminAddress();
    }

    private void geoCodeStateAdminAddress() {
        if (UserPreferences.getHomeLocation() == null) {
            Log.e(TAG, "No home address available in Geocode State Admin - Exiting");

            updateViewWithVoterInfo();

            return;
        }

        Log.v(TAG, "Attempting to geocode state admin address");

        // state
        CivicApiAddress stateAdminAddress = mVoterInfo.getAdminAddress(ElectionAdministrationBody.AdminBody.STATE);
        if (stateAdminAddress != null) {
            new GeocodeQuery(mContext, new GeocodeQuery.GeocodeCallBackListener() {
                @Override
                public void callback(String key, double latitude, double longitude, double distance) {
                    if (key.equals("error")) {
                        Log.e(TAG, "Failed to geocode administrative body physical address!");
                    } else {
                        CivicApiAddress address = mVoterInfo.getAdminAddress(key);
                        if (address != null) {
                            address.latitude = latitude;
                            address.longitude = longitude;
                            address.distance = distance;
                        } else {
                            Log.e(TAG, "Failed to set geocode result on election admin body!");
                        }
                    }

                    geocodeLocalAdminAddress();
                }
            }, ElectionAdministrationBody.AdminBody.STATE,
                    stateAdminAddress.toGeocodeString(), UserPreferences.getHomeLocation(), UserPreferences.useMetric(), null).execute();
        } else {
            Log.e(TAG, "State Admin Address is Null");
            geocodeLocalAdminAddress();
        }
    }

    private void geocodeLocalAdminAddress() {
        if (UserPreferences.getHomeLocation() == null) {
            Log.e(TAG, "No home address available in Geocode Local Admin - Exiting");

            updateViewWithVoterInfo();

            return;
        }

        Log.v(TAG, "Attempting to geocode local admin address");

        // local
        CivicApiAddress localAdminAddress = mVoterInfo.getAdminAddress(ElectionAdministrationBody.AdminBody.LOCAL);
        if (localAdminAddress != null) {
            new GeocodeQuery(mContext, new GeocodeQuery.GeocodeCallBackListener() {
                @Override
                public void callback(String key, double latitude, double longitude, double distance) {
                    if (key.equals("error")) {
                        Log.e(TAG, "Failed to geocode administrative body physical address!");
                    } else {
                        CivicApiAddress address = mVoterInfo.getAdminAddress(key);
                        if (address != null) {
                            address.latitude = latitude;
                            address.longitude = longitude;
                            address.distance = distance;
                        } else {
                            Log.e(TAG, "Failed to set geocode result on election admin body!");
                        }
                    }

                    //Callback to View to show downloaded contents
                    updateViewWithVoterInfo();
                }
            }, ElectionAdministrationBody.AdminBody.LOCAL,
                    localAdminAddress.toGeocodeString(), UserPreferences.getHomeLocation(), UserPreferences.useMetric(), null).execute();
        } else {
            Log.e(TAG, "Local Admin Address is NULL");

            //Callback to View to show downloaded contents
            updateViewWithVoterInfo();
        }
    }

    private void updateViewWithVoterInfo() {
        getView().hideStatusView();
        getView().showGoButton();

        if (mVoterInfo.otherElections != null && mVoterInfo.otherElections.size() > 0) {
            //Setup all elections data and show chooser

            mElections = new ArrayList<>(mVoterInfo.otherElections);

            //Add the default election to the front of the list.
            mElections.add(0, mVoterInfo.election);

            getView().showElectionPicker();
            mSelectedElection = 0;
            getView().setElectionText(mVoterInfo.election.getName());
        }

        mParties = mVoterInfo.getUniqueParties();
        mParties.add(0, allPartiesString);

        if (mParties.size() > 1) {
            getView().setPartyText(mParties.get(0));
            mSelectedParty = 0;
            getView().showPartyPicker();
        }
    }
}
