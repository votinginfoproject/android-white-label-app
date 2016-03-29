package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.BuildConfig;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.HomeActivity;
import com.votinginfoproject.VotingInformationProject.models.CivicApiError;
import com.votinginfoproject.VotingInformationProject.models.Contest;
import com.votinginfoproject.VotingInformationProject.models.Election;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;
import com.votinginfoproject.VotingInformationProject.models.api.interactors.CivicInfoInteractor;
import com.votinginfoproject.VotingInformationProject.models.api.requests.CivicInfoRequest;
import com.votinginfoproject.VotingInformationProject.models.api.requests.StopLightCivicInfoRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */


public class HomeFragment extends Fragment implements CivicInfoInteractor.CivicInfoCallback {

    private final String TAG = HomeFragment.class.getSimpleName();

    private Button homeGoButton;
    private HomeActivity myActivity;
    private Context mContext;
    private EditText homeEditTextAddress;
    private TextView homeTextViewStatus;
    private Spinner homeElectionSpinner;
    private View homeElectionSpinnerWrapper;
    private Spinner homePartySpinner;
    private View homePartySpinnerWrapper;
    private ImageView homeSelectContactButton;
    private Button aboutUsButton;

    private Election currentElection;
    private String address;
    private OnInteractionListener mListener;
    private boolean isTest;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    /**
     * For use when testing only.  Sets flag to indicate that we're testing the app, so it will
     * use the special test election ID for the query.
     */
    public void doTestRun() {
        isTest = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myActivity = (HomeActivity) getActivity();
        currentElection = new Election();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mContext = myActivity.getApplicationContext();

        // read flag from api_keys file for whether to use test election or not
        isTest = mContext.getResources().getBoolean(R.bool.use_test_election);

        homeTextViewStatus = (TextView) rootView.findViewById(R.id.home_textview_status);

        homeGoButton = (Button) rootView.findViewById(R.id.home_go_button);
        aboutUsButton = (Button) rootView.findViewById(R.id.home_about_us_button);

        homeSelectContactButton = (ImageView) rootView.findViewById(R.id.home_select_contact_button);

        homeEditTextAddress = (EditText) rootView.findViewById(R.id.home_edittext_address);
        homeEditTextAddress.setText(getAddress());

        homeElectionSpinner = (Spinner) rootView.findViewById(R.id.home_election_spinner);
        homeElectionSpinnerWrapper = rootView.findViewById(R.id.home_election_spinner_wrapper);

        homePartySpinner = (Spinner) rootView.findViewById(R.id.home_party_spinner);
        homePartySpinnerWrapper = rootView.findViewById(R.id.home_party_spinner_wrapper);

        setupViewListeners();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mContext = null;
    }

    /**
     * Query the Civic Info API after the entered address has changed
     *
     * @param new_address New address entered
     */
    public void queryWithNewAddress(String new_address) {
        Log.d(TAG, "queryWithNewAddress");

        showLoadingState();

        setAddress(new_address);

        // clear previous election before making a query for a new address
        mListener.searchedAddress(null);
        currentElection = null;
        myActivity.setSelectedParty("");

        // only hide election picker when searching with a new address
        homeElectionSpinnerWrapper.setVisibility(View.GONE);
        homeGoButton.setVisibility(View.GONE);
        constructVoterInfoQuery();
    }

    private void showLoadingState() {
        homeTextViewStatus.setVisibility(View.VISIBLE);
        homeSelectContactButton.setEnabled(false);
        homeTextViewStatus.setText(R.string.fragment_home_status_loading);
        homeEditTextAddress.setEnabled(false);
    }

    private void hideLoadingState() {
        homeEditTextAddress.setEnabled(true);
        homeSelectContactButton.setEnabled(true);
    }

