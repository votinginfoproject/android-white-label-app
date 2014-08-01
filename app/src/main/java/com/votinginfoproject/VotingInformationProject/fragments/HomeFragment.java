package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.asynctasks.CivicInfoApiQuery;
import com.votinginfoproject.VotingInformationProject.models.*;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    Button homeGoButton;
    CivicInfoApiQuery.CallBackListener voterInfoListener;
    CivicInfoApiQuery.CallBackListener voterInfoErrorListener;
    Context context;
    EditText homeEditTextAddress;
    TextView homeTextViewStatus;
    Spinner homeElectionSpinner;
    View homeElectionSpinnerWrapper;

    Election currentElection;
    String address;
    SharedPreferences preferences;
    private OnInteractionListener mListener;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        currentElection = new Election();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity().getApplicationContext();

        homeTextViewStatus = (TextView)rootView.findViewById(R.id.home_textview_status);

        homeGoButton = (Button)rootView.findViewById(R.id.home_go_button);
        homeGoButton.setVisibility(View.INVISIBLE);

        homeEditTextAddress = (EditText)rootView.findViewById(R.id.home_edittext_address);
        homeEditTextAddress.setText(getAddress());

        homeElectionSpinner = (Spinner)rootView.findViewById(R.id.home_election_spinner);
        homeElectionSpinnerWrapper = rootView.findViewById(R.id.home_election_spinner_wrapper);

        setupViewListeners();
        setupCivicAPIListeners();

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

        // EditText onSearch Listener
        homeEditTextAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mListener != null) {
                    String address = view.getText().toString();
                    setAddress(address);
                    constructVoterInfoQuery();
                }
                // Return false to close the keyboard
                return false;
            }
        });

        // Spinner listener
        homeElectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected (AdapterView < ? > adapterView, View view, int index, long id){

                Election selectedElection = (Election) adapterView.getItemAtPosition(index);
                Log.d("HomeFragment", "Selected via election picker: " + selectedElection.toString());
                // Only fire a new voterInfo query if the election changes
                if (!selectedElection.getId().equals(currentElection.getId())) {
                    currentElection = selectedElection;
                    constructVoterInfoQuery();
                }
            }
            @Override
            public void onNothingSelected (AdapterView < ? > adapterView){
                // PASS
            }
        });
    }

    /**
     * Check for Internet connectivity before querying API.  If the Internet is unavailable or
     * disconnected, display a message and quit the app.
     */
    public void checkInternetConnectivity() {
        Context context = VIPAppContext.getContext();
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            CharSequence errorMessage = context.getResources().getText(R.string.home_error_no_internet);
            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            getActivity().finish();
        }
    }
    private void constructVoterInfoQuery() {
        checkInternetConnectivity(); // check for connection before querying

        String electionId = "";
        try {
            electionId = currentElection.getId();
        } catch (NullPointerException e) {}

        try {
            Resources res = context.getResources();
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").authority("www.googleapis.com").appendPath("civicinfo");
            builder.appendPath(res.getString(R.string.civic_info_api_version));
            builder.appendPath("voterinfo").appendQueryParameter("officialOnly", "true");
            if (!electionId.isEmpty()) {
                builder.appendQueryParameter("electionId", electionId);
            }
            builder.appendQueryParameter("address", address);
            builder.appendQueryParameter("key", res.getString(R.string.google_api_browser_key));
            String apiUrl = builder.build().toString();
            Log.d("HomeActivity", "searchedAddress: " + apiUrl);
            homeTextViewStatus.setText(R.string.home_status_loading);
            homeTextViewStatus.setVisibility(View.VISIBLE);
            new CivicInfoApiQuery<VoterInfo>(VoterInfo.class, voterInfoListener, voterInfoErrorListener).execute(apiUrl);
        } catch (Exception e) {
            Log.e("HomeActivity Exception", "searchedAddress: " + address);
        }
    }

    private void setupCivicAPIListeners() {

        // Callback for voterInfoQuery result
        voterInfoListener = new CivicInfoApiQuery.CallBackListener() {
            @Override
            public void callback(Object result) {
                if (result == null) { return; }
                VoterInfo voterInfo = (VoterInfo)result;
                currentElection = voterInfo.election;
                homeTextViewStatus.setVisibility(View.GONE);
                homeGoButton.setVisibility(View.VISIBLE);

                // read Go button to user, if TalkBack enabled
                homeGoButton.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

                mListener.searchedAddress(voterInfo);

                // Show election picker if there are other elections
                ArrayList<Election> elections = new ArrayList<Election>(voterInfo.otherElections);
                elections.add(0, voterInfo.election);
                setSpinnerElections(elections);
            }
        };

        // Callback for voterInfoQuery error result
        voterInfoErrorListener = new CivicInfoApiQuery.CallBackListener() {
            @Override
            public void callback(Object result) {
                try {
                    homeGoButton.setVisibility(View.INVISIBLE);
                    CivicApiError error = (CivicApiError) result;
                    Log.d("HomeFragment", "Civic API returned error");
                    Log.d("HomeFragment", error.code + ": " + error.message);
                    CivicApiError.Error error1 = error.errors.get(0);
                    Log.d("HomeFragment", error1.domain + " " + error1.reason + " " + error1.message);
                    if (CivicApiError.errorMessages.get(error1.reason) != null) {
                        homeTextViewStatus.setText(CivicApiError.errorMessages.get(error1.reason));
                    } else {
                        // TODO: catch this with exception handler below once we've identified them all
                        Log.d("HomeFragment", "Unknown API error reason: " + error1.reason);
                        homeTextViewStatus.setText(R.string.home_error_unknown);
                    }
                } catch(NullPointerException e) {
                    Log.e("HomeFragment", "Null encountered in API error result");
                    homeTextViewStatus.setText(R.string.home_error_unknown);
                }

                // read error result, if TalkBack enabled
                homeTextViewStatus.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        };
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnInteractionListener {
        public void onGoButtonPressed(View view);
        public void searchedAddress(VoterInfo voterInfo);
    }

    public String getAddress() {
        // Bias the returned address towards a saved address in preferences if one does
        //  not exist in memory
        if (address == null || address.isEmpty()) {
            String addressKey = getString(R.string.LAST_ADDRESS_KEY);
            address = preferences.getString(addressKey, "");
        }
        return address;
    }

    public void setAddress(String address) {
        SharedPreferences.Editor editor = preferences.edit();
        String addressKey = getString(R.string.LAST_ADDRESS_KEY);
        editor.putString(addressKey, address);
        editor.apply();
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
        ArrayAdapter<Election> adapter =
                new ArrayAdapter<Election>(getActivity(), R.layout.home_election_spinner_view, elections);
        homeElectionSpinner.setAdapter(adapter);
    };

}
