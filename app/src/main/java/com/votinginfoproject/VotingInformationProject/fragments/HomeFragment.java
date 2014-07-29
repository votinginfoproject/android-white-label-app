package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.models.CivicInfoApiQuery;
import com.votinginfoproject.VotingInformationProject.models.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class HomeFragment extends Fragment {

    Button homeGoButton;
    CivicInfoApiQuery.CallBackListener voterInfoListener;
    CivicInfoApiQuery.CallBackListener voterInfoErrorListener;
    Context context;
    EditText homeEditTextAddress;
    TextView homeTextViewStatus;

    String address;
    SharedPreferences preferences;
    private OnInteractionListener mListener;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
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
        homeGoButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onGoButtonPressed(view);
            }
        });

        // EditText onSearch Listener
        homeEditTextAddress.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH && mListener != null) {
                String address = view.getText().toString();
                setAddress(address);
                try {
                    // TODO: election ID should be in the keys file, not hard-coded
                    String apiUrl = "voterinfo?officialOnly=true" +
                            "&electionId=2000" +
                            "&address=" + URLEncoder.encode(address, "UTF-8") +
                            "&key=";
                    Log.d("HomeActivity", "searchedAddress: " + apiUrl);
                    homeTextViewStatus.setText(R.string.home_status_loading);
                    homeTextViewStatus.setVisibility(View.VISIBLE);
                    new CivicInfoApiQuery<VoterInfo>(context, VoterInfo.class, voterInfoListener, voterInfoErrorListener).execute(apiUrl);
                } catch (UnsupportedEncodingException e) {
                    Log.e("HomeActivity Exception", "searchedAddress: " + address);
                }
            }
            // Return false to close the keyboard
            return false;
        });

    }

    private void setupCivicAPIListeners() {

        // Callback for voterInfoQuery result
        voterInfoListener = (result) -> {
            if (result == null) { return; }
            VoterInfo voterInfo = (VoterInfo) result;
            homeTextViewStatus.setVisibility(View.GONE);
            homeGoButton.setVisibility(View.VISIBLE);
            mListener.searchedAddress(voterInfo);
        };

        // Callback for voterInfoQuery error result
        voterInfoErrorListener = (result) -> {
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
        editor.commit();
        this.address = address;
    }
}