    private void setupViewListeners() {
        // Go Button onClick Listener
        homeGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onGoButtonPressed(view);
                }
            }
        });

        // About Us Button onClickListener
        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onAboutUsButtonPressed();
                }
            }
        });

        // EditText image button onClick listener
        homeSelectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onSelectContactButtonPressed(view);
                }
            }
        });

        // EditText onSearch Listener
        homeEditTextAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        && mListener != null) {
                    queryWithNewAddress(view.getText().toString());
                }

                // Return false to close the keyboard
                return false;
            }
        });

        // election spinner listener
        homeElectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {
                Election selectedElection = (Election) adapterView.getItemAtPosition(index);
                Log.d(TAG, "Selected via election picker: " + selectedElection.toString());
                // Only fire a new voterInfo query if the election changes
                if (!selectedElection.getId().equals(currentElection.getId())) {
                    currentElection = selectedElection;
                    constructVoterInfoQuery();
                }

                ((TextView) view).setTextColor(Color.WHITE);
                view.setBackgroundResource(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // PASS
            }
        });

        // party spinner listener
        homePartySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {
                //If index is zero, no real value is selected
                if (index == 0) {
                    //Disable Button
                    homeGoButton.setEnabled(false);
                    homeGoButton.setAlpha(0.5f);
                } else {
                    //Enable Button
                    homeGoButton.setEnabled(true);
                    homeGoButton.setAlpha(1.f);

                    myActivity.setSelectedParty((String) adapterView.getItemAtPosition(index));
                    Log.d(TAG, "Selected via party picker: " + myActivity.getSelectedParty());
                }

                ((TextView) view).setTextColor(Color.WHITE);
                view.setBackgroundResource(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // PASS
            }
        });
    }

    /**
     * Check for Internet connectivity before querying API.  If the Internet is unavailable or
     * disconnected, display a message and quit the app.
     */
    public void checkInternetConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            homeGoButton.setVisibility(View.INVISIBLE);
            homeTextViewStatus.setText(mContext.getResources().getText(R.string.fragment_home_error_no_internet));
        }
    }

    private void constructVoterInfoQuery() {
        checkInternetConnectivity(); // check for connection before querying

        String electionId = "";

        if (isTest) {
            electionId = "2000"; // test election ID (for use only in testing)
        }

        try {
            electionId = currentElection.getId();
        } catch (NullPointerException e) {
            Log.e(TAG, "Current election is unset");
        }

        CivicInfoRequest request;

        //Check if we are building with the Debug settings, if so attempt to use StopLight
        if (BuildConfig.DEBUG && getActivity().getResources().getBoolean(R.bool.use_stoplight)) {
            request = new StopLightCivicInfoRequest(getActivity(), electionId, address);

            //Set address to test string for directions api
            address = getActivity().getString(R.string.test_address);
            homeEditTextAddress.setText(address);
        } else {
            request = new CivicInfoRequest(getActivity(), electionId, address);
        }

        CivicInfoInteractor civicInfoInteractor = new CivicInfoInteractor();
        civicInfoInteractor.enqueueRequest(request, this);
    }

    private void presentVoterInfoResult(VoterInfo voterInfo) {
        currentElection = voterInfo.election;
        homeTextViewStatus.setVisibility(View.GONE);
        homeGoButton.setVisibility(View.VISIBLE);

        // read Go button to user, if TalkBack enabled
        homeGoButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

        mListener.searchedAddress(voterInfo);

        // Show election picker if there are other elections
        ArrayList<Election> elections = new ArrayList<>(voterInfo.otherElections);
        elections.add(0, voterInfo.election);

        setSpinnerElections(elections);
        setSpinnerParty(voterInfo.contests);
    }

    public String getAddress() {
        return address;
    }

    /**
     * Store a new address into shared preferences, and clear out last saved election.
     *
     * @param address Address string to store
     */
    public void setAddress(String address) {
        this.address = address;
    }

    // Assumes that the currently selected election is the first in the list
    public void setSpinnerElections(List<Election> elections) {
        if (elections == null || elections.size() < 2) {
            homeElectionSpinnerWrapper.setVisibility(View.GONE);

            return;
        } else {
            homeElectionSpinnerWrapper.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<Election> adapter = new ArrayAdapter<>(myActivity, R.layout.home_spinner_view, elections);
        homeElectionSpinner.setAdapter(adapter);

        homeElectionSpinner.setSelection(0, true);

        //Force selected text view style
        View v = homeElectionSpinner.getSelectedView();
        ((TextView) v).setTextColor(Color.WHITE);
        v.setBackgroundResource(0);
    }

    public void setSpinnerParty(List<Contest> contests) {
        HashSet<String> parties = new HashSet<>(5);
        if (contests != null) {
            for (Contest contest : contests) {
                // if contest has a primary party listed, it must be for a primary election
                if (contest.primaryParty != null && !contest.primaryParty.isEmpty()) {
                    parties.add(contest.primaryParty);
                }
            }
        } else {
            Log.d(TAG, "No contests for election");
        }

        if (!parties.isEmpty()) {
            // convert set to list for adapter
            List<String> partiesList = new ArrayList<>(parties);
            // sort list alphabetically
            Collections.sort(partiesList);

            partiesList.add(0, mContext.getString(R.string.fragment_home_party_spinner_description));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(myActivity, R.layout.home_spinner_view, partiesList);

            homePartySpinner.setAdapter(adapter);

            homePartySpinner.setSelection(0, true);

            View selectedView = homePartySpinner.getSelectedView();
            ((TextView) selectedView).setTextColor(Color.WHITE);
            selectedView.setBackgroundResource(0);

            homePartySpinnerWrapper.setVisibility(View.VISIBLE);
        } else {
            homePartySpinnerWrapper.setVisibility(View.GONE);
        }
    }

    @Override
    public void civicInfoResponse(VoterInfo response) {
        if (response != null) {
            homeGoButton.setVisibility(View.VISIBLE);

            if (response.isSuccessful()) {
                presentVoterInfoResult(response);
            } else {
                homeGoButton.setVisibility(View.INVISIBLE);

                CivicApiError error = response.getError();

                CivicApiError.Error error1 = error.errors.get(0);

                Log.e(TAG, "Civic API returned error: " + error.code + ": " +
                        error.message + " : " + error1.domain + " : " + error1.reason);

                if (CivicApiError.errorMessages.get(error1.reason) != null) {
                    homeTextViewStatus.setText(CivicApiError.errorMessages.get(error1.reason));
                } else {
                    Log.e(TAG, "Unknown API error reason: " + error1.reason);
                    homeTextViewStatus.setText(R.string.fragment_home_error_unknown);
                }

                // read error result, if TalkBack enabled
                homeTextViewStatus.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                homeTextViewStatus.setVisibility(View.VISIBLE);
            }
        } else {
            Log.d(TAG, "API returned null response");
            homeTextViewStatus.setText(R.string.fragment_home_error_unknown);
            homeTextViewStatus.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        }

        hideLoadingState();
    }

    public interface OnInteractionListener {
        void onGoButtonPressed(View view);

        void onAboutUsButtonPressed();

        void onSelectContactButtonPressed(View view);

        void searchedAddress(VoterInfo voterInfo);
    }
}
