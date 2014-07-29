package com.votinginfoproject.VotingInformationProject.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.adapters.LocationsAdapter;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VIPAppContext;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;


public class VIPMapFragment extends SupportMapFragment {

    View rootView;
    ArrayList<PollingLocation> allLocations;

    // track which location filter button was last clicked, and only refresh list if it changed
    int lastSelectedListButton = R.id.locations_list_all_button;

    public static VIPMapFragment newInstance() {
        return new VIPMapFragment();
    }

    public static VIPMapFragment newInstance(GoogleMapOptions options) {
        Bundle args = new Bundle();

        Resources res = VIPAppContext.getContext().getResources();

        args.putParcelable(res.getString(R.string.google_api_android_key), options);
        VIPMapFragment fragment = new VIPMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public VIPMapFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("MapFragment", "Creating map fragment");
        //rootView = inflater.inflate(R.layout.fragment_map, container, false);
        rootView = super.onCreateView(inflater, container, savedInstanceState);

        VIPTabBarActivity myActivity = (VIPTabBarActivity) this.getActivity();

        allLocations = myActivity.getAllLocations();

        return rootView;
    }


}