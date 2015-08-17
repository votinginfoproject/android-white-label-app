package com.votinginfoproject.VotingInformationProject.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.votinginfoproject.VotingInformationProject.R;
import com.votinginfoproject.VotingInformationProject.activities.VIPTabBarActivity;
import com.votinginfoproject.VotingInformationProject.adapters.LocationsAdapter;
import com.votinginfoproject.VotingInformationProject.models.PollingLocation;
import com.votinginfoproject.VotingInformationProject.models.VoterInfo;

import java.util.ArrayList;
import java.util.List;


public class LocationsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    static final ButterKnife.Action<View> HIDE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setVisibility(View.GONE);
        }
    };
    static final ButterKnife.Action<View> SHOW = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setVisibility(View.VISIBLE);
        }
    };

    VoterInfo voterInfo;
    LocationsAdapter listAdapter;
    View rootView;
    TextView noneFoundMessage;
    ListView locationsList;
    ArrayList<PollingLocation> allLocations;
    VIPTabBarActivity.FilterLabels filterLabels;

    @Bind({R.id.locations_list_map_button, R.id.locations_list, R.id.locations_list_spinner})
    List<View> locationViews;

    @Bind(R.id.mail_only_message)
    TextView mailOnlyMessage;

    // track which location filter was last selected, and only refresh list if it changed
    long lastSelectedFilterItem = 0; // default to all items, which is first in list

    public static LocationsFragment newInstance() {
        return new LocationsFragment();
    }
    public LocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_locations, container, false);
        ButterKnife.bind(this, rootView);
        final VIPTabBarActivity myActivity = (VIPTabBarActivity)this.getActivity();

        Resources res = myActivity.getResources();

        // election labels
        TextView election_name_label = (TextView)rootView.findViewById(R.id.locations_election_title);
        TextView election_date_label = (TextView)rootView.findViewById(R.id.locations_election_subtitle);
        election_name_label.setText(voterInfo.election.name);
        election_date_label.setText(voterInfo.election.getFormattedDate());

        // clear directions polyline, if set from directions view
        myActivity.polylineCallback("", null);

        if(voterInfo.isMailOnly()) {
            ButterKnife.apply(locationViews, HIDE);
            mailOnlyMessage.setVisibility(View.VISIBLE);
        } else {
            ButterKnife.apply(locationViews, SHOW);
            mailOnlyMessage.setVisibility(View.GONE);
        }

        // click handler for map button
        rootView.findViewById(R.id.locations_list_map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // zoom to user address
                myActivity.polylineCallback(null, null);
                myActivity.showMap("home");
            }
        });

        // set up locations list
        locationsList = (ListView)rootView.findViewById(R.id.locations_list);
        locationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get location ID tagged onto its distance label in the list adapter
                String itemTag = view.findViewById(R.id.location_list_item_distance).getTag().toString();
                // open directions view on list item tap (will open map view on item button tap)
                Log.d("LocationsFragment", "Clicked list item " + itemTag);
                myActivity.showDirections(itemTag);
            }
        });

        View feedback_layout = myActivity.getLayoutInflater().inflate(R.layout.feedback_link, locationsList, false);
        // 'false' argument here is to make the footer list item not clickable (text instead is clickable)
        locationsList.addFooterView(feedback_layout, null, false);

        // get labels for dropdown
        filterLabels = myActivity.getFilterLabels();

        // build filter dropdown list, and initialize with all locations
        ArrayList<String> filterOptions = new ArrayList<>(4);
        // always show 'all sites' option
        filterOptions.add(filterLabels.ALL);
        // show the other three options if there are any
        if (!voterInfo.getOpenEarlyVoteSites().isEmpty()) {
            filterOptions.add(filterLabels.EARLY);
        }
        if (!voterInfo.getPollingLocations().isEmpty()) {
            filterOptions.add(filterLabels.POLLING);
        }
        if(!voterInfo.getOpenDropOffLocations().isEmpty()) {
            filterOptions.add(filterLabels.DROPBOX);
        }

        Spinner filterSpinner = (Spinner) rootView.findViewById(R.id.locations_list_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter(myActivity,
                R.layout.location_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(R.layout.locations_spinner_view);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(this);
        filterSpinner.setSelection(0); // all locations by default

        // find label for empty results
        noneFoundMessage = (TextView) rootView.findViewById(R.id.locations_none_found_message);

        // initialize list adapter with all locations
        allLocations = voterInfo.getAllLocations();
        // copy the list, so the original isn't destroyed by the adapter construction
        //noinspection unchecked
        listAdapter = new LocationsAdapter(myActivity, (ArrayList<PollingLocation>) allLocations.clone());
        locationsList.setAdapter(listAdapter);

        if (allLocations.isEmpty()) {
            locationsList.setVisibility(View.GONE);
            noneFoundMessage.setText(R.string.locations_none_found);
            noneFoundMessage.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    /**
     * Helper function to set locations list when filter button clicked
     * @param locations PollingLocation objects to put in the list
     */
    private void setFilter(List<PollingLocation> locations) {
        if (locations != null && !locations.isEmpty()) {
            listAdapter.clear();
            listAdapter.addAll(locations);
            locationsList.setVisibility(View.VISIBLE);
            noneFoundMessage.setVisibility(View.GONE);
        } else {
            locationsList.setVisibility(View.GONE);
            // should only be able to select 'all' filter with no results
            noneFoundMessage.setText(R.string.locations_none_found);
            noneFoundMessage.setVisibility(View.VISIBLE);
        }
    }

    public void refreshList() {
        if (listAdapter != null) {
            locationsList.invalidate();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // get election info
        voterInfo = ((VIPTabBarActivity) activity).getVoterInfo();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // do nothing if selected filter item is unchanged
        if (id == lastSelectedFilterItem) {
            return;
        }

        lastSelectedFilterItem = id;

        String selection = (String) parent.getItemAtPosition(position);
        if (selection == filterLabels.ALL) {
            setFilter(allLocations);
        } else if (selection == filterLabels.EARLY) {
            setFilter(voterInfo.getOpenEarlyVoteSites());
        } else if (selection == filterLabels.POLLING) {
            setFilter(voterInfo.getPollingLocations());
        } else if (selection == filterLabels.DROPBOX) {
            setFilter(voterInfo.getOpenDropOffLocations());
        } else {
            Log.e("LocationsFragment", "Selected item " + selection + "isn't recognized!");
            setFilter(allLocations);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // required method implementation
    }
}
